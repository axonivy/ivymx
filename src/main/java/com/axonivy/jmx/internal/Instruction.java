package com.axonivy.jmx.internal;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.management.MBeanException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import com.axonivy.jmx.MBean;
import com.axonivy.jmx.MException;

/**
 * Parses the {@link MBean#value() MBean.name} and {@link MBean#description} value to an {@link Instruction} that can be exeuted to resolve the runtime {@link MBean#value() MBean.name} and {@link MBean#description} values.<br>
 * The parsed String values can contain EL scripts enclosed in #{...}. The EL scripts can contain attributes of the {@link MBean}. The attributes can be provided as fields or getter/setter methods. It is also possible to access attributes of
 * attributes.<br>
 * Example:
 * <pre>
 * {@code @MBean}
 * public class MyBean
 * {
 * private String name = "Reto";
 *
 * private Application getApplication()
 * {
 * return new Application();
 * }
 * }
 *
 * public class Application
 * {
 * String getDescription()
 * {
 * return "Weiss";
 * }
 * }
 * -----
 * Instruction.parseInstruction(manager, MyBean.class, "Hello #{name}").execute(new MyBean()) -> "Hello Reto"
 * Instruction.parseInstruction(manager, MyBean.class, "Hello #{application.description}").execute(new MyBean()) -> "Hello Weiss"
 * </pre>
 *
 *
 * @author rwei
 * @since 01.07.2013
 */
abstract class Instruction {
  private static final Class<?>[] NO_PARAMETERS = new Class[0];

  abstract String execute(Object baseObject);

  static Instruction parseInstruction(MBeanManager manager, Class<?> mBeanClass, String instruction) {
    return ElParser.parse(manager, mBeanClass, instruction);
  }

  private static class ElParser {
    private static final String START_TAG = "#{";
    private static final String END_TAG = "}";

    private MBeanManager manager;
    private Class<?> mBeanClass;
    private String instruction;
    private MainInstruction main = new MainInstruction();

    public ElParser(MBeanManager manager, Class<?> mBeanClass, String instruction) {
      this.manager = manager;
      this.mBeanClass = mBeanClass;
      this.instruction = instruction;
    }

    public static Instruction parse(MBeanManager manager, Class<?> mBeanClass, String instruction) {
      return new ElParser(manager, mBeanClass, instruction).parseInstruction();
    }

    private Instruction parseInstruction() {
      while (hasStartTag()) {
        parseToStartTag();
        parseElInstruction();
      }
      parseToEnd();
      return main;
    }

    private boolean hasStartTag() {
      return instruction.contains(START_TAG);
    }

    private void parseToEnd() {
      Instruction constantEndStringLiteral = new StringLiteralInstruction(instruction);
      main.instructions.add(constantEndStringLiteral);
    }

    private void parseElInstruction() {
      if (!instruction.contains(END_TAG)) {
        throw new IllegalArgumentException("Parameter instructionr does contains more '" + START_TAG + "' than '" + END_TAG + "'");
      }
      String elScript = StringUtils.substringBefore(instruction, END_TAG);
      Instruction elInstruction = parseElInstruction(elScript);
      main.instructions.add(elInstruction);
      instruction = StringUtils.substringAfter(instruction, END_TAG);
    }

    private void parseToStartTag() {
      StringLiteralInstruction constantStartStringLiteral = new StringLiteralInstruction(StringUtils.substringBefore(instruction, START_TAG));
      main.instructions.add(constantStartStringLiteral);
      instruction = StringUtils.substringAfter(instruction, START_TAG);
    }

    private Instruction parseElInstruction(String elInstruction) {
      try {
        AbstractValueAccessor valueAccessor = new BeanValueAccessor();
        Class<?> currentClass = mBeanClass;
        for (String attribute : elInstruction.split("\\.")) {
          Pair<AbstractValueAccessor, Class<?>> result = createValueAccessor(valueAccessor, currentClass, attribute);
          valueAccessor = result.getLeft();
          currentClass = result.getRight();
        }
        return new ElExpression(elInstruction, valueAccessor);
      } catch (Exception ex) {
        throw new IllegalArgumentException("Failed to parse instruction '" + instruction + "' on bean class '" + mBeanClass + "'", ex);
      }

    }

    private Pair<AbstractValueAccessor, Class<?>> createValueAccessor(AbstractValueAccessor previousResolver,
        Class<?> currentClass, String attribute) throws IllegalArgumentException, SecurityException, NoSuchFieldException {
      try {
        return createGetterMethodAccessor(previousResolver, currentClass, attribute);
      } catch (NoSuchMethodException ex) {
        return createFieldAccessor(previousResolver, currentClass, attribute);
      }
    }

    private Pair<AbstractValueAccessor, Class<?>> createFieldAccessor(AbstractValueAccessor previousResolver, Class<?> currentClass, String attribute) throws IllegalArgumentException, SecurityException, NoSuchFieldException {
      try {
        Field field = currentClass.getDeclaredField(attribute);
        return new ImmutablePair<AbstractValueAccessor, Class<?>>(new FieldBasedValueAccessor(previousResolver, field), field.getType());
      } catch (NoSuchFieldException ex) {
        if (currentClass.getSuperclass() == null) {
          throw ex;
        }
        return createFieldAccessor(previousResolver, currentClass.getSuperclass(), attribute);
      }
    }

    private Pair<AbstractValueAccessor, Class<?>> createGetterMethodAccessor(AbstractValueAccessor previousResolver, Class<?> currentClass, String attribute)
        throws NoSuchMethodException {
      String methodName = "get" + StringUtils.capitalize(attribute);
      Method method = getMethod(currentClass, methodName);
      return new ImmutablePair<AbstractValueAccessor, Class<?>>(new MethodBasedValueAccessor(manager, previousResolver, method), method.getReturnType());
    }

    private static Method getMethod(Class<?> clazz, String methodName) throws NoSuchMethodException {
      try {
        return clazz.getMethod(methodName, NO_PARAMETERS);
      } catch (NoSuchMethodException ex) {
        return getNonPublicMethod(clazz, methodName);
      }
    }

    private static Method getNonPublicMethod(Class<?> clazz, String methodName) throws NoSuchMethodException {
      try {
        return clazz.getDeclaredMethod(methodName, NO_PARAMETERS);
      } catch (NoSuchMethodException ex) {
        if (clazz.getSuperclass() == null) {
          throw ex;
        }
        return getNonPublicMethod(clazz.getSuperclass(), methodName);
      }
    }
  }

  private static class MainInstruction extends Instruction {
    private List<Instruction> instructions = new ArrayList<Instruction>();

    @Override
    String execute(Object baseObject) {
      StringBuilder builder = new StringBuilder(1024);
      for (Instruction instruction : instructions) {
        builder.append(instruction.execute(baseObject));
      }
      return builder.toString();
    }
  }

  private static class StringLiteralInstruction extends Instruction {
    private String literal;

    public StringLiteralInstruction(String literal) {
      this.literal = literal;
    }

    @Override
    String execute(Object baseObject) {
      return literal;
    }
  }

  private static class ElExpression extends Instruction {
    private AbstractValueAccessor valueAccessor;
    private String instruction;

    private ElExpression(String instruction, AbstractValueAccessor valueAccessor) {
      this.instruction = instruction;
      this.valueAccessor = valueAccessor;
    }

    @Override
    String execute(Object baseObject) {
      try {
        return String.valueOf(valueAccessor.getValue(baseObject));
      } catch (MBeanException ex) {
        throw new MException("Cannot resolve '" + instruction + "' on mBean '" + baseObject.getClass().getName() + "'", ex);
      }
    }
  }
}

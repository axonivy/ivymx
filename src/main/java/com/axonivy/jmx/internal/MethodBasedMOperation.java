package com.axonivy.jmx.internal;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

import javax.management.MBeanException;
import javax.management.MBeanParameterInfo;
import javax.management.ReflectionException;
import javax.management.openmbean.OpenMBeanOperationInfo;
import javax.management.openmbean.OpenMBeanOperationInfoSupport;
import javax.management.openmbean.OpenMBeanParameterInfo;
import javax.management.openmbean.OpenMBeanParameterInfoSupport;

import org.apache.commons.lang3.ArrayUtils;

import com.axonivy.jmx.MException;
import com.axonivy.jmx.MOperation;

/**
 * Implements a jmx operation by forwarding it to a method of a target class.
 * @author rwei
 * @since 01.07.2013
 */
class MethodBasedMOperation
{
  private Method method;
  private MOperation operation;
  private OpenMBeanOperationInfo mBeanInfo;
  private MBeanManager manager;
  private AbstractValueAccessor targetAccessor;
  private Instruction nameInstruction;
  private Instruction descriptionInstruction;
  private OpenMBeanParameterInfo[] parameterInfos;

  MethodBasedMOperation(MBeanManager manager, AbstractValueAccessor targetAccessor, Method method, MOperation operation)
  {
    this.manager = manager;
    this.targetAccessor = targetAccessor;
    this.method = method;
    method.setAccessible(true);
    this.operation = operation;
    mBeanInfo = createMBeanInfo();
    nameInstruction = Instruction.parseInstruction(manager, method.getDeclaringClass(), mBeanInfo.getName());
    descriptionInstruction = Instruction.parseInstruction(manager, method.getDeclaringClass(), mBeanInfo.getDescription());
  }

  private OpenMBeanOperationInfoSupport createMBeanInfo()
  {
    String name = MInternalUtils.getOperationName(method, operation);
    String description = MInternalUtils.getDescription(operation.description(), name);
    parameterInfos = buildParameterInfos();
    return new OpenMBeanOperationInfoSupport(
            name,
            description,
            parameterInfos,
            manager.toOpenType(method.getReturnType()),
            operation.impact().toInt());
  }

  private OpenMBeanOperationInfo evaluateInfo(Object mBean)
  {
    return new OpenMBeanOperationInfoSupport(
            evaluateName(mBean),
            evaluateDescription(mBean),
            parameterInfos,
            mBeanInfo.getReturnOpenType(),
            mBeanInfo.getImpact());
  }

  Object invoke(final Object beanInstance, final Object[] params) throws MBeanException, ReflectionException
  {
    final Object target = targetAccessor.getValue(beanInstance);
    try
    {
      return manager.executeInContext(new Callable<Object>(){

        @Override
        public Object call() throws Exception
        {
          return method.invoke(target, params);
        }});
    }
    catch (IllegalArgumentException ex)
    {
      throw new ReflectionException(ex);
    }
    catch (IllegalAccessException ex)
    {
      throw new ReflectionException(ex);
    }
    catch (Exception ex)
    {
      throw new MBeanException(ex);
    }
  }

  private OpenMBeanParameterInfo[] buildParameterInfos()
  {
    OpenMBeanParameterInfo[] paramInfos = new OpenMBeanParameterInfo[method.getParameterTypes().length];
    int pos=0;
    for (Class<?> type : method.getParameterTypes())
    {
      String parameterName = getParameterName(pos);
      String parameterDescription = getParameterDescription(pos, parameterName);
      OpenMBeanParameterInfo info = new OpenMBeanParameterInfoSupport(
              parameterName,
              parameterDescription,
              manager.toOpenType(type));
      paramInfos[pos++] = info;
    }
    return paramInfos;
  }

  private String getParameterName(int pos)
  {
    String[] names = operation.params();
    if (ArrayUtils.getLength(names) <= pos ||
        names[pos] == null ||
        names[pos].isEmpty())
    {
      return "arg"+pos;
    }
    return names[pos];
  }

  private String getParameterDescription(int pos, String parameterName)
  {
    String[] descriptions = operation.paramDescriptions();
    if (ArrayUtils.getLength(descriptions) <= pos ||
        descriptions[pos] == null ||
        descriptions[pos].isEmpty())
    {
      return parameterName;
    }
    return descriptions[pos];
  }

  private static String buildSignature(OpenMBeanOperationInfo info)
  {
    return buildSignature(info.getName(), info.getSignature());
  }

  private static String buildSignature(String name, MBeanParameterInfo[] signature)
  {
    StringBuilder sig = new StringBuilder(512);
    sig.append(name);
    sig.append("(");
    boolean firstParam = true;
    for (MBeanParameterInfo paramInfo : signature)
    {
      if (!firstParam)
      {
        sig.append(", ");
      }
      firstParam = false;
      sig.append(paramInfo.getType());
    }
    sig.append(")");
    return sig.toString();
  }


  static String buildSignature(String actionName, String[] paramsSignature)
  {
    StringBuilder sig = new StringBuilder(512);
    sig.append(actionName);
    sig.append("(");
    boolean firstParam = true;
    for (String type : paramsSignature)
    {
      if (!firstParam)
      {
        sig.append(", ");
      }
      firstParam = false;
      sig.append(type);
    }
    sig.append(")");
    return sig.toString();
  }

  private String evaluateDescription(Object mBean)
  {
    try
    {
      Object target = targetAccessor.getValue(mBean);
      return descriptionInstruction.execute(target);
    }
    catch(MBeanException ex)
    {
      throw new MException(ex);
    }
  }

  private String evaluateName(Object mBean)
  {
    try
    {
      Object target = targetAccessor.getValue(mBean);
      return nameInstruction.execute(target);
    }
    catch(MBeanException ex)
    {
      throw new MException(ex);
    }
  }

  void evaluate(Object mBean, MBeanInstanceInfo mBeanInstanceInfo)
  {
    OpenMBeanOperationInfo info = evaluateInfo(mBean);
    String evaluatedSignature = buildSignature(info);
    mBeanInstanceInfo.addOperation(this, evaluatedSignature, info);
  }

  @Override
  public String toString()
  {
    return "MOperation "+targetAccessor.getAccessPath()+"."+buildSignature(method.getName(), mBeanInfo.getSignature());
  }
}

package com.axonivy.jmx;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.management.MBeanOperationInfo;

/**
 * <p>Use this annotation to manage a method as operation of a {@link MBean}.</p>
 * <p>Example:</p>
 * <pre>
 * {@code @MBean}
 * public class MyBean
 * {
 *   private int count;
 *
 *   {@code @MOperation}
 *   private void increaseCounter()
 *   {
 *     return count++;
 *   }
 * }</pre>
 * <p>This bean provides an operation {@code increaseCounter}</p>
 * <p>You can use EL expressions to dynamically resolve the name and description of the managed operation.<br>
 * E.g. #{name} will be replaced by the value of the field name if it exists or the value returned by method getName().<br>
 * Also complex expressions like <code>#{pmv.application.name}</code> works.</p>
 *
 * <p>Example:</p>
 * <pre>
 *  {@code @MBean}(value="Test:type=TestType")
 *  public class TestBean
 *  {
 *    {@code @MInclude}
 *    private Counter errors = new Counter("errors");
 *  }
 *
 *  public static class Counter
 *  {
 *    private int cnt=0;
 *
 *    private String name;
 *
 *    public Counter(String name)
 *    {
 *      this.name = name;
 *    }
 *
 *    public String getCapitalizedName()
 *    {
 *      return StringUtils.capitalize(name);
 *    }
 *
 *    {@code @MOperation}(name="reset#{capitalizedName}", description="Resets the #{name} counter")
 *    public void resetCnt()
 *    {
 *      cnt = 0;
 *    }
 *  }
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target( { ElementType.METHOD})
@Inherited
public @interface MOperation
{
  enum Impact
  {
    INFO(MBeanOperationInfo.INFO),
    ACTION(MBeanOperationInfo.ACTION),
    ACTION_INFO(MBeanOperationInfo.ACTION_INFO),
    UNKNOWN(MBeanOperationInfo.UNKNOWN);

    private int value;
    private Impact(int value)
    {
      this.value=value;
    }

    public int toInt()
    {
      return value;
    }
  }
  String name() default "";

  String description() default "";

  /** @return names of the operation's parameters */
  String[] params() default {};

  /** @return descriptions of the operation's parameters */
  String[] paramDescriptions() default {};

  /**
   * @return impact of the operation
   * @see javax.management.MBeanOperationInfo#getImpact()
   */
  Impact impact() default Impact.UNKNOWN;
}

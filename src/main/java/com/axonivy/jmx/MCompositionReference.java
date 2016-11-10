package com.axonivy.jmx;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Use this annotation on fields that reference {@link MBean mbeans} or methods that return references to {@link MBean mbeans} which should be registered and unregistered<br>
 *  if the parent {@link MBean mbean} (the {@link MBean mbean} containing the annotated fields or methods) is registered or unregistered.</p>
 *  <p>Warning:<br>
 *  Only use this annotation on fields or methods that references the same composition {@link MBean mbean} during the whole lifecycle of the parent {@link MBean mbean}.<br>
 *  If one changes the referenced object after the parent {@link MBean mbean} is registered then the framework <b>does not</b> unregister the old composition {@link MBean mbean}<br>
 *  and registers the new referenced composition {@link MBean mbean}. Moreover the old composition {@link MBean mbean} will still be strong referenced by the framework and therefore
 *  not garbage collected as long as the parent bean is registered.</p>
 *  <p>Example:</p>
 *  <pre>
 *  {@code @MBean}("Demo:type=ParentBean")
 *  public class ParentBean
 *  {
 *    {@code @MCompositionReference}
 *    private CompositionBean composition = new CompositionBean;
 *  }
 *
 *  {@code @MBean}("Demo:type=CompositionBean")
 *  public class CompositionBean
 *  {
 *
 *  }
 *  </pre>
 *  <p>If now one calls:</p>
 *  <pre>
 *  ParentBean bean = new ParentBean();
 *  MBeans.registerMBeanFor(bean);
 *  </pre>
 *  <p>then two MBeans get registered the <code>Demo:type=ParentBean</code> and <code>Demo:type=CompositionBean</code>.<br>
 *  Moreover, if now one calls:</p>
 *  <pre>
 *  MBeans.unregisterMBeanFor(bean);
 *  </pre>
 *  <p>then also both MBeans are unregistered.</p>
 * @author rwei
 * @since 28.06.2013
 * @author rwei
 * @since 23.08.2013
 */
@Retention(RetentionPolicy.RUNTIME)
@Target( { ElementType.FIELD, ElementType.METHOD})
@Inherited
public @interface MCompositionReference
{
  /**
   * If set to true then the name of the composition {@link MBean mbean} is build by concat the name of the parent {@link MBean mbean}
   * with the {@link MBean#value() value} attribute of the composition {@link MBean mbean} {@code @MBean}annotation.
   * @return true if the name should be concatenated
   */
  boolean concatName() default false;
}

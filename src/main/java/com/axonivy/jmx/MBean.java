package com.axonivy.jmx;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
  * <p>Use this annotation to manage an object as an MBean.</p>
  * <p>You can use EL expressions to dynamically resolve the name and description of the MBean.<br>
  * E.g. #{name} will be replaced by the value of the field name if it exists or the value returned by method getName().<br>
  * Also complex expressions like <code>#{pmv.application.name}</code> works.
  *
  * <p>Example:
  * <pre>
  * {@code @MBean}(value="ivy:type=MyBean,name=#{name}", description="#{descr}")
  * public class MyBean
  * {
  *   private String name="Hello";
  *
  *   private String getDescr()
  *   {
  *     return "Description of "+name;
  *   }
  * }
  * </pre>
  * The bean will be register with the name {@code ivy:type=MyBean,name=Hello} and a description {@code Description of Hello}.<br>
  * To register a MBean either use {@link MBeans#registerMBeanFor(Object)} or @{link MCollections}.
  * Use {@link MAttribute} and {@link MOperation} to add managed attributes and operations to a MBean.
  */
@Retention(RetentionPolicy.RUNTIME)
@Target( { ElementType.TYPE })
@Inherited
public @interface MBean
{
  String value();
  String description() default "";
  /**
   * If set to true then the bean will be registered even if another bean with the same name is already registered.
   * This is done by modifying the name till it is unique and the bean can be registered with the new unique name
   * @return true if the name should be made unique.
   */
  boolean makeNameUnique() default false;
}

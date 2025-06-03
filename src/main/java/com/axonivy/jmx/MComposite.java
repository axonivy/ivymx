package com.axonivy.jmx;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Use this annotation on classes that are used as complex {@link MAttribute attribute} types. Use {@link MItem} to define the items of the complex value.</p>
 *  Exmaple:
 *  <pre>
 *  {@code @MBean}
 *  public class MyBean
 *  {
 *    {@code @MAttribute}
 *    private Complex complex;
 *  }
 *
 *  {@code @MComposite}
 *  public class Complex
 *  {
 *    {@code @MItem}
 *    private String name;
 *
 *    {@code @MItem}
 *    private String getDesription()
 *    {
 *      return "desc";
 *    }
 *  }
 *  </pre>
 *
 * @author rwei
 * @since 28.06.2013
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
public @interface MComposite {
  /**
   * Name of the type. If not defined the class name will be used a type name
   * @return type name
   */
  String value() default "";
}

package com.axonivy.jmx;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Use this annotation to include {@link MAttribute} or {@link MOperation} of another class that is aggregated by the {@link MBean} as attribute and operations of the {@link MBean} itself.</p>
 * Example:
 * <pre>
 * {@code @MBean}("bean:name=example")
 * class MyBean
 * {
 * {@code @MInclude}
 * private Aggregate aggregate = new Aggregate();
 * }
 *
 * class Aggregate
 * {
 * {@code @MAttribute}
 * private int count;
 *
 * {@code @MOperation}
 * public void start()
 * {
 * }
 * }
 * </pre>
 * The bean (bean:name=example) will provide an attribute {@code count} and an operation {@code start}.
 * @author rwei
 * @since 28.06.2013
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
@Inherited
public @interface MInclude {
  /**
   * If no type is declared on a {@link MInclude} annotation the type of the field or the return type of the method the annotation is declared on
   * will be used to search for more annotations to include to the managed bean.
   * However, if the real type of the field or return type of the method is not the declared type but a sub type and the annotations are declared
   * on the sub type instead of the declared type then use this annotation attribute to specify the real type.
   * @return real implemenation type
   */
  Class<?> type() default Void.class;
}

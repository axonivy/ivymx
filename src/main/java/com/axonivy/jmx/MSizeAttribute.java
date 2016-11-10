package com.axonivy.jmx;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Use this annotation to manage the size of an object as attribute on a {@link MBean}.
 * The following object types are supported:
 * <ul>
 *   <li>{@link java.util.Collection} -&gt; Attribute value: {@link java.util.Collection#size()}</li>
 *   <li>{@link java.util.Map} -&gt; Attribute value: {@link java.util.Map#size()}</li>
 *   <li>{@link java.lang.String} -&gt; Attribute value: {@link java.lang.String#length()}</li>
 * </ul>
 * Example:
 * <pre>
 * {@code @MBean}
 * class MyBean
 * {
 *   {@code @MSizeAttribute}
 *   private List{@code<String>} strings;
 * }
 * </pre>
 * This will provide an attribute with the name strings. The value is the number of Strings in the List.
 * @author rwei
 * @since 28.06.2013
 */
@Retention(RetentionPolicy.RUNTIME)
@Target( { ElementType.FIELD, ElementType.METHOD})
@Inherited
public @interface MSizeAttribute
{
  String description() default "";

  String name() default "";
}

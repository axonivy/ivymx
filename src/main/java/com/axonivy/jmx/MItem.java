package com.axonivy.jmx;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An item of a {@link MComposite}.
 * @see MComposite
 * @author rwei
 * @since 28.06.2013
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
@Inherited
public @interface MItem {
  String description() default "";

  String name() default "";

  /**
   * If no type is declared on a {@link MItem} annotation the type of the field or the return type of the method the annotation is declared on
   * will be used to search for more annotations such as {@link MComposite} that defines the open type of the item.
   * However, if the real type of the field or return type of the method is not the declared type but a sub type and the annotations are declared
   * on the sub type instead of the declared type then use this annotation attribute to specify the real type.
   * If the declared type is a {@link java.util.List} or a sub class of it with one generic parameter then you can use the type attribute to specify the real content type of the list.
   * @return real implementation type
   */
  Class<?> type() default Void.class;
}

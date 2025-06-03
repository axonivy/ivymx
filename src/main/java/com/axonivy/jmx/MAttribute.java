package com.axonivy.jmx;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Use this annotation to manage a value of a field or a value that is provided by bean accessor method as attribute of a {@link MBean}.</p>
 * <p>Example:</p>
 * <pre>
 * {@code @MBean}
 * public class MyBean
 * {
 * {@code @MAttribute}
 * private String myAttribute;
 *
 * private int count;
 *
 * {@code @MAttribute(name="counter", description="My first counter", writable=true)}
 * private int getCount()
 * {
 * return count;
 * }
 *
 * private void setCount(int count)
 * {
 * this.count=count;
 * }
 * }</pre>
 * <p>
 * The bean provides a read only attribute {@code myAttribute} and a writable attribute {@code counter} with the description "My first counter".</p>
 * <p>You can use EL expressions to dynamically resolve the name and description of the managed attribute.<br>
 * E.g. #{name} will be replaced by the value of the field name if it exists or the value returned by method getName().<br>
 * Also complex expressions like <code>#{pmv.application.name}</code> works.</p>
 *
 * <p>Example:</p>
 * <pre>
 * {@code @MBean}(value="Test:type=TestType")
 * public class TestBean
 * {
 * {@code @MInclude}
 * private Counter errors = new Counter("errors");
 * }
 *
 * public static class Counter
 * {
 * {@code @MAttribute}(name="#{name}", description="All #{name} that were occured")
 * private int cnt=0;
 *
 * private String name;
 *
 * public Counter(String name)
 * {
 * this.name = name;
 * }
 * }
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
@Inherited
public @interface MAttribute {
  String description() default "";

  String name() default "";

  boolean isWritable() default false;

  /**
   * If no type is declared on a {@link MAttribute} annotation the type of the field or the return type of the method the annotation is declared on
   * will be used to search for more annotations such as {@link MComposite} that defines the open type of the attribute.
   * However, if the real type of the field or return type of the method is not the declared type but a sub type and the annotations are declared
   * on the sub type instead of the declared type then use this annotation attribute to specify the real type.
   * If the declared type is a {@link java.util.List} or a sub class of it with one generic parameter then you can use the type attribute to specify the real content type of the list.
   * @return real implementation type
   */
  Class<?> type() default Void.class;
}

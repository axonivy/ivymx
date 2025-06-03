package com.axonivy.jmx;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectInstance;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenMBeanAttributeInfo;
import javax.management.openmbean.OpenType;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.axonivy.jmx.internal.MBeanManager;

public class TestIgnoreSyntheticMethods extends BaseMTest<TestIgnoreSyntheticMethods.TestBean> {
  private final List<Throwable> errors = new ArrayList<>();

  @MBean("Test:type=CompositionBean")
  private static class CompositionBean {}

  @SuppressWarnings("unused")
  static class BaseBean<T> {
    @MOperation
    public void operation() {}

    @MAttribute
    public CompositeBean getAttribute() {
      return new CompositeBean();
    }

    @MCompositionReference
    public CompositionBean getCompositionBean() {
      return new CompositionBean();
    }

    @MItem
    public Integer getCount() {
      return 0;
    }
  }

  @MComposite("Composite Data 1")
  public static class CompositeBean extends BaseBean<String> {}

  @MBean("Test:type=TestType")
  public static class TestBean extends BaseBean<String> {}

  public TestIgnoreSyntheticMethods() throws MalformedObjectNameException {
    super(new TestBean(), "Test:type=TestType");
  }

  @Override
  @Before
  public void before() {
    MBeanManager.getInstance().setRegisterMBeanErrorStrategy((mBean, error) -> errors.add(error));
    super.before();
    Method[] methods = TestBean.class.getDeclaredMethods();
    assertThat(methods).hasSize(4);
    assertSyntheticMethod(methods, "getAttribute", MAttribute.class);
    assertSyntheticMethod(methods, "operation", MOperation.class);
    assertSyntheticMethod(methods, "getCompositionBean", MCompositionReference.class);
    assertSyntheticMethod(methods, "getCount", MItem.class);

    methods = CompositeBean.class.getDeclaredMethods();
    assertThat(methods).hasSize(4);
    assertSyntheticMethod(methods, "getAttribute", MAttribute.class);
    assertSyntheticMethod(methods, "operation", MOperation.class);
    assertSyntheticMethod(methods, "getCompositionBean", MCompositionReference.class);
    assertSyntheticMethod(methods, "getCount", MItem.class);

    Method[] superMethods = TestBean.class.getSuperclass().getDeclaredMethods();
    assertThat(superMethods).hasSize(4);
    assertNonSyntheticMethod(superMethods, "getAttribute", MAttribute.class);
    assertNonSyntheticMethod(superMethods, "operation", MOperation.class);
    assertNonSyntheticMethod(superMethods, "getCompositionBean", MCompositionReference.class);
    assertNonSyntheticMethod(superMethods, "getCount", MItem.class);
  }

  @Override
  @After
  public void after() {
    super.after();
    MBeanManager.getInstance().setRegisterMBeanErrorStrategy(MConstants.DEFAULT_ERROR_STRATEGY);
    assertThat(errors).isEmpty();
  }

  private void assertNonSyntheticMethod(Method[] methods, String name, Class<? extends Annotation> annotation) {
    assertMethod(methods, name, annotation, false);
  }

  private void assertSyntheticMethod(Method[] methods, String name, Class<? extends Annotation> annotation) {
    assertMethod(methods, name, annotation, true);
  }

  private void assertMethod(Method[] methods, String name, Class<? extends Annotation> annotation, boolean isSynthetic) {
    Method method = Stream.of(methods)
        .filter(m -> Objects.equals(name, m.getName()))
        .findAny()
        .orElse(null);
    assertThat(method).isNotNull();
    assertThat(method.getAnnotation(annotation)).isNotNull();
    assertThat(method.isSynthetic()).isEqualTo(isSynthetic);
  }

  @Test
  public void operation() throws Exception {
    MBeanInfo beanInfo = getMBeanInfoFromBeanServer();
    assertThat(beanInfo).isNotNull();
    assertThat(beanInfo.getOperations()).hasSize(1);
    assertThat(beanInfo.getOperations()[0].getName()).isEqualTo("operation");
  }

  @Test
  public void attribute() throws Exception {
    MBeanInfo beanInfo = getMBeanInfoFromBeanServer();
    assertThat(beanInfo).isNotNull();
    assertThat(beanInfo.getAttributes()).hasSize(1);
    assertThat(beanInfo.getAttributes()[0].getName()).isEqualTo("attribute");
  }

  @Test
  public void compositionBean() throws Exception {
    ObjectInstance bean = getBeanOrNullFromBeanServer("Test:type=CompositionBean");
    assertThat(bean).isNotNull();
  }

  @Test
  public void item() throws Exception {
    MBeanInfo beanInfo = getMBeanInfoFromBeanServer();
    MBeanAttributeInfo attributeInfo = beanInfo.getAttributes()[0];
    assertThat(attributeInfo).isInstanceOf(OpenMBeanAttributeInfo.class);
    OpenMBeanAttributeInfo openAttributeInfo = (OpenMBeanAttributeInfo) attributeInfo;
    OpenType<?> openType = openAttributeInfo.getOpenType();
    assertThat(openType).isInstanceOf(CompositeType.class);
    CompositeType cType = (CompositeType) openType;
    assertThat(cType.containsKey("count")).isTrue();
  }
}

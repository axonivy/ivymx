package com.axonivy.jmx;

import static org.assertj.core.api.Assertions.assertThat;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanException;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestMBeanBasedMAttribute extends BaseMTest<TestMBeanBasedMAttribute.TestBean> {
  @MBean("Test:type=TestType")
  public static class TestBean {
    @MAttribute
    private EmbeddedBean beanField;

    private EmbeddedBean beanMethod;

    @MAttribute
    public EmbeddedBean getBeanMethod() {
      return beanMethod;
    }
  }

  @Override
  @BeforeEach
  public void before() {
    super.before();
    MBeans.registerMBeanFor(embeddedBean);
  }

  @MBean("Test:type=EmbeddedBean")
  public static class EmbeddedBean {
    @MAttribute
    String test = "Hello World";
  }

  private final EmbeddedBean embeddedBean = new EmbeddedBean();
  private final Object embeddedBeanName;

  public TestMBeanBasedMAttribute() throws MalformedObjectNameException {
    super(new TestBean(), "Test:type=TestType");
    embeddedBeanName = new ObjectName("Test:type=EmbeddedBean");
  }

  @Test
  public void testAttributeInfoBeanField() throws IntrospectionException, InstanceNotFoundException, ReflectionException {
    MBeanAttributeInfo attributeInfo = getAttributeInfo("beanField");
    assertThat(attributeInfo.getDescription()).isEqualTo("beanField");
    assertThat(attributeInfo.getType()).isEqualTo(ObjectName.class.getName());
    assertThat(attributeInfo.isReadable()).isEqualTo(true);
    assertThat(attributeInfo.isWritable()).isEqualTo(false);
    assertThat(attributeInfo.isIs()).isEqualTo(false);
  }

  @Test
  public void testReadAttributeBeanField() throws AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException {
    testBean.beanField = null;
    assertThat(getAttribute("beanField")).isNull();
    testBean.beanField = embeddedBean;
    assertThat(getAttribute("beanField")).isEqualTo(embeddedBeanName);
  }

  @Test
  public void testAttributeInfoEnumMethod() throws IntrospectionException, InstanceNotFoundException, ReflectionException {
    MBeanAttributeInfo attributeInfo = getAttributeInfo("beanMethod");
    assertThat(attributeInfo.getDescription()).isEqualTo("beanMethod");
    assertThat(attributeInfo.getType()).isEqualTo(ObjectName.class.getName());
    assertThat(attributeInfo.isReadable()).isEqualTo(true);
    assertThat(attributeInfo.isWritable()).isEqualTo(false);
    assertThat(attributeInfo.isIs()).isEqualTo(false);
  }

  @Test
  public void testReadAttributeEnumMethod() throws AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException {
    testBean.beanMethod = null;
    assertThat(getAttribute("beanMethod")).isNull();
    testBean.beanMethod = embeddedBean;
    assertThat(getAttribute("beanMethod")).isEqualTo(embeddedBeanName);
  }
}

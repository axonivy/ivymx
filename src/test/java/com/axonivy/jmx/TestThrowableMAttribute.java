package com.axonivy.jmx;

import static org.assertj.core.api.Assertions.assertThat;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanException;
import javax.management.MalformedObjectNameException;
import javax.management.ReflectionException;
import javax.management.openmbean.CompositeData;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.junit.Test;

public class TestThrowableMAttribute extends BaseMTest<TestThrowableMAttribute.TestBean> {
  @MBean("Test:type=TestType")
  public static class TestBean {
    @MAttribute
    private Exception errorField;

    private Throwable errorMethod;

    @MAttribute
    public Throwable getErrorMethod() {
      return errorMethod;
    }
  }

  public TestThrowableMAttribute() throws MalformedObjectNameException {
    super(new TestBean(), "Test:type=TestType");
  }

  @Test
  public void testAttributeInfoErrorField() throws IntrospectionException, InstanceNotFoundException, ReflectionException {
    MBeanAttributeInfo attributeInfo = getAttributeInfo("errorField");
    assertThat(attributeInfo.getDescription()).isEqualTo("errorField");
    assertThat(attributeInfo.getType()).isEqualTo(CompositeData.class.getName());
    assertThat(attributeInfo.isReadable()).isEqualTo(true);
    assertThat(attributeInfo.isWritable()).isEqualTo(false);
    assertThat(attributeInfo.isIs()).isEqualTo(false);
  }

  @Test
  public void testReadAttributeErrorField() throws AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException {
    testBean.errorField = null;
    assertThat(getAttribute("errorField")).isNull();
    testBean.errorField = new RuntimeException("Gugus");
    Object value = getAttribute("errorField");
    assertThat(value).isNotNull();
    assertThat(value).isInstanceOf(CompositeData.class);

    CompositeData error = (CompositeData) value;
    assertThat(error.get("message")).isEqualTo("Gugus");
    assertThat(error.get("type")).isEqualTo(RuntimeException.class.getName());
    assertThat(error.get("stackTrace")).isEqualTo(ExceptionUtils.getStackTrace(testBean.errorField));
  }

  @Test
  public void testAttributeInfoErrorMethod() throws IntrospectionException, InstanceNotFoundException, ReflectionException {
    MBeanAttributeInfo attributeInfo = getAttributeInfo("errorMethod");
    assertThat(attributeInfo.getDescription()).isEqualTo("errorMethod");
    assertThat(attributeInfo.getType()).isEqualTo(CompositeData.class.getName());
    assertThat(attributeInfo.isReadable()).isEqualTo(true);
    assertThat(attributeInfo.isWritable()).isEqualTo(false);
    assertThat(attributeInfo.isIs()).isEqualTo(false);
  }

  @Test
  public void testReadAttributeErrorMethod() throws AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException {
    testBean.errorMethod = null;
    assertThat(getAttribute("errorMethod")).isNull();
    testBean.errorMethod = new RuntimeException("Gugus");
    Object value = getAttribute("errorMethod");
    assertThat(value).isNotNull();
    assertThat(value).isInstanceOf(CompositeData.class);

    CompositeData error = (CompositeData) value;
    assertThat(error.get("message")).isEqualTo("Gugus");
    assertThat(error.get("type")).isEqualTo(RuntimeException.class.getName());
    assertThat(error.get("stackTrace")).isEqualTo(ExceptionUtils.getStackTrace(testBean.errorMethod));
  }
}

package com.axonivy.jmx;

import static org.assertj.core.api.Assertions.assertThat;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanException;
import javax.management.MalformedObjectNameException;
import javax.management.ReflectionException;

import org.junit.jupiter.api.Test;

import com.axonivy.jmx.TestEnumBasedMAttribute.TestBean.TestEnum;

public class TestEnumBasedMAttribute extends BaseMTest<TestEnumBasedMAttribute.TestBean> {
  @MBean("Test:type=TestType")
  public static class TestBean {
    public enum TestEnum {
      VALUE1,
      VALUE2
    }

    @MAttribute(isWritable = true)
    private TestEnum enumField;

    private TestEnum enumMethod;

    @MAttribute(isWritable = true)
    public TestEnum getEnumMethod() {
      return enumMethod;
    }

    public void setEnumMethod(TestEnum enumMethod) {
      this.enumMethod = enumMethod;
    }
  }

  public TestEnumBasedMAttribute() throws MalformedObjectNameException {
    super(new TestBean(), "Test:type=TestType");
  }

  @Test
  public void testAttributeInfoEnumField() throws IntrospectionException, InstanceNotFoundException, ReflectionException {
    MBeanAttributeInfo attributeInfo = getAttributeInfo("enumField");
    assertThat(attributeInfo.getDescription()).isEqualTo("enumField");
    assertThat(attributeInfo.getType()).isEqualTo("java.lang.String");
    assertThat(attributeInfo.isReadable()).isEqualTo(true);
    assertThat(attributeInfo.isWritable()).isEqualTo(true);
    assertThat(attributeInfo.isIs()).isEqualTo(false);
  }

  @Test
  public void testReadAttributeEnumField() throws AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException {
    testBean.enumField = TestEnum.VALUE1;
    assertThat(getAttribute("enumField")).isEqualTo(TestEnum.VALUE1.name());
    testBean.enumField = TestEnum.VALUE2;
    assertThat(getAttribute("enumField")).isEqualTo(TestEnum.VALUE2.name());
  }

  @Test
  public void testWriteAttributeEnumField() throws InstanceNotFoundException, InvalidAttributeValueException, AttributeNotFoundException, ReflectionException, MBeanException {
    setAttribute("enumField", TestEnum.VALUE1.name());
    assertThat(testBean.enumField).isEqualTo(TestEnum.VALUE1);
    setAttribute("enumField", TestEnum.VALUE2.name());
    assertThat(testBean.enumField).isEqualTo(TestEnum.VALUE2);
  }

  @Test
  public void testAttributeInfoEnumMethod() throws IntrospectionException, InstanceNotFoundException, ReflectionException {
    MBeanAttributeInfo attributeInfo = getAttributeInfo("enumMethod");
    assertThat(attributeInfo.getDescription()).isEqualTo("enumMethod");
    assertThat(attributeInfo.getType()).isEqualTo("java.lang.String");
    assertThat(attributeInfo.isReadable()).isEqualTo(true);
    assertThat(attributeInfo.isWritable()).isEqualTo(true);
    assertThat(attributeInfo.isIs()).isEqualTo(false);
  }

  @Test
  public void testReadAttributeEnumMethod() throws AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException {
    testBean.enumMethod = TestEnum.VALUE1;
    assertThat(getAttribute("enumMethod")).isEqualTo(TestEnum.VALUE1.name());
    testBean.enumMethod = TestEnum.VALUE2;
    assertThat(getAttribute("enumMethod")).isEqualTo(TestEnum.VALUE2.name());
  }

  @Test
  public void testWriteAttributeEnumMethod() throws InstanceNotFoundException, InvalidAttributeValueException, AttributeNotFoundException, ReflectionException, MBeanException {
    setAttribute("enumMethod", TestEnum.VALUE1.name());
    assertThat(testBean.enumMethod).isEqualTo(TestEnum.VALUE1);
    setAttribute("enumMethod", TestEnum.VALUE2.name());
    assertThat(testBean.enumMethod).isEqualTo(TestEnum.VALUE2);
  }
}

package com.axonivy.jmx;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanException;
import javax.management.MalformedObjectNameException;
import javax.management.ReflectionException;

import org.junit.jupiter.api.Test;

public class TestFieldBasedMAttribute extends BaseMTest<TestFieldBasedMAttribute.TestBean> {
  public static class BaseTestBean {
    @MAttribute(description = "An int counter", isWritable = true)
    private int counter = 123;

    public int getCounter() {
      return counter;
    }

    public void setCounter(int counter) {
      this.counter = counter;
    }
  }

  @MBean("Test:type=TestType")
  public static class TestBean extends BaseTestBean {
    @MAttribute(name = "configName", description = "A String config")
    private String config = "ConfigString";
    @MAttribute
    private boolean isRunning;
  }

  public TestFieldBasedMAttribute() throws MalformedObjectNameException {
    super(new TestBean(), "Test:type=TestType");
  }

  @Test
  public void testAttributeInfoCounter() throws IntrospectionException, InstanceNotFoundException, ReflectionException {
    MBeanAttributeInfo attributeInfo = getAttributeInfo("counter");
    assertThat(attributeInfo.getDescription()).isEqualTo("An int counter");
    assertThat(attributeInfo.getType()).isEqualTo("java.lang.Integer");
    assertThat(attributeInfo.isReadable()).isEqualTo(true);
    assertThat(attributeInfo.isWritable()).isEqualTo(true);
    assertThat(attributeInfo.isIs()).isEqualTo(false);
  }

  @Test
  public void testReadAttributeCounter() throws AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException {
    assertThat(getAttribute("counter")).isEqualTo(123);
  }

  @Test
  public void testWriteAttributeCounter() throws InstanceNotFoundException, InvalidAttributeValueException, AttributeNotFoundException, ReflectionException, MBeanException {
    setAttribute("counter", 321);
    assertThat(getAttribute("counter")).isEqualTo(321);
  }

  @Test
  public void testAttributeInfoConfig() throws IntrospectionException, InstanceNotFoundException, ReflectionException {
    MBeanAttributeInfo attributeInfo = getAttributeInfo("configName");
    assertThat(attributeInfo.getDescription()).isEqualTo("A String config");
    assertThat(attributeInfo.getType()).isEqualTo("java.lang.String");
    assertThat(attributeInfo.isReadable()).isEqualTo(true);
    assertThat(attributeInfo.isWritable()).isEqualTo(false);
    assertThat(attributeInfo.isIs()).isEqualTo(false);
  }

  @Test
  public void testReadAttributeConfig() throws AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException {
    assertThat(getAttribute("configName")).isEqualTo("ConfigString");
  }

  @Test
  public void testWriteAttributeConfig() throws InstanceNotFoundException, InvalidAttributeValueException, AttributeNotFoundException, ReflectionException, MBeanException {
    assertThatThrownBy(() -> setAttribute("configName", "NewConfig")).isInstanceOf(MBeanException.class);
  }

  @Test
  public void testAttributeInfoIsRunning() throws IntrospectionException, InstanceNotFoundException, ReflectionException {
    MBeanAttributeInfo attributeInfo = getAttributeInfo("isRunning");
    assertThat(attributeInfo.getDescription()).isEqualTo("isRunning");
    assertThat(attributeInfo.getType()).isEqualTo("java.lang.Boolean");
    assertThat(attributeInfo.isReadable()).isEqualTo(true);
    assertThat(attributeInfo.isWritable()).isEqualTo(false);
    assertThat(attributeInfo.isIs()).isEqualTo(false);
  }

  @Test
  public void testReadAttributeIsRunning() throws AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException {
    assertThat(getAttribute("isRunning")).isEqualTo(false);
    testBean.isRunning = true;
    assertThat(getAttribute("isRunning")).isEqualTo(true);
  }

  @Test
  public void testWriteAttributeIsRunning() throws InstanceNotFoundException, InvalidAttributeValueException, AttributeNotFoundException, ReflectionException, MBeanException {
    assertThatThrownBy(() -> setAttribute("isRunning", true)).isInstanceOf(MBeanException.class);
  }

  @Test
  public void testReadAttributes() throws InstanceNotFoundException, ReflectionException {
    testBean.isRunning = true;
    testBean.setCounter(102);
    testBean.config = "Hi Guys";
    List<Attribute> attributes = MBeans.getMBeanServer().getAttributes(testBeanObjectName, new String[] {"isRunning", "configName", "counter"}).asList();
    assertThat(attributes).isNotNull();
    assertThat(attributes).hasSize(3);
    assertThat(attributes.get(0).getName()).isEqualTo("isRunning");
    assertThat(attributes.get(0).getValue()).isEqualTo(true);
    assertThat(attributes.get(1).getName()).isEqualTo("configName");
    assertThat(attributes.get(1).getValue()).isEqualTo("Hi Guys");
    assertThat(attributes.get(2).getName()).isEqualTo("counter");
    assertThat(attributes.get(2).getValue()).isEqualTo(102);
  }

  @Test
  public void testWriteAttributes() throws InstanceNotFoundException, ReflectionException {
    AttributeList attributes = new AttributeList();
    attributes.add(new Attribute("counter", 38583));
    MBeans.getMBeanServer().setAttributes(testBeanObjectName, attributes);
    assertThat(testBean.getCounter()).isEqualTo(38583);
  }

  @Test
  public void testWriteNonExistingAttribute() throws InstanceNotFoundException, InvalidAttributeValueException, AttributeNotFoundException, ReflectionException, MBeanException {
    assertThatThrownBy(() -> setAttribute("blah", "bluh")).isInstanceOf(AttributeNotFoundException.class);
  }

  @Test
  public void testReadNonExistingAttribute() throws AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException {
    assertThatThrownBy(() -> getAttribute("blah")).isInstanceOf(AttributeNotFoundException.class);
  }
}

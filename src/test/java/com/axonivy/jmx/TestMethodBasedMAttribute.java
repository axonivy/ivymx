package com.axonivy.jmx;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanException;
import javax.management.MalformedObjectNameException;
import javax.management.ReflectionException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.axonivy.jmx.internal.LogErrorStrategy;
import com.axonivy.jmx.util.LogTestAppender;

public class TestMethodBasedMAttribute extends BaseMTest<TestMethodBasedMAttribute.TestBean> {
  private final LogTestAppender logAppender = new LogTestAppender(Level.ERROR);

  @MBean("Test:type=TestType")
  public static class TestBean {
    private boolean isRunning;
    private String description;
    private boolean isFemale;
    private boolean isMale;

    @MAttribute
    public boolean isRunning() {
      return isRunning;
    }

    public void setRunning(boolean isRunning) {
      this.isRunning = isRunning;
    }

    @MAttribute(isWritable = true)
    private boolean isMale() {
      return isMale;
    }

    @SuppressWarnings("unused")
    private void setMale(boolean male) {
      this.isMale = male;
    }

    @MAttribute(name = "isFemale", description = "Is this a female bean", isWritable = true)
    public boolean getFemale() {
      return isFemale;
    }

    public void setFemale(boolean female) {
      this.isFemale = female;
    }

    @MAttribute(name = "description", isWritable = true)
    public String getDesc() {
      return description;
    }

    public void setDesc(String description) {
      this.description = description;
    }
  }

  @MBean("NoSetter:name=forAttribute")
  private static class NoSetterForAttribute {
    @MAttribute(isWritable = true)
    public boolean getThis() {
      return true;
    }
  }

  @MBean("NoGetter:name=forAttribute")
  private static class NoGetterForAttribute {
    @MAttribute
    public boolean doThis() {
      return true;
    }
  }

  @BeforeEach
  @Override
  public void before() {
    super.before();
    Logger.getLogger(LogErrorStrategy.class).addAppender(logAppender);
  }

  @AfterEach
  @Override
  public void after() {
    super.after();
    Logger.getLogger(LogErrorStrategy.class).removeAppender(logAppender);
  }

  public TestMethodBasedMAttribute() throws MalformedObjectNameException {
    super(new TestBean(), "Test:type=TestType");
  }

  @Test
  public void testAttributeInfoRunning() throws IntrospectionException, InstanceNotFoundException, ReflectionException {
    MBeanAttributeInfo attributeInfo = getAttributeInfo("running");
    assertThat(attributeInfo.getDescription()).isEqualTo("running");
    assertThat(attributeInfo.getType()).isEqualTo("java.lang.Boolean");
    assertThat(attributeInfo.isReadable()).isEqualTo(true);
    assertThat(attributeInfo.isWritable()).isEqualTo(false);
    assertThat(attributeInfo.isIs()).isEqualTo(true);
  }

  @Test
  public void testReadAttributeRunning() throws AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException {
    testBean.isRunning = false;
    assertThat(getAttribute("running")).isEqualTo(false);
    testBean.isRunning = true;
    assertThat(getAttribute("running")).isEqualTo(true);
  }

  @Test
  public void testWriteAttributeRunning() throws InstanceNotFoundException, InvalidAttributeValueException, AttributeNotFoundException, ReflectionException, MBeanException {
    assertThatThrownBy(() -> setAttribute("running", false)).isInstanceOf(MBeanException.class);
  }

  @Test
  public void testAttributeInfoMale() throws IntrospectionException, InstanceNotFoundException, ReflectionException {
    MBeanAttributeInfo attributeInfo = getAttributeInfo("male");
    assertThat(attributeInfo.getDescription()).isEqualTo("male");
    assertThat(attributeInfo.getType()).isEqualTo("java.lang.Boolean");
    assertThat(attributeInfo.isReadable()).isEqualTo(true);
    assertThat(attributeInfo.isWritable()).isEqualTo(true);
    assertThat(attributeInfo.isIs()).isEqualTo(true);
  }

  @Test
  public void testReadAttributeMale() throws AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException {
    testBean.isMale = false;
    assertThat(getAttribute("male")).isEqualTo(false);
    testBean.isMale = true;
    assertThat(getAttribute("male")).isEqualTo(true);
  }

  @Test
  public void testWriteAttributeMale() throws InstanceNotFoundException, InvalidAttributeValueException, AttributeNotFoundException, ReflectionException, MBeanException {
    setAttribute("male", false);
    assertThat(testBean.isMale).isEqualTo(false);
    setAttribute("male", true);
    assertThat(testBean.isMale).isEqualTo(true);
  }

  @Test
  public void testAttributeInfoIsFemale() throws IntrospectionException, InstanceNotFoundException, ReflectionException {
    MBeanAttributeInfo attributeInfo = getAttributeInfo("isFemale");
    assertThat(attributeInfo.getDescription()).isEqualTo("Is this a female bean");
    assertThat(attributeInfo.getType()).isEqualTo("java.lang.Boolean");
    assertThat(attributeInfo.isReadable()).isEqualTo(true);
    assertThat(attributeInfo.isWritable()).isEqualTo(true);
    assertThat(attributeInfo.isIs()).isEqualTo(false);
  }

  @Test
  public void testReadAttributeIsFemale() throws AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException {
    testBean.isFemale = false;
    assertThat(getAttribute("isFemale")).isEqualTo(false);
    testBean.isFemale = true;
    assertThat(getAttribute("isFemale")).isEqualTo(true);
  }

  @Test
  public void testWriteAttributeIsFemale() throws InstanceNotFoundException, InvalidAttributeValueException, AttributeNotFoundException, ReflectionException, MBeanException {
    setAttribute("isFemale", false);
    assertThat(testBean.isFemale).isEqualTo(false);
    setAttribute("isFemale", true);
    assertThat(testBean.isFemale).isEqualTo(true);
  }

  @Test
  public void testAttributeInfoDescription() throws IntrospectionException, InstanceNotFoundException, ReflectionException {
    MBeanAttributeInfo attributeInfo = getAttributeInfo("description");
    assertThat(attributeInfo.getDescription()).isEqualTo("description");
    assertThat(attributeInfo.getType()).isEqualTo("java.lang.String");
    assertThat(attributeInfo.isReadable()).isEqualTo(true);
    assertThat(attributeInfo.isWritable()).isEqualTo(true);
    assertThat(attributeInfo.isIs()).isEqualTo(false);
  }

  @Test
  public void testReadAttributeDescription() throws AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException {
    testBean.description = "Blah Bluh";
    assertThat(getAttribute("description")).isEqualTo("Blah Bluh");
  }

  @Test
  public void testWriteAttributeDescription() throws InstanceNotFoundException, InvalidAttributeValueException, AttributeNotFoundException, ReflectionException, MBeanException {
    setAttribute("description", "blah");
    assertThat(testBean.description).isEqualTo("blah");
  }

  @Test
  public void testNoGetterForAttribute() throws Exception {
    assertThat(logAppender.getRecording()).isEmpty();
    MBeans.registerMBeanFor(new NoGetterForAttribute());
    assertThat(logAppender.getRecording()).contains("Name of getter method for an attribute must start with get...() or is...() but is 'doThis'");
  }

  @Test
  public void testNoSetterForAttribute() throws Exception {
    Logger.getLogger(LogErrorStrategy.class).addAppender(logAppender);
    assertThat(logAppender.getRecording()).isEmpty();
    MBeans.registerMBeanFor(new NoSetterForAttribute());
    assertThat(logAppender.getRecording()).contains("Method 'setThis(...)' must be available to set attribute 'this' on class 'com.axonivy.jmx.TestMethodBasedMAttribute$NoSetterForAttribute'");
  }
}

package com.axonivy.jmx;

import static org.assertj.core.api.Assertions.assertThat;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanException;
import javax.management.MalformedObjectNameException;
import javax.management.ReflectionException;

import org.junit.Test;

public class TestMInclude extends BaseMTest<TestMInclude.TestBean> {

  @MBean(value = "Test:type=TestType")
  public static class TestBean {
    @MInclude
    private final Application application = new Application();

    private final Pmv myPmv = new Pmv();

    @MInclude
    public Pmv getPmv() {
      return myPmv;
    }
  }

  public static class Pmv {
    @MAttribute(isWritable = true)
    private String pmvName = "TestPmv";

    private int count = 0;

    @MOperation
    public void increaseCount() {
      count++;
    }
  }

  public static class Application {
    private int cnt = 0;

    @MAttribute
    public String getName() {
      return "TestApp";
    }

    @MOperation
    public void increaseCnt() {
      cnt++;
    }

  }

  public TestMInclude() throws MalformedObjectNameException {
    super(new TestBean(), "Test:type=TestType");
  }

  @Test
  public void testReadAttributePmvName() throws AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException {
    assertThat(getAttribute("pmvName")).isEqualTo("TestPmv");
  }

  @Test
  public void testWriteAttributePmvName() throws AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException, InvalidAttributeValueException {
    setAttribute("pmvName", "Hello World");
    assertThat(testBean.getPmv().pmvName).isEqualTo("Hello World");
  }

  @Test
  public void testReadAttributeName() throws AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException {
    assertThat(getAttribute("name")).isEqualTo("TestApp");
  }

  @Test
  public void testInvokeOperationIncreaseCount() throws InstanceNotFoundException, ReflectionException, MBeanException {
    assertThat(testBean.getPmv().count).isEqualTo(0);
    invokeOperation("increaseCount");
    assertThat(testBean.getPmv().count).isEqualTo(1);
  }

  @Test
  public void testInvokeOperationIncreaseCnt() throws InstanceNotFoundException, ReflectionException, MBeanException {
    assertThat(testBean.application.cnt).isEqualTo(0);
    invokeOperation("increaseCnt");
    assertThat(testBean.application.cnt).isEqualTo(1);
  }
}

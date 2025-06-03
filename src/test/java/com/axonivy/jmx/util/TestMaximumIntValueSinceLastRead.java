package com.axonivy.jmx.util;

import static org.assertj.core.api.Assertions.assertThat;

import javax.management.MBeanAttributeInfo;

import org.junit.Test;

import com.axonivy.jmx.BaseMTest;
import com.axonivy.jmx.MBean;
import com.axonivy.jmx.MInclude;

/**
 * Test class {@link MaximumIntValueSinceLastRead}
 */
public class TestMaximumIntValueSinceLastRead extends BaseMTest<TestMaximumIntValueSinceLastRead.TestBean> {
  @MBean(value = "Test:type=TestType")
  public static class TestBean {
    @MInclude
    private final MaximumIntValueSinceLastRead maxValue = new MaximumIntValueSinceLastRead("maxValue");
  }

  public TestMaximumIntValueSinceLastRead() throws Exception {
    super(new TestBean(), "Test:type=TestType");
  }

  @Test
  public void testMetaInfo() throws Exception {
    MBeanAttributeInfo attributeInfo = getAttributeInfo("maxValue");
    assertThat(attributeInfo.getName()).isEqualTo("maxValue");
    assertThat(attributeInfo.getType()).isEqualTo(Integer.class.getName());
  }

  @Test
  public void testResetMaximumValue() throws Exception {
    assertThat(getAttribute("maxValue")).isEqualTo(0);
    testBean.maxValue.addValue(100);
    assertThat(getAttribute("maxValue")).isEqualTo(100);
    testBean.maxValue.addValue(5);
    assertThat(getAttribute("maxValue")).isEqualTo(5);
  }

  @Test
  public void testMaximumValue() throws Exception {
    assertThat(getAttribute("maxValue")).isEqualTo(0);
    testBean.maxValue.addValue(10);
    testBean.maxValue.addValue(100);
    testBean.maxValue.addValue(90);
    testBean.maxValue.addValue(20);
    assertThat(getAttribute("maxValue")).isEqualTo(100);
  }

  @Test
  public void testReturnsLastValueIfNoValueWasAdded() throws Exception {
    assertThat(getAttribute("maxValue")).isEqualTo(0);
    testBean.maxValue.addValue(100);
    testBean.maxValue.addValue(20);
    assertThat(getAttribute("maxValue")).isEqualTo(100);
    assertThat(getAttribute("maxValue")).isEqualTo(20);
    assertThat(getAttribute("maxValue")).isEqualTo(20);
  }
}

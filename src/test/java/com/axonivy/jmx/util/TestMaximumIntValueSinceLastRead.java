package com.axonivy.jmx.util;

import static org.assertj.core.api.Assertions.assertThat;

import javax.management.MBeanAttributeInfo;

import mockit.Deencapsulation;

import org.junit.Test;

import com.axonivy.jmx.BaseMTest;
import com.axonivy.jmx.MBean;
import com.axonivy.jmx.MInclude;
import com.axonivy.jmx.util.MaximumIntValueSinceLastRead;

/**
 * Test class {@link MaximumIntValueSinceLastRead}
 */
public class TestMaximumIntValueSinceLastRead extends BaseMTest<TestMaximumIntValueSinceLastRead.TestBean>
{
  private static final long FIFTEEN_MINUTES = 15L*60L*1000L*1000L*1000L;
  private static final long FIVE_MINUTES = 5L*60L*1000L*1000L*1000L;

  @MBean(value="Test:type=TestType")
  public static class TestBean
  {
    @MInclude
    private MaximumIntValueSinceLastRead maxValue = new MaximumIntValueSinceLastRead("maxValue");
  }
  
  public TestMaximumIntValueSinceLastRead() throws Exception
  {
    super(new TestBean(), "Test:type=TestType");
  }
  
  @Test
  public void testMetaInfo() throws Exception
  {
    MBeanAttributeInfo attributeInfo = getAttributeInfo("maxValue");
    assertThat(attributeInfo.getName()).isEqualTo("maxValue");
    assertThat(attributeInfo.getType()).isEqualTo(Integer.class.getName());
  }  
  
  @Test
  public void testResetMaximumValue() throws Exception
  {
    assertThat(getAttribute("maxValue")).isEqualTo(0);
    testBean.maxValue.addValue(100);
    assertThat(getAttribute("maxValue")).isEqualTo(100);
    testBean.maxValue.addValue(5);
    assertThat(getAttribute("maxValue")).isEqualTo(5);
  }

  @Test
  public void testMaximumValue() throws Exception
  {
    assertThat(getAttribute("maxValue")).isEqualTo(0);
    testBean.maxValue.addValue(10);
    testBean.maxValue.addValue(100);
    testBean.maxValue.addValue(90);
    testBean.maxValue.addValue(20);
    assertThat(getAttribute("maxValue")).isEqualTo(100);
  }
  
  @Test
  public void testReturnsLastValueIfNoValueWasAdded() throws Exception
  {
    assertThat(getAttribute("maxValue")).isEqualTo(0);
    testBean.maxValue.addValue(100);
    testBean.maxValue.addValue(20);
    assertThat(getAttribute("maxValue")).isEqualTo(100);
    assertThat(getAttribute("maxValue")).isEqualTo(20);
    assertThat(getAttribute("maxValue")).isEqualTo(20);
  }

  @Test 
  public void testInvalidMaximumValue() throws Exception
  {
    assertThat(getAttribute("maxValue")).isEqualTo(0);
    
    testBean.maxValue.addValue(100);
    testBean.maxValue.addValue(10);
    Deencapsulation.setField(testBean.maxValue, System.nanoTime()-FIFTEEN_MINUTES);
    
    assertThat(getAttribute("maxValue")).isEqualTo(10);        
    
    testBean.maxValue.addValue(20);
    testBean.maxValue.addValue(10);
    Deencapsulation.setField(testBean.maxValue, System.nanoTime()-FIVE_MINUTES);
    
    assertThat(getAttribute("maxValue")).isEqualTo(20);
  }

}

package com.axonivy.jmx;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.TimeUnit;

import javax.management.MBeanAttributeInfo;
import javax.management.MalformedObjectNameException;

import org.junit.jupiter.api.Test;

public class TestMCache extends BaseMTest<TestMCache.TestBean> {
  @MBean("Test:type=TestType")
  public static class TestBean {
    private int cachedAttribute1Seconds = 0;
    private int cachedAttribute10Milliseconds = 0;

    @MAttribute
    @MCache(timeout = 1)
    public int getCachedAttribute1Seconds() {
      return cachedAttribute1Seconds++;
    }

    @MAttribute
    @MCache(timeout = 10, unit = TimeUnit.MILLISECONDS)
    public int getCachedAttribute10Milliseconds() {
      return cachedAttribute10Milliseconds++;
    }
  }

  public TestMCache() throws MalformedObjectNameException {
    super(new TestBean(), "Test:type=TestType");
  }

  @Test
  public void cachedAttribute1Seconds() throws Exception {
    MBeanAttributeInfo attributeInfo = getAttributeInfo("cachedAttribute1Seconds");
    assertThat(attributeInfo.getDescription()).isEqualTo("cachedAttribute1Seconds");
    assertThat(attributeInfo.getType()).isEqualTo(Integer.class.getName());
    assertThat(attributeInfo.isReadable()).isEqualTo(true);
    assertThat(attributeInfo.isWritable()).isEqualTo(false);
    assertThat(attributeInfo.isIs()).isEqualTo(false);
    long startTime = System.currentTimeMillis();
    while (System.currentTimeMillis() - startTime < 1500L) {
      assertThat((Integer) getAttribute("cachedAttribute1Seconds")).isBetween(0, 1);
      Thread.sleep(100L);
    }
    assertThat(this.testBean.cachedAttribute1Seconds).isEqualTo(2);
  }

  @Test
  public void cachedAttribute10Milliseconds() throws Exception {
    MBeanAttributeInfo attributeInfo = getAttributeInfo("cachedAttribute10Milliseconds");
    assertThat(attributeInfo.getDescription()).isEqualTo("cachedAttribute10Milliseconds");
    assertThat(attributeInfo.getType()).isEqualTo(Integer.class.getName());
    assertThat(attributeInfo.isReadable()).isEqualTo(true);
    assertThat(attributeInfo.isWritable()).isEqualTo(false);
    assertThat(attributeInfo.isIs()).isEqualTo(false);
    long startTime = System.currentTimeMillis();
    while (System.currentTimeMillis() - startTime < 15L) {
      assertThat((Integer) getAttribute("cachedAttribute10Milliseconds")).isBetween(0, 1);
      Thread.sleep(1L);
    }
    assertThat(this.testBean.cachedAttribute10Milliseconds).isEqualTo(2);
  }

}

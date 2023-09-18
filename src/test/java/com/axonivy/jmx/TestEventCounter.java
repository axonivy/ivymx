package com.axonivy.jmx;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Test;
import com.axonivy.jmx.util.EventCounter;

public class TestEventCounter
{
  private EventCounter testCounter = new EventCounter("TestCounter");

  @Test
  public void testCounter()
  {
    assertThat(testCounter.getCount()).isEqualTo(0L);
    testCounter.increase();
    assertThat(testCounter.getCount()).isEqualTo(1L);
    testCounter.increase();
    testCounter.increase();
    assertThat(testCounter.getCount()).isEqualTo(3L);
    testCounter.reset();
    assertThat(testCounter.getCount()).isEqualTo(0L);
    testCounter.increase();
    assertThat(testCounter.getCount()).isEqualTo(1L);
  }

  @Test
  public void testIncrease()
  {
    assertThat(testCounter.getCount()).isEqualTo(0L);
    testCounter.increase(5L);
    assertThat(testCounter.getCount()).isEqualTo(5L);
    testCounter.increase(4L);
    assertThat(testCounter.getCount()).isEqualTo(9L);
    testCounter.reset();
    assertThat(testCounter.getCount()).isEqualTo(0L);
    testCounter.increase(7L);
    assertThat(testCounter.getCount()).isEqualTo(7L);
  }
}
package com.axonivy.jmx;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.axonivy.jmx.util.EventCounter;

public class TestEventCounter
{
  private EventCounter testCounter;
  
  @Test
  public void testCounter()
  {
    testCounter = new EventCounter("TestCounter");
    
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
}
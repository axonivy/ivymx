package com.axonivy.jmx;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.axonivy.jmx.util.OperationExecutionCounter;
import com.axonivy.jmx.util.OperationExecutionCounter.StopWatch;

public class TestOperationExecutionCounter {
  @Test
  public void testCounting() throws InterruptedException {
    OperationExecutionCounter testCounter = new OperationExecutionCounter("TestCounter");

    assertThat(testCounter.getCount()).isEqualTo(0L);

    long duration = startStopWatchForUs(50_000, testCounter);
    long max = duration;
    long min = duration;
    long total = duration;

    assertThat(testCounter.getCount()).isEqualTo(1L);
    assertThat(testCounter.getTotalExecutionTimeInMicroSeconds()).isGreaterThan(40_000L).isLessThanOrEqualTo(total);
    assertThat(testCounter.getMaxExecutionTimeInMicroSeconds()).isGreaterThan(40_000L).isLessThanOrEqualTo(max);
    assertThat(testCounter.getMinExecutionTimeInMicroSeconds()).isGreaterThan(40_000L).isLessThanOrEqualTo(min);

    duration = startStopWatchForUs(100_000, testCounter);
    total = total + duration;
    max = Math.max(max, duration);
    min = Math.min(min, duration);

    assertThat(testCounter.getCount()).isEqualTo(2L);
    assertThat(testCounter.getTotalExecutionTimeInMicroSeconds()).isGreaterThan(140_000L).isLessThanOrEqualTo(total);
    assertThat(testCounter.getMaxExecutionTimeInMicroSeconds()).isGreaterThan(90_000L).isLessThanOrEqualTo(max);
    assertThat(testCounter.getMinExecutionTimeInMicroSeconds()).isGreaterThan(40_000L).isLessThanOrEqualTo(min);

    duration = startStopWatchForUs(20_000, testCounter);
    total = total + duration;
    max = Math.max(max, duration);
    min = Math.min(min, duration);

    assertThat(testCounter.getCount()).isEqualTo(3L);
    assertThat(testCounter.getTotalExecutionTimeInMicroSeconds()).isGreaterThan(160_000L).isLessThanOrEqualTo(total);
    assertThat(testCounter.getMaxExecutionTimeInMicroSeconds()).isGreaterThan(90_000L).isLessThanOrEqualTo(max);
    assertThat(testCounter.getMinExecutionTimeInMicroSeconds()).isGreaterThan(10_000L).isLessThanOrEqualTo(min);
  }

  @Test
  public void testNestedCounting() throws InterruptedException {
    long period2, start2;
    long period3, start3;
    long period1, start1;
    OperationExecutionCounter testCounter = new OperationExecutionCounter("TestCounter");
    {
      start1 = System.nanoTime();
      StopWatch watch1 = testCounter.start();
      {
        start2 = System.nanoTime();
        StopWatch watch2 = testCounter.start();
        Thread.sleep(50);
        watch2.stop();
        period2 = (System.nanoTime() - start1) / 1000L;
      }
      Thread.sleep(50);
      watch1.stop();
      period1 = (System.nanoTime() - start2) / 1000L;
    }
    {
      start3 = System.nanoTime();
      StopWatch watch3 = testCounter.start();
      Thread.sleep(20);
      watch3.stop();
      period3 = (System.nanoTime() - start3) / 1000L;
    }
    long totalTime = period1 + period2 + period3;
    long min = Math.min(period2, period3);
    long max = Math.max(period1, period3);
    assertThat(testCounter.getCount()).isEqualTo(3L);
    assertThat(testCounter.getTotalExecutionTimeInMicroSeconds()).isGreaterThan(160_000L).isLessThanOrEqualTo(totalTime);
    assertThat(testCounter.getMaxExecutionTimeInMicroSeconds()).isGreaterThan(90_000L).isLessThanOrEqualTo(max);
    assertThat(testCounter.getMinExecutionTimeInMicroSeconds()).isGreaterThan(10_000L).isLessThanOrEqualTo(min);
  }

  @Test
  public void testDelta() throws InterruptedException {
    OperationExecutionCounter testCounter = new OperationExecutionCounter("TestCounter");

    startStopWatchForUs(5000, testCounter);
    assertThat(testCounter.getMaxExecutionTimeDeltaInMicroSeconds()).isEqualTo(0);
    assertThat(testCounter.getMinExecutionTimeDeltaInMicroSeconds()).isEqualTo(0);

    long duration = startStopWatchForUs(50_000, testCounter);
    long max = duration;
    long min = duration;

    duration = startStopWatchForUs(100_000, testCounter);
    max = Math.max(max, duration);
    min = Math.min(min, duration);

    duration = startStopWatchForUs(20_000, testCounter);
    max = Math.max(max, duration);
    min = Math.min(min, duration);

    assertThat(testCounter.getMaxExecutionTimeDeltaInMicroSeconds()).isGreaterThan(85_000L).isLessThanOrEqualTo(max);
    assertThat(testCounter.getMinExecutionTimeDeltaInMicroSeconds()).isGreaterThan(10_000L).isLessThanOrEqualTo(min);

    duration = startStopWatchForUs(200_000, testCounter);
    max = duration;
    min = duration;

    duration = startStopWatchForUs(120_000, testCounter);
    max = Math.max(max, duration);
    min = Math.min(min, duration);

    assertThat(testCounter.getMaxExecutionTimeDeltaInMicroSeconds()).isGreaterThan(185_000L).isLessThanOrEqualTo(max);
    assertThat(testCounter.getMinExecutionTimeDeltaInMicroSeconds()).isGreaterThan(110_000L).isLessThanOrEqualTo(min);

    duration = startStopWatchForUs(50_000, testCounter);
    max = duration;
    min = duration;

    duration = startStopWatchForUs(20_000, testCounter);
    max = Math.max(max, duration);
    min = Math.min(min, duration);

    assertThat(testCounter.getMaxExecutionTimeDeltaInMicroSeconds()).isGreaterThan(35_000L).isLessThanOrEqualTo(max);
    assertThat(testCounter.getMinExecutionTimeDeltaInMicroSeconds()).isGreaterThan(10_000L).isLessThanOrEqualTo(min);

  }

  private long startStopWatchForUs(int us, OperationExecutionCounter testCounter) throws InterruptedException {
    long start = System.nanoTime();
    StopWatch watch = testCounter.start();
    long ms = us / 1000L;
    int ns = (int) (us % 1000L * 1000L);
    Thread.sleep(ms, ns);
    watch.stop();
    return (System.nanoTime() - start) / 1000L;
  }
}

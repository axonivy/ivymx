package com.axonivy.jmx.util;

import java.util.concurrent.atomic.AtomicInteger;

import com.axonivy.jmx.MAttribute;

/**
 * Stores the maximum value of the values given with {@link #addValue(int)}. If the maximum value is read with {@link #getMaximumValueAndReset()} it is reset to 0. 
 * However, if no value was added since the last read the last value added is returned.
 */
public class MaximumIntValueSinceLastRead extends AbstractMValue
{
  private AtomicInteger maximumValue = new AtomicInteger(Integer.MIN_VALUE);
  private AtomicInteger lastValue = new AtomicInteger(0);
  private long lastMaximumValueResetTimestamp;
  private static final long MAXIMUM_VALUE_VALID_TIME_PERIOD = 10L* 60L *1000L * 1000L * 1000L; // 10 min

  
  public MaximumIntValueSinceLastRead(String name)
  {
    super(name);
  }
  
  public MaximumIntValueSinceLastRead(String name, String description)
  {
    super(name, description);
  }

  public void addValue(int value)
  {
    int maxValue;
    lastValue.set(value);
    do
    {
      maxValue = maximumValue.intValue();
      if (maxValue > value)
      {
        return;
      }
    } while (!maximumValue.compareAndSet(maxValue,  value));
  }
  
  /**
   * Gets the maximum value added with {@link #addValue(int)} since the last call to this method. 
   * If no value was added since the last call then the last value that was added with {@link #addValue(int)} (a.k.a the current value) is returned. 
   * @return maximum value
   */
  @MAttribute(name="#{name}", description="#{description}")
  public int getMaximumValueAndReset()
  {
    int maxValue = maximumValue.getAndSet(Integer.MIN_VALUE);
    int currentValue = lastValue.get();
    if (isMaximumValueNotValid(maxValue))
    {
      maxValue = currentValue;
    }
    lastMaximumValueResetTimestamp = System.nanoTime();
    return maxValue;
  }
  
  private boolean isMaximumValueNotValid(int maxValue)
  {
    return maxValue == Integer.MIN_VALUE || lastMaximumValueResetTimestamp + MAXIMUM_VALUE_VALID_TIME_PERIOD < System.nanoTime();
  }
}

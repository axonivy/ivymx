package com.axonivy.jmx.util;

import com.axonivy.jmx.MAttribute;
import com.axonivy.jmx.MOperation;

/**
 * Counts the executions and measures the execution time of operations. 
 * Exports the count value and durations as MBean attribute with a given name. Counter can be reseted using MBean operation reset...  
 */
public class OperationExecutionCounter extends AbstractMValue
{
  private static final long DELTA_LIMIT_VALID_TIME_PERIOD = 10L* 60L *1000L * 1000L * 1000L; // 10 min
  private long totalNanoTime;
  private long maxNanoTime = Long.MIN_VALUE;
  private long minNanoTime = Long.MAX_VALUE;
  private long maxNanoTimeDelta = Long.MIN_VALUE;
  private long minNanoTimeDelta = Long.MAX_VALUE;
  private long lastMaxTimeResetTimestamp = Long.MIN_VALUE;
  private long lastMinTimeResetTimestamp = Long.MIN_VALUE;
  
  private long count;
  private String action;
  private String object;
  
  public OperationExecutionCounter(String name)
  {
    this(name, "Number of executions, since server start", "call", "execute");
  }
  
  public OperationExecutionCounter(String name, String description, String object, String action)
  {
    super(name, description);
    this.action = action;
    this.object = object;
  }
  
  public String getAction()
  {
    return action;
  }
  
  public String getObject()
  {
    return object;
  }

  public StopWatch start()
  {
    return new StopWatch();
  }
  
  @MAttribute(name="#{name}TotalExecutionTimeInMicroSeconds", description="Total time in micro seconds needed to #{action} #{object}s, since server start or last call to reset().")
  public synchronized long getTotalExecutionTimeInMicroSeconds()
  {
    return totalNanoTime / 1000;
  }
  
  
  @MAttribute(name="#{name}", description="#{description}")
  public synchronized long getCount()
  {
    return count;
  }
  

  @MAttribute(name="#{name}MaxExecutionTimeInMicroSeconds", description="Maximum time in micro seconds needed to #{action} a #{object}, since server start or last call to reset().")
  public synchronized long getMaxExecutionTimeInMicroSeconds()
  {
    if (maxNanoTime == Long.MIN_VALUE)
    {
      return 0;
    }
    return maxNanoTime / 1000;
  }
  
  @MAttribute(name="#{name}MaxExecutionTimeDeltaInMicroSeconds", description="Maximum time in micro seconds needed to #{action} a #{object}, since last call to this method.")
  public synchronized long getMaxExecutionTimeDeltaInMicroSeconds()
  {
    long value = 0;
    if (isMaxExecutionTimeDeltaValid())
    {
      value = maxNanoTimeDelta / 1000;
    }
    resetMaxExecutionTimeDelta();
    return value;
  }

  private boolean isMaxExecutionTimeDeltaValid()
  {
    return maxNanoTimeDelta > Long.MIN_VALUE && 
           lastMaxTimeResetTimestamp + DELTA_LIMIT_VALID_TIME_PERIOD > System.nanoTime();
  }

  private void resetMaxExecutionTimeDelta()
  {
    maxNanoTimeDelta = Long.MIN_VALUE;
    lastMaxTimeResetTimestamp = System.nanoTime();
  }
  

  @MAttribute(name="#{name}MinExecutionTimeInMicroSeconds", description="Minimum time in micro seconds needed to #{action} a #{object}, since server start or last call to reset().")
  public synchronized long getMinExecutionTimeInMicroSeconds()
  {
    if (minNanoTime== Long.MAX_VALUE)
    {
      return 0;
    }
    return minNanoTime / 1000;
  }
 
  @MAttribute(name="#{name}MinExecutionTimeDeltaInMicroSeconds", description="Minimum time in micro seconds needed to #{action} a #{object}, since last call to this method.")
  public synchronized long getMinExecutionTimeDeltaInMicroSeconds()
  {
    long value = 0;
    if (isMinExectutionTimeDeltaValid())
    {
      value = minNanoTimeDelta / 1000;
    }
    resetMinExecutionTimeDelta();
    return value;
  }

  private boolean isMinExectutionTimeDeltaValid()
  {
    return minNanoTimeDelta < Long.MAX_VALUE && 
           lastMinTimeResetTimestamp + DELTA_LIMIT_VALID_TIME_PERIOD > System.nanoTime();
  }

  private void resetMinExecutionTimeDelta()
  {
    minNanoTimeDelta = Long.MAX_VALUE;
    lastMinTimeResetTimestamp = System.nanoTime();
  }

  
  @MOperation(name="reset#{capitalizedName}", description="Reset collected data.")
  public synchronized void reset()
  {
    totalNanoTime = 0;
    count = 0;
    minNanoTime = Long.MAX_VALUE;
    maxNanoTime = Long.MIN_VALUE;
    minNanoTimeDelta = Long.MAX_VALUE;
    maxNanoTimeDelta = Long.MIN_VALUE;            
  }

  private synchronized void newValue(long value)
  {
    totalNanoTime += value;
    if (value > maxNanoTime)
    {
      maxNanoTime = value;
    }
    if (value > maxNanoTimeDelta)
    {
      maxNanoTimeDelta = value;
    }
    if (value < minNanoTime)
    {
      minNanoTime = value;
    }
    if (value < minNanoTimeDelta)
    {
      minNanoTimeDelta = value;
    }
    count++;
  }

  public class StopWatch
  {
    private long startTimestamp = System.nanoTime();
    private long stopTimestamp = System.nanoTime();
    
    public void stop()
    {
      stopTimestamp = System.nanoTime();
      newValue(stopTimestamp - startTimestamp);
    }
    
    public long getTimeInNanoSeconds()
    {
      return stopTimestamp - startTimestamp;
    }
    
    public long getTimeInMilliSeconds()
    {
      return getTimeInNanoSeconds()/1000000L;
    }
  }
}

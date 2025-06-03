package com.axonivy.jmx.util;

import java.util.concurrent.atomic.AtomicLong;

import com.axonivy.jmx.MAttribute;
import com.axonivy.jmx.MOperation;

/**
 * Counts events. Exports the count value as MBean attribute with a given name. Counter can be reseted using MBean operation reset...
 */
public class EventCounter extends AbstractMValue {
  private final AtomicLong counter = new AtomicLong();

  public EventCounter(String name) {
    super(name);
  }

  public EventCounter(String name, String description) {
    super(name, description);
  }

  public void increase() {
    counter.getAndIncrement();
  }

  public void increase(long delta) {
    counter.getAndAdd(delta);
  }

  @MAttribute(name = "#{name}", description = "#{description}")
  public long getCount() {
    return counter.get();
  }

  @MOperation(name = "reset#{capitalizedName}")
  public void reset() {
    counter.set(0);
  }
}

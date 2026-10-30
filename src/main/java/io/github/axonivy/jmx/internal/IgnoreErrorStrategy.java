package io.github.axonivy.jmx.internal;

import io.github.axonivy.jmx.IRegisterMBeanErrorStrategy;

public class IgnoreErrorStrategy implements IRegisterMBeanErrorStrategy {

  public static final IRegisterMBeanErrorStrategy INSTANCE = new IgnoreErrorStrategy();

  private IgnoreErrorStrategy() {
  }

  @Override
  public void errorRegisteringMBean(Object mBean, Throwable error) {
    // ignore
  }

}

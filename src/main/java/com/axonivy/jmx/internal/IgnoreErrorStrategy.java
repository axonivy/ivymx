package com.axonivy.jmx.internal;

import com.axonivy.jmx.IRegisterMBeanErrorStrategy;

public class IgnoreErrorStrategy implements IRegisterMBeanErrorStrategy
{

  public static final IRegisterMBeanErrorStrategy INSTANCE = new IgnoreErrorStrategy();
  
  private IgnoreErrorStrategy()
  {
  }

  @Override
  public void errorRegisteringMBean(Object mBean, Throwable error)
  {
    // ignore
  }

}

package io.github.axonivy.jmx.internal;

import io.github.axonivy.jmx.IRegisterMBeanErrorStrategy;
import io.github.axonivy.jmx.MException;

public class ThrowRuntimeExceptionErrorStrategy implements IRegisterMBeanErrorStrategy {
  public static final IRegisterMBeanErrorStrategy INSTANCE = new ThrowRuntimeExceptionErrorStrategy();

  private ThrowRuntimeExceptionErrorStrategy() {

  }

  @Override
  public void errorRegisteringMBean(Object mBean, Throwable error) {
    if (error instanceof RuntimeException) {
      throw (RuntimeException) error;
    }
    if (error instanceof Error) {
      throw (Error) error;
    }
    throw new MException(error);
  }

}

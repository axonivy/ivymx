package com.axonivy.jmx;

import com.axonivy.jmx.internal.IgnoreErrorStrategy;
import com.axonivy.jmx.internal.LogErrorStrategy;
import com.axonivy.jmx.internal.ThrowRuntimeExceptionErrorStrategy;

public class MConstants {
  /** Throws a runtime exception. This is the default strategy. If no other strategy is set this strategy is used */
  public static final IRegisterMBeanErrorStrategy THROW_RUNTIME_EXCEPTION_ERROR_STRATEGY = ThrowRuntimeExceptionErrorStrategy.INSTANCE;
  /** Logs the registration error */
  public static final IRegisterMBeanErrorStrategy LOG_ERROR_STRATEGY = LogErrorStrategy.INSTANCE;
  /** Ignores all registration errors */
  public static final IRegisterMBeanErrorStrategy IGNORE_ERROR_STRATEGY = IgnoreErrorStrategy.INSTANCE;
  /** This strategy is set by default */
  public static final IRegisterMBeanErrorStrategy DEFAULT_ERROR_STRATEGY = LOG_ERROR_STRATEGY;
}

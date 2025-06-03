package com.axonivy.jmx;

import java.util.concurrent.Callable;

/**
 * An execution context. The context setups some kind of context before a given callable is called. After that is tears down the
 * context.
 */
public interface IExecutionContext {
  /**
   * Executes the given callee in some execution context.
   * Implementors can setup some kind of execution context before calling the callee. After calling it the execution context should
   * be tear down. However the implementor must ensure that the method {@link Callable#call()} of the given callee is called.
   * @param <T> the result type
   * @param callee the callee to call inside the execution context
   * @return result of the callee
   * @throws Exception if callee throws an exception
   */
  <T> T executeInContext(Callable<T> callee) throws Exception;
}

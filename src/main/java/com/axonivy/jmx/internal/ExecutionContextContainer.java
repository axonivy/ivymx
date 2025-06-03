package com.axonivy.jmx.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import com.axonivy.jmx.IExecutionContext;

/**
 * An execution context that is a container for other execution contexts. The container ensures that the {@link Callable#call()} method of the
 * given <code>callee</code> parameter in method {@link #executeInContext(Callable)} is executed within all execution contexts
 * added to this container.
 * @author rwei
 * @since 26.02.2010
 */
public class ExecutionContextContainer implements IExecutionContext {
  /** The execution contexts contained in this container */
  private List<IExecutionContext> fExecutionContexts = new ArrayList<IExecutionContext>();

  /**
   * @see com.axonivy.jmx.IExecutionContext#executeInContext(java.util.concurrent.Callable)
   */
  @Override
  public <T> T executeInContext(Callable<T> callee) throws Exception {
    ContainerExecutionContext<T> context = new ContainerExecutionContext<T>(callee);
    return context.call();
  }

  /**
   * Add the given execution context to the container
   * @param executionContext the execution context to add
   */
  public void addExecutionContext(IExecutionContext executionContext) {
    assert executionContext != null : "Parameter executionContext must not be null";
    assert !fExecutionContexts.contains(executionContext) : "Parameter executionContext already added to the container";
    fExecutionContexts.add(executionContext);
  }

  /**
   * Removes the given execution context from the container
   * @param executionContext the execution context to remove
   */
  public void removeExecutionContext(IExecutionContext executionContext) {
    assert executionContext != null : "Parameter executionContext must not be null";
    assert fExecutionContexts.contains(executionContext) : "Parameter executionContext was no previously added to the container";
    fExecutionContexts.remove(executionContext);
  }

  /**
   * This class holds the information which execution context to call next until all
   * execution contexts are called. Then it calls the given callee.
   * @author rwei
   * @since 26.02.2010
   * @param <T>
   */
  private class ContainerExecutionContext<T> implements Callable<T> {
    /** The callee to execute */
    private Callable<T> fCallee;
    /** The position inside the the execution environments to call next */
    private int fPosition = 0;

    /**
     * Constructor
     * @param callee
     */
    public ContainerExecutionContext(Callable<T> callee) {
      assert callee != null : "Parameter callee must not be null";
      fCallee = callee;
    }

    /**
     * @see java.util.concurrent.Callable#call()
     */
    @Override
    public T call() throws Exception {
      IExecutionContext executionContext;

      if (fPosition < fExecutionContexts.size()) {
        executionContext = fExecutionContexts.get(fPosition);
        fPosition++;
        return executionContext.executeInContext(this);
      } else {
        return fCallee.call();
      }
    }
  }

}

package com.axonivy.jmx;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * Caches the value of an attribute computed by a method for the given amount of time. 
 * Instead of calling the method again the cached value is returned until the cache times out.<br>
 * Example:
 * <pre><code>
 * {@literal @MCache(10)}
 * {@literal @MAttribute}
 * public int compute()
 * {
 * }
 * </code></pre>
 * You can poll the attribute as fast as you can but the method <code>compute()</code> is only executed once every 10 seconds. 
 * This also means that the value of the attribute is only updated every 10 seconds.
 * Use this annotation if the computation of the attribute last long or is a heavy operation (e.g. database call, etc.)  
 * @author rwei
 * @since 1.1.0
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface MCache {
  
  /** 
   * The amount of time the cached value is used until it is computed again.
   * If {@link #unit()} is not specified the timeout is interpreted as {@link TimeUnit#SECONDS seconds}
   * @return time after which the cache timeouts
   */
  int timeout();
  
  /** 
   * The unit in which {@link #timeout()} is interpreted
   * @return time unit of the timeout value 
   */
  TimeUnit unit() default TimeUnit.SECONDS;
}

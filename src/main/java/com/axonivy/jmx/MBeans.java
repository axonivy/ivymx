package com.axonivy.jmx;

import java.util.Collection;

import javax.management.MBeanServer;

import com.axonivy.jmx.internal.MBeanManager;

/**
 * <p>Provides methods to {@link #registerMBeanFor(Object) register} and {@link #unregisterMBeanFor(Object) unregister} {@link MBean MBeans}</p>
 * <p>Simple Example:</p>
 * <pre>
 * {@code @MBean}("GettingStarted:name=Person")
 * public class Person
 * {
 *   public static void main(String[] args)
 *   {
 *     Person person = new Person();
 *     MBeans.registerMBeanFor(person);
 *   }
 * }
 * </pre>
 */
public class MBeans
{
  private static MBeanManager manager = MBeanManager.getInstance();

  /**
   * Constructor
   */
  private MBeans()
  {
  }

  public static void registerMBeanFor(Object object)
  {
    manager.registerMBeanFor(object);
  }

  /**
   * Registers all objects in the given collection that are MBeans ({@link #isMBean(Object)}).
   * @param objects objects to register as MBeans
   * @see #isMBean(Object)
   * @see #registerMBeanFor(Object)
   */
  public static void registerMBeansFor(Collection<? extends Object> objects)
  {
    manager.registerMBeansFor(objects);
  }


  public static boolean isMBean(Object object)
  {
    return manager.isMBean(object);
  }

  public static MBeanServer getMBeanServer()
  {
    return manager.getMBeanServer();
  }

  public static void unregisterMBeanFor(Object object)
  {
    manager.unregisterMBeanFor(object);
  }

  /**
   * Unregisters all objects in the given collection that are MBeans ({@link #isMBean(Object)}).
   * @param objects Objects to unregister
   * @see #isMBean(Object)
   * @see #unregisterMBeanFor(Object)
   */
  public static void unregisterMBeansFor(Collection<? extends Object> objects)
  {
    manager.unregisterMBeansFor(objects);
  }

  public static void unregisterAllMBeans()
  {
    manager.unregisterAllMBeans();
  }

  /**
   * All method calls that are done when calling a managed operations or reading and writing managed attributes through bean accessor methods
   * are executed within the given execution context. This can be used to ensure the methods are called in the right security context for example.
   * @param executionContext the execution context to add
   */
  public static void addExecutionContext(IExecutionContext executionContext)
  {
    manager.addExecutionContext(executionContext);
  }

  public static void removeExecutionContext(IExecutionContext executionContext)
  {
    manager.removeExecutionContext(executionContext);
  }

  public static void setRegisterMBeanErrorStrategy(IRegisterMBeanErrorStrategy strategy)
  {
    manager.setRegisterMBeanErrorStrategy(strategy);
  }
}

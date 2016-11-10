package com.axonivy.jmx.internal;

import java.lang.management.ManagementFactory;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.management.openmbean.OpenType;

import com.axonivy.jmx.IExecutionContext;
import com.axonivy.jmx.IRegisterMBeanErrorStrategy;
import com.axonivy.jmx.MBean;
import com.axonivy.jmx.MConstants;
import com.axonivy.jmx.MException;

/**
 * Global entry point for the management library implementation.<br>
 * Contains registries of execution contextes, {@link MBean} classes and
 * strategies to convert java classes to jmx open types.
 * @author rwei
 * @since 01.07.2013
 */
public class MBeanManager
{
  private ExecutionContextContainer executionContexts = new ExecutionContextContainer();

  private ConcurrentHashMap<Object, MBeanProxy> proxyRegistry = new ConcurrentHashMap<Object, MBeanProxy>();

  private ConcurrentHashMap<Class<?>, MBeanType> mBeanTypes = new ConcurrentHashMap<Class<?>, MBeanType>();

  private OpenTypeConverterStrategy[] openTypeConverterStrategies = {
          new SimpleTypeConverterStrategy(),
          new EnumTypeConverterStrategy(),
          new ListTypeConverterStrategy(this),
          new CompositeTypeConverterStrategy(this),
          new UriTypeConverterStrategy(),
          new MBeanConverterStrategy(this),
          new ThrowableConverterStrategy(),
          new PropertiesConverterStrategy()};

  private IRegisterMBeanErrorStrategy registerErrorStrategy = MConstants.DEFAULT_ERROR_STRATEGY;

  private static final MBeanManager INSTANCE = new MBeanManager();

  public static MBeanManager getInstance()
  {
	return INSTANCE;
  }

  public void registerMBeanFor(Object object)
  {
    registerMBeanFor(object, null);
  }

  public void registerMBeanFor(Object object, ObjectName parentName)
  {
    try
    {
      ensureMBeanProxyIsNotYetRegistered(object);
      MBeanType mBeanType = getMBeanTypeFor(object);
      MBeanProxy mBean = new MBeanProxy(mBeanType, object, parentName);
      registerMBeanProxy(object, mBean);
      registerMBean(mBean);
      registerCompositionMBeans(mBean);
    }
    catch(Throwable error)
    {
      registerErrorStrategy.errorRegisteringMBean(object, error);
    }
  }

  public void registerMBeansFor(Collection<? extends Object> objects)
  {
    for (Object object : objects)
    {
      if(isMBean(object))
      {
        registerMBeanFor(object);
      }
    }
  }

  private void registerCompositionMBeans(MBeanProxy mBean)
  {
    for (MCompositionReferenceValue compositionReferenceValue : mBean.getCompositionReferences())
    {
      ObjectName parentName = null;
      if (compositionReferenceValue.isConcatName())
      {
        parentName = mBean.getObjectName();
      }
      registerMBeanFor(compositionReferenceValue.getReferencedMBean(), parentName);
    }
  }

  private void registerMBean(MBeanProxy mBean) throws InstanceAlreadyExistsException,
          MBeanRegistrationException, NotCompliantMBeanException
  {
    ObjectName name = mBean.getObjectName();
    if (mBean.makeUniqueName())
    {
      while (getMBeanServer().isRegistered(name))
      {
        name = mBean.getNextPossibleUniqueObjectName();
      }
    }
    getMBeanServer().registerMBean(mBean, name);
  }

  private void ensureMBeanProxyIsNotYetRegistered(Object object)
  {
    if (proxyRegistry.containsKey(object))
    {
      throw new IllegalArgumentException("MBean for parameter object is already registered");
    }
  }

  private void registerMBeanProxy(Object object, MBeanProxy mBean)
  {
    if (proxyRegistry.putIfAbsent(object,  mBean) != null)
    {
      throw new IllegalArgumentException("MBean for parameter object is already registered");
    }
  }

  void ifAnnotatedRegisterMBeanFor(Object object)
  {
    if (isMBean(object))
    {
      registerMBeanFor(object);
    }
  }

  public boolean isMBean(Object object)
  {
    if (object == null)
    {
      return false;
    }
    return object.getClass().isAnnotationPresent(MBean.class);
  }

  public MBeanServer getMBeanServer()
  {
    return ManagementFactory.getPlatformMBeanServer();
  }

  public void unregisterMBeanFor(Object object)
  {
    MBeanProxy mBean = unregisterMBeanProxy(object);
    if (mBean != null)
    {
      unregisterMBean(mBean);
      unregisterCompositionMBeans(mBean);
    }
  }

  public void unregisterMBeansFor(Collection<? extends Object> objects)
  {
    for(Object object : objects)
    {
      if (isMBean(object))
      {
        unregisterMBeanFor(object);
      }
    }
  }

  private void unregisterCompositionMBeans(MBeanProxy mBean)
  {
    for (MCompositionReferenceValue compositionReferenceValue : mBean.getCompositionReferences())
    {
      unregisterMBeanFor(compositionReferenceValue.getReferencedMBean());
    }
  }

  private void unregisterMBean(MBeanProxy mBean)
  {
    try
    {
      getMBeanServer().unregisterMBean(mBean.getObjectName());
    }
    catch(InstanceNotFoundException ex)
    {
      // ignore
    }
    catch(Exception ex)
    {
      throw new MException(ex);
    }
  }

  private MBeanProxy unregisterMBeanProxy(Object object)
  {
    return proxyRegistry.remove(object);
  }

  void ifAnnotatedUnregisterMBeanFor(Object object)
  {
    if (isMBean(object))
    {
      unregisterMBeanFor(object);
    }
  }

  public void unregisterAllMBeans()
  {
    for (Object object : proxyRegistry.keySet().toArray())
    {
      unregisterMBeanFor(object);
    }
  }

  public void addExecutionContext(IExecutionContext executionContext)
  {
    executionContexts.addExecutionContext(executionContext);
  }

  public void removeExecutionContext(IExecutionContext executionContext)
  {
    executionContexts.removeExecutionContext(executionContext);
  }

  <T> T executeInContext(Callable<T> callable) throws Exception
  {
    return executionContexts.executeInContext(callable);
  }

  MBeanType getMBeanTypeFor(Object mBean)
  {
    Class<?> mBeanClass = mBean.getClass();
    MBeanType mBeanType = mBeanTypes.get(mBeanClass);
    if (mBeanType == null)
    {
      mBeanType = new MBeanType(this, mBeanClass);
      MBeanType alreadyDefined =  mBeanTypes.putIfAbsent(mBeanClass, mBeanType);
      if (alreadyDefined != null)
      {
        mBeanType = alreadyDefined;
      }
    }
    return mBeanType;
  }

  OpenType<?> toOpenType(Type type)
  {
    return getOpenTypeConverterStrategy(type).toOpenType(type);
  }

  AbstractValueConverter getValueConverter(Type type)
  {
    return getOpenTypeConverterStrategy(type).getValueConverter(type);
  }

  private OpenTypeConverterStrategy getOpenTypeConverterStrategy(Type type)
  {
    for (OpenTypeConverterStrategy strategy : openTypeConverterStrategies)
    {
      if (strategy.canHandle(type))
      {
        return strategy;
      }
    }
    throw new IllegalArgumentException("Type '"+type+"' cannot be converted to a jmx open type. No strategy found.");
  }

  public void setRegisterMBeanErrorStrategy(IRegisterMBeanErrorStrategy strategy)
  {
    registerErrorStrategy = strategy;
  }
}

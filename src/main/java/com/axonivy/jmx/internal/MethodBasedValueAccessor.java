package com.axonivy.jmx.internal;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

import javax.management.MBeanException;

/**
 * Access a value from by calling getter/setter methods a target class
 * @author rwei
 * @since 01.07.2013
 */
class MethodBasedValueAccessor extends AbstractValueAccessor
{
  private Method getterMethod;
  private Method setterMethod;
  private MBeanManager manager;
  
  public MethodBasedValueAccessor(MBeanManager manager, AbstractValueAccessor targetAccessor, Method getterMethod)
  {
    this(manager, targetAccessor, null, getterMethod);
  }

  public MethodBasedValueAccessor(MBeanManager manager, AbstractValueAccessor targetAccessor,
          AbstractValueConverter valueConverter, Method getterMethod)
  {
    this(manager, targetAccessor, valueConverter, getterMethod, null);
  }

  public MethodBasedValueAccessor(MBeanManager manager, AbstractValueAccessor targetAccessor, AbstractValueConverter valueConverter, Method getterMethod, Method setterMethod)
  {
    super(targetAccessor, valueConverter);
    this.manager = manager;
    this.getterMethod = getterMethod;
    this.getterMethod.setAccessible(true);
    if (setterMethod != null)
    {
      this.setterMethod = setterMethod;
      this.setterMethod.setAccessible(true);
    }
  }

  @Override
  public Object getValueFromTarget(final Object target) throws MBeanException
  {
    try
    {
      return manager.executeInContext(new Callable<Object>(){

        @Override
        public Object call() throws Exception
        {
          return getterMethod.invoke(target);
        }});
    }
    catch(Exception ex)
    {
      throw new MBeanException(ex, "Could not get value with method '"+getterMethod.getName()+" of class '"+target.getClass().getName()+"'");
    }
  }
  
  @Override
  public void setValueToTarget(final Object target, final Object value) throws MBeanException
  {
    if (setterMethod == null)
    {
      throw new MBeanException(new NoSuchMethodException("No setter method available to set value '"+value+"' on class '"+target.getClass().getName()+"'"));
    }
    try
    {
      manager.executeInContext(new Callable<Void>(){

        @Override
        public Void call() throws Exception
        {
          setterMethod.invoke(target, value);
          return null;
        }});
    }
    catch(Exception ex)
    {
      throw new MBeanException(ex, "Could not set value with method '"+setterMethod.getName()+" of class '"+target.getClass().getName()+"'");
    }
  }
  
  @Override
  protected String getAccessName()
  {
    return getterMethod.getName()+"()";
  }

}

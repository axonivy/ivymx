package com.axonivy.jmx.internal;

import javax.management.MBeanException;

/**
 * Gives jmx access (read/write) to a managed value on a target object. The target object will be resolved using the {@code targetAccessor}.   
 * The managed value will be converted to a jmx open data value and vice versa by the {@code valueConverter}.  
 * @author rwei
 * @since 01.07.2013
 */
abstract class AbstractValueAccessor
{
  private AbstractValueAccessor targetAccessor = null;
  private AbstractValueConverter valueConverter = IdentityValueConverter.IDENTITY;  
  
  protected AbstractValueAccessor()
  {
  }
  
  protected AbstractValueAccessor(AbstractValueAccessor targetAccessor)
  {
    this.targetAccessor = targetAccessor;
  }

  protected AbstractValueAccessor(AbstractValueAccessor targetAccessor, AbstractValueConverter valueConverter)
  {
    this.targetAccessor = targetAccessor;
    if (valueConverter != null)
    {
      this.valueConverter = valueConverter;
    }
  }

  Object getValue(Object rootTarget) throws MBeanException
  {
    Object target = getTarget(rootTarget);
    Object value = getValueFromTarget(target);
    return valueConverter.toOpenDataValue(value);
  }
  
  void setValue(Object rootTarget, Object value) throws MBeanException
  {
    Object target = getTarget(rootTarget);
    value = valueConverter.toJavaValue(value);
    setValueToTarget(target, value);
  }

  String getAccessPath()
  {
    if (targetAccessor != null)
    {
      return targetAccessor.getAccessPath()+"."+getAccessName();
    }
    return getAccessName();
  }
  
  private Object getTarget(Object rootTarget) throws MBeanException
  {
    if (targetAccessor != null)
    {
      return targetAccessor.getValue(rootTarget);      
    }
    return rootTarget;
  }
  
  @Override
  public String toString()
  {
    return getClass().getSimpleName()+" [accessPath="+getAccessPath()+"]";
  }

  /**
   * Read the value from the given target object. The target object is already resolved. Returned original value. Do not convert it. 
   * Subclasses have to implement this method.  
   * @param target the object on which to read the value
   * @return original unconverted value
   * @throws MBeanException
   */
  protected abstract Object getValueFromTarget(Object target) throws MBeanException;
  
  /**
   * Write the value to the given target object. The target object is already resolved. The given value already has the correct type. 
   * Do not convert it.
   * Subclasses have to implement this method
   * @param target the object on which to write the value to
   * @param value value to write. Has already the correct type.
   * @throws MBeanException
   */
  protected abstract void setValueToTarget(Object target, Object value) throws MBeanException;
  
  /**
   * e.g. this, fieldName, getMethod(), size(), length(), etc.
   * @return access name
   */
  protected abstract String getAccessName();
}

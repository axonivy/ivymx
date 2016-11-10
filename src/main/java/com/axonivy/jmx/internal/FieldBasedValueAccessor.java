package com.axonivy.jmx.internal;

import java.lang.reflect.Field;

import javax.management.MBeanException;

/**
 * Access a value from a field of a target class
 * @author rwei
 * @since 01.07.2013
 */
class FieldBasedValueAccessor extends AbstractValueAccessor
{
  private Field resolverField;
  
  FieldBasedValueAccessor(AbstractValueAccessor previousResolver, Field resolverField)
  {
    super(previousResolver);
    this.resolverField = resolverField;
    this.resolverField.setAccessible(true);
  }
  
  FieldBasedValueAccessor(AbstractValueAccessor previousResolver, AbstractValueConverter valueConverter, Field resolverField)
  {
    super(previousResolver, valueConverter);
    this.resolverField = resolverField;
    this.resolverField.setAccessible(true);
  }
  
  @Override
  protected Object getValueFromTarget(Object target) throws MBeanException
  {
    try
    {
      return resolverField.get(target);
    }
    catch(Exception ex)
    {
      throw new MBeanException(ex, "Cannot read value from  field '"+resolverField.getName()+"' of class '"+target.getClass().getName()+"'");
    }
  }
  
  @Override
  protected void setValueToTarget(Object target, Object value) throws MBeanException
  {
    try
    {
      resolverField.set(target,  value);
    }
    catch (Exception ex)
    {
      throw new MBeanException(ex, "Cannot write value to field '"+resolverField.getName()+"' of class '"+target.getClass().getName()+"'");
    }
  }
  
  @Override
  protected String getAccessName()
  {
    return resolverField.getName();
  }
}

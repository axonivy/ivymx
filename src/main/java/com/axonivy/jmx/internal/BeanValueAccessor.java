package com.axonivy.jmx.internal;

import javax.management.MBeanException;

/**
 * Gets as value a managed bean. Setting is not possible. This is normally the root target value accessor. 
 * @author rwei
 * @since 01.07.2013
 */
class BeanValueAccessor extends AbstractValueAccessor
{
  @Override
  public Object getValueFromTarget(Object target)
  {
    return target;
  }
  
  @Override
  protected void setValueToTarget(Object target, Object value) throws MBeanException
  {
    throw new IllegalStateException("Cannot set bean value");
  }
  
  @Override
  protected String getAccessName()
  {
    return "this";
  }
}

package com.axonivy.jmx.internal;

import java.util.Collection;

import javax.management.MBeanException;

/**
 * Gets the size value of a collection. Setting the size is not allowed
 * @author rwei
 * @since 01.07.2013
 */
class CollectionSizeValueAccessor extends AbstractValueAccessor
{
  CollectionSizeValueAccessor(AbstractValueAccessor targetAccessor)
  {
    super(targetAccessor);
  }
  
  @Override
  protected Object getValueFromTarget(Object target) throws MBeanException
  {
    if (target == null)
    {
      return 0;
    }
    return ((Collection<?>)target).size();
  }

  @Override
  protected void setValueToTarget(Object target, Object value) throws MBeanException
  {
    throw new IllegalStateException("Should never be called");
  }
  
  @Override
  protected String getAccessName()
  {
    return "size()";
  }
}
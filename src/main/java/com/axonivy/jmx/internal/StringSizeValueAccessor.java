package com.axonivy.jmx.internal;

import javax.management.MBeanException;

class StringSizeValueAccessor extends AbstractValueAccessor
{
  StringSizeValueAccessor(AbstractValueAccessor targetAccessor)
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
    return ((String)target).length();
  }

  @Override
  protected void setValueToTarget(Object target, Object value) throws MBeanException
  {
    throw new IllegalStateException("Should never be called");
  }
  
  @Override
  protected String getAccessName()
  {
    return "length()";
  }
}

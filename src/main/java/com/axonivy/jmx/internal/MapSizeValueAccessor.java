package com.axonivy.jmx.internal;

import java.util.Map;

import javax.management.MBeanException;

class MapSizeValueAccessor extends AbstractValueAccessor {
  MapSizeValueAccessor(AbstractValueAccessor targetAccessor) {
    super(targetAccessor);
  }

  @Override
  protected Object getValueFromTarget(Object target) throws MBeanException {
    if (target == null) {
      return 0;
    }
    return ((Map<?, ?>) target).size();
  }

  @Override
  protected void setValueToTarget(Object target, Object value) throws MBeanException {
    throw new IllegalStateException("Should never be called");
  }

  @Override
  protected String getAccessName() {
    return "size()";
  }
}

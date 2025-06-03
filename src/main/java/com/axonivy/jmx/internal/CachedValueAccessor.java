package com.axonivy.jmx.internal;

import javax.management.MBeanException;

import com.axonivy.jmx.MCache;

class CachedValueAccessor extends AbstractValueAccessor {
  private final MethodBasedValueAccessor methodAccessor;
  private final MCache config;
  private Object cache;
  private long lastReadTimestamp = 0;

  CachedValueAccessor(MethodBasedValueAccessor methodAccessor, MCache config) {
    this.methodAccessor = methodAccessor;
    this.config = config;
  }

  @Override
  protected Object getValueFromTarget(Object target) throws MBeanException {
    if (isOutdated()) {
      Object value = methodAccessor.getValue(target);
      setCache(value);
      return value;
    }
    return getCache();
  }

  @Override
  protected void setValueToTarget(Object target, Object value) throws MBeanException {
    methodAccessor.setValue(target, value);
    resetCache();
  }

  @Override
  protected String getAccessName() {
    return methodAccessor + " (Cached for " + config.timeout() + " " + config.unit().name() + ")";
  }

  private synchronized boolean isOutdated() {
    return System.currentTimeMillis() - lastReadTimestamp > config.unit().toMillis(config.timeout());
  }

  private synchronized void setCache(Object value) {
    lastReadTimestamp = System.currentTimeMillis();
    cache = value;
  }

  private synchronized Object getCache() {
    return cache;
  }

  private synchronized void resetCache() {
    lastReadTimestamp = 0;
    cache = null;
  }
}

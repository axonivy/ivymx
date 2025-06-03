package com.axonivy.jmx.internal;

import java.lang.reflect.Type;

import javax.management.MBeanException;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;

import com.axonivy.jmx.MBean;

class MBeanConverterStrategy implements OpenTypeConverterStrategy {
  private AbstractValueConverter valueConverter;

  MBeanConverterStrategy(MBeanManager manager) {
    valueConverter = new MBeanValueConverter(manager);
  }

  @Override
  public boolean canHandle(Type type) {
    return type instanceof Class<?> && ((Class<?>) type).isAnnotationPresent(MBean.class);
  }

  @Override
  public OpenType<?> toOpenType(Type type) {
    return SimpleType.OBJECTNAME;
  }

  @Override
  public AbstractValueConverter getValueConverter(Type type) {
    return valueConverter;
  }

  private static class MBeanValueConverter extends AbstractValueConverter {
    private MBeanManager manager;

    public MBeanValueConverter(MBeanManager manager) {
      this.manager = manager;
    }

    @Override
    protected Object toOpenDataValue(Object javaValue) throws MBeanException {
      if (javaValue == null) {
        return null;
      }
      try {
        return new ObjectName(manager.getMBeanTypeFor(javaValue).evaluateName(javaValue));
      } catch (MalformedObjectNameException ex) {
        throw new MBeanException(ex);
      }
    }

    @Override
    protected Object toJavaValue(Object openDataValue) throws MBeanException {
      throw new MBeanException(new IllegalStateException("Not supported"));
    }
  }

}

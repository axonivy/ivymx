package com.axonivy.jmx.internal;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import javax.management.MBeanException;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.axonivy.jmx.MException;

public class ThrowableConverterStrategy implements OpenTypeConverterStrategy {
  @Override
  public boolean canHandle(Type type) {
    return type instanceof Class && Throwable.class.isAssignableFrom((Class<?>) type);
  }

  @Override
  public OpenType<?> toOpenType(Type type) {
    String name = ((Class<?>) type).getName();
    try {
      return new CompositeType(name, name,
          new String[] {"message", "type", "stackTrace"},
          new String[] {"messsage", "type", "stackTrace"},
          new OpenType[] {SimpleType.STRING, SimpleType.STRING, SimpleType.STRING});
    } catch (OpenDataException ex) {
      throw new MException(ex);
    }
  }

  @Override
  public AbstractValueConverter getValueConverter(Type type) {
    return new ThrowableValueConverter((CompositeType) toOpenType(type));
  }

  private static class ThrowableValueConverter extends AbstractValueConverter {
    private CompositeType compositeType;

    public ThrowableValueConverter(CompositeType compositeType) {
      this.compositeType = compositeType;
    }

    @Override
    protected Object toOpenDataValue(Object javaValue) throws MBeanException {
      if (javaValue == null) {
        return javaValue;
      }
      Throwable error = (Throwable) javaValue;
      Map<String, Object> items = new HashMap<String, Object>();

      items.put("message", error.getMessage());
      items.put("type", error.getClass().getName());
      items.put("stackTrace", ExceptionUtils.getStackTrace(error));
      try {
        return new CompositeDataSupport(compositeType, items);
      } catch (OpenDataException ex) {
        throw new MBeanException(ex);
      }
    }

    @Override
    protected Object toJavaValue(Object openDataValue) throws MBeanException {
      return new MBeanException(new IllegalStateException("No implemented"));
    }

  }
}

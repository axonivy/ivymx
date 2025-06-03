package com.axonivy.jmx.internal;

import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import javax.management.MBeanException;
import javax.management.openmbean.ArrayType;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.OpenType;

import com.axonivy.jmx.MException;

/**
 * Strategy to convert {@link java.util.List} objects to arrays.
 * @author rwei
 * @since 01.07.2013
 */
class ListTypeConverterStrategy implements OpenTypeConverterStrategy {
  private MBeanManager manager;

  ListTypeConverterStrategy(MBeanManager manager) {
    this.manager = manager;
  }

  @Override
  public boolean canHandle(Type type) {
    if (type instanceof ParameterizedType) {
      type = ((ParameterizedType) type).getRawType();
      return type instanceof Class<?> && List.class.isAssignableFrom((Class<?>) type);
    }
    return false;
  }

  @Override
  public OpenType<?> toOpenType(Type type) {
    try {
      Type contentType = getContentType(type);
      return new ArrayType<CompositeType>(1, manager.toOpenType(contentType));
    } catch (OpenDataException ex) {
      throw new MException(ex);
    }
  }

  private Type getContentType(Type type) {
    return ((ParameterizedType) type).getActualTypeArguments()[0];
  }

  @Override
  public AbstractValueConverter getValueConverter(Type type) {
    try {
      Type contentType = getContentType(type);
      OpenType<?> contentOpenType = manager.toOpenType(contentType);
      Class<?> contentOpenClass = Class.forName(contentOpenType.getClassName());
      return new List2ArrayConverter(contentOpenClass, manager.getValueConverter(contentType));
    } catch (ClassNotFoundException ex) {
      throw new MException(ex);
    }
  }

  private static class List2ArrayConverter extends AbstractValueConverter {
    private Class<?> contentOpenClass;
    private AbstractValueConverter contentValueConverter;

    public List2ArrayConverter(Class<?> contentOpenClass, AbstractValueConverter contentValueConverter) {
      this.contentOpenClass = contentOpenClass;
      this.contentValueConverter = contentValueConverter;
    }

    @Override
    public Object toOpenDataValue(Object javaValue) throws MBeanException {
      if (javaValue == null) {
        return null;
      }
      List<?> list = (List<?>) javaValue;
      Object[] openData = (Object[]) Array.newInstance(contentOpenClass, list.size());
      int pos = 0;
      for (Object value : list) {
        openData[pos++] = contentValueConverter.toOpenDataValue(value);
      }
      return openData;
    }

    @Override
    public Object toJavaValue(Object openDataValue) throws MBeanException {
      throw new MBeanException(new IllegalStateException("Not supported"));
    }

  }
}

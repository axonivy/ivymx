package com.axonivy.jmx.internal;

import java.lang.reflect.Type;

import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;

/**
 * Strategy to convert values of {@link Enum} classes to Strings values
 * @author rwei
 * @since 01.07.2013
 */
class EnumTypeConverterStrategy implements OpenTypeConverterStrategy {

  @Override
  public boolean canHandle(Type type) {
    return (type instanceof Class) && ((Class<?>) type).isEnum();
  }

  @Override
  public OpenType<?> toOpenType(Type type) {
    return SimpleType.STRING;
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  @Override
  public AbstractValueConverter getValueConverter(Type type) {
    return new EnumValueConverter((Class<?>) type);
  }

}

package com.axonivy.jmx.internal;

/**
 * Converts values of an {@link Enum} class to Strings and vice versa
 * @author rwei
 * @since 01.07.2013
 * @param <T>
 */
class EnumValueConverter<T extends Enum<T>> extends AbstractValueConverter {
  private Class<T> enumType;

  EnumValueConverter(Class<T> enumType) {
    this.enumType = enumType;
  }

  @Override
  protected Object toOpenDataValue(Object javaValue) {
    if (javaValue == null) {
      return javaValue;
    }
    return ((Enum<?>) javaValue).name();
  }

  @Override
  protected Object toJavaValue(Object openDataValue) {
    if (openDataValue == null) {
      return openDataValue;
    }

    return Enum.valueOf(enumType, openDataValue.toString());
  }

}

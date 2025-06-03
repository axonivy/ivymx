package com.axonivy.jmx.internal;

import java.lang.reflect.Type;

import javax.management.openmbean.OpenType;

/**
 * Strategy how to convert java types to {@link OpenType open types} and java values to open data values.
 * @author rwei
 * @since 01.07.2013
 */
interface OpenTypeConverterStrategy {
  /**
   * @param type type
   * @return true if the strategy can convert the given type to a {@link OpenType}.
   */
  public boolean canHandle(Type type);

  /**
   * Convert the given java type to an {@link OpenType}.
   * @param type type
   * @return open type
   */
  public OpenType<?> toOpenType(Type type);

  /**
   * @param type type
   * @return a {@link AbstractValueConverter converter} that can convert values of the given type to open data values
   */
  public AbstractValueConverter getValueConverter(Type type);
}

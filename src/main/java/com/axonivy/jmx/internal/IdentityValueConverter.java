package com.axonivy.jmx.internal;

/**
 * Converts nothing simple returns the same value as given.
 * @author rwei
 * @since 01.07.2013
 */
class IdentityValueConverter extends AbstractValueConverter {
  static IdentityValueConverter IDENTITY = new IdentityValueConverter();

  private IdentityValueConverter() {}

  @Override
  protected Object toOpenDataValue(Object javaValue) {
    return javaValue;
  }

  @Override
  protected Object toJavaValue(Object openDataValue) {
    return openDataValue;
  }

}

package com.axonivy.jmx.internal;

import javax.management.MBeanException;

/**
 * Converts java values to to jmx open data values and vice versa
 * @author rwei
 * @since 01.07.2013
 * @see javax.management.openmbean.OpenType
 */
abstract class AbstractValueConverter {
  protected abstract Object toOpenDataValue(Object javaValue) throws MBeanException;

  protected abstract Object toJavaValue(Object openDataValue) throws MBeanException;
}

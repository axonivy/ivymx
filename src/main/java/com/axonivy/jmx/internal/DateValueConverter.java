package com.axonivy.jmx.internal;

import java.util.Date;

import javax.management.MBeanException;

/**
 * Converts objects of classes that are childs of java.util.Date to to raw java.util.Date objects.
 * This is necessary because open type does only support java.util.Date and not extension of it like java.sql.Date. 
 */
class DateValueConverter extends AbstractValueConverter
{
  static DateValueConverter INSTANCE = new DateValueConverter();
  
  private DateValueConverter()
  {
  }

  @Override
  protected Object toOpenDataValue(Object javaValue) throws MBeanException
  {
    if (javaValue == null)
    {
      return javaValue;
    }
    if (Date.class.equals(javaValue.getClass()))
    {
      return javaValue;
    }
    // javaValue value is not a java.util.Date but a child class of it. E.g. java.sql.Date. -> convert to java.util.Date
    Date dateValue = (Date)javaValue;
    return new Date(dateValue.getTime());
  }

  @Override
  protected Object toJavaValue(Object openDataValue) throws MBeanException
  {
    return openDataValue;
  }

}

package com.axonivy.jmx.internal;

import java.lang.reflect.Type;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;
import javax.management.MBeanException;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;


public class NewDateTimeApiConverterStrategy implements OpenTypeConverterStrategy
{
  @Override
  public boolean canHandle(Type type)
  {
    return toOpenType(type) != null;
  }

  @Override
  public OpenType<?> toOpenType(Type type)
  {
    if (Instant.class.equals(type))
    {
      return SimpleType.DATE;
    }
    if (LocalDateTime.class.equals(type))
    {
      return SimpleType.DATE;
    }
    if (OffsetDateTime.class.equals(type))
    {
      return SimpleType.DATE;
    }
    if (ZonedDateTime.class.equals(type))
    {
      return SimpleType.DATE;
    }
    return null;
  }

  @Override
  public AbstractValueConverter getValueConverter(Type type)
  {
    if (Instant.class.equals(type))
    {
      return InstantValueConverter.INSTANCE;
    }
    if (LocalDateTime.class.equals(type))
    {
      return LocalDateTimeValueConverter.INSTANCE;
    }
    if (OffsetDateTime.class.equals(type))
    {
      return OffsetDateTimeValueConverter.INSTANCE;
    }
    if (ZonedDateTime.class.equals(type))
    {
      return ZonedDateTimeValueConverter.INSTANCE;
    }
    return null;
  }

  private static final class InstantValueConverter extends AbstractValueConverter
  {

    private static final InstantValueConverter INSTANCE = new InstantValueConverter();

    private InstantValueConverter()
    {
    }

    @Override
    protected Object toOpenDataValue(Object javaValue) throws MBeanException
    {
      if (javaValue == null)
      {
        return javaValue;
      }
      return Date.from((Instant)javaValue);
    }

    @Override
    protected Object toJavaValue(Object openDataValue) throws MBeanException
    {
      if (openDataValue == null)
      {
        return openDataValue;
      }
      return ((Date)openDataValue).toInstant();
    }
  }

  private static final class LocalDateTimeValueConverter extends AbstractValueConverter
  {
    private static final LocalDateTimeValueConverter INSTANCE = new LocalDateTimeValueConverter();

    private LocalDateTimeValueConverter()
    {
    }

    @Override
    protected Object toOpenDataValue(Object javaValue) throws MBeanException
    {
      if (javaValue == null)
      {
        return javaValue;
      }
      Instant instant = ((LocalDateTime)javaValue).atZone(ZoneId.systemDefault()).toInstant();
      return Date.from(instant);
    }

    @Override
    protected Object toJavaValue(Object openDataValue) throws MBeanException
    {
      if (openDataValue == null)
      {
        return openDataValue;
      }
      Instant instant = ((Date)openDataValue).toInstant();
      return instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
  }

  private static final class OffsetDateTimeValueConverter extends AbstractValueConverter
  {
    private static final OffsetDateTimeValueConverter INSTANCE = new OffsetDateTimeValueConverter();

    private OffsetDateTimeValueConverter()
    {
    }

    @Override
    protected Object toOpenDataValue(Object javaValue) throws MBeanException
    {
      if (javaValue == null)
      {
        return javaValue;
      }
      Instant instant = ((OffsetDateTime)javaValue).toInstant();
      return Date.from(instant);
    }

    @Override
    protected Object toJavaValue(Object openDataValue) throws MBeanException
    {
      if (openDataValue == null)
      {
        return openDataValue;
      }
      Instant instant = ((Date)openDataValue).toInstant();
      ZoneOffset offset = ZoneId.systemDefault().getRules().getOffset(instant);
      return instant.atOffset(offset);
    }
  }

  private static final class ZonedDateTimeValueConverter extends AbstractValueConverter
  {
    private static final ZonedDateTimeValueConverter INSTANCE = new ZonedDateTimeValueConverter();

    private ZonedDateTimeValueConverter()
    {
    }

    @Override
    protected Object toOpenDataValue(Object javaValue) throws MBeanException
    {
      if (javaValue == null)
      {
        return javaValue;
      }
      Instant instant = ((ZonedDateTime)javaValue).toInstant();
      return Date.from(instant);
    }

    @Override
    protected Object toJavaValue(Object openDataValue) throws MBeanException
    {
      if (openDataValue == null)
      {
        return openDataValue;
      }
      Instant instant = ((Date)openDataValue).toInstant();
      return instant.atZone(ZoneId.systemDefault());
    }
  }
}

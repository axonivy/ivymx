package com.axonivy.jmx.internal;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;

import javax.management.MBeanException;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;

/**
 * Strategy that can convert {@link URI} to {@link String}
 * @author rwei
 * @since 01.07.2013
 */
public class UriTypeConverterStrategy implements OpenTypeConverterStrategy
{

  @Override
  public boolean canHandle(Type type)
  {
    return URI.class.equals(type);
  }

  @Override
  public OpenType<?> toOpenType(Type type)
  {
    return SimpleType.STRING;
  }

  @Override
  public AbstractValueConverter getValueConverter(Type type)
  {
    return UriValueConverter.INSTANCE;
  }

  private static class UriValueConverter extends AbstractValueConverter
  {
    static final AbstractValueConverter INSTANCE = new UriValueConverter();
    
    private UriValueConverter()
    {      
    }

    @Override
    public Object toOpenDataValue(Object javaValue)
    {
      if (javaValue == null)
      {
        return null;      
      }
      return javaValue.toString();

    }

    @Override
    public Object toJavaValue(Object openDataValue) throws MBeanException
    {
      if (openDataValue == null)
      {
        return openDataValue;
      }
      try
      {
        return new URI(openDataValue.toString());
      }
      catch(URISyntaxException ex)
      {
        throw new MBeanException(ex);
      }
    }

  }
}

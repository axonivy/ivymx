package com.axonivy.jmx.internal;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;

import org.apache.commons.lang3.ClassUtils;

/**
 * Strategy that can convert java types to {@link SimpleType simple types}.  
 * @author rwei
 * @since 01.07.2013
 */
class SimpleTypeConverterStrategy implements OpenTypeConverterStrategy
{
  private static final List<SimpleType<?>> SIMPLE_TYPES = Arrays.asList(
          SimpleType.BIGDECIMAL, 
          SimpleType.BIGINTEGER, 
          SimpleType.BOOLEAN, 
          SimpleType.BYTE, 
          SimpleType.CHARACTER, 
          SimpleType.DATE, 
          SimpleType.DOUBLE, 
          SimpleType.FLOAT, 
          SimpleType.INTEGER,
          SimpleType.LONG, 
          SimpleType.OBJECTNAME, 
          SimpleType.SHORT,
          SimpleType.STRING, 
          SimpleType.VOID);
  
  @Override
  public boolean canHandle(Type type)
  {
    return type instanceof Class<?> && toOpenType(type)!=null;
  }
  
  @Override
  public OpenType<?> toOpenType(Type type)
  {
    Class<?> clazz = (Class<?>)type;
    clazz = primitiveToWrapper(clazz);
    for (SimpleType<?> simpleType : SIMPLE_TYPES)
    {  
      if (simpleType.getClassName().equals(clazz.getName()))
      {
        return simpleType;
      }
    }
    return null;
  }

  @Override
  public AbstractValueConverter getValueConverter(Type type)
  {
    if (Date.class.equals(type))
    {
      return DateValueConverter.INSTANCE;
    }
    return IdentityValueConverter.IDENTITY;
  }

  private Class<?> primitiveToWrapper(Class<?> type)
  {
    if (Void.TYPE.equals(type))
    {
      return Void.class;
    }
    return ClassUtils.primitiveToWrapper(type);
  }
}

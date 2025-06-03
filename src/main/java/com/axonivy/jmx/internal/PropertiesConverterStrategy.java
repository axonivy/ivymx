package com.axonivy.jmx.internal;

import java.lang.reflect.Type;
import java.util.Properties;

import javax.management.MBeanException;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;
import javax.management.openmbean.TabularData;
import javax.management.openmbean.TabularDataSupport;
import javax.management.openmbean.TabularType;

import com.axonivy.jmx.MException;
import com.axonivy.jmx.util.MUtils;

/**
 * Converts {@link java.util.Properties} to open type and vice versa
 */
class PropertiesConverterStrategy implements OpenTypeConverterStrategy {
  private static final CompositeType PROPERTY_NAME_VALUE_PAIR;
  private static final TabularType PROPERTIES;
  private static final String PROPERTY_NAME_ITEM = "propertyName";
  private static final String PROPERTY_VALUE_ITEM = "propertyValue";

  static {
    try {
      PROPERTY_NAME_VALUE_PAIR = new CompositeType("PropertyNameValuePair", "Property name and value pair", new String[] {PROPERTY_NAME_ITEM, PROPERTY_VALUE_ITEM}, new String[] {"Name of the property", "Value of the property"},
          new OpenType[] {SimpleType.STRING, SimpleType.STRING});
      PROPERTIES = new TabularType(Properties.class.getName(), Properties.class.getName(), PROPERTY_NAME_VALUE_PAIR, new String[] {PROPERTY_NAME_ITEM});
    } catch (OpenDataException ex) {
      throw new MException(ex);
    }
  }

  @Override
  public boolean canHandle(Type type) {
    return type.equals(Properties.class);
  }

  @Override
  public OpenType<?> toOpenType(Type type) {
    return PROPERTIES;
  }

  @Override
  public AbstractValueConverter getValueConverter(Type type) {
    return PropertiesValueConverter.INSTANCE;
  }

  /**
   * Converts {@link java.util.Properties} to open type object and vice versa
   * @author rwei
   * @since 27.01.2014
   */
  private static class PropertiesValueConverter extends AbstractValueConverter {
    static final PropertiesValueConverter INSTANCE = new PropertiesValueConverter();

    private PropertiesValueConverter() {}

    @Override
    protected Object toOpenDataValue(Object javaValue) throws MBeanException {
      try {
        if (javaValue == null) {
          return null;
        }
        Properties properties = (Properties) javaValue;
        TabularData tabularData = new TabularDataSupport(PROPERTIES);
        for (String key : properties.stringPropertyNames()) {
          CompositeData row = new CompositeDataSupport(PROPERTY_NAME_VALUE_PAIR, new String[] {PROPERTY_NAME_ITEM, PROPERTY_VALUE_ITEM}, new String[] {key, properties.getProperty(key)});
          tabularData.put(row);
        }
        return tabularData;
      } catch (OpenDataException ex) {
        throw new MBeanException(ex);
      }
    }

    @Override
    protected Object toJavaValue(Object openDataValue) throws MBeanException {
      if (openDataValue == null) {
        return null;
      }
      Properties properties = new Properties();
      for (CompositeData row : MUtils.toRows((TabularData) openDataValue)) {
        properties.setProperty((String) row.get(PROPERTY_NAME_ITEM), (String) row.get(PROPERTY_VALUE_ITEM));
      }
      return properties;
    }
  }
}

package com.axonivy.jmx.internal;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.apache.commons.lang3.StringUtils;

class NameInstruction 
{
  private ObjectName template;
  private boolean isRelative;
  private List<Property<Instruction>> valueInstructions;
  
  private NameInstruction(MBeanManager manager, Class<?> mBeanClass, String instruction) throws MalformedObjectNameException
  {
    isRelative = isRelative(instruction);
    if (isRelative)
    {
      instruction = ":"+instruction;
    }
    template = new ObjectName(instruction);
    List<Property<String>> properties = getProperties();
    valueInstructions = properties
            .stream()
            .map(property -> new Property<>(property.getKey(), Instruction.parseInstruction(manager, mBeanClass, property.getValue())))
            .collect(Collectors.toList());
  }

  static NameInstruction parseInstruction(MBeanManager manager, Class<?> mBeanClass, String value)
  {
    try
    {
      return new NameInstruction(manager, mBeanClass, value);
    }
    catch(MalformedObjectNameException ex)
    {
      throw new IllegalArgumentException("Object name of MBean '"+value+"'is malformed", ex);
    }
  }
  
  String execute(Object mBean)
  {
    List<Property<String>> evaluatedValues = evaluateAndQuoteValues(mBean);
    String name = buildName(evaluatedValues);
    return name;
  }

  private boolean isRelative(String instruction)
  {
    int firstColon = StringUtils.indexOf(instruction, ":");
    if (firstColon < 0)
    {
      return true;
    }
    int firstEqual = StringUtils.indexOf(instruction, "=");
    return firstEqual<firstColon; // colon appears in quoted value
  }

  private List<Property<String>> getProperties()
  {
    List<Property<String>> properties = template
        .getKeyPropertyList()
        .entrySet()
        .stream()
        .map(entry -> new Property<>(entry.getKey(), entry.getValue()))
        .collect(Collectors.toList());
    String strName = template.getKeyPropertyListString();
    Collections.sort(properties, new PropertyComparator(strName));
    return properties;
  }

  private List<Property<String>> evaluateAndQuoteValues(Object mBean)
  {
    List<Property<String>> evaluatedValues = valueInstructions
            .stream()
            .map(property -> new Property<>(property.getKey(), property.getValue().execute(mBean)))
            .map(this::quoteIfNecessary)
            .collect(Collectors.toList());
    return evaluatedValues;
  }
 
  private String buildName(List<Property<String>> evaluatedValues)
  {
    StringBuilder name = new StringBuilder(256);
    if (!isRelative)
    {
      name.append(template.getDomain());
      name.append(':');
    }
    String properties = evaluatedValues
            .stream()
            .map(Property::toString)
            .collect(Collectors.joining(","));
    name.append(properties);
    return name.toString();
  }

  private Property<String> quoteIfNecessary(Property<String> property)
  {
    if (needsQuoting(property.getValue()))
    {
      return new Property<String>(property.getKey(), ObjectName.quote(property.getValue()));
    }
    return property;
  }
  
  private boolean needsQuoting(String value)
  {
    boolean isQuoted = StringUtils.startsWith(value, "\"") && StringUtils.endsWith(value, "\"");
    return !isQuoted && StringUtils.containsAny(value, ',',':','=','\n', '\"');
  }

  private static final class Property<T>
  {
    private String key;
    private T value;
    
    private Property(String key, T value)
    {
      this.key = key;
      this.value = value;
    }
    
    private T getValue()
    {
      return value;
    }
    
    private String getKey()
    {
      return key;
    }
    
    @Override
    public String toString()
    {
      return key+"="+value;
    }
  }
  
  private static final class PropertyComparator implements Comparator<Property<String>>
  {
    private String strName;

    private PropertyComparator(String strName)
    {
      this.strName = strName;
    }

    @Override
    public int compare(Property<String> property1, Property<String> property2)
    {
      int positionProperty1 = StringUtils.indexOf(strName, property1.getKey()+"=");
      int positionProperty2 = StringUtils.indexOf(strName, property2.getKey()+"=");
      return Integer.compare(positionProperty1, positionProperty2);
    }    
  }
}

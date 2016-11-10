package com.axonivy.jmx.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.management.MBeanException;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.OpenType;

import com.axonivy.jmx.MComposite;
import com.axonivy.jmx.MException;

/**
 * Caches all relevant information about a class that is annotated with a {@link MComposite} annotation.
 * @author rwei
 * @since 01.07.2013
 */
class MCompositeType
{
  private Class<?> mCompositeClass;
  private MComposite annotation;
  private OpenType<?> openType;
  private Map<String, DynamicMItem> items = new HashMap<String, DynamicMItem>();
  private MBeanManager manager;

  MCompositeType(MBeanManager manager, Class<?> mCompositeClass)
  {
    this.manager = manager;
    this.mCompositeClass = mCompositeClass;
    annotation = mCompositeClass.getAnnotation(MComposite.class);
    if (annotation == null)
    {
      throw new IllegalArgumentException("Composite class '"+mCompositeClass+"' must contain a @MComposite annotation");
    }
  }

  OpenType<?> getOpenType()
  {
    if (openType == null)
    {
      String name = mCompositeClass.getName();
      String description = MInternalUtils.getDescription(annotation.value(), name);

      List<String> itemNames = new ArrayList<String>();
      List<String> itemDescriptions = new ArrayList<String>();
      List<OpenType<?>> itemTypes = new ArrayList<OpenType<?>>();

      addItems(itemNames, itemDescriptions, itemTypes);

      try
      {
        openType = new CompositeType(name, description,
                itemNames.toArray(new String[itemNames.size()]),
                itemDescriptions.toArray(new String[itemDescriptions.size()]),
                itemTypes.toArray(new OpenType[itemTypes.size()]));
      }
      catch (OpenDataException ex)
      {
        throw new MException(ex);
      }
    }
    return openType;
  }

  private void addItems(List<String> itemNames, List<String> itemDescriptions, List<OpenType<?>> itemTypes)
  {
    items = MItemCreator.create(manager, mCompositeClass);
    for (DynamicMItem item : items.values())
    {
      itemNames.add(item.getName());
      itemDescriptions.add(item.getDescription());
      itemTypes.add(item.getOpenType());
    }
  }

  AbstractValueConverter getValueConverter()
  {
    return new CompositeValueConverter();
  }

  private class CompositeValueConverter extends AbstractValueConverter
  {

    @Override
    public Object toOpenDataValue(Object javaValue) throws MBeanException
    {
      if (javaValue == null)
      {
        return null;
      }
      Map<String, Object> values = new HashMap<String, Object>();
      for (DynamicMItem item : items.values())
      {
        values.put(item.getName(), item.getValue(javaValue));
      }
      try
      {
        return new CompositeDataSupport((CompositeType)openType, values);
      }
      catch(OpenDataException ex)
      {
        throw new MBeanException(ex);
      }
    }

    @Override
    public Object toJavaValue(Object openDataValue) throws MBeanException
    {
      throw new MBeanException(new IllegalStateException("Not supported"));
    }

  }
}

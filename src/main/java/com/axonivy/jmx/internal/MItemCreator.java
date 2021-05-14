package com.axonivy.jmx.internal;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import javax.management.openmbean.OpenType;

import com.axonivy.jmx.MItem;

/**
 * Analyzes a given class an creates {@link DynamicMItem} for every field or method that is annotated with a {@link MItem}.
 * @author rwei
 * @since 01.07.2013
 */
class MItemCreator extends MCreator
{
  private MItemCreator(MBeanManager manager, Class<?> mCompositeClass)
  {
    super(manager, mCompositeClass);
  }

  static Map<String, DynamicMItem> create(MBeanManager manager, Class<?> mCompositeClass)
  {
    return new MItemCreator(manager, mCompositeClass).createItemsForAnnoations();
  }
  
  private Map<String, DynamicMItem> createItemsForAnnoations()
  {
    Map<String, DynamicMItem> items = new HashMap<String, DynamicMItem>();
    for (Class<?> clazz : getClassesToAnalyze())
    {
      addFieldItems(clazz, items);
      addMethodItems(clazz, items);
    }
    return items;
  }
  
  

  private void addFieldItems(Class<?> clazz, Map<String, DynamicMItem> items)
  {
    for (Field field : clazz.getDeclaredFields())
    {
      MItem item = field.getAnnotation(MItem.class);
      if (item != null)
      {
        DynamicMItem mItem = createItem(item, field);
        items.put(mItem.getName(), mItem);
      }
    }
  }

  private void addMethodItems(Class<?> clazz, Map<String, DynamicMItem> items)
  {
    for (Method method : MInternalUtils.getNonSyntheticDeclaredMethods(clazz))
    {
      MItem item = method.getAnnotation(MItem.class);
      if (item != null)
      {
        DynamicMItem mItem = createItem(item, method);
        items.put(mItem.getName(), mItem);
      }
    }
  }

  private DynamicMItem createItem(MItem item, Method method)
  {
    String name = MInternalUtils.getAttributeName(method, item.name());
    String description = MInternalUtils.getDescription(item.description(), name);
    Type itemType = MInternalUtils.getManagedTyped(method.getGenericReturnType(), item.type());
    OpenType<?> openType = manager.toOpenType(itemType);
    
    AbstractValueConverter valueConverter = manager.getValueConverter(itemType);
    AbstractValueAccessor valueAccessor = new MethodBasedValueAccessor(manager, targetAccessor, valueConverter, method);
    
    return new DynamicMItem(name, description, openType, valueAccessor);
  }

  private DynamicMItem createItem(MItem item, Field field)
  {
    String name = MInternalUtils.getAttributeName(field, item.name());
    String description = MInternalUtils.getDescription(item.description(), name);
    Type itemType = MInternalUtils.getManagedTyped(field.getGenericType(), item.type());
    OpenType<?> openType = manager.toOpenType(itemType);
    
    AbstractValueConverter valueConverter = manager.getValueConverter(itemType);
    AbstractValueAccessor valueAccessor = new FieldBasedValueAccessor(targetAccessor, valueConverter, field);
    
    return new DynamicMItem(name, description, openType, valueAccessor);
  }
}

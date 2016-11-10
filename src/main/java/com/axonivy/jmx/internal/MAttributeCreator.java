package com.axonivy.jmx.internal;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.management.openmbean.OpenMBeanAttributeInfo;
import javax.management.openmbean.OpenMBeanAttributeInfoSupport;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;

import com.axonivy.jmx.MAttribute;
import com.axonivy.jmx.MInclude;
import com.axonivy.jmx.MSizeAttribute;

/**
 * Analyzes a given class an creates {@link DynamicMAttribute} for every field or method that is annotated with a {@link MAttribute}.
 * @author rwei
 * @since 01.07.2013
 */
class MAttributeCreator extends MCreator
{
  private MAttributeCreator(MBeanManager manager, Class<?> mBeanClass)
  {
    super(manager, mBeanClass);
  }

  private MAttributeCreator(MBeanManager manager, Class<?> mBeanClass, AbstractValueAccessor targetAccessor)
  {
    super(manager, mBeanClass, targetAccessor);
  }

  static List<DynamicMAttribute> create(MBeanManager manager, Class<?> mBeanClass)
  {
    return new MAttributeCreator(manager, mBeanClass).createAttributesForAnnotations();
  }
  
  private List<DynamicMAttribute> createAttributesForAnnotations()
  {
    List<DynamicMAttribute> attributes = new ArrayList<DynamicMAttribute>();
    addFieldBasedAttributes(attributes);
    addMethodBasedAttributes(attributes);
    return attributes;
  }
  
  private void addMethodBasedAttributes(List<DynamicMAttribute> attributes)
  {
    for (Class<?> clazz : getClassesToAnalyze())
    {
      addMethodBasedAttributesDeclaredOn(clazz, attributes);
    }
  }

  private void addMethodBasedAttributesDeclaredOn(Class<?> clazz, List<DynamicMAttribute> attributes)
  {
    for (Method method : clazz.getDeclaredMethods())
    {
      MAttribute attribute = method.getAnnotation(MAttribute.class);
      if (attribute != null)
      {
        DynamicMAttribute dynamicMAttribute = createAttribute(method, attribute);
        attributes.add(dynamicMAttribute);
      }
      MSizeAttribute sizeAttribute = method.getAnnotation(MSizeAttribute.class);
      if (sizeAttribute != null)
      {
        DynamicMAttribute fieldAttribute = createAttribute(method, sizeAttribute);
        attributes.add(fieldAttribute);
      }
      MInclude include = method.getAnnotation(MInclude.class);
      if (include != null)
      {
        Class<?> includedType = MInternalUtils.getIncludedType(method.getReturnType(), include);
        MethodBasedValueAccessor includedValueAccesssor = new MethodBasedValueAccessor(manager, targetAccessor, method);
        MAttributeCreator creator = new MAttributeCreator(manager, includedType, includedValueAccesssor);
        attributes.addAll(creator.createAttributesForAnnotations());
      }        
    }
  }

  private void addFieldBasedAttributes(List<DynamicMAttribute> attribs)
  {
    for (Class<?> clazz : getClassesToAnalyze())
    {
      addFieldBasedAttributeDeclaredOn(clazz, attribs);
    }
  }

  private void addFieldBasedAttributeDeclaredOn(Class<?> clazz, List<DynamicMAttribute> attributes)
  {
    for (Field field : clazz.getDeclaredFields())
    {
      MAttribute attribute = field.getAnnotation(MAttribute.class);
      if (attribute != null)
      {        
        DynamicMAttribute fieldAttribute = createAttribute(field, attribute);
        attributes.add(fieldAttribute);
      }
      MSizeAttribute sizeAttribute = field.getAnnotation(MSizeAttribute.class);
      if (sizeAttribute != null)
      {
        DynamicMAttribute fieldAttribute = createAttribute(field, sizeAttribute);
        attributes.add(fieldAttribute);
      }
      MInclude include = field.getAnnotation(MInclude.class);
      if (include != null)
      {
        Class<?> includedType = MInternalUtils.getIncludedType(field.getType(), include);
        FieldBasedValueAccessor includedValueAccessor = new FieldBasedValueAccessor(targetAccessor, field);
        MAttributeCreator creator = new MAttributeCreator(manager, includedType, includedValueAccessor);
        attributes.addAll(creator.createAttributesForAnnotations());
      }
    }
  }

  private DynamicMAttribute createAttribute(Field field, MSizeAttribute sizeAttribute)
  {
    OpenMBeanAttributeInfo mBeanInfo = createMBeanInfo(field, sizeAttribute);    
    
    AbstractValueAccessor fieldValueAccessor = new FieldBasedValueAccessor(targetAccessor, field);
    AbstractValueAccessor sizeValueAccessor = createSizeValueAccessor(field.getType(), fieldValueAccessor);
    return createAttribute(mBeanInfo, sizeValueAccessor);
  }
    
  private AbstractValueAccessor createSizeValueAccessor(Class<?> type,
          AbstractValueAccessor fieldValueAccessor)
  {
    if (Collection.class.isAssignableFrom(type))
    {
      return new CollectionSizeValueAccessor(fieldValueAccessor);
    }
    else if (Map.class.isAssignableFrom(type))
    {
      return new MapSizeValueAccessor(fieldValueAccessor);
    }
    else if (String.class.isAssignableFrom(type))
    {
      return new StringSizeValueAccessor(fieldValueAccessor);
    }
    throw new IllegalArgumentException("Annotation @"+MSizeAttribute.class.getSimpleName()+" not allowed on a field or method with type "+type);
  }

  private DynamicMAttribute createAttribute(Field field, MAttribute attribute)
  {    
    OpenMBeanAttributeInfo mBeanInfo = createMBeanInfo(field, attribute);    
    Type managedType = MInternalUtils.getManagedTyped(field.getGenericType(), attribute.type());
    AbstractValueConverter valueConverter = manager.getValueConverter(managedType);
    AbstractValueAccessor valueAccessor = new FieldBasedValueAccessor(targetAccessor, valueConverter, field);
    return createAttribute(mBeanInfo, valueAccessor);
  }

  private DynamicMAttribute createAttribute(OpenMBeanAttributeInfo mBeanInfo,
          AbstractValueAccessor valueAccessor)
  {
    Instruction nameInstruction = Instruction.parseInstruction(manager, mBeanClass, mBeanInfo.getName());
    Instruction descriptionInstruction = Instruction.parseInstruction(manager, mBeanClass, mBeanInfo.getDescription());
    return new DynamicMAttribute(valueAccessor, targetAccessor, mBeanInfo, nameInstruction, descriptionInstruction);
  }
  
  private DynamicMAttribute createAttribute(Method method, MSizeAttribute sizeAttribute)
  {
    OpenMBeanAttributeInfo mBeanInfo = createMBeanInfo(method, sizeAttribute);    
    AbstractValueAccessor methodValueAccessor = new MethodBasedValueAccessor(manager, targetAccessor, method);
    AbstractValueAccessor collectionSizeValueAccessor = createSizeValueAccessor(method.getReturnType(), methodValueAccessor);
    return createAttribute(mBeanInfo, collectionSizeValueAccessor);
  }
  
  private DynamicMAttribute createAttribute(Method method, MAttribute attribute)
  {
    OpenMBeanAttributeInfo mBeanInfo = createMBeanInfo(method, attribute);    
    AbstractValueAccessor valueAccessor = createValueAccessor(method, mBeanInfo, attribute);
    return createAttribute(mBeanInfo, valueAccessor);
  }
  
  private AbstractValueAccessor createValueAccessor(Method getterMethod, OpenMBeanAttributeInfo mBeanInfo, MAttribute attribute)
  {
    Method setterMethod = null;
    Type managedTyped = MInternalUtils.getManagedTyped(getterMethod.getGenericReturnType(), attribute.type());
    if (mBeanInfo.isWritable())
    {
      setterMethod = evaluateSetMethod(getterMethod, mBeanInfo);
      return new MethodBasedValueAccessor(manager, targetAccessor, manager.getValueConverter(managedTyped), getterMethod, setterMethod);
    }
    else
    {
      return new MethodBasedValueAccessor(manager, targetAccessor, manager.getValueConverter(managedTyped), getterMethod);
    }
  }

  private static Method evaluateSetMethod(Method getterMethod, OpenMBeanAttributeInfo mBeanInfo)
  {
    Class<?> targetClass = getterMethod.getDeclaringClass();
    String methodName = "set"+MInternalUtils.getAttributeNameFromMethod(getterMethod);
    try
    {
      return targetClass.getMethod(methodName, getterMethod.getReturnType());
    }
    catch (NoSuchMethodException ex)
    {
      try
      {
        return targetClass.getDeclaredMethod(methodName,  getterMethod.getReturnType());
      }
      catch(NoSuchMethodException ex2)
      {
        throw new IllegalArgumentException("Method '"+methodName+"(...)' must be available to set attribute '"+ mBeanInfo.getName()+"' on class '"+targetClass.getName()+"'");
      }
    }
  }

  private OpenMBeanAttributeInfo createMBeanInfo(Field field, MAttribute attribute)
  {
    Type managedType = MInternalUtils.getManagedTyped(field.getGenericType(), attribute.type());
    return createMBeanInfo(field, attribute.name(), attribute.description(), manager.toOpenType(managedType), attribute.isWritable());
  }
  
  private OpenMBeanAttributeInfo createMBeanInfo(Method method, MSizeAttribute sizeAttribute)
  {
    return createMBeanInfo(method, sizeAttribute.name(), sizeAttribute.description(), SimpleType.INTEGER, false, false); 
  }

  private OpenMBeanAttributeInfo createMBeanInfo(Field field, MSizeAttribute sizeAttribute)
  {
    return createMBeanInfo(field, sizeAttribute.name(), sizeAttribute.description(), SimpleType.INTEGER, false);
  }
  
  private OpenMBeanAttributeInfo createMBeanInfo(Field field, String name, String description, OpenType<?> type, boolean isWritable)
  {
    name = MInternalUtils.getAttributeName(field, name);
    description = MInternalUtils.getDescription(description, name);
    return new OpenMBeanAttributeInfoSupport(name, description, type, true, isWritable, false);
  }
  
  private OpenMBeanAttributeInfo createMBeanInfo(Method method, MAttribute attribute)
  {
    return createMBeanInfo(method, attribute, MInternalUtils.getManagedTyped(method.getGenericReturnType(), attribute.type()));
  }

  private OpenMBeanAttributeInfo createMBeanInfo(Method method, MAttribute attribute, Type attributeType)
  {
    return createMBeanInfo(method, attribute.name(), attribute.description(), manager.toOpenType(attributeType), attribute.isWritable(), isIsMethod(method));
  }

  private OpenMBeanAttributeInfo createMBeanInfo(Method method, String name, String description, OpenType<?> attributeType, boolean isWritable, boolean isIsMethod)
  {
    name = MInternalUtils.getAttributeName(method, name);
    description = MInternalUtils.getDescription(description, name);
    return new OpenMBeanAttributeInfoSupport(name, description, attributeType, true, isWritable, isIsMethod);
  }

  private static boolean isIsMethod(Method method)
  {
    return method.getName().startsWith("is") && method.getReturnType().equals(Boolean.TYPE);
  }
}

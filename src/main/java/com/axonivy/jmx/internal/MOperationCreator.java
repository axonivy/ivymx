package com.axonivy.jmx.internal;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.axonivy.jmx.MInclude;
import com.axonivy.jmx.MOperation;

/**
 * Analyzes a given class an creates {@link MethodBasedMOperation} for every method that is annotated with a {@link MOperation}.
 * @author rwei
 * @since 01.07.2013
 */
class MOperationCreator extends MCreator
{
  private MOperationCreator(MBeanManager manager, Class<?> mBeanClass)
  {
    super(manager, mBeanClass);
  }

  private MOperationCreator(MBeanManager manager, Class<?> mBeanClass, AbstractValueAccessor targetResolver)
  {
    super(manager, mBeanClass, targetResolver);
  }

  static List<MethodBasedMOperation> create(MBeanManager manager, Class<?> mBeanClass)
  {
    return new MOperationCreator(manager, mBeanClass).createOperationsForAnnotations();
  }

  private List<MethodBasedMOperation> createOperationsForAnnotations()
  {
    List<MethodBasedMOperation> operations = new ArrayList<MethodBasedMOperation>();
    addMethodBasedOperations(operations);
    return operations;
  }

  private void addMethodBasedOperations(List<MethodBasedMOperation> operations)
  {
    for (Class<?> clazz : getClassesToAnalyze())
    {
      addMethodBasedOperationsDeclaredOn(clazz, operations);
      addMethodBasedOperationsDeclaredOnIncludedFields(clazz, operations);
    }
  }

  private void addMethodBasedOperationsDeclaredOn(Class<?> clazz, List<MethodBasedMOperation> operations)
  {
    for (Method method : MInternalUtils.getNonSyntheticDeclaredMethods(clazz))
    {
      MOperation operation = method.getAnnotation(MOperation.class);
      if (operation != null)
      {
        MethodBasedMOperation methodOperation = new MethodBasedMOperation(manager, targetAccessor, method, operation);
        operations.add(methodOperation);
      }
      MInclude include = method.getAnnotation(MInclude.class);
      if (include != null)
      {
        MethodBasedValueAccessor includedValueAccessor = new MethodBasedValueAccessor(manager, targetAccessor, method);
        Class<?> includedType = MInternalUtils.getIncludedType(method.getReturnType(), include);
        MOperationCreator creator = new MOperationCreator(manager, includedType, includedValueAccessor);
        operations.addAll(creator.createOperationsForAnnotations());
      }        
    }
  }
  
  private void addMethodBasedOperationsDeclaredOnIncludedFields(Class<?> clazz,
          List<MethodBasedMOperation> operations)
  {
    for (Field field : clazz.getDeclaredFields())
    {
      MInclude include = field.getAnnotation(MInclude.class);
      if (include != null)
      {
        Class<?> includedType = MInternalUtils.getIncludedType(field.getType(), include);
        FieldBasedValueAccessor includedValueAccessor = new FieldBasedValueAccessor(targetAccessor, field);
        MOperationCreator creator = new MOperationCreator(manager, includedType, includedValueAccessor);
        operations.addAll(creator.createOperationsForAnnotations());
      }        
    }
  }
}

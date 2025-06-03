package com.axonivy.jmx.internal;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.axonivy.jmx.MCompositionReference;
import com.axonivy.jmx.MInclude;

/**
 * Analyzes a given class an creates {@link MCompositionReferenceInfo} for every field or method that is annotated with a {@link MCompositionReference}.
 * @author rwei
 * @since 01.07.2013
 */
class MCompositionReferenceCreator extends MCreator {
  private MCompositionReferenceCreator(MBeanManager manager, Class<?> mBeanClass) {
    super(manager, mBeanClass);
  }

  private MCompositionReferenceCreator(MBeanManager manager, Class<?> mBeanClass, AbstractValueAccessor targetAccessor) {
    super(manager, mBeanClass, targetAccessor);
  }

  static List<MCompositionReferenceInfo> create(MBeanManager manager, Class<?> mBeanClass) {
    return new MCompositionReferenceCreator(manager, mBeanClass).createInfosForAnnotations();
  }

  private List<MCompositionReferenceInfo> createInfosForAnnotations() {
    List<MCompositionReferenceInfo> infos = new ArrayList<MCompositionReferenceInfo>();
    addFieldBasedCompositionReferenceInfos(infos);
    addMethodBasedCompositionReferenceInfos(infos);
    return infos;
  }

  private void addMethodBasedCompositionReferenceInfos(List<MCompositionReferenceInfo> infos) {
    for (Class<?> clazz : getClassesToAnalyze()) {
      addMethodBasedCompositionReferenceInfosDeclaredOn(clazz, infos);
    }
  }

  private void addMethodBasedCompositionReferenceInfosDeclaredOn(Class<?> clazz, List<MCompositionReferenceInfo> infos) {
    for (Method method : MInternalUtils.getNonSyntheticDeclaredMethods(clazz)) {
      MCompositionReference annotation = method.getAnnotation(MCompositionReference.class);
      if (annotation != null) {
        checkTypeCompatibility(method.getReturnType());
        infos.add(new MCompositionReferenceInfo(annotation, new MethodBasedValueAccessor(manager, targetAccessor, method)));
      }
      MInclude include = method.getAnnotation(MInclude.class);
      if (include != null) {
        Class<?> includedType = MInternalUtils.getIncludedType(method.getReturnType(), include);
        MethodBasedValueAccessor includedValueAccesssor = new MethodBasedValueAccessor(manager, targetAccessor, method);
        MCompositionReferenceCreator creator = new MCompositionReferenceCreator(manager, includedType, includedValueAccesssor);
        infos.addAll(creator.createInfosForAnnotations());
      }
    }
  }

  private void addFieldBasedCompositionReferenceInfos(List<MCompositionReferenceInfo> infos) {
    for (Class<?> clazz : getClassesToAnalyze()) {
      addFieldBasedCompositionReferenceInfosDeclaredOn(clazz, infos);
    }
  }

  private void addFieldBasedCompositionReferenceInfosDeclaredOn(Class<?> clazz, List<MCompositionReferenceInfo> infos) {
    for (Field field : clazz.getDeclaredFields()) {
      MCompositionReference annotation = field.getAnnotation(MCompositionReference.class);
      if (annotation != null) {
        checkTypeCompatibility(field.getType());
        infos.add(new MCompositionReferenceInfo(annotation, new FieldBasedValueAccessor(targetAccessor, field)));
      }
      MInclude include = field.getAnnotation(MInclude.class);
      if (include != null) {
        Class<?> includedType = MInternalUtils.getIncludedType(field.getType(), include);
        FieldBasedValueAccessor includedValueAccessor = new FieldBasedValueAccessor(targetAccessor, field);
        MCompositionReferenceCreator creator = new MCompositionReferenceCreator(manager, includedType, includedValueAccessor);
        infos.addAll(creator.createInfosForAnnotations());
      }
    }
  }

  private void checkTypeCompatibility(Class<?> type) {
    if (Void.TYPE.equals(type) || Void.class.equals(type)) {
      throw new IllegalArgumentException("You must not use @" + MCompositionReference.class.getSimpleName() + " on a method with no return value.");
    }
    if (type.isPrimitive()) {
      throw new IllegalArgumentException("You must not use @" + MCompositionReference.class.getSimpleName() + " on a field with a primitive type or a method returning a primitive type.");
    }
  }
}

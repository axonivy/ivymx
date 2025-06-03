package com.axonivy.jmx.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.axonivy.jmx.MBean;

/**
 * Caches all relevant information about a class that is annotated with {@link MBean}
 * @author rwei
 * @since 01.07.2013
 */
class MBeanType {
  private MBean annotation;
  private List<DynamicMAttribute> attributes;
  private List<MethodBasedMOperation> operations;
  private MBeanManager manager;
  private Class<?> mBeanClass;
  private Instruction descriptionInstruction;
  private NameInstruction nameInstruction;
  private List<MCompositionReferenceInfo> compositionReferenceInfos;

  MBeanType(MBeanManager manager, Class<?> mBeanClass) {
    this.manager = manager;
    this.mBeanClass = mBeanClass;
    annotation = mBeanClass.getAnnotation(MBean.class);
    if (annotation == null) {
      throw new IllegalArgumentException("Bean '" + mBeanClass + "' must contain a @MBean annotation");
    }
  }

  private void evaluateMBeanInfo(Object mBean, MBeanInstanceInfo mBeanInstanceInfo) {
    String name = mBeanClass.getName();
    String description = evaluateDescription(mBean);
    mBeanInstanceInfo.setMBeanInfo(name, description);
  }

  private String evaluateDescription(Object mBean) {
    if (descriptionInstruction == null) {
      descriptionInstruction = Instruction.parseInstruction(manager, mBeanClass, annotation.description());
    }
    return descriptionInstruction.execute(mBean);
  }

  String evaluateName(Object mBean) {
    if (nameInstruction == null) {
      nameInstruction = NameInstruction.parseInstruction(manager, mBeanClass, annotation.value());
    }
    return nameInstruction.execute(mBean);
  }

  private void evaluateOperations(Object mBean, MBeanInstanceInfo mBeanInstanceInfo) {
    if (operations == null) {
      operations = MOperationCreator.create(manager, mBeanClass);
    }
    for (MethodBasedMOperation operation : operations) {
      operation.evaluate(mBean, mBeanInstanceInfo);
    }
  }

  private void evaluateAttributes(Object mBean, MBeanInstanceInfo mBeanInstanceInfo) {
    if (attributes == null) {
      attributes = MAttributeCreator.create(manager, mBeanClass);
    }
    for (DynamicMAttribute attribute : attributes) {
      attribute.evaluate(mBean, mBeanInstanceInfo);
    }
  }

  boolean makeUniqueName() {
    return annotation.makeNameUnique();
  }

  List<MCompositionReferenceValue> getCompositionReferences(Object parentMBean) {
    List<MCompositionReferenceInfo> infos = getCompositionReferenceInfos();
    if (infos.isEmpty()) {
      return Collections.emptyList();
    }
    List<MCompositionReferenceValue> compositionReferenceValues = new ArrayList<MCompositionReferenceValue>(infos.size());
    for (MCompositionReferenceInfo compositionReferenceInfo : infos) {
      MCompositionReferenceValue compositionReferenceValue = compositionReferenceInfo.getValue(parentMBean);
      if (compositionReferenceValue != null) {
        compositionReferenceValues.add(compositionReferenceValue);
      }
    }
    return compositionReferenceValues;
  }

  private List<MCompositionReferenceInfo> getCompositionReferenceInfos() {
    if (compositionReferenceInfos == null) {
      compositionReferenceInfos = MCompositionReferenceCreator.create(manager, mBeanClass);
    }
    return compositionReferenceInfos;
  }

  MBeanInstanceInfo getMBeanInstanceInfo(Object originalObject) {
    MBeanInstanceInfo mBeanInstanceInfo = new MBeanInstanceInfo();

    evaluateAttributes(originalObject, mBeanInstanceInfo);
    evaluateOperations(originalObject, mBeanInstanceInfo);
    evaluateMBeanInfo(originalObject, mBeanInstanceInfo);

    return mBeanInstanceInfo;
  }
}

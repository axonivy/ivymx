package com.axonivy.jmx.internal;

import javax.management.MBeanException;
import javax.management.openmbean.OpenMBeanAttributeInfo;
import javax.management.openmbean.OpenMBeanAttributeInfoSupport;

import com.axonivy.jmx.MException;

/**
 * A managed attribute. Can read and write an attribute. Gives {@link OpenMBeanAttributeInfo information} about the attribute.
 * @author rwei
 * @since 01.07.2013
 */
class DynamicMAttribute {
  private OpenMBeanAttributeInfo mBeanInfo;
  private AbstractValueAccessor valueAccessor;
  private Instruction nameInstruction;
  private Instruction descriptionInstruction;
  private AbstractValueAccessor targetAccessor;

  DynamicMAttribute(AbstractValueAccessor valueAccessor, AbstractValueAccessor targetAccessor, OpenMBeanAttributeInfo mBeanInfo, Instruction nameInstruction, Instruction descriptionInstruction) {
    this.valueAccessor = valueAccessor;
    this.targetAccessor = targetAccessor;
    this.mBeanInfo = mBeanInfo;
    this.nameInstruction = nameInstruction;
    this.descriptionInstruction = descriptionInstruction;

  }

  private OpenMBeanAttributeInfo evaluateInfo(Object mBean) {
    return new OpenMBeanAttributeInfoSupport(
        evaluateName(mBean),
        evaluateDescription(mBean),
        mBeanInfo.getOpenType(),
        mBeanInfo.isReadable(),
        mBeanInfo.isWritable(),
        mBeanInfo.isIs());
  }

  private String evaluateDescription(Object mBean) {
    try {
      Object target = targetAccessor.getValue(mBean);
      return descriptionInstruction.execute(target);
    } catch (MBeanException ex) {
      throw new MException(ex);
    }
  }

  private String evaluateName(Object mBean) {
    try {
      Object target = targetAccessor.getValue(mBean);
      return nameInstruction.execute(target);
    } catch (MBeanException ex) {
      throw new MException(ex);
    }
  }

  Object getValue(Object beanInstance) throws MBeanException {
    return valueAccessor.getValue(beanInstance);
  }

  void setValue(Object beanInstance, Object openDataValue) throws MBeanException {
    valueAccessor.setValue(beanInstance, openDataValue);
  }

  void evaluate(Object mBean, MBeanInstanceInfo mBeanInstanceInfo) {
    mBeanInstanceInfo.addAttribute(this, evaluateInfo(mBean));
  }

  boolean isWritable() {
    return mBeanInfo.isWritable();
  }

  @Override
  public String toString() {
    return "MAttribute " + valueAccessor.getAccessPath();
  }

}

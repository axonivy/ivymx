package io.github.axonivy.jmx.internal;

import javax.management.MBeanException;

import io.github.axonivy.jmx.MCompositionReference;
import io.github.axonivy.jmx.MException;

class MCompositionReferenceInfo {
  private MCompositionReference annotation;
  private AbstractValueAccessor valueAccessor;

  MCompositionReferenceInfo(MCompositionReference annotation, AbstractValueAccessor valueAccessor) {
    this.annotation = annotation;
    this.valueAccessor = valueAccessor;
  }

  MCompositionReferenceValue getValue(Object parentMBean) {
    try {
      Object referencedMBean = valueAccessor.getValue(parentMBean);
      if (referencedMBean == null) {
        return null;
      }
      return new MCompositionReferenceValue(annotation, referencedMBean);
    } catch (MBeanException ex) {
      throw new MException(ex);
    }
  }

}

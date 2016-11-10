package com.axonivy.jmx.internal;

import com.axonivy.jmx.MCompositionReference;

class MCompositionReferenceValue
{
  private MCompositionReference annotation;
  private Object referencedMBean;

  MCompositionReferenceValue(MCompositionReference annotation, Object referencedMBean)
  {
    this.annotation = annotation;
    this.referencedMBean = referencedMBean;
  }
  
  Object getReferencedMBean()
  {
    return referencedMBean;
  }
  
  boolean isConcatName()
  {
    return annotation.concatName();
  }
}

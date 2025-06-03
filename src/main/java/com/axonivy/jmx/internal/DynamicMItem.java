package com.axonivy.jmx.internal;

import javax.management.MBeanException;
import javax.management.openmbean.OpenType;

/**
 * An item of a composite data object. Can read the value of the item. Gives information (name, description, type) about the item.
 * @author rwei
 * @since 01.07.2013
 */
class DynamicMItem {
  private String name;
  private String description;
  private OpenType<?> openType;
  private AbstractValueAccessor valueAccessor;

  DynamicMItem(String name, String description, OpenType<?> openType, AbstractValueAccessor valueAccessor) {
    this.name = name;
    this.description = description;
    this.openType = openType;
    this.valueAccessor = valueAccessor;
  }

  String getName() {
    return name;
  }

  String getDescription() {
    return description;
  }

  OpenType<?> getOpenType() {
    return openType;
  }

  Object getValue(Object beanInstance) throws MBeanException {
    return valueAccessor.getValue(beanInstance);
  }
}

package com.axonivy.jmx.internal;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.DynamicMBean;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.axonivy.jmx.MException;

/**
 * Proxy that implements a {@link DynamicMBean} but forwards all jmx requests (read/write attribute, execute operations) to the {@link #originalObject}
 * @author rwei
 * @since 01.07.2013
 */
class MBeanProxy implements DynamicMBean {
  private static final Logger LOGGER = LoggerFactory.getLogger(MBeanProxy.class);
  private Object originalObject;
  private ObjectName objectName;
  private ObjectName uniqueObjectName;
  private ObjectName parentName;
  private MBeanType mBeanType;
  private int uniqueId = 1;
  private List<MCompositionReferenceValue> compositionReferences;
  private MBeanInstanceInfo mBeanInstanceInfo;
  private AtomicBoolean registered = new AtomicBoolean();

  MBeanProxy(MBeanType mBeanType, Object originalObject, ObjectName parentName) {
    this.mBeanType = mBeanType;
    this.originalObject = originalObject;
    this.parentName = parentName;
  }

  ObjectName getObjectName() {
    if (uniqueObjectName != null) {
      return uniqueObjectName;
    }
    return getSpecifiedObjectName();
  }

  private ObjectName getSpecifiedObjectName() {
    if (objectName == null) {
      String objectNameStr = mBeanType.evaluateName(originalObject);
      if (parentName != null) {
        objectNameStr = parentName.toString() + "," + objectNameStr;
      }
      try {
        objectName = new ObjectName(objectNameStr);
      } catch (MalformedObjectNameException ex) {
        throw new IllegalArgumentException("Object name of MBean '" + objectNameStr + "'is malformed", ex);
      }
    }
    return objectName;
  }

  boolean makeUniqueName() {
    return mBeanType.makeUniqueName();
  }

  ObjectName getNextPossibleUniqueObjectName() {
    try {
      uniqueObjectName = new ObjectName(getSpecifiedObjectName().toString() + " @" + uniqueId++);
      return uniqueObjectName;
    } catch (MalformedObjectNameException ex) {
      throw new MException(ex);
    }
  }

  List<MCompositionReferenceValue> getCompositionReferences() {
    if (compositionReferences == null) {
      compositionReferences = mBeanType.getCompositionReferences(originalObject);
    }
    return compositionReferences;
  }

  /**
   * @see javax.management.DynamicMBean#getAttribute(java.lang.String)
   */
  @Override
  public Object getAttribute(String attribute) throws AttributeNotFoundException, MBeanException,
      ReflectionException {
    DynamicMAttribute dynamicAttribute = getMBeanInstanceInfo().getAttribute(attribute);
    return dynamicAttribute.getValue(originalObject);
  }

  /**
   * @see javax.management.DynamicMBean#setAttribute(javax.management.Attribute)
   */
  @Override
  public void setAttribute(Attribute attribute) throws AttributeNotFoundException,
      InvalidAttributeValueException, MBeanException, ReflectionException {
    DynamicMAttribute dynamicAttribute = getMBeanInstanceInfo().getAttribute(attribute.getName());
    if (!dynamicAttribute.isWritable()) {
      throw new MBeanException(new IllegalAccessException("Jmx attribute '" + attribute.getName() + "' can not be set because it is not writable"));
    }
    if (LOGGER.isInfoEnabled()) {
      LOGGER.info("Attribute ''{0}'' of MBean ''{1}'' set to new value ''{2}''.", attribute.getName(), objectName, attribute.getValue());
    }
    dynamicAttribute.setValue(originalObject, attribute.getValue());
  }

  /**
   * @see javax.management.DynamicMBean#getAttributes(java.lang.String[])
   */
  @Override
  public AttributeList getAttributes(String[] attributeNames) {
    AttributeList readAttributes = new AttributeList();
    for (String name : attributeNames) {
      Object value;
      try {
        value = getAttribute(name);
        readAttributes.add(new Attribute(name, value));
      } catch (Exception ex) {
        LOGGER.warn("Could not read attribute with name '" + name + "'", ex);
      }
    }
    return readAttributes;
  }

  /**
   * @see javax.management.DynamicMBean#setAttributes(javax.management.AttributeList)
   */
  @Override
  public AttributeList setAttributes(AttributeList attributes) {
    AttributeList writtenAttributes = new AttributeList();
    for (Attribute attribute : attributes.asList()) {
      try {
        setAttribute(attribute);
        writtenAttributes.add(attribute);
      } catch (Exception ex) {
        LOGGER.warn("Could not set attribute with name '" + attribute.getName() + "' to value '" + attribute.getValue() + "'", ex);
      }
    }
    return writtenAttributes;
  }

  /**
   * @see javax.management.DynamicMBean#invoke(java.lang.String, java.lang.Object[], java.lang.String[])
   */
  @Override
  public Object invoke(String actionName, Object[] params, String[] signature) throws MBeanException,
      ReflectionException {
    MethodBasedMOperation operation = getMBeanInstanceInfo().getOperation(actionName, signature);
    if (LOGGER.isInfoEnabled()) {
      LOGGER.info("Operation ''{0}'' invoked on MBean ''{1}''", MethodBasedMOperation.buildSignature(actionName, signature), objectName);
    }
    return operation.invoke(originalObject, params);
  }

  /**
   * @see javax.management.DynamicMBean#getMBeanInfo()
   */
  @Override
  public MBeanInfo getMBeanInfo() {
    return getMBeanInstanceInfo().getMBeanInfo();
  }

  private MBeanInstanceInfo getMBeanInstanceInfo() {
    if (mBeanInstanceInfo == null) {
      mBeanInstanceInfo = mBeanType.getMBeanInstanceInfo(originalObject);
    }
    return mBeanInstanceInfo;
  }

  public boolean register() {
    return registered.compareAndSet(false, true);
  }
}

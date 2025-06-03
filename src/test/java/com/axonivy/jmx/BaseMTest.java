package com.axonivy.jmx;

import static org.assertj.core.api.Assertions.assertThat;

import javax.management.Attribute;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

public class BaseMTest<T> {
  protected T testBean;
  protected ObjectName testBeanObjectName;

  public BaseMTest(T testBean, String testBeanObjectName) throws MalformedObjectNameException {
    this.testBean = testBean;
    this.testBeanObjectName = new ObjectName(testBeanObjectName);
  }

  @After
  public void after() {
    MBeans.unregisterAllMBeans();
  }

  @Before
  public void before() {
    MBeans.registerMBeanFor(testBean);
  }

  public Object getAttribute(String attributeName) throws AttributeNotFoundException, MBeanException, InstanceNotFoundException,
      ReflectionException {
    return MBeans.getMBeanServer().getAttribute(testBeanObjectName, attributeName);
  }

  public void setAttribute(String attributeName, Object value) throws InstanceNotFoundException, InvalidAttributeValueException,
      ReflectionException, MBeanException, AttributeNotFoundException {
    MBeans.getMBeanServer().setAttribute(testBeanObjectName, new Attribute(attributeName, value));
  }

  public Object invokeOperation(String operationName, Object[] argValues, String[] argNames) throws InstanceNotFoundException, ReflectionException, MBeanException {
    return MBeans.getMBeanServer().invoke(testBeanObjectName, operationName, argValues, argNames);
  }

  public Object invokeOperation(String operationName) throws ReflectionException, InstanceNotFoundException, MBeanException {
    return invokeOperation(operationName, ArrayUtils.EMPTY_OBJECT_ARRAY, ArrayUtils.EMPTY_STRING_ARRAY);
  }

  public MBeanAttributeInfo getAttributeInfo(String attributeName) throws IntrospectionException, InstanceNotFoundException, ReflectionException {
    for (MBeanAttributeInfo info : getMBeanInfoFromBeanServer().getAttributes()) {
      if (attributeName.equals(info.getName())) {
        return info;
      }
    }
    Assert.fail("Attribute info for attribute '" + attributeName + "' not found");
    return null;
  }

  public MBeanOperationInfo getOperationInfo(String operationName) throws IntrospectionException, InstanceNotFoundException, ReflectionException {
    for (MBeanOperationInfo info : getMBeanInfoFromBeanServer().getOperations()) {
      if (operationName.equals(info.getName())) {
        return info;
      }
    }
    Assert.fail("Operation info for operation '" + operationName + "' not found");
    return null;
  }

  public MBeanInfo getMBeanInfoFromBeanServer() throws ReflectionException, IntrospectionException,
      InstanceNotFoundException {
    return MBeans.getMBeanServer().getMBeanInfo(testBeanObjectName);
  }

  public ObjectInstance getTestBeanFromBeanServer() throws InstanceNotFoundException {
    return getBeanFromBeanServer(testBeanObjectName);
  }

  protected ObjectInstance getBeanOrNullFromBeanServer(String objectName) throws MalformedObjectNameException, NullPointerException {
    try {
      return getBeanFromBeanServer(new ObjectName(objectName));
    } catch (InstanceNotFoundException ex) {
      return null;
    }
  }

  private ObjectInstance getBeanFromBeanServer(ObjectName objectName) throws InstanceNotFoundException {
    return MBeans.getMBeanServer().getObjectInstance(objectName);
  }

  public void assertNotRegistered() {
    assertThat(MBeans.getMBeanServer().isRegistered(testBeanObjectName)).isEqualTo(false);
  }

  public void assertRegistered() {
    assertThat(MBeans.getMBeanServer().isRegistered(testBeanObjectName)).isEqualTo(true);
  }

}

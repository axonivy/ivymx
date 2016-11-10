package com.axonivy.jmx.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.management.AttributeNotFoundException;
import javax.management.MBeanInfo;
import javax.management.ReflectionException;
import javax.management.openmbean.OpenMBeanAttributeInfo;
import javax.management.openmbean.OpenMBeanInfoSupport;
import javax.management.openmbean.OpenMBeanOperationInfo;

class MBeanInstanceInfo
{
  private MBeanInfo mBeanInfo;  
  private Map<String, DynamicMAttribute> attributes = new HashMap<String, DynamicMAttribute>();  
  private Map<String, MethodBasedMOperation> operations = new HashMap<String, MethodBasedMOperation>();
  private List<OpenMBeanAttributeInfo> attributeInfos = new ArrayList<OpenMBeanAttributeInfo>();
  private List<OpenMBeanOperationInfo> operationInfos = new ArrayList<OpenMBeanOperationInfo>();
  
  MBeanInfo getMBeanInfo()
  {
    return mBeanInfo;
  }
  
  DynamicMAttribute getAttribute(String attribute) throws AttributeNotFoundException
  {
    DynamicMAttribute dynamicAttribute = attributes.get(attribute);
    if (dynamicAttribute == null)
    {
      throw new AttributeNotFoundException("There is no '"+attribute+"' attribute available");
    }
    return dynamicAttribute;
  }
  
  MethodBasedMOperation getOperation(String actionName, String[] signature) throws ReflectionException
  {
    String methodSignature = MethodBasedMOperation.buildSignature(actionName, signature);
    MethodBasedMOperation operation = operations.get(methodSignature);
    if (operation == null)
    {
      throw new ReflectionException(new IllegalArgumentException("No operation with signature '"+methodSignature+"' available"));
    }
    return operation;
  }

  void addAttribute(DynamicMAttribute attribute, OpenMBeanAttributeInfo attributeInfo)
  {
    String attributeName = attributeInfo.getName();
    if (attributes.containsKey(attributeName))
    {
      throw new IllegalArgumentException("Attribute "+attributeName +" already exists");
    }
    attributes.put(attributeName, attribute);
    attributeInfos.add(attributeInfo);
  }

  void addOperation(MethodBasedMOperation operation, String signature, OpenMBeanOperationInfo operationInfo)
  {
    if (operations.containsKey(signature))
    {
      throw new IllegalArgumentException("Operation with signature "+signature+" already exists");
    }
    operations.put(signature, operation);
    operationInfos.add(operationInfo);
  }

  void setMBeanInfo(String name, String description)
  {
    this.mBeanInfo = new OpenMBeanInfoSupport(name, description, getAttributeInfos(), null, getOperationInfos(), null); 
  }

  private OpenMBeanAttributeInfo[] getAttributeInfos()
  {
    return attributeInfos.toArray(new OpenMBeanAttributeInfo[attributeInfos.size()]);
  }

  private OpenMBeanOperationInfo[] getOperationInfos()
  {
    return operationInfos.toArray(new OpenMBeanOperationInfo[operationInfos.size()]);
  }
}

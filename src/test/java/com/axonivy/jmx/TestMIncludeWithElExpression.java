package com.axonivy.jmx;

import static org.assertj.core.api.Assertions.assertThat;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanException;
import javax.management.MBeanOperationInfo;
import javax.management.MalformedObjectNameException;
import javax.management.ReflectionException;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import com.axonivy.jmx.MOperation.Impact;

public class TestMIncludeWithElExpression extends BaseMTest<TestMIncludeWithElExpression.TestBean> {

  @MBean(value = "Test:type=TestType")
  public static class TestBean {
    @MInclude
    private final Counter errors = new Counter("errors");

    @MInclude
    private final Counter requests = new Counter("requests");
  }

  public static class Counter {
    @MAttribute(name = "#{name}", description = "All #{name} that were occured")
    private int cnt = 0;

    private final String name;

    public Counter(String name) {
      this.name = name;
    }

    public String getName() {
      return name;
    }

    public String getCapitalizedName() {
      return StringUtils.capitalize(name);
    }

    @MOperation(name = "reset#{capitalizedName}", description = "Resets the #{name} counter")
    public void resetCnt() {
      cnt = 0;
    }
  }

  public TestMIncludeWithElExpression() throws MalformedObjectNameException {
    super(new TestBean(), "Test:type=TestType");
  }

  @Test
  public void testErrorsAttributeInfo() throws IntrospectionException, InstanceNotFoundException, ReflectionException {
    MBeanAttributeInfo attributeInfo = getAttributeInfo("errors");
    assertThat(attributeInfo.getName()).isEqualTo("errors");
    assertThat(attributeInfo.getDescription()).isEqualTo("All errors that were occured");
    assertThat(attributeInfo.getType()).isEqualTo("java.lang.Integer");
    assertThat(attributeInfo.isReadable()).isEqualTo(true);
    assertThat(attributeInfo.isWritable()).isEqualTo(false);
    assertThat(attributeInfo.isIs()).isEqualTo(false);
  }

  @Test
  public void testResetErrorsOperationInfo() throws IntrospectionException, InstanceNotFoundException, ReflectionException {
    MBeanOperationInfo operationInfo = getOperationInfo("resetErrors");
    assertThat(operationInfo.getName()).isEqualTo("resetErrors");
    assertThat(operationInfo.getDescription()).isEqualTo("Resets the errors counter");
    assertThat(operationInfo.getImpact()).isEqualTo(Impact.UNKNOWN.toInt());
    assertThat(operationInfo.getReturnType()).isEqualTo(Void.class.getName());
    assertThat(operationInfo.getSignature()).isEmpty();
  }

  @Test
  public void testReadErrorsAttribute() throws AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException {
    testBean.errors.cnt = 45;
    assertThat(getAttribute("errors")).isEqualTo(45);
  }

  @Test
  public void testInvokeResetErrorsOperation() throws InstanceNotFoundException, ReflectionException, MBeanException {
    testBean.errors.cnt = 11;
    invokeOperation("resetErrors");
    assertThat(testBean.errors.cnt).isEqualTo(0);
  }

  @Test
  public void testRequestsAttributeInfo() throws IntrospectionException, InstanceNotFoundException, ReflectionException {
    MBeanAttributeInfo attributeInfo = getAttributeInfo("requests");
    assertThat(attributeInfo.getName()).isEqualTo("requests");
    assertThat(attributeInfo.getDescription()).isEqualTo("All requests that were occured");
    assertThat(attributeInfo.getType()).isEqualTo("java.lang.Integer");
    assertThat(attributeInfo.isReadable()).isEqualTo(true);
    assertThat(attributeInfo.isWritable()).isEqualTo(false);
    assertThat(attributeInfo.isIs()).isEqualTo(false);
  }

  @Test
  public void testResetRequestsOperationInfo() throws IntrospectionException, InstanceNotFoundException, ReflectionException {
    MBeanOperationInfo operationInfo = getOperationInfo("resetRequests");
    assertThat(operationInfo.getName()).isEqualTo("resetRequests");
    assertThat(operationInfo.getDescription()).isEqualTo("Resets the requests counter");
    assertThat(operationInfo.getImpact()).isEqualTo(Impact.UNKNOWN.toInt());
    assertThat(operationInfo.getReturnType()).isEqualTo(Void.class.getName());
    assertThat(operationInfo.getSignature()).isEmpty();
  }

  @Test
  public void testReadRequestsAttribute() throws AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException {
    testBean.requests.cnt = 46;
    assertThat(getAttribute("requests")).isEqualTo(46);
  }

  @Test
  public void testInvokeResetRequestsOperation() throws InstanceNotFoundException, ReflectionException, MBeanException {
    testBean.requests.cnt = 12;
    invokeOperation("resetRequests");
    assertThat(testBean.requests.cnt).isEqualTo(0);
  }

}

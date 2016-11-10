package com.axonivy.jmx;

import static org.assertj.core.api.Assertions.assertThat;

import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.MBeanException;
import javax.management.MBeanOperationInfo;
import javax.management.MalformedObjectNameException;
import javax.management.ReflectionException;

import org.junit.Test;

import com.axonivy.jmx.MBean;
import com.axonivy.jmx.MOperation;
import com.axonivy.jmx.MOperation.Impact;

public class TestMOperation extends BaseMTest<TestMOperation.TestBean>
{
  public interface IBean
  {
    @MOperation(description="Searches a sub string inside an other string", paramDescriptions={"String to search in", "String to search for"})
    public int searchSubString(String string, String substring);
  }
  
  public static class BaseTestBean
  {
    @MOperation(description="Concats two strings", params={"prefix", "suffix"})
    public String concat(String prefix, String suffix)
    {
      return prefix+suffix;
    }
  }
  
  @MBean("Test:type=TestType")
  public static class TestBean extends BaseTestBean implements IBean
  {
    private boolean isRunning;
    
    
    @MOperation(name="doStop", description="Stops the bean", impact=Impact.ACTION)
    public void stop()
    {
      isRunning=false;
    }
    
    @MOperation
    public void start()
    {
      isRunning=true;
    }
    
    @Override
    public int searchSubString(String string, String substring)
    {
      return string.indexOf(substring);
    }
  }

  public TestMOperation() throws MalformedObjectNameException
  {
    super(new TestBean(), "Test:type=TestType");
  }
  
  @Test
  public void testInvokeNoArgsOperations() throws InstanceNotFoundException, MBeanException, ReflectionException 
  {
    assertThat(testBean.isRunning).isEqualTo(false);
    invokeOperation("start");
    assertThat(testBean.isRunning).isEqualTo(true);
    invokeOperation("doStop");
    assertThat(testBean.isRunning).isEqualTo(false);
  }

  @Test 
  public void testInvokeArgsOperation() throws InstanceNotFoundException, ReflectionException, MBeanException
  {
    assertThat(invokeOperation("concat", new Object[]{"Hello", "World"}, new String[]{"java.lang.String", "java.lang.String"})).isEqualTo("HelloWorld");
  }
  
  @Test(expected=ReflectionException.class)
  public void testInvokeArgsOperationWithWrongArgCount() throws InstanceNotFoundException, ReflectionException, MBeanException
  {
    invokeOperation("concat", new Object[]{"Hello"}, new String[]{"java.lang.String"});
  }

  @Test(expected=ReflectionException.class)
  public void testInvokeArgsOperationWithWrongSignature() throws InstanceNotFoundException, ReflectionException, MBeanException
  {
    invokeOperation("concat", new Object[]{"Hello", 123}, new String[]{"java.lang.String", "int"});
  }
  
  @Test(expected=ReflectionException.class)
  public void testInvokeWithWrongOperationName() throws InstanceNotFoundException, ReflectionException, MBeanException
  {
    invokeOperation("blah");
  }
  
  @Test
  public void testOperationInfoStart() throws IntrospectionException, InstanceNotFoundException, ReflectionException
  {
    MBeanOperationInfo operationInfo = getMBeanInfoFromBeanServer().getOperations()[0];
    assertThat(operationInfo.getName()).isEqualTo("start");
    assertThat(operationInfo.getDescription()).isEqualTo("start");
    assertThat(operationInfo.getReturnType()).isEqualTo("java.lang.Void");
    assertThat(operationInfo.getImpact()).isEqualTo(Impact.UNKNOWN.toInt());
    assertThat(operationInfo.getSignature()).isNotNull();
    assertThat(operationInfo.getSignature()).hasSize(0);
  }

  @Test
  public void testOperationInfoDoStop() throws IntrospectionException, InstanceNotFoundException, ReflectionException
  {
    MBeanOperationInfo operationInfo = getMBeanInfoFromBeanServer().getOperations()[1];
    assertThat(operationInfo.getName()).isEqualTo("doStop");
    assertThat(operationInfo.getDescription()).isEqualTo("Stops the bean");
    assertThat(operationInfo.getReturnType()).isEqualTo("java.lang.Void");
    assertThat(operationInfo.getImpact()).isEqualTo(Impact.ACTION.toInt());
    assertThat(operationInfo.getSignature()).isNotNull();
    assertThat(operationInfo.getSignature()).hasSize(0);
  }

  @Test
  public void testOperationInfoConcat() throws IntrospectionException, InstanceNotFoundException, ReflectionException
  {
    MBeanOperationInfo operationInfo = getMBeanInfoFromBeanServer().getOperations()[3];
    assertThat(operationInfo.getName()).isEqualTo("concat");
    assertThat(operationInfo.getDescription()).isEqualTo("Concats two strings");
    assertThat(operationInfo.getReturnType()).isEqualTo("java.lang.String");
    assertThat(operationInfo.getImpact()).isEqualTo(Impact.UNKNOWN.toInt());
    assertThat(operationInfo.getSignature()).isNotNull();
    assertThat(operationInfo.getSignature()).hasSize(2);
    assertThat(operationInfo.getSignature()[0].getName()).isEqualTo("prefix");
    assertThat(operationInfo.getSignature()[0].getType()).isEqualTo("java.lang.String");
    assertThat(operationInfo.getSignature()[0].getDescription()).isEqualTo("prefix");
    assertThat(operationInfo.getSignature()[1].getName()).isEqualTo("suffix");
    assertThat(operationInfo.getSignature()[1].getType()).isEqualTo("java.lang.String");
    assertThat(operationInfo.getSignature()[1].getDescription()).isEqualTo("suffix");
  }

  @Test
  public void testOperationInfoIndexOf() throws IntrospectionException, InstanceNotFoundException, ReflectionException
  {
    MBeanOperationInfo operationInfo = getMBeanInfoFromBeanServer().getOperations()[2];
    assertThat(operationInfo.getName()).isEqualTo("searchSubString");
    assertThat(operationInfo.getDescription()).isEqualTo("Searches a sub string inside an other string");
    assertThat(operationInfo.getReturnType()).isEqualTo("java.lang.Integer");
    assertThat(operationInfo.getImpact()).isEqualTo(Impact.UNKNOWN.toInt());
    assertThat(operationInfo.getSignature()).isNotNull();
    assertThat(operationInfo.getSignature()).hasSize(2);
    assertThat(operationInfo.getSignature()[0].getName()).isEqualTo("arg0");
    assertThat(operationInfo.getSignature()[0].getType()).isEqualTo("java.lang.String");
    assertThat(operationInfo.getSignature()[0].getDescription()).isEqualTo("String to search in");
    assertThat(operationInfo.getSignature()[1].getName()).isEqualTo("arg1");
    assertThat(operationInfo.getSignature()[1].getType()).isEqualTo("java.lang.String");
    assertThat(operationInfo.getSignature()[1].getDescription()).isEqualTo("String to search for");
  }
}

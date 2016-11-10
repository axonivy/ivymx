package com.axonivy.jmx;

import static org.assertj.core.api.Assertions.assertThat;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanException;
import javax.management.MalformedObjectNameException;
import javax.management.ReflectionException;

import org.junit.Test;

import com.axonivy.jmx.MBean;
import com.axonivy.jmx.MSizeAttribute;
import com.axonivy.jmx.TestFieldBasedMAttribute.BaseTestBean;

public class TestMSizeAttributeOnString extends BaseMTest<TestMSizeAttributeOnString.TestBean>
{
  @MBean("Test:type=TestType")
  public static class TestBean extends BaseTestBean
  {
    @MSizeAttribute
    private String name;
    
    @MSizeAttribute(name="lastName", description="last name")
    private String getName()
    {
      return name;
    }
  }

  public TestMSizeAttributeOnString() throws MalformedObjectNameException
  {
    super(new TestBean(), "Test:type=TestType");
  }
  
  @Test
  public void testAttributeInfoNames() throws IntrospectionException, InstanceNotFoundException, ReflectionException 
  {
    MBeanAttributeInfo attributeInfo = getAttributeInfo("name");
    assertThat(attributeInfo.getDescription()).isEqualTo("name");
    assertThat(attributeInfo.getType()).isEqualTo("java.lang.Integer");
    assertThat(attributeInfo.isReadable()).isEqualTo(true);
    assertThat(attributeInfo.isWritable()).isEqualTo(false);
    assertThat(attributeInfo.isIs()).isEqualTo(false);
  }
  
  @Test
  public void testReadAttributeNames() throws AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException
  {
    testBean.name = "Weiss";
    assertThat(getAttribute("name")).isEqualTo(5);
    testBean.name= "Reto Weiss";
    assertThat(getAttribute("name")).isEqualTo(10);
  }

  @Test
  public void testAttributeInfoLastNames() throws IntrospectionException, InstanceNotFoundException, ReflectionException 
  {
    MBeanAttributeInfo attributeInfo = getAttributeInfo("lastName");
    assertThat(attributeInfo.getDescription()).isEqualTo("last name");
    assertThat(attributeInfo.getType()).isEqualTo("java.lang.Integer");
    assertThat(attributeInfo.isReadable()).isEqualTo(true);
    assertThat(attributeInfo.isWritable()).isEqualTo(false);
    assertThat(attributeInfo.isIs()).isEqualTo(false);
  }
  
  @Test
  public void testReadAttributeLastNames() throws AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException
  {
    testBean.name = "Buetler";
    assertThat(getAttribute("lastName")).isEqualTo(7);
    testBean.name = "Bruno Buetler";
    assertThat(getAttribute("lastName")).isEqualTo(13);
  }
  
  
  @Test
  public void testNullValue() throws AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException
  {
    testBean.name=null;
    assertThat(getAttribute("lastName")).isEqualTo(0);
    assertThat(getAttribute("name")).isEqualTo(0);
  }


}

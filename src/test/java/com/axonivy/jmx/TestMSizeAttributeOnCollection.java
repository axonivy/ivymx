package com.axonivy.jmx;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanException;
import javax.management.MalformedObjectNameException;
import javax.management.ReflectionException;

import org.junit.Test;

import com.axonivy.jmx.TestFieldBasedMAttribute.BaseTestBean;

public class TestMSizeAttributeOnCollection extends BaseMTest<TestMSizeAttributeOnCollection.TestBean>
{
  @MBean("Test:type=TestType")
  public static class TestBean extends BaseTestBean
  {
    @MSizeAttribute
    private List<String> names=new ArrayList<String>();
    
    @MSizeAttribute(name="lastNames", description="last names")
    private List<String> getNames()
    {
      return names;
    }
  }

  public TestMSizeAttributeOnCollection() throws MalformedObjectNameException
  {
    super(new TestBean(), "Test:type=TestType");
  }
  
  @Test
  public void testAttributeInfoNames() throws IntrospectionException, InstanceNotFoundException, ReflectionException 
  {
    MBeanAttributeInfo attributeInfo = getAttributeInfo("names");
    assertThat(attributeInfo.getDescription()).isEqualTo("names");
    assertThat(attributeInfo.getType()).isEqualTo("java.lang.Integer");
    assertThat(attributeInfo.isReadable()).isEqualTo(true);
    assertThat(attributeInfo.isWritable()).isEqualTo(false);
    assertThat(attributeInfo.isIs()).isEqualTo(false);
  }
  
  @Test
  public void testReadAttributeNames() throws AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException
  {
    testBean.names.add("Weiss");
    assertThat(getAttribute("names")).isEqualTo(1);
    testBean.names.add("Buetler");
    assertThat(getAttribute("names")).isEqualTo(2);
  }

  @Test
  public void testAttributeInfoLastNames() throws IntrospectionException, InstanceNotFoundException, ReflectionException 
  {
    MBeanAttributeInfo attributeInfo = getAttributeInfo("lastNames");
    assertThat(attributeInfo.getDescription()).isEqualTo("last names");
    assertThat(attributeInfo.getType()).isEqualTo("java.lang.Integer");
    assertThat(attributeInfo.isReadable()).isEqualTo(true);
    assertThat(attributeInfo.isWritable()).isEqualTo(false);
    assertThat(attributeInfo.isIs()).isEqualTo(false);
  }
  
  @Test
  public void testReadAttributeLastNames() throws AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException
  {
    testBean.names.add("Weiss");
    assertThat(getAttribute("lastNames")).isEqualTo(1);
    testBean.names.add("Buetler");
    assertThat(getAttribute("lastNames")).isEqualTo(2);
  }
  
  @Test
  public void testNullValue() throws AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException
  {
    testBean.names=null;
    assertThat(getAttribute("lastNames")).isEqualTo(0);
    assertThat(getAttribute("names")).isEqualTo(0);
  }

}

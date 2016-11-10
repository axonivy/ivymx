package com.axonivy.jmx;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.net.URISyntaxException;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanException;
import javax.management.MalformedObjectNameException;
import javax.management.ReflectionException;

import org.junit.Test;

import com.axonivy.jmx.MAttribute;
import com.axonivy.jmx.MBean;

public class TestUriConverter extends BaseMTest<TestUriConverter.TestBean>
{
  private static URI TEST_URI2;
  private static URI TEST_URI; 

  static
  {
    try
    {
      TEST_URI = new URI("http://localhost:8081/ivy");
      TEST_URI2 = new URI("ftp://ftp.soreco.ch/blah");
    }
    catch (URISyntaxException ex)
    {
    }
  }
  @MBean("Test:type=TestType")
  public static class TestBean
  {
    @MAttribute(isWritable=true)
    private URI uriField;
    
    private URI uriMethod;
    
    @MAttribute(isWritable=true)
    public URI getUriMethod()
    {
      return uriMethod;
    }
    
    public void setUriMethod(URI uriMethod)
    {
      this.uriMethod = uriMethod;
    }
  }
  
  public TestUriConverter() throws MalformedObjectNameException
  {
    super(new TestBean(), "Test:type=TestType");
  }
  
  @Test
  public void testAttributeInfoEnumField() throws IntrospectionException, InstanceNotFoundException, ReflectionException 
  {
    MBeanAttributeInfo attributeInfo = getAttributeInfo("uriField");
    assertThat(attributeInfo.getDescription()).isEqualTo("uriField");
    assertThat(attributeInfo.getType()).isEqualTo("java.lang.String");
    assertThat(attributeInfo.isReadable()).isEqualTo(true);
    assertThat(attributeInfo.isWritable()).isEqualTo(true);
    assertThat(attributeInfo.isIs()).isEqualTo(false);
  }
  
  @Test
  public void testReadAttributeEnumField() throws AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException
  {
    testBean.uriField = TEST_URI;
    assertThat(getAttribute("uriField")).isEqualTo(TEST_URI.toString());
    testBean.uriField = TEST_URI2;
    assertThat(getAttribute("uriField")).isEqualTo(TEST_URI2.toString());
  }

  @Test
  public void testWriteAttributeEnumField() throws InstanceNotFoundException, InvalidAttributeValueException, AttributeNotFoundException, ReflectionException, MBeanException
  {
    setAttribute("uriField", TEST_URI.toString());
    assertThat(testBean.uriField).isEqualTo(TEST_URI);
    setAttribute("uriField", TEST_URI2.toString());
    assertThat(testBean.uriField).isEqualTo(TEST_URI2);
  }

  @Test
  public void testAttributeInfoEnumMethod() throws IntrospectionException, InstanceNotFoundException, ReflectionException 
  {
    MBeanAttributeInfo attributeInfo = getAttributeInfo("uriMethod");
    assertThat(attributeInfo.getDescription()).isEqualTo("uriMethod");
    assertThat(attributeInfo.getType()).isEqualTo("java.lang.String");
    assertThat(attributeInfo.isReadable()).isEqualTo(true);
    assertThat(attributeInfo.isWritable()).isEqualTo(true);
    assertThat(attributeInfo.isIs()).isEqualTo(false);
  }
  
  @Test
  public void testReadAttributeEnumMethod() throws AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException
  {
    testBean.uriMethod = TEST_URI;
    assertThat(getAttribute("uriMethod")).isEqualTo(TEST_URI.toString());
    testBean.uriMethod = TEST_URI2;
    assertThat(getAttribute("uriMethod")).isEqualTo(TEST_URI2.toString());
  }

  @Test
  public void testWriteAttributeEnumMethod() throws InstanceNotFoundException, InvalidAttributeValueException, AttributeNotFoundException, ReflectionException, MBeanException
  {
    setAttribute("uriMethod", TEST_URI.toString());
    assertThat(testBean.uriMethod).isEqualTo(TEST_URI);
    setAttribute("uriMethod", TEST_URI2.toString());
    assertThat(testBean.uriMethod).isEqualTo(TEST_URI2);
  }


}

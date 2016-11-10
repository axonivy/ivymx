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
import javax.management.openmbean.ArrayType;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenMBeanAttributeInfo;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;

import org.junit.Test;

import com.axonivy.jmx.MAttribute;
import com.axonivy.jmx.MBean;
import com.axonivy.jmx.MComposite;
import com.axonivy.jmx.MItem;
import com.axonivy.jmx.TestMBeans.BaseTestBean;

public class TestCompositeDataMAttributeWithType extends BaseMTest<TestCompositeDataMAttributeWithType.TestBean>
{
  
  public TestCompositeDataMAttributeWithType() throws MalformedObjectNameException
  {
    super(new TestBean(), "Test:type=TestType");
  }

  @MBean(value="Test:type=TestType")
  public static class TestBean extends BaseTestBean
  {
    @MAttribute(type=CompData.class)
    private ICompData compositeData = new CompData();
    
    @MAttribute(type=CompData.class)
    private List<ICompData> compositeDatas = new ArrayList<ICompData>();
    
    @MAttribute(type=CompData.class)
    private List<ICompData> getCompDatas()
    {
      return compositeDatas;
    }       
  }

  public static interface ICompData
  {
    
  }
  
  @MComposite("Composite Data 1")
  public static class CompData implements ICompData
  {
    @MItem
    private String item1;
    
    private Integer count;
    
    @MItem
    private Integer getCount()
    {
      return count;
    }
    
    @MItem(type=CompData2.class)
    private ICompData2 compData;
    
    @MItem(type=CompData2.class)
    private ICompData2 getMyCompData()
    {
      return compData;
    }
  }
  
  public static interface ICompData2
  {
    
  }
  
  @MComposite("Composite Data 2")
  public static class CompData2 implements ICompData2
  {
    @MItem
    private String item2;
  }
  
  @Test
  public void testCompositeDataInfo() throws IntrospectionException, InstanceNotFoundException, ReflectionException
  {
    MBeanAttributeInfo attributeInfo = getAttributeInfo("compositeData");
    assertThat(attributeInfo.getDescription()).isEqualTo("compositeData");
    assertThat(attributeInfo).isInstanceOf(OpenMBeanAttributeInfo.class);
    assertThat(attributeInfo.isReadable()).isEqualTo(true);
    assertThat(attributeInfo.isWritable()).isEqualTo(false);
    assertThat(attributeInfo.isIs()).isEqualTo(false);

    OpenMBeanAttributeInfo openAttributeInfo = (OpenMBeanAttributeInfo)attributeInfo;
    OpenType<?> openType = openAttributeInfo.getOpenType();
    assertThat(openType).isNotNull();
    assertThat(openType).isInstanceOf(CompositeType.class);
  }
  
  @Test
  public void testCompositeTypeOfCompData() throws IntrospectionException, InstanceNotFoundException, ReflectionException
  {
    CompositeType compType = getCompositeTypeForCompData();
    assertThat(compType.getClassName()).isEqualTo(CompositeData.class.getName());
    assertThat(compType.getTypeName()).isEqualTo(CompData.class.getName());
    assertThat(compType.getDescription()).isEqualTo("Composite Data 1");
    assertThat(compType.keySet()).hasSize(4);
    assertThat(compType.keySet()).contains("item1", "compData", "myCompData", "count");
    assertThat(compType.getDescription("item1")).isEqualTo("item1");
    assertThat(compType.getDescription("count")).isEqualTo("count");
    assertThat(compType.getDescription("compData")).isEqualTo("compData");
    assertThat(compType.getDescription("myCompData")).isEqualTo("myCompData");
    assertThat((Object)compType.getType("item1")).isEqualTo(SimpleType.STRING);
    assertThat((Object)compType.getType("count")).isEqualTo(SimpleType.INTEGER);
    OpenType<?> openType = compType.getType("compData");
    assertThat(openType).isNotNull();
    assertThat(openType).isInstanceOf(CompositeType.class);
    openType = compType.getType("myCompData");
    assertThat(openType).isNotNull();
    assertThat(openType).isInstanceOf(CompositeType.class);
  }
  
  @Test
  public void testReadCompositeData() throws InstanceNotFoundException, ReflectionException, AttributeNotFoundException, MBeanException, IntrospectionException
  {
    ((CompData)testBean.compositeData).item1 = "Hi all";
    ((CompData)testBean.compositeData).count = 14;

    Object value = getAttribute("compositeData");
    assertThat(value).isNotNull();
    assertThat(value).isInstanceOf(CompositeData.class);
    CompositeData data = (CompositeData)value;
    assertThat(data.getCompositeType()).isEqualTo(getCompositeTypeForCompData());
    
    assertThat(data.get("item1")).isEqualTo("Hi all");
    assertThat(data.get("count")).isEqualTo(14);
  }

  @Test
  public void testReadNullCompositeData() throws InstanceNotFoundException, ReflectionException, AttributeNotFoundException, MBeanException
  {
    testBean.compositeData = null;

    Object value = getAttribute("compositeData");
    assertThat(value).isNull();
  }

  @Test
  public void testCompositeDatasInfo() throws IntrospectionException, InstanceNotFoundException, ReflectionException
  {
    MBeanAttributeInfo attributeInfo = getAttributeInfo("compositeDatas");
    assertThat(attributeInfo.getDescription()).isEqualTo("compositeDatas");
    assertThat(attributeInfo).isInstanceOf(OpenMBeanAttributeInfo.class);
    assertThat(attributeInfo.isReadable()).isEqualTo(true);
    assertThat(attributeInfo.isWritable()).isEqualTo(false);
    assertThat(attributeInfo.isIs()).isEqualTo(false);

    OpenMBeanAttributeInfo openAttributeInfo = (OpenMBeanAttributeInfo)attributeInfo;
    OpenType<?> openType = openAttributeInfo.getOpenType();
    assertThat(openType).isNotNull();
    assertThat(openType).isInstanceOf(ArrayType.class);
    ArrayType<?> arrayType = (ArrayType<?>)openType;
    assertThat(arrayType.getDimension()).isEqualTo(1);
    assertThat(arrayType.getElementOpenType()).isInstanceOf(CompositeType.class);
    assertThat((CompositeType)arrayType.getElementOpenType()).isEqualTo(getCompositeTypeForCompData());
  }
  
  @Test
  public void testEmptyCompositeDatas() throws AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException
  {
    Object value = getAttribute("compositeDatas");
    assertThat(value).isInstanceOf(CompositeData[].class);
    CompositeData[] datas = (CompositeData[])value;
    assertThat(datas).hasSize(0);
  }

  @Test
  public void testCompositeDatas() throws AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException
  {
    CompData data = new CompData();
    data.item1 = "Hello";
    data.count = 15;
    testBean.compositeDatas.add(data);
    data = new CompData();
    testBean.compositeDatas.add(data);
    Object value = getAttribute("compositeDatas");
    CompositeData[] datas = (CompositeData[])value;
    assertThat(datas).hasSize(2);
    assertThat(datas[0].get("item1")).isEqualTo("Hello");
    assertThat(datas[0].get("count")).isEqualTo(15);
  }
  
  @Test
  public void testCompDatasInfo() throws IntrospectionException, InstanceNotFoundException, ReflectionException
  {
    MBeanAttributeInfo attributeInfo = getAttributeInfo("compDatas");
    assertThat(attributeInfo.getDescription()).isEqualTo("compDatas");
    assertThat(attributeInfo).isInstanceOf(OpenMBeanAttributeInfo.class);
    assertThat(attributeInfo.isReadable()).isEqualTo(true);
    assertThat(attributeInfo.isWritable()).isEqualTo(false);
    assertThat(attributeInfo.isIs()).isEqualTo(false);

    OpenMBeanAttributeInfo openAttributeInfo = (OpenMBeanAttributeInfo)attributeInfo;
    OpenType<?> openType = openAttributeInfo.getOpenType();
    assertThat(openType).isNotNull();
    assertThat(openType).isInstanceOf(ArrayType.class);
    ArrayType<?> arrayType = (ArrayType<?>)openType;
    assertThat(arrayType.getDimension()).isEqualTo(1);
    assertThat(arrayType.getElementOpenType()).isInstanceOf(CompositeType.class);
    assertThat((CompositeType)arrayType.getElementOpenType()).isEqualTo(getCompositeTypeForCompData());
  }
  
  @Test
  public void testEmptyCompDatas() throws AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException
  {
    Object value = getAttribute("compDatas");
    assertThat(value).isInstanceOf(CompositeData[].class);
    CompositeData[] datas = (CompositeData[])value;
    assertThat(datas).hasSize(0);
  }

  @Test
  public void testCompDatas() throws AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException
  {
    CompData data = new CompData();
    data.item1 = "Hello";
    data.count = 15;
    testBean.compositeDatas.add(data);
    data = new CompData();
    testBean.compositeDatas.add(data);
    Object value = getAttribute("compDatas");
    CompositeData[] datas = (CompositeData[])value;
    assertThat(datas).hasSize(2);
    assertThat(datas[0].get("item1")).isEqualTo("Hello");
    assertThat(datas[0].get("count")).isEqualTo(15);
  }
  
  @Test
  public void testCompositeTypeOfCompositeData2() throws IntrospectionException, InstanceNotFoundException, ReflectionException
  {
    CompositeType compType = getCompositeTypeForCompData2();    
    assertThat(compType).isNotNull();
    assertThat(compType.getClassName()).isEqualTo(CompositeData.class.getName());
    assertThat(compType.getTypeName()).isEqualTo(CompData2.class.getName());
    assertThat(compType.getDescription()).isEqualTo("Composite Data 2");
    assertThat(compType.keySet()).hasSize(1);
    assertThat(compType.keySet()).contains("item2");
    assertThat(compType.getDescription("item2")).isEqualTo("item2");
    assertThat((Object)compType.getType("item2")).isEqualTo(SimpleType.STRING);
  }

  private CompositeType getCompositeTypeForCompData() throws IntrospectionException,
          InstanceNotFoundException, ReflectionException
  {
    MBeanAttributeInfo attributeInfo = getAttributeInfo("compositeData");
    OpenType<?> openType = ((OpenMBeanAttributeInfo)attributeInfo).getOpenType(); 
    CompositeType compType = (CompositeType)openType;
    return compType;
  }
  

  private CompositeType getCompositeTypeForCompData2() throws IntrospectionException,
          InstanceNotFoundException, ReflectionException
  {
    return (CompositeType)getCompositeTypeForCompData().getType("compData");
  }
}

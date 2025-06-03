package com.axonivy.jmx;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Date;
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

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.axonivy.jmx.TestMBeans.BaseTestBean;
import com.axonivy.jmx.internal.LogErrorStrategy;
import com.axonivy.jmx.util.LogTestAppender;

public class TestCompositeDataMAttribute extends BaseMTest<TestCompositeDataMAttribute.TestBean> {
  private final LogTestAppender logAppender = new LogTestAppender(Level.ERROR);

  public TestCompositeDataMAttribute() throws MalformedObjectNameException {
    super(new TestBean(), "Test:type=TestType");
  }

  @MBean(value = "Test:type=TestType")
  public static class TestBean extends BaseTestBean {
    @MAttribute
    private CompData compositeData = new CompData();

    @MAttribute
    private final List<CompData> compositeDatas = new ArrayList<>();
  }

  @MComposite("Composite Data 1")
  public static class CompData {
    @MItem
    private String item1;
    @MItem(description = "Item Nr. 2")
    private CompositeData2 item2 = new CompositeData2();

    private Integer count;

    @MItem
    private Integer getCount() {
      return count;
    }
  }

  @MComposite
  public static class CompositeData2 {
    @MItem(name = "startDate")
    private Date date;
  }

  @MBean("ivy:type=TestBean2")
  public static class TestBean2 {
    @MAttribute
    private RecursiveComposite recursiveComposite;
  }

  @MComposite
  public static class RecursiveComposite {
    @MItem
    private RecursiveComposite recursiveItem;
  }

  @Before
  @Override
  public void before() {
    super.before();
    Logger.getLogger(LogErrorStrategy.class).addAppender(logAppender);
  }

  @After
  @Override
  public void after() {
    super.after();
    Logger.getLogger(LogErrorStrategy.class).removeAppender(logAppender);
  }

  @Test
  public void testCompositeDataInfo() throws IntrospectionException, InstanceNotFoundException, ReflectionException {
    MBeanAttributeInfo attributeInfo = getAttributeInfo("compositeData");
    assertThat(attributeInfo.getDescription()).isEqualTo("compositeData");
    assertThat(attributeInfo).isInstanceOf(OpenMBeanAttributeInfo.class);
    assertThat(attributeInfo.isReadable()).isEqualTo(true);
    assertThat(attributeInfo.isWritable()).isEqualTo(false);
    assertThat(attributeInfo.isIs()).isEqualTo(false);

    OpenMBeanAttributeInfo openAttributeInfo = (OpenMBeanAttributeInfo) attributeInfo;
    OpenType<?> openType = openAttributeInfo.getOpenType();
    assertThat(openType).isNotNull();
    assertThat(openType).isInstanceOf(CompositeType.class);
  }

  @Test
  public void testCompositeTypeOfCompData() throws IntrospectionException, InstanceNotFoundException, ReflectionException {
    CompositeType compType = getCompositeTypeForCompData();
    assertThat(compType.getClassName()).isEqualTo(CompositeData.class.getName());
    assertThat(compType.getTypeName()).isEqualTo(CompData.class.getName());
    assertThat(compType.getDescription()).isEqualTo("Composite Data 1");
    assertThat(compType.keySet()).hasSize(3);
    assertThat(compType.keySet()).contains("item1", "item2", "count");
    assertThat(compType.getDescription("item1")).isEqualTo("item1");
    assertThat(compType.getDescription("item2")).isEqualTo("Item Nr. 2");
    assertThat(compType.getDescription("count")).isEqualTo("count");
    assertThat((Object) compType.getType("item1")).isEqualTo(SimpleType.STRING);
    assertThat((Object) compType.getType("count")).isEqualTo(SimpleType.INTEGER);
    OpenType<?> openType = compType.getType("item2");
    assertThat(openType).isNotNull();
    assertThat(openType).isInstanceOf(CompositeType.class);
  }

  @Test
  public void testCompositeTypeOfCompositeData2() throws IntrospectionException, InstanceNotFoundException, ReflectionException {
    CompositeType compType = getCompositeTypeForCompositeData2();
    assertThat(compType).isNotNull();
    assertThat(compType.getClassName()).isEqualTo(CompositeData.class.getName());
    assertThat(compType.getTypeName()).isEqualTo(CompositeData2.class.getName());
    assertThat(compType.getDescription()).isEqualTo(CompositeData2.class.getName());
    assertThat(compType.keySet()).hasSize(1);
    assertThat(compType.keySet()).contains("startDate");
    assertThat(compType.getDescription("startDate")).isEqualTo("startDate");
    assertThat((Object) compType.getType("startDate")).isEqualTo(SimpleType.DATE);
  }

  @Test
  public void testReadCompositeData() throws InstanceNotFoundException, ReflectionException, AttributeNotFoundException, MBeanException, IntrospectionException {
    testBean.compositeData.item1 = "Hi all";
    testBean.compositeData.count = 14;
    Date testDate = new Date();
    testBean.compositeData.item2.date = testDate;

    Object value = getAttribute("compositeData");
    assertThat(value).isNotNull();
    assertThat(value).isInstanceOf(CompositeData.class);
    CompositeData data = (CompositeData) value;
    assertThat(data.getCompositeType()).isEqualTo(getCompositeTypeForCompData());

    assertThat(data.get("item1")).isEqualTo("Hi all");
    assertThat(data.get("count")).isEqualTo(14);
    assertThat(data.get("item2")).isNotNull();
    assertThat(data.get("item2")).isInstanceOf(CompositeData.class);
    CompositeData data2 = (CompositeData) data.get("item2");
    assertThat(data2.getCompositeType()).isEqualTo(getCompositeTypeForCompositeData2());
    assertThat(data2.get("startDate")).isEqualTo(testDate);
  }

  @Test
  public void testReadNullCompositeData() throws InstanceNotFoundException, ReflectionException, AttributeNotFoundException, MBeanException {
    testBean.compositeData = null;

    Object value = getAttribute("compositeData");
    assertThat(value).isNull();
  }

  @Test
  public void testReadNullCompData() throws InstanceNotFoundException, ReflectionException, AttributeNotFoundException, MBeanException {
    testBean.compositeData.item2 = null;

    Object value = getAttribute("compositeData");
    assertThat(value).isNotNull();
    assertThat(value).isInstanceOf(CompositeData.class);
    CompositeData data = (CompositeData) value;
    assertThat(data.get("item2")).isNull();
  }

  @Test
  public void testCompositeDatasInfo() throws IntrospectionException, InstanceNotFoundException, ReflectionException {
    MBeanAttributeInfo attributeInfo = getAttributeInfo("compositeDatas");
    assertThat(attributeInfo.getDescription()).isEqualTo("compositeDatas");
    assertThat(attributeInfo).isInstanceOf(OpenMBeanAttributeInfo.class);
    assertThat(attributeInfo.isReadable()).isEqualTo(true);
    assertThat(attributeInfo.isWritable()).isEqualTo(false);
    assertThat(attributeInfo.isIs()).isEqualTo(false);

    OpenMBeanAttributeInfo openAttributeInfo = (OpenMBeanAttributeInfo) attributeInfo;
    OpenType<?> openType = openAttributeInfo.getOpenType();
    assertThat(openType).isNotNull();
    assertThat(openType).isInstanceOf(ArrayType.class);
    ArrayType<?> arrayType = (ArrayType<?>) openType;
    assertThat(arrayType.getDimension()).isEqualTo(1);
    assertThat(arrayType.getElementOpenType()).isInstanceOf(CompositeType.class);
    assertThat((CompositeType) arrayType.getElementOpenType()).isEqualTo(getCompositeTypeForCompData());
  }

  @Test
  public void testEmptyCompositeDatas() throws AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException {
    Object value = getAttribute("compositeDatas");
    assertThat(value).isInstanceOf(CompositeData[].class);
    CompositeData[] datas = (CompositeData[]) value;
    assertThat(datas).hasSize(0);
  }

  @Test
  public void testCompositeDatas() throws AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException {
    CompData data = new CompData();
    data.item1 = "Hello";
    testBean.compositeDatas.add(data);
    data = new CompData();
    data.item2 = new CompositeData2();
    Date testDate = new Date();
    data.item2.date = testDate;
    testBean.compositeDatas.add(data);
    Object value = getAttribute("compositeDatas");
    CompositeData[] datas = (CompositeData[]) value;
    assertThat(datas).hasSize(2);
    assertThat(datas[0].get("item1")).isEqualTo("Hello");
    assertThat(datas[1].get("item2")).isInstanceOf(CompositeData.class);
    CompositeData item2 = (CompositeData) datas[1].get("item2");
    assertThat(item2.get("startDate")).isEqualTo(testDate);
  }

  @Test
  public void testRecursiveCompositeClass() {
    assertThat(logAppender.getRecording()).isEmpty();
    MBeans.registerMBeanFor(new TestBean2());
    assertThat(logAppender.getRecording()).contains("Could not register MBean 'com.axonivy.jmx.TestCompositeDataMAttribute$TestBean");
    assertThat(logAppender.getRecording()).contains("java.lang.StackOverflowError");
  }

  private CompositeType getCompositeTypeForCompositeData2() throws IntrospectionException, InstanceNotFoundException, ReflectionException {
    OpenType<?> openType = getCompositeTypeForCompData().getType("item2");
    return (CompositeType) openType;
  }

  private CompositeType getCompositeTypeForCompData() throws IntrospectionException,
      InstanceNotFoundException, ReflectionException {
    MBeanAttributeInfo attributeInfo = getAttributeInfo("compositeData");
    OpenType<?> openType = ((OpenMBeanAttributeInfo) attributeInfo).getOpenType();
    return (CompositeType) openType;
  }
}

package com.axonivy.jmx;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Properties;

import javax.management.MBeanAttributeInfo;
import javax.management.MalformedObjectNameException;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenMBeanAttributeInfo;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;
import javax.management.openmbean.TabularData;
import javax.management.openmbean.TabularDataSupport;
import javax.management.openmbean.TabularType;

import org.junit.Test;

import com.axonivy.jmx.util.MUtils;

public class TestPropertiesConverter extends BaseMTest<TestPropertiesConverter.TestBean> {
  @MBean("Test:type=TestType")
  public static class TestBean {
    @MAttribute(isWritable = true)
    private final Properties field = new Properties();

    public TestBean() {
      field.setProperty("name", "Weiss");
      field.setProperty("vorname", "Reto");
    }
  }

  public TestPropertiesConverter() throws MalformedObjectNameException {
    super(new TestBean(), "Test:type=TestType");
  }

  @Test
  public void testReadAttribute() throws Exception {
    TabularData properties = (TabularData) getAttribute("field");
    assertThat(properties).isNotNull();
    assertThat(properties.size()).isEqualTo(2);
    for (CompositeData property : MUtils.toRows(properties)) {
      assertThat(property.getCompositeType().keySet()).containsExactly("propertyName", "propertyValue");
      Object key = property.get("propertyName");
      Object value = property.get("propertyValue");
      assertThat(key).isNotNull();
      if ("name".equals(key)) {
        assertThat(value).isEqualTo("Weiss");
      } else {
        assertThat(value).isEqualTo("Reto");
      }
    }
  }

  @Test
  public void testWriteAttribute() throws Exception {
    String[] keyValue = {"propertyName", "propertyValue"};
    CompositeType type = new CompositeType("properties", "properties", keyValue, keyValue, new OpenType[] {SimpleType.STRING, SimpleType.STRING});
    TabularType tabularType = new TabularType("Properties", "properties", type, new String[] {"propertyName"});
    TabularDataSupport properties = new TabularDataSupport(tabularType);
    properties.put(new CompositeDataSupport(type, keyValue, new String[] {"company", "ivyTeam"}));
    properties.put(new CompositeDataSupport(type, keyValue, new String[] {"address", "Alpenstrasse 9"}));
    properties.put(new CompositeDataSupport(type, keyValue, new String[] {"city", "Zug"}));

    setAttribute("field", properties);

    assertThat(testBean.field).hasSize(3);
    assertThat(testBean.field.keySet()).containsOnly("company", "address", "city");
    assertThat(testBean.field.get("company")).isEqualTo("ivyTeam");
    assertThat(testBean.field.get("address")).isEqualTo("Alpenstrasse 9");
    assertThat(testBean.field.get("city")).isEqualTo("Zug");
  }

  @Test
  public void testAttributeMetaInfo() throws Exception {
    MBeanAttributeInfo attributeInfo = getAttributeInfo("field");
    assertThat(attributeInfo.getType()).isEqualTo("javax.management.openmbean.TabularData");

    OpenMBeanAttributeInfo openAttributeInfo = (OpenMBeanAttributeInfo) attributeInfo;
    OpenType<?> attributeType = openAttributeInfo.getOpenType();
    assertThat(attributeType).isInstanceOf(TabularType.class);

    TabularType attributeTabularType = (TabularType) attributeType;
    assertThat(attributeTabularType.getIndexNames()).containsOnly("propertyName");
    assertThat(attributeTabularType.getClassName()).isEqualTo(TabularData.class.getName());
    assertThat(attributeTabularType.getTypeName()).isEqualTo(Properties.class.getName());
    assertThat(attributeTabularType.getDescription()).isEqualTo(Properties.class.getName());

    OpenType<?> elementType = attributeTabularType.getRowType();
    assertThat(elementType).isNotNull();
    assertThat(elementType).isInstanceOf(CompositeType.class);

    CompositeType elementCompositeType = (CompositeType) elementType;
    assertThat(elementCompositeType.getClassName()).isEqualTo(CompositeData.class.getName());
    assertThat(elementCompositeType.getTypeName()).isEqualTo("PropertyNameValuePair");
    assertThat(elementCompositeType.getDescription()).isEqualTo("Property name and value pair");
    assertThat(elementCompositeType.keySet()).containsOnly("propertyName", "propertyValue");

    assertThat(elementCompositeType.getDescription("propertyName")).isEqualTo("Name of the property");
    assertThat((Object) elementCompositeType.getType("propertyName")).isEqualTo(SimpleType.STRING);
    assertThat(elementCompositeType.getDescription("propertyValue")).isEqualTo("Value of the property");
    assertThat((Object) elementCompositeType.getType("propertyValue")).isEqualTo(SimpleType.STRING);
  }
}

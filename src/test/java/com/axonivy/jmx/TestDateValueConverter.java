package com.axonivy.jmx;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;

import javax.management.MalformedObjectNameException;
import javax.management.openmbean.CompositeData;

import org.junit.Test;

public class TestDateValueConverter extends BaseMTest<TestDateValueConverter.TestBean> {
  @MBean("Test:type=TestType")
  public static class TestBean {
    @MAttribute(isWritable = true)
    private final Date dateField = new java.sql.Date(0);

    private Date dateMethod = new java.sql.Date(1);

    @MAttribute
    private final TestComposite composite = new TestComposite();

    @MAttribute(isWritable = true)
    public Date getDateMethod() {
      return dateMethod;
    }

    public void setDateMethod(Date dateMethod) {
      this.dateMethod = dateMethod;
    }
  }

  @MComposite
  public static class TestComposite {
    @MItem
    private final Date dateField = new java.sql.Date(2);

    private final Date dateMethod = new java.sql.Date(3);

    @MItem
    public Date getDateMethod() {
      return dateMethod;
    }
  }

  public TestDateValueConverter() throws MalformedObjectNameException {
    super(new TestBean(), "Test:type=TestType");
  }

  @Test
  public void testReadDateAttribute() throws Exception {
    Date date = (Date) getAttribute("dateField");
    assertThat(date).isExactlyInstanceOf(Date.class);
    assertThat(date).isEqualTo(new Date(0));
  }

  @Test
  public void testWriteDateAttribute() throws Exception {
    Date value = new Date(100);
    setAttribute("dateField", value);
    assertThat(testBean.dateField).isSameAs(value);
  }

  @Test
  public void testReadDateMethod() throws Exception {
    Date date = (Date) getAttribute("dateMethod");
    assertThat(date).isExactlyInstanceOf(Date.class);
    assertThat(date).isEqualTo(new Date(1));
  }

  @Test
  public void testWriteDateMethod() throws Exception {
    Date value = new Date(101);
    setAttribute("dateMethod", value);
    assertThat(testBean.dateMethod).isSameAs(value);
  }

  @Test
  public void testReadCompositeDateField() throws Exception {
    CompositeData composite = (CompositeData) getAttribute("composite");
    assertThat(composite.get("dateField")).isExactlyInstanceOf(Date.class);
    assertThat(composite.get("dateField")).isEqualTo(new Date(2));
  }

  @Test
  public void testReadCompositeDateMethod() throws Exception {
    CompositeData composite = (CompositeData) getAttribute("composite");
    assertThat(composite.get("dateMethod")).isExactlyInstanceOf(Date.class);
    assertThat(composite.get("dateMethod")).isEqualTo(new Date(3));
  }
}

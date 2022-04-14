package com.axonivy.jmx;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.junit.Before;
import org.junit.Test;

public class TestMMap extends BaseMTest<TestMMap.TestBean>
{
  private final Map<String, TestBean> testMap = MCollections.managedMap(new HashMap<String, TestBean>());

  @MBean(value="Test:type=TestType,id=#{id}")
  public static class TestBean
  {
    private static int global=0;
    private final int id = global++;
    public String getId()
    {
      return ""+id;
    }
  }

  @MBean(value="Test:name=Hello")
  public static class HelloBean extends TestBean
  {
  }

  public TestMMap() throws MalformedObjectNameException
  {
    super(new TestBean(), "Test:type=TestType,id="+(TestBean.global-1));
  }

  @Override
  @Before
  public void before()
  {
    // Do not register test bean by default
  }

  @Test
  public void testPut()
  {
    testMap.put("blah", testBean);
    assertRegistered();
  }

  @Test
  public void testPut2()
  {
    testMap.put("blah", testBean);
    testMap.put("blah", new TestBean());
    assertNotRegistered();
  }

  @Test
  public void testPut2SameName() throws MalformedObjectNameException
  {
    HelloBean bean1 = new HelloBean();
    HelloBean bean2 = new HelloBean();
    testMap.put("blah", bean1);
    testMap.put("blah", bean2);
    assertThat(MBeans.getMBeanServer().isRegistered(new ObjectName("Test:name=Hello"))).isTrue();
  }

  @Test
  public void testRemove()
  {
    testMap.put("blah", testBean);
    testMap.remove("blah");
    assertNotRegistered();
  }

  @Test
  public void testClear()
  {
    testMap.put("blah", testBean);
    testMap.clear();
    assertNotRegistered();
  }

  @Test
  public void testPutAll()
  {
    Map<String, TestBean> map = new HashMap<String, TestBean>();
    map.put("blah", testBean);
    testMap.putAll(map);
    assertRegistered();
  }
}

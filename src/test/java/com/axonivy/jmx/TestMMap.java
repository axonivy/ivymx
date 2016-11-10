package com.axonivy.jmx;

import java.util.HashMap;
import java.util.Map;

import javax.management.MalformedObjectNameException;

import org.junit.Before;
import org.junit.Test;

import com.axonivy.jmx.MBean;
import com.axonivy.jmx.MCollections;

public class TestMMap extends BaseMTest<TestMMap.TestBean>
{
  private Map<String, TestBean> testMap = MCollections.managedMap(new HashMap<String, TestBean>());

  @MBean(value="Test:type=TestType,id=#{id}")
  public static class TestBean
  {
    private static int global=0;
    private int id = global++;
    public String getId()
    {
      return ""+id;
    }
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

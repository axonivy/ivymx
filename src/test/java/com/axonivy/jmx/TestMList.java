package com.axonivy.jmx;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.management.MalformedObjectNameException;

import org.junit.Before;
import org.junit.Test;

import com.axonivy.jmx.MBean;
import com.axonivy.jmx.MCollections;

public class TestMList extends BaseMTest<TestMList.TestBean>
{
  private List<TestBean> testList = MCollections.managedList(new ArrayList<TestBean>());

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
  
  public TestMList() throws MalformedObjectNameException
  {
    super(new TestBean(), "Test:type=TestType,id="+(TestBean.global-1));
  }

  @Override
  @Before
  public void before()
  {
    // do not register test bean
  }
  
  @Test
  public void testAdd()
  {
    testList.add(testBean);
    assertRegistered();
  }
  
  @Test
  public void testAdd2()
  {
    testList.add(new TestBean());
    testList.add(0, testBean);
    assertRegistered();
  }
  
  @Test 
  public void testSet()
  {
    testList.add(testBean);
    testList.set(0, new TestBean());
    assertNotRegistered();
  }

  @Test 
  public void testAddAll()
  {
    testList.addAll(Arrays.asList(testBean, new TestBean()));
    assertRegistered();
  }
  
  @Test 
  public void testAddAll2()
  {
    testList.add(new TestBean());
    testList.addAll(0, Arrays.asList(testBean));
    assertRegistered();
  }
 
  @Test
  public void testRemove()
  {
    testList.add(testBean);
    testList.remove(testBean);
    assertNotRegistered();
  }
  
  @Test
  public void testRemove2()
  {
    testList.add(testBean);
    testList.remove(0);
    assertNotRegistered();
  }

  @Test
  public void testRemoveAll()
  {
    testList.add(testBean);
    testList.removeAll(Arrays.asList(testBean));
    assertNotRegistered();
  }

  @Test 
  public void testClear()
  {
    testList.add(testBean);
    testList.clear();
    assertNotRegistered();
  }
  
  @Test 
  public void testRetainAll()
  {
    testList.add(testBean);
    testList.retainAll(Collections.emptyList());
    assertNotRegistered();
  }
}

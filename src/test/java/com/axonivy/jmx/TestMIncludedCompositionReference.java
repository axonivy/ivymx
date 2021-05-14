package com.axonivy.jmx;

import static org.assertj.core.api.Assertions.assertThat;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectInstance;

import org.junit.Test;

public class TestMIncludedCompositionReference extends BaseMTest<TestMIncludedCompositionReference.TestBean>
{ 
  public static class TestBaseBean
  {
    @MInclude
    private final IncludeBean3 include3 = new IncludeBean3();
    
    private final IncludeBean4 includeBean4 = new IncludeBean4();
    
    @MInclude 
    private IncludeBean4 getIncludeBean4()
    {
      return includeBean4;
    }    
  }
  
  @MBean(value="Test:type=TestBean")
  public static class TestBean extends TestBaseBean
  {
    @MInclude
    private final IncludeBean1 include = new IncludeBean1();
  }
  
  public static class IncludeBean1
  {
    @MCompositionReference()
    private final TestCompositionBean1 comp = new TestCompositionBean1();
    
    private final IncludeBean2 includeBean2 = new IncludeBean2();
    
    @MInclude 
    private IncludeBean2 getIncludeBean2()
    {
      return includeBean2;
    }    
  }

  public static class IncludeBean2
  {
    private final TestCompositionBean2 comp = new TestCompositionBean2();
    
    @MCompositionReference()
    private TestCompositionBean2 getComp()
    {
      return comp;
    }
  }

  public static class IncludeBean3
  {
    @MCompositionReference()
    private final TestCompositionBean3 comp = new TestCompositionBean3();
  }

  public static class IncludeBean4
  {
    private final TestCompositionBean4 comp = new TestCompositionBean4();
    
    @MCompositionReference()
    private TestCompositionBean4 getComp()
    {
      return comp;
    }
  }

  @MBean(value="Test:type=TestCompositionBean1")
  public static class TestCompositionBean1
  {    
  }
  
  @MBean(value="Test:type=TestCompositionBean2")
  public static class TestCompositionBean2
  {    
  }
  
  @MBean(value="Test:type=TestCompositionBean3")
  public static class TestCompositionBean3
  {    
  }

  @MBean(value="Test:type=TestCompositionBean4")
  public static class TestCompositionBean4
  {    
  }

  private static TestBean TEST_BEAN = new TestBean();
  private static String[] NAMES = {
    "TestBean", 
    "TestCompositionBean1",
    "TestCompositionBean2",
    "TestCompositionBean3",
    "TestCompositionBean4" };
  
    
  public TestMIncludedCompositionReference() throws MalformedObjectNameException
  {
    super(TEST_BEAN, getMBeanName("TestBean"));
  }
  
  @Test
  public void testRegister() throws NullPointerException, MalformedObjectNameException
  {
    for (String name : NAMES)
    {
      ObjectInstance instance = getBeanOrNullFromBeanServer(getMBeanName(name));
      assertThat(instance).isNotNull();
    }
  }
  
  @Test
  public void testUnregister() throws MalformedObjectNameException, NullPointerException
  {
    MBeans.unregisterMBeanFor(TEST_BEAN);
    for (String name : NAMES)
    {
      ObjectInstance instance = getBeanOrNullFromBeanServer(getMBeanName(name));
      assertThat(instance).isNull();
    }
  }

  private static String getMBeanName(String name)
  {
    return "Test:type="+name;
  }
}

package com.axonivy.jmx;

import static org.assertj.core.api.Assertions.assertThat;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectInstance;

import org.junit.Test;


public class TestMCompositionReference extends BaseMTest<TestMCompositionReference.TestBean>
{ 
  @MBean(value="Test:type=TestBean,name=#{name}")
  public static class TestBean
  {
    private final String name;
    
    @MCompositionReference
    private TestBean composition;

    @MCompositionReference(concatName=true)
    private TestConcatBean concat;
    
    private TestBean compositionBean;
    
    
    private TestBean(String name)
    {
      this.name = name;
    }
    
    @MCompositionReference
    TestBean getCompositionBean()
    {
      return compositionBean;
    }
  }
  
  @MBean("child=TestConcatBean")
  public static class TestConcatBean
  {
    @MCompositionReference(concatName=true)
    private final TestConcatBean2 concat = new TestConcatBean2();
  }

  @MBean("sub=TestConcatBean2")
  public static class TestConcatBean2
  {
  }

  private static final String MODIFIED = "modified";
  private static TestBean TEST_BEAN = null;
  private static String[] NAMES = {
    "top", 
    "1st level field", 
    "1st level method",
    "2nd level field of 1st level field",
    "2nd level method of 1st level field",
    "2nd level field of 1st level method",
    "2nd level method of 1st level method",
    "top,child=TestConcatBean",
    "top,child=TestConcatBean,sub=TestConcatBean2"};

  public TestMCompositionReference() throws MalformedObjectNameException
  {
    super(createTestBean(), getMBeanName(TEST_BEAN.name));
  }

  private static TestBean createTestBean()
  {
    TEST_BEAN = new TestBean(NAMES[0]);
    TEST_BEAN.concat = new TestConcatBean();
    TEST_BEAN.composition = new TestBean(NAMES[1]);
    TEST_BEAN.compositionBean = new TestBean(NAMES[2]);
    TEST_BEAN.composition.composition = new TestBean(NAMES[3]);
    TEST_BEAN.composition.compositionBean = new TestBean(NAMES[4]);
    TEST_BEAN.compositionBean.composition = new TestBean(NAMES[5]);
    TEST_BEAN.compositionBean.compositionBean = new TestBean(NAMES[6]);
    return TEST_BEAN;
  }

  @Test
  public void testRegister() throws NullPointerException, MalformedObjectNameException
  {
    for (String name : NAMES)
    {
      ObjectInstance instance = getBeanOrNullFromBeanServer(getMBeanName(name));
      assertThat(instance).as("Instance "+name).isNotNull();
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

  @Test
  public void testModifyCompositionDoesNotInfluenceRegisteredMBeans() throws MalformedObjectNameException, NullPointerException
  {
    TEST_BEAN.composition = new TestBean(MODIFIED);
    ObjectInstance instance = getBeanOrNullFromBeanServer(getMBeanName(MODIFIED));
    assertThat(instance).isNull();
  }
  
  @Test
  public void testModifyCompositionDoesNotInfluenceUnregisteredMBeans() throws MalformedObjectNameException, NullPointerException
  {
    TEST_BEAN.composition = new TestBean(MODIFIED);
    MBeans.unregisterMBeanFor(TEST_BEAN);
    for (String name : NAMES)
    {
      ObjectInstance instance = getBeanOrNullFromBeanServer(getMBeanName(name));
      assertThat(instance).isNull();
    }
  }

  private static String getMBeanName(String name)
  {
    return "Test:type=TestBean,name="+name;
  }
}

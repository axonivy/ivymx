package com.axonivy.jmx;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Arrays;
import java.util.List;

import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectInstance;
import javax.management.ReflectionException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Test;

import com.axonivy.jmx.internal.LogErrorStrategy;
import com.axonivy.jmx.util.LogTestAppender;

public class TestMBeans extends BaseMTest<TestMBeans.TestBean>
{
  private final LogTestAppender logAppender = new LogTestAppender(Level.ERROR);
  
  public static class BaseTestBean
  {
    @SuppressWarnings("unused")
    private String getName()
    {
      return "Test";
    }

    @SuppressWarnings("unused")
    private String surname="Name";
  }

  @MBean(value="Test:type=TestType,name=#{name}#{surname},app=#{pmv.application.name}", description="Description of #{application.name}")
  public static class TestBean extends BaseTestBean
  {
    @SuppressWarnings("unused")
    private Application application = new Application();


    public Pmv getPmv()
    {
      return new Pmv();
    }
  }

  public static class Pmv
  {
    public Application getApplication()
    {
      return new Application();
    }
  }

  public static class Application
  {
    public String getName()
    {
      return "TestApp";
    }
  }

  @MBean(value="Test:name=TestUniqueName", makeNameUnique=true)
  public static class TestUniqueNameBean
  {
  }

  public TestMBeans() throws MalformedObjectNameException
  {
    super(new TestBean(), "Test:type=TestType,name=TestName,app=TestApp");
  }

  @Test
  public void testRegister() throws InstanceNotFoundException, NullPointerException
  {
    ObjectInstance objectInstance = getTestBeanFromBeanServer();
    assertThat(objectInstance).isNotNull();
    assertThat(objectInstance.getClassName()).isEqualTo(TestBean.class.getName());
    assertThat(objectInstance.getObjectName()).isEqualTo(testBeanObjectName);
  }

  @Test
  public void testRegisterCollection() throws InstanceNotFoundException, MalformedObjectNameException, NullPointerException
  {
    MBeans.unregisterAllMBeans();
    List<Object> beans = Arrays.asList(new TestUniqueNameBean(), new TestUniqueNameBean(), new TestBean(), new Object());
    MBeans.registerMBeansFor(beans);
    ObjectInstance objectInstance = getTestBeanFromBeanServer();
    ObjectInstance objectInstance1 = getBeanOrNullFromBeanServer("Test:name=TestUniqueName");
    ObjectInstance objectInstance2 = getBeanOrNullFromBeanServer("Test:name=TestUniqueName @1");
    assertThat(objectInstance).isNotNull();
    assertThat(objectInstance1).isNotNull();
    assertThat(objectInstance2).isNotNull();
  }

  @Test
  public void testRegisterSameNameTwice()
  {
    MBeans.registerMBeanFor(new TestBean());
  }

  @Test
  public void testMakeUniqueName() throws MalformedObjectNameException, NullPointerException
  {
    ObjectInstance objectInstance1 = getBeanOrNullFromBeanServer("Test:name=TestUniqueName");
    ObjectInstance objectInstance2 = getBeanOrNullFromBeanServer("Test:name=TestUniqueName @1");
    ObjectInstance objectInstance3 = getBeanOrNullFromBeanServer("Test:name=TestUniqueName @2");

    assertThat(objectInstance1).isNull();
    assertThat(objectInstance2).isNull();
    assertThat(objectInstance3).isNull();

    MBeans.registerMBeanFor(new TestUniqueNameBean());

    objectInstance1 = getBeanOrNullFromBeanServer("Test:name=TestUniqueName");
    objectInstance2 = getBeanOrNullFromBeanServer("Test:name=TestUniqueName @1");
    objectInstance3 = getBeanOrNullFromBeanServer("Test:name=TestUniqueName @2");

    assertThat(objectInstance1).isNotNull();
    assertThat(objectInstance2).isNull();
    assertThat(objectInstance3).isNull();

    MBeans.registerMBeanFor(new TestUniqueNameBean());

    objectInstance1 = getBeanOrNullFromBeanServer("Test:name=TestUniqueName");
    objectInstance2 = getBeanOrNullFromBeanServer("Test:name=TestUniqueName @1");
    objectInstance3 = getBeanOrNullFromBeanServer("Test:name=TestUniqueName @2");

    assertThat(objectInstance1).isNotNull();
    assertThat(objectInstance2).isNotNull();
    assertThat(objectInstance3).isNull();

    MBeans.registerMBeanFor(new TestUniqueNameBean());

    objectInstance1 = getBeanOrNullFromBeanServer("Test:name=TestUniqueName");
    objectInstance2 = getBeanOrNullFromBeanServer("Test:name=TestUniqueName @1");
    objectInstance3 = getBeanOrNullFromBeanServer("Test:name=TestUniqueName @2");

    assertThat(objectInstance1).isNotNull();
    assertThat(objectInstance2).isNotNull();
    assertThat(objectInstance3).isNotNull();
  }

  @Test
  public void testUnregister()
  {
    MBeans.unregisterMBeanFor(testBean);
    assertThatThrownBy(() -> getTestBeanFromBeanServer()).isInstanceOf(InstanceNotFoundException.class);
  }

  @Test
  public void testUnregisterCollection() throws MalformedObjectNameException, NullPointerException
  {
    MBeans.unregisterAllMBeans();
    List<Object> beans = Arrays.asList(new TestUniqueNameBean(), new TestUniqueNameBean(), new TestBean(), new Object());
    MBeans.registerMBeansFor(beans);
    MBeans.unregisterMBeansFor(beans);
    ObjectInstance objectInstance = getBeanOrNullFromBeanServer(testBeanObjectName.toString());
    ObjectInstance objectInstance1 = getBeanOrNullFromBeanServer("Test:name=TestUniqueName");
    ObjectInstance objectInstance2 = getBeanOrNullFromBeanServer("Test:name=TestUniqueName @1");
    assertThat(objectInstance).isNull();
    assertThat(objectInstance1).isNull();
    assertThat(objectInstance2).isNull();
  }

  @Test
  public void testDescription() throws IntrospectionException, InstanceNotFoundException, ReflectionException
  {
    assertThat(getMBeanInfoFromBeanServer().getDescription()).isEqualTo("Description of TestApp");
  }

  @Test
  public void testRegisterNotMBean()
  {
    Logger.getLogger(LogErrorStrategy.class).addAppender(logAppender);
    assertThat(logAppender.getRecording()).isEmpty();
    MBeans.registerMBeanFor(new Object());
    assertThat(logAppender.getRecording()).contains("Bean 'class java.lang.Object' must contain a @MBean annotation");
  }
}

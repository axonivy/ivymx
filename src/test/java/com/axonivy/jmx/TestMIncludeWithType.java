package com.axonivy.jmx;

import static org.assertj.core.api.Assertions.assertThat;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanException;
import javax.management.MalformedObjectNameException;
import javax.management.ReflectionException;

import org.junit.Test;

public class TestMIncludeWithType extends BaseMTest<TestMIncludeWithType.TestBean>
{

  @MBean(value="Test:type=TestType")
  public static class TestBean 
  {
    @MInclude(type=Application.class)
    private final IApplication application = new Application();
    
    private final Pmv myPmv = new Pmv();
    
    @MInclude(type=Pmv.class)
    public IPmv getPmv()
    {
      return myPmv;
    }
  }
  
  public interface IPmv
  {
    
  }

  public static class Pmv implements IPmv
  {
    @MAttribute(isWritable=true)
    private String pmvName = "TestPmv";
    
    private int count=0;
    
    @MOperation
    public void increaseCount()
    {
      count++;
    }
  }
  
  public interface IApplication
  {
    
  }

  public static class Application implements IApplication
  {
    private int cnt=0;

    @MAttribute
    public String getName()
    {
      return "TestApp";
    }
    
    @MOperation
    public void increaseCnt()
    {
      cnt++;
    }
    
  }


  public TestMIncludeWithType() throws MalformedObjectNameException
  {
    super(new TestBean(), "Test:type=TestType");
  }

  @Test
  public void testReadAttributePmvName() throws AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException
  {
    assertThat(getAttribute("pmvName")).isEqualTo("TestPmv");
  }
  
  @Test
  public void testWriteAttributePmvName() throws AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException, InvalidAttributeValueException
  {
    setAttribute("pmvName", "Hello World");    
    assertThat(((Pmv)testBean.getPmv()).pmvName).isEqualTo("Hello World");
  }

  @Test
  public void testReadAttributeName() throws AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException
  {
    assertThat(getAttribute("name")).isEqualTo("TestApp");
  }
  
  @Test
  public void testInvokeOperationIncreaseCount() throws InstanceNotFoundException, ReflectionException, MBeanException
  {
    assertThat(((Pmv)testBean.getPmv()).count).isEqualTo(0);
    invokeOperation("increaseCount");
    assertThat(((Pmv)testBean.getPmv()).count).isEqualTo(1);
  }

  @Test
  public void testInvokeOperationIncreaseCnt() throws InstanceNotFoundException, ReflectionException, MBeanException
  {
    assertThat(((Application)testBean.application).cnt).isEqualTo(0);
    invokeOperation("increaseCnt");
    assertThat(((Application)testBean.application).cnt).isEqualTo(1);
  }
}

package com.axonivy.jmx;

import static org.assertj.core.api.Assertions.assertThat;

import javax.management.NotCompliantMBeanException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.axonivy.jmx.MAttribute;
import com.axonivy.jmx.MBean;
import com.axonivy.jmx.MBeans;
import com.axonivy.jmx.MConstants;
import com.axonivy.jmx.MException;
import com.axonivy.jmx.internal.LogErrorStrategy;
import com.axonivy.jmx.util.LogTestAppender;

public class TestRegisterErrorStrategy
{
  private static final TestBean TEST_BEAN = new TestBean();
  private final LogTestAppender LOG_APPENDER = new LogTestAppender(Level.ERROR);

  @MBean(value="Test:type=TestType")
  public static class TestBean
  {
    @MAttribute
    private NotCompatibleOpenType attr;
  }

  public static class NotCompatibleOpenType
  {
  }

  @Before
  public void before()
  {
    Logger.getLogger(LogErrorStrategy.class).addAppender(LOG_APPENDER);
  }

  @After
  public void after()
  {
    MBeans.unregisterAllMBeans();
    MBeans.setRegisterMBeanErrorStrategy(MConstants.DEFAULT_ERROR_STRATEGY);
  }

  @Test(expected=MException.class)
  public void testDefaultStrategy()
  {
    MBeans.registerMBeanFor(TEST_BEAN);
  }

  @Test(expected=MException.class)
  public void testThrowRuntimeExceptionStrategy()
  {
    MBeans.setRegisterMBeanErrorStrategy(MConstants.THROW_RUNTIME_EXCEPTION_ERROR_STRATEGY);
    MBeans.registerMBeanFor(TEST_BEAN);
  }

  @Test
  public void testLogErrorStrategy()
  {
    MBeans.setRegisterMBeanErrorStrategy(MConstants.LOG_ERROR_STRATEGY);
    assertThat(LOG_APPENDER.getRecording()).isEmpty();
    MBeans.registerMBeanFor(TEST_BEAN);
    assertThat(LOG_APPENDER.getRecording()).contains("Could not register MBean '"+TEST_BEAN+"'");
    assertThat(LOG_APPENDER.getRecording()).contains(NotCompliantMBeanException.class.getName());
  }

  @Test
  public void testIgnoreErrorStrategy()
  {
    MBeans.setRegisterMBeanErrorStrategy(MConstants.IGNORE_ERROR_STRATEGY);
    assertThat(LOG_APPENDER.getRecording()).isEmpty();
  }

}

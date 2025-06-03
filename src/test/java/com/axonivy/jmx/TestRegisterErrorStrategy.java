package com.axonivy.jmx;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import javax.management.NotCompliantMBeanException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.axonivy.jmx.internal.LogErrorStrategy;
import com.axonivy.jmx.util.LogTestAppender;

public class TestRegisterErrorStrategy {
  private static final TestBean TEST_BEAN = new TestBean();
  private final LogTestAppender logAppender = new LogTestAppender(Level.ERROR);

  @MBean(value = "Test:type=TestType")
  public static class TestBean {
    @MAttribute
    private NotCompatibleOpenType attr;
  }

  public static class NotCompatibleOpenType {}

  @Before
  public void before() {
    Logger.getLogger(LogErrorStrategy.class).addAppender(logAppender);
  }

  @After
  public void after() {
    MBeans.unregisterAllMBeans();
    MBeans.setRegisterMBeanErrorStrategy(MConstants.DEFAULT_ERROR_STRATEGY);
  }

  @Test
  public void testDefaultStrategy() {
    assertThat(logAppender.getRecording()).isEmpty();
    MBeans.registerMBeanFor(TEST_BEAN);
    assertThat(logAppender.getRecording()).contains("Could not register MBean '" + TEST_BEAN + "'");
    assertThat(logAppender.getRecording()).contains(NotCompliantMBeanException.class.getName());
  }

  @Test
  public void testThrowRuntimeExceptionStrategy() {
    MBeans.setRegisterMBeanErrorStrategy(MConstants.THROW_RUNTIME_EXCEPTION_ERROR_STRATEGY);
    assertThatThrownBy(() -> MBeans.registerMBeanFor(TEST_BEAN)).isInstanceOf(MException.class);
  }

  @Test
  public void testLogErrorStrategy() {
    MBeans.setRegisterMBeanErrorStrategy(MConstants.LOG_ERROR_STRATEGY);
    assertThat(logAppender.getRecording()).isEmpty();
    MBeans.registerMBeanFor(TEST_BEAN);
    assertThat(logAppender.getRecording()).contains("Could not register MBean '" + TEST_BEAN + "'");
    assertThat(logAppender.getRecording()).contains(NotCompliantMBeanException.class.getName());
  }

  @Test
  public void testIgnoreErrorStrategy() {
    MBeans.setRegisterMBeanErrorStrategy(MConstants.IGNORE_ERROR_STRATEGY);
    assertThat(logAppender.getRecording()).isEmpty();
  }

}

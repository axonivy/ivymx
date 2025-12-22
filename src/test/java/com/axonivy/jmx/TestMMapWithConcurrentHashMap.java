package com.axonivy.jmx;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import javax.management.MalformedObjectNameException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestMMapWithConcurrentHashMap extends BaseMTest<TestMMapWithConcurrentHashMap.TestBean> {
  private final Map<String, TestBean> testMap = MCollections.managedMap(new ConcurrentHashMap<String, TestBean>());

  @MBean(value = "Test:type=TestType,id=#{id}")
  public static class TestBean {
    private static AtomicInteger global = new AtomicInteger();
    private final int id = global.getAndIncrement();
    public String getId() {
      return "" + id;
    }
  }
  public TestMMapWithConcurrentHashMap() throws MalformedObjectNameException {
    super(null, "Test:type=TestType,id=" + (TestBean.global.get()));
  }

  @Override
  @Before
  public void before() {
    MBeans.setRegisterMBeanErrorStrategy(MConstants.THROW_RUNTIME_EXCEPTION_ERROR_STRATEGY);
  }

  @Override
  @After
  public void after() {
    MBeans.setRegisterMBeanErrorStrategy(MConstants.DEFAULT_ERROR_STRATEGY);
    MBeans.unregisterAllMBeans();
  }

  @Test
  public void computeIfAbstent() throws InterruptedException {
    var threads = new BeanCreator[8];
    for (int i = 0; i < threads.length; i++) {
      threads[i] = new BeanCreator();
    }
    for (int i = 0; i < threads.length; i++) {
      threads[i].start();
    }
    for (int i = 0; i < threads.length; i++) {
      threads[i].join();
    }
    var bean = threads[0].bean;
    assertThat(bean).isNotNull();
    for (int i = 0; i < threads.length; i++) {
      threads[i].assertNoError();
      assertThat(threads[i].bean).isSameAs(bean);
    }
    assertRegistered();
    assertThat(TestBean.global.get()).isEqualTo(1);
  }

  private final class BeanCreator extends Thread {
    private TestBean bean;
    private Throwable error;
    
    public void run() {
      try {
        bean = testMap.computeIfAbsent("blah", key -> new TestBean());
      } catch (Throwable th) {
        error = th;
      }
    }

    void assertNoError() {
      assertThat(error).isNull();
    }
  }
}

package com.axonivy.jmx;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiFunction;
import java.util.function.Function;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestMMap extends BaseMTest<TestMMap.TestBean> {
  private final Map<String, TestBean> testMap = MCollections.managedMap(new HashMap<String, TestBean>());

  @MBean(value = "Test:type=TestType,id=#{id}")
  public static class TestBean {
    private static int global = 0;
    private final int id = global++;
    public String getId() {
      return "" + id;
    }
  }

  @MBean(value = HelloBean.TEST_NAME_HELLO)
  public static class HelloBean extends TestBean {
    static final String TEST_NAME_HELLO = "Test:name=Hello";
  }

  public TestMMap() throws MalformedObjectNameException {
    super(new TestBean(), "Test:type=TestType,id=" + (TestBean.global - 1));
  }

  @Override
  @BeforeEach
  public void before() {
    MBeans.setRegisterMBeanErrorStrategy(MConstants.THROW_RUNTIME_EXCEPTION_ERROR_STRATEGY);
  }

  @Override
  @AfterEach
  public void after() {
    MBeans.setRegisterMBeanErrorStrategy(MConstants.DEFAULT_ERROR_STRATEGY);
    MBeans.unregisterAllMBeans();
  }

  @Test
  public void put() {
    testMap.put("blah", testBean);
    assertRegistered();
  }

  @Test
  public void put_same_key() {
    testMap.put("blah", testBean);
    testMap.put("blah", new TestBean());
    assertNotRegistered();
  }

  @Test
  public void put_same_key_2() throws MalformedObjectNameException {
    HelloBean bean1 = new HelloBean();
    HelloBean bean2 = new HelloBean();
    testMap.put("blah", bean1);
    testMap.put("blah", bean2);
    assertThat(MBeans.getMBeanServer().isRegistered(new ObjectName("Test:name=Hello"))).isTrue();
  }

  @Test
  public void put_same_key_and_value() {
    testMap.put("blah", testBean);
    testMap.put("blah", testBean);
    assertRegistered();
  }

  @Test
  public void put_null() {
    testMap.put("blah", testBean);
    testMap.put("blah", null);
    assertNotRegistered();
  }

  @Test
  public void remove() {
    testMap.put("blah", testBean);
    testMap.remove("blah");
    assertNotRegistered();
  }

  @Test
  public void clear() {
    testMap.put("blah", testBean);
    testMap.clear();
    assertNotRegistered();
  }

  @Test
  public void putAll() {
    Map<String, TestBean> map = new HashMap<>();
    map.put("blah", testBean);
    
    testMap.putAll(map);
    
    assertRegistered();
  }

  @Test 
  public void computeIfAbsent_absent() {
    var methodWasCalled = new AtomicBoolean();
    var originalMap = new HashMap<String, TestBean>() {
      @Override
      public TestBean computeIfAbsent(String key, Function<? super String, ? extends TestBean> fct) {
        methodWasCalled.set(true);
        return super.computeIfAbsent(key, fct);
      }
    };
    var test = MCollections.managedMap(originalMap);
    
    var result = test.computeIfAbsent("blah", k -> testBean);
    
    assertRegistered();
    assertThat(result).isSameAs(testBean);
    assertThat(test.get("blah")).isSameAs(testBean);
    assertThat(methodWasCalled).isTrue();
  }

  @Test 
  public void computeIfAbsent_present() {
    testMap.put("blah", testBean);

    var result = testMap.computeIfAbsent("blah", k -> null);
    
    assertRegistered();
    assertThat(testMap.get("blah")).isSameAs(testBean);
    assertThat(result).isSameAs(testBean);
  }

  @Test 
  public void computeIfAbsent_newValue_null() {
    var result = testMap.computeIfAbsent("blah", k -> null);
   
    assertNotRegistered();
    assertThat(testMap.get("blah")).isNull();
    assertThat(testMap.containsKey("blah")).isFalse();
    assertThat(result).isNull();
  }

  @Test 
  public void computeIfAbsent_oldValue_null() {
    testMap.put("blah", null);
   
    var result = testMap.computeIfAbsent("blah", k -> testBean);
   
    assertRegistered();
    assertThat(testMap.get("blah")).isSameAs(testBean);
    assertThat(result).isSameAs(testBean);
  }

  @Test 
  public void putIfAbsent_absent() {
    var methodWasCalled = new AtomicBoolean();
    var originalMap = new HashMap<String, TestBean>() {
      @Override
      public TestBean putIfAbsent(String key, TestBean value) {
        methodWasCalled.set(true);
        return super.putIfAbsent(key, value);
      }
    };
    var test = MCollections.managedMap(originalMap);
   
    var result = test.putIfAbsent("blah", testBean);
   
    assertRegistered();
    assertThat(result).isNull();
    assertThat(test.get("blah")).isSameAs(testBean);
    assertThat(methodWasCalled).isTrue();
  }

  @Test 
  public void putIfAbsent_present() {
    testMap.put("blah", testBean);
   
    var result = testMap.putIfAbsent("blah", null);
   
    assertRegistered();
    assertThat(testMap.get("blah")).isSameAs(testBean);
    assertThat(result).isSameAs(testBean);
  }

  @Test 
  public void putIfAbsent_newValue_null() {
    var result = testMap.putIfAbsent("blah", null);
   
    assertNotRegistered();
    assertThat(testMap.get("blah")).isNull();
    assertThat(testMap.containsKey("blah")).isTrue();
    assertThat(result).isNull();
  }

  @Test 
  public void putIfAbsent_oldValue_null() {
    testMap.put("blah", null);
   
    var result = testMap.putIfAbsent("blah", testBean);
   
    assertRegistered();
    assertThat(result).isNull();;
    assertThat(testMap.get("blah")).isSameAs(testBean);
  }

  @Test 
  public void replace() {
    var methodWasCalled = new AtomicBoolean();
    var originalMap = new HashMap<String, TestBean>() {
      @Override
      public TestBean replace(String key, TestBean value) {
        methodWasCalled.set(true);
        return super.replace(key, value);
      }
    };
    var test = MCollections.managedMap(originalMap);
   
    var result = test.replace("blah", testBean);
   
    assertNotRegistered();
    assertThat(result).isNull();
    assertThat(test.get("blah")).isNull();
    assertThat(test.containsKey("blah")).isFalse();
    assertThat(methodWasCalled).isTrue();
  }

  @Test 
  public void replace_oldValue_null() {
    testMap.put("blah", null);
   
    var result = testMap.replace("blah", testBean);
   
    assertRegistered();
    assertThat(result).isNull();
    assertThat(testMap.get("blah")).isSameAs(testBean);
  }

  @Test
  public void replace_newValue_null() {
    testMap.put("blah", null);
   
    var result = testMap.replace("blah", null);
   
    assertNotRegistered();
    assertThat(result).isNull();
    assertThat(testMap.get("blah")).isNull();
    assertThat(testMap.containsKey("blah")).isTrue();
  }

  @Test
  public void replace_oldValue_not_null() {
    testMap.put("blah", testBean);
    assertRegistered();
   
    var result = testMap.replace("blah", null);
   
    assertNotRegistered();
    assertThat(result).isSameAs(testBean);
    assertThat(testMap.get("blah")).isNull();
    assertThat(testMap.containsKey("blah")).isTrue();
  }

  @Test
  public void replace_old_with_new() throws MalformedObjectNameException {
    testMap.put("blah", testBean);
    assertRegistered();
    var testBean2 = new HelloBean();
   
    var result = testMap.replace("blah", testBean2);
   
    assertNotRegistered();
    assertThat(MBeans.getMBeanServer().isRegistered(new ObjectName(HelloBean.TEST_NAME_HELLO))).isTrue();
    assertThat(result).isSameAs(testBean);
    assertThat(testMap.get("blah")).isSameAs(testBean2);
  }

  @Test
  public void replaceAll() {
      var methodWasCalled = new AtomicBoolean();
    var originalMap = new HashMap<String, TestBean>() {
      @Override
      public void replaceAll(BiFunction<? super String, ? super TestBean, ? extends TestBean> function) {
        methodWasCalled.set(true);
        super.replaceAll(function);
      }
    };
    var test = MCollections.managedMap(originalMap);
   
    test.replaceAll((key, bean) -> testBean);
   
    assertNotRegistered();
    assertThat(test.get("blah")).isNull();
    assertThat(methodWasCalled).isTrue();
  }
  
  @Test 
  public void replaceAll_oldValue_null() {
    testMap.put("blah", null);
   
    testMap.replaceAll((key, oldValue) -> testBean);
   
    assertRegistered();
    assertThat(testMap.get("blah")).isSameAs(testBean);
  }

  @Test
  public void replaceAll_newValue_null() {
    testMap.put("blah", null);
   
    testMap.replaceAll((key, oldValue) -> null);
   
    assertNotRegistered();
    assertThat(testMap.get("blah")).isNull();
    assertThat(testMap.containsKey("blah")).isTrue();
  }

  @Test
  public void replaceAll_oldValue_not_null() {
    testMap.put("blah", testBean);
    assertRegistered();
   
    testMap.replaceAll((key, oldValue) -> null);
   
    assertNotRegistered();
    assertThat(testMap.get("blah")).isNull();
    assertThat(testMap.containsKey("blah")).isTrue();
  }

  @Test
  public void replaceAll_old_with_new() throws MalformedObjectNameException {
    testMap.put("blah", testBean);
    assertRegistered();
    var testBean2 = new HelloBean();
   
    testMap.replace("blah", testBean2);
   
    assertNotRegistered();
    assertThat(MBeans.getMBeanServer().isRegistered(new ObjectName(HelloBean.TEST_NAME_HELLO))).isTrue();
    assertThat(testMap.get("blah")).isSameAs(testBean2);
  }

  @Test 
  public void compute() {
    var methodWasCalled = new AtomicBoolean();
    var originalMap = new HashMap<String, TestBean>() {
      @Override
      public TestBean compute(String key,
          BiFunction<? super String, ? super TestBean, ? extends TestBean> remappingFunction) {
        methodWasCalled.set(true);
        return super.compute(key, remappingFunction);
      }
    };
    var test = MCollections.managedMap(originalMap);
   
    var result = test.compute("blah", (key, bean) -> testBean);
   
    assertRegistered();
    assertThat(result).isSameAs(testBean);
    assertThat(test.get("blah")).isSameAs(testBean);
    assertThat(methodWasCalled).isTrue();
  }

  @Test 
  public void compute_oldValue_null() {
    testMap.put("blah", null);
   
    var result = testMap.compute("blah", (key, oldValue) -> testBean);
   
    assertRegistered();
    assertThat(result).isSameAs(testBean);
    assertThat(testMap.get("blah")).isSameAs(testBean);
  }

  @Test
  public void compute_newValue_null() {
    testMap.put("blah", null);
   
    var result = testMap.compute("blah", (key, oldValue) -> null);
   
    assertNotRegistered();
    assertThat(result).isNull();
    assertThat(testMap.get("blah")).isNull();
    assertThat(testMap.containsKey("blah")).isFalse();
  }

  @Test
  public void compute_oldValue_not_null() {
    testMap.put("blah", testBean);
    assertRegistered();
   
    var result = testMap.compute("blah", (key, oldValue) -> null);
   
    assertNotRegistered();
    assertThat(result).isNull();
    assertThat(testMap.get("blah")).isNull();
    assertThat(testMap.containsKey("blah")).isFalse();
  }

  @Test
  public void compute_old_with_new() throws MalformedObjectNameException {
    testMap.put("blah", testBean);
    assertRegistered();
    var testBean2 = new HelloBean();
   
    var result = testMap.compute("blah", (key, oldValue) -> testBean2);
   
    assertNotRegistered();
    assertThat(MBeans.getMBeanServer().isRegistered(new ObjectName(HelloBean.TEST_NAME_HELLO))).isTrue();
    assertThat(result).isSameAs(testBean2);
    assertThat(testMap.get("blah")).isSameAs(testBean2);
  }

  @Test 
  public void merge() {
    var methodWasCalled = new AtomicBoolean();
    var originalMap = new HashMap<String, TestBean>() {
      @Override
      public TestBean merge(String key, TestBean value,
              BiFunction<? super TestBean, ? super TestBean, ? extends TestBean> remappingFunction) {
        methodWasCalled.set(true);
        return super.merge(key, value, remappingFunction);
      }
    };
    var test = MCollections.managedMap(originalMap);
   
    var result = test.merge("blah", testBean, (oldValue, newValue) -> newValue);
   
    assertRegistered();
    assertThat(result).isSameAs(testBean);
    assertThat(test.get("blah")).isSameAs(testBean);
    assertThat(methodWasCalled).isTrue();
  }

  @Test 
  public void merge_oldValue_null() {
    testMap.put("blah", null);
   
    var result = testMap.merge("blah", testBean, (oldValue, newValue) -> newValue);
   
    assertRegistered();
    assertThat(result).isSameAs(testBean);
    assertThat(testMap.get("blah")).isSameAs(testBean);
  }

  @Test
  public void merge_newValue_null() {
    testMap.put("blah", null);
   
    var result = testMap.merge("blah", testBean, (oldValue, newValue) -> null);
   
    assertRegistered();
    assertThat(result).isSameAs(testBean);
    assertThat(testMap.get("blah")).isSameAs(testBean);
  }

  @Test
  public void merge_oldValue_not_null() {
    testMap.put("blah", testBean);
    assertRegistered();
   
    var result = testMap.merge("blah", testBean, (oldValue, newValue) -> null);
   
    assertNotRegistered();
    assertThat(result).isNull();
    assertThat(testMap.get("blah")).isNull();
    assertThat(testMap.containsKey("blah")).isFalse();
  }

  @Test
  public void merge_old_with_new() throws MalformedObjectNameException {
    testMap.put("blah", testBean);
    assertRegistered();
    var testBean2 = new HelloBean();
   
    var result = testMap.merge("blah", testBean2, (oldValue, newValue) -> newValue);
   
    assertNotRegistered();
    assertThat(MBeans.getMBeanServer().isRegistered(new ObjectName(HelloBean.TEST_NAME_HELLO))).isTrue();
    assertThat(result).isSameAs(testBean2);
    assertThat(testMap.get("blah")).isSameAs(testBean2);
  }

  @Test
  public void merge_old_with_old() throws MalformedObjectNameException {
    testMap.put("blah", testBean);
    assertRegistered();
    var testBean2 = new HelloBean();
   
    var result = testMap.merge("blah", testBean2, (oldValue, newValue) -> oldValue);
   
    assertRegistered();
    assertThat(MBeans.getMBeanServer().isRegistered(new ObjectName(HelloBean.TEST_NAME_HELLO))).isFalse();
    assertThat(result).isSameAs(testBean);
    assertThat(testMap.get("blah")).isSameAs(testBean);
  }
}


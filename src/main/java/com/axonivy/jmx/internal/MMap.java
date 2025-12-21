package com.axonivy.jmx.internal;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.axonivy.jmx.MBean;

/**
 * Proxy for the given original map that:
 * <ul>
 * <li>Registers every {@link MBean} object that is added to the map.</li>
 * <li>Unregisters every {@link MBean} object that is removed from the map.</li>
 * </ul>
 * @author rwei
 * @since 01.07.2013
 * @param <T> map key type
 * @param <V> map value type
 */
public class MMap<T, V> implements Map<T, V> {
  private final Map<T, V> originalMap;

  private final MBeanManager manager = MBeanManager.getInstance();

  /**
   * Constructor
   * @param originalMap original map
   */
  public MMap(Map<T, V> originalMap) {
    this.originalMap = originalMap;
  }

  /**
   * @see java.util.AbstractMap#entrySet()
   */
  @Override
  public Set<java.util.Map.Entry<T, V>> entrySet() {
    return originalMap.entrySet();
  }

  /**
   * @see java.util.AbstractMap#put(java.lang.Object, java.lang.Object)
   */
  @Override
  public V put(T key, V value) {
    V oldValue = originalMap.put(key, value);
    replaceMBean(oldValue, value);
    return oldValue;
  }

  /**
   * @see java.util.Map#size()
   */
  @Override
  public int size() {
    return originalMap.size();
  }

  /**
   * @see java.util.Map#isEmpty()
   */
  @Override
  public boolean isEmpty() {
    return originalMap.isEmpty();
  }

  /**
   * @see java.util.Map#containsKey(java.lang.Object)
   */
  @Override
  public boolean containsKey(Object key) {
    return originalMap.containsKey(key);
  }

  /**
   * @see java.util.Map#containsValue(java.lang.Object)
   */
  @Override
  public boolean containsValue(Object value) {
    return originalMap.containsValue(value);
  }

  /**
   * @see java.util.Map#get(java.lang.Object)
   */
  @Override
  public V get(Object key) {
    return originalMap.get(key);
  }

  /**
   * @see java.util.Map#remove(java.lang.Object)
   */
  @Override
  public V remove(Object key) {
    V removedObject = originalMap.remove(key);
    manager.ifAnnotatedUnregisterMBeanFor(removedObject);
    return removedObject;
  }

  /**
   * @see java.util.Map#putAll(java.util.Map)
   */
  @Override
  public void putAll(Map<? extends T, ? extends V> m) {
    for (Map.Entry<? extends T, ? extends V> entry : m.entrySet()) {
      put(entry.getKey(), entry.getValue());
    }
  }

  /**
   * @see java.util.Map#clear()
   */
  @Override
  public void clear() {
    for (V value : values()) {
      manager.ifAnnotatedUnregisterMBeanFor(value);
    }
    originalMap.clear();
  }

  /**
   * @see java.util.Map#keySet()
   */
  @Override
  public Set<T> keySet() {
    return originalMap.keySet();
  }

  /**
   * @see java.util.Map#values()
   */
  @Override
  public Collection<V> values() {
    return originalMap.values();
  }

  @Override
  public V computeIfAbsent(T key, Function<? super T, ? extends V> mappingFunction) {
    V value = originalMap.computeIfAbsent(key, mappingFunction);
    manager.ifAnnotatedRegisterMBeanFor(value);
    return value;
  }

  @Override
  public V computeIfPresent(T key, BiFunction<? super T, ? super V, ? extends V> remappingFunction) {
    BiFunction<? super T, ? super V, ? extends V> fct = (k, oldValue) -> {
      var newValue = remappingFunction.apply(k, oldValue);
      if (newValue != oldValue && oldValue != null) {
        manager.ifAnnotatedUnregisterMBeanFor(oldValue);
      }
      return newValue;
    };
    V value = originalMap.computeIfPresent(key, fct);
    if (value != null) {
      manager.ifAnnotatedRegisterMBeanFor(value);
    } 
    return value;
  }

  @Override
  public V putIfAbsent(T key, V value) {
    V oldValue = originalMap.putIfAbsent(key, value);
    if (oldValue == null) {
      if  (originalMap.get(key) == value) {
        manager.ifAnnotatedRegisterMBeanFor(value);
      }
    } 
    return oldValue;
  }

  @Override
  public V replace(T key, V value) {
    var oldValue = originalMap.replace(key, value);
    if (oldValue != null) {
      manager.ifAnnotatedUnregisterMBeanFor(oldValue);
    }
    if (value != null && originalMap.get(key) == value) {
      manager.ifAnnotatedRegisterMBeanFor(value);
    }
    return oldValue;
  }

  @Override
  public void replaceAll(BiFunction<? super T, ? super V, ? extends V> function) {
    BiFunction<? super T, ? super V, ? extends V> fct = (key, oldValue) -> {
      var newValue = function.apply(key, oldValue);
      replaceMBean(oldValue, newValue);
      return newValue;
    };
    originalMap.replaceAll(fct);
  }

  @Override
  public V compute(T key, BiFunction<? super T, ? super V, ? extends V> remappingFunction) {
    BiFunction<? super T, ? super V, ? extends V> fct = (k, oldValue) -> {
      var newValue = remappingFunction.apply(key, oldValue);
      replaceMBean(oldValue, newValue);
      return newValue;
    };
    return originalMap.compute(key, fct);
  }

  @Override
  public V merge(T key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
    BiFunction<? super V, ? super V, ? extends V> fct = (oldValue, newValue) -> {
      var v = remappingFunction.apply(oldValue, newValue);
      if (v != oldValue && oldValue != null) {
        manager.ifAnnotatedUnregisterMBeanFor(oldValue);
      }
      return v;
    };
    var newValue = originalMap.merge(key, value, fct);
    if (newValue != null && newValue == value) {
      manager.ifAnnotatedRegisterMBeanFor(newValue);
    }
    return newValue;
  }

  private void replaceMBean(V oldValue, V newValue) {
    if (oldValue == newValue) {
      return;
    }
    if (oldValue != null) {
      manager.ifAnnotatedUnregisterMBeanFor(oldValue);
    }
    if (newValue != null) {
      manager.ifAnnotatedRegisterMBeanFor(newValue);
    }
  }
}

package com.axonivy.jmx.internal;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

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
public class MMap<T, V> implements Map<T, V>
{
  private Map<T, V> originalMap;

  private MBeanManager manager = MBeanManager.getInstance();

  /**
   * Constructor
   * @param originalMap original map
   */
  public MMap(Map<T, V> originalMap)
  {
    this.originalMap = originalMap;
  }

  /**
   * @see java.util.AbstractMap#entrySet()
   */
  @Override
  public Set<java.util.Map.Entry<T, V>> entrySet()
  {
    return originalMap.entrySet();
  }

  /**
   * @see java.util.AbstractMap#put(java.lang.Object, java.lang.Object)
   */
  @Override
  public V put(T key, V value)
  {
    manager.ifAnnotatedRegisterMBeanFor(value);
    V previousValue = originalMap.put(key, value);
    if (value != previousValue)
    {
      manager.ifAnnotatedUnregisterMBeanFor(previousValue);
    }
    return previousValue;
  }

  /**
   * @see java.util.Map#size()
   */
  @Override
  public int size()
  {
    return originalMap.size();
  }

  /**
   * @see java.util.Map#isEmpty()
   */
  @Override
  public boolean isEmpty()
  {
    return originalMap.isEmpty();
  }

  /**
   * @see java.util.Map#containsKey(java.lang.Object)
   */
  @Override
  public boolean containsKey(Object key)
  {
    return originalMap.containsKey(key);
  }

  /**
   * @see java.util.Map#containsValue(java.lang.Object)
   */
  @Override
  public boolean containsValue(Object value)
  {
    return originalMap.containsValue(value);
  }

  /**
   * @see java.util.Map#get(java.lang.Object)
   */
  @Override
  public V get(Object key)
  {
    return originalMap.get(key);
  }

  /**
   * @see java.util.Map#remove(java.lang.Object)
   */
  @Override
  public V remove(Object key)
  {
    V removedObject = originalMap.remove(key);
    manager.ifAnnotatedUnregisterMBeanFor(removedObject);
    return removedObject;
  }

  /**
   * @see java.util.Map#putAll(java.util.Map)
   */
  @Override
  public void putAll(Map<? extends T, ? extends V> m)
  {
    for (Map.Entry<? extends T, ? extends V> entry : m.entrySet())
    {
      manager.ifAnnotatedRegisterMBeanFor(entry.getValue());
      Object previousValue = originalMap.put(entry.getKey(), entry.getValue());
      if (previousValue != entry.getValue())
      {
        manager.ifAnnotatedUnregisterMBeanFor(previousValue);
      }
    }
  }

  /**
   * @see java.util.Map#clear()
   */
  @Override
  public void clear()
  {
    for (V value : values())
    {
      manager.ifAnnotatedUnregisterMBeanFor(value);
    }
    originalMap.clear();
  }

  /**
   * @see java.util.Map#keySet()
   */
  @Override
  public Set<T> keySet()
  {
    return originalMap.keySet();
  }

  /**
   * @see java.util.Map#values()
   */
  @Override
  public Collection<V> values()
  {
    return originalMap.values();
  }
}

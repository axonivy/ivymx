package com.axonivy.jmx;

import java.util.List;
import java.util.Map;

import com.axonivy.jmx.internal.MList;
import com.axonivy.jmx.internal.MMap;

/**
 * A managed collection automatically registers MBeans added to the collection and unregister them if they are removed from the collection.<br>
 * Example:
 * <pre>{@code private List<MyBean> beans = MCollections.managedList(new ArrayList<MyBean>());}</pre>
 * @author rwei
 * @since 28.06.2013
 */
public class MCollections {
  /**
   * Converts the given list to a managed list. MBeans added are automatically register. MBeans removed are unregistered.
   * @param originalList list to convert to a managed list.
   * @param <T> list element type
   * @return managed list
   */
  public static <T> List<T> managedList(List<T> originalList) {
    return new MList<>(originalList);
  }

  /**
   * Converts the given map to a managed map. MBeans put are automatically register. MBeans removed are unregistered.
   * @param originalMap map to convert to a managed map.
   * @param <T> map key type
   * @param <V> map value type
   * @return managed map
   */
  public static <T, V> Map<T, V> managedMap(Map<T, V> originalMap) {
    return new MMap<>(originalMap);
  }
}

package com.axonivy.jmx.internal;

import java.util.AbstractList;
import java.util.List;

import com.axonivy.jmx.MBean;

/**
 * Proxy for the given original list that:
 * <ul>
 * <li>Registers every {@link MBean} object that is added to the list.</li>
 * <li>Unregisters every {@link MBean} object that is removed from the list.</li>
 * </ul>
 * @author rwei
 * @since 01.07.2013
 * @param <T> list element type
 */
public class MList<T> extends AbstractList<T> implements List<T> {
  private List<T> originalList;

  private MBeanManager manager = MBeanManager.getInstance();

  public MList(List<T> originalList) {
    this.originalList = originalList;
  }

  /**
   * @see java.util.AbstractList#get(int)
   */
  @Override
  public T get(int index) {
    return originalList.get(index);
  }

  /**
   * @see java.util.AbstractCollection#size()
   */
  @Override
  public int size() {
    return originalList.size();
  }

  /**
   * @see java.util.AbstractList#set(int, java.lang.Object)
   */
  @Override
  public T set(int index, T element) {
    T previousElement;
    manager.ifAnnotatedRegisterMBeanFor(element);
    previousElement = originalList.set(index, element);
    manager.ifAnnotatedUnregisterMBeanFor(previousElement);
    return previousElement;
  }

  /**
   * @see java.util.AbstractList#add(int, java.lang.Object)
   */
  @Override
  public void add(int index, T element) {
    manager.ifAnnotatedRegisterMBeanFor(element);
    originalList.add(index, element);
  }

  /**
   * @see java.util.AbstractList#remove(int)
   */
  @Override
  public T remove(int index) {
    T object = originalList.remove(index);
    manager.ifAnnotatedUnregisterMBeanFor(object);
    return object;
  }
}

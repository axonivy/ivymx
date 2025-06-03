package com.axonivy.jmx.internal;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for all other creator classes.
 * @author rwei
 * @since 01.07.2013
 */
class MCreator {
  protected MBeanManager manager;
  protected Class<?> mBeanClass;
  protected AbstractValueAccessor targetAccessor;
  protected String targetAccessPath;

  protected MCreator(MBeanManager manager, Class<?> mBeanClass) {
    this(manager, mBeanClass, new BeanValueAccessor());
  }

  protected MCreator(MBeanManager manager, Class<?> mBeanClass, AbstractValueAccessor targetAccessor) {
    this.targetAccessor = targetAccessor;
    this.manager = manager;
    this.mBeanClass = mBeanClass;
  }

  protected List<Class<?>> getClassesToAnalyze() {
    List<Class<?>> classesToAnalyze = new ArrayList<Class<?>>();
    getClassesToAnalyze(mBeanClass, classesToAnalyze);
    return classesToAnalyze;
  }

  private void getClassesToAnalyze(Class<? extends Object> clazz, List<Class<?>> classesToAnalyze) {
    classesToAnalyze.add(clazz);
    for (Class<?> implementedInterface : clazz.getInterfaces()) {
      getClassesToAnalyze(implementedInterface, classesToAnalyze);
    }
    if (clazz.getSuperclass() != null) {
      getClassesToAnalyze(clazz.getSuperclass(), classesToAnalyze);
    }
  }
}

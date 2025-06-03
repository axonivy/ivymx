package com.axonivy.jmx.internal;

import java.lang.reflect.Type;
import java.util.concurrent.ConcurrentHashMap;

import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenType;

import com.axonivy.jmx.MComposite;

/**
 * Strategy that converts java classes with a {@link MComposite} annotation to a {@link CompositeType}
 * @author rwei
 * @since 01.07.2013
 */
class CompositeTypeConverterStrategy implements OpenTypeConverterStrategy {
  private ConcurrentHashMap<Class<?>, MCompositeType> mCompositeTypes = new ConcurrentHashMap<Class<?>, MCompositeType>();
  private MBeanManager manager;

  CompositeTypeConverterStrategy(MBeanManager manager) {
    this.manager = manager;
  }

  @Override
  public boolean canHandle(Type type) {
    if (type instanceof Class) {
      Class<?> clazz = (Class<?>) type;
      return clazz.isAnnotationPresent(MComposite.class);
    }
    return false;
  }

  @Override
  public OpenType<?> toOpenType(Type type) {
    return getMCompositeType(type).getOpenType();
  }

  private MCompositeType getMCompositeType(Type mCompositeClass) {
    Class<?> clazz = (Class<?>) mCompositeClass;
    MCompositeType mCompositeType = mCompositeTypes.get(clazz);
    if (mCompositeType == null) {
      mCompositeType = new MCompositeType(manager, clazz);
      MCompositeType alreadyRegistered = mCompositeTypes.putIfAbsent(clazz, mCompositeType);
      if (alreadyRegistered != null) {
        mCompositeType = alreadyRegistered;
      }
    }
    return mCompositeType;
  }

  @Override
  public AbstractValueConverter getValueConverter(Type type) {
    return getMCompositeType(type).getValueConverter();
  }

}

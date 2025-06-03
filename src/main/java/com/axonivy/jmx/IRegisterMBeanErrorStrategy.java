package com.axonivy.jmx;

/**
 * Strategy what to do if an error occurs during the registration of an {@link MBean}
 * @see MBeans#setRegisterMBeanErrorStrategy(IRegisterMBeanErrorStrategy)
 */
public interface IRegisterMBeanErrorStrategy {
  /**
   * Called if an error occurs during the registration of an {@link MBean}
   * @param mBean the mbean that fails to register
   * @param error the error that occured
   */
  void errorRegisteringMBean(Object mBean, Throwable error);
}

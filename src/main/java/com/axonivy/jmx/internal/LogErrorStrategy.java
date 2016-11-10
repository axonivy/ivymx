package com.axonivy.jmx.internal;

import java.text.MessageFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.axonivy.jmx.IRegisterMBeanErrorStrategy;

public class LogErrorStrategy implements IRegisterMBeanErrorStrategy
{
  public static final IRegisterMBeanErrorStrategy INSTANCE = new LogErrorStrategy();

  private LogErrorStrategy()
  {
  }

  private Logger logger = LoggerFactory.getLogger(LogErrorStrategy.class);

  @Override
  public void errorRegisteringMBean(Object mBean, Throwable error)
  {
	if (logger.isErrorEnabled())
	{
		String msg = MessageFormat.format("Could not register MBean ''{0}''", mBean);
		logger.error(msg, error);
	}
  }
}

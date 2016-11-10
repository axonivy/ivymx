package com.axonivy.jmx;

import javax.management.MBeanException;

/**
 * Exception thrown by the management library if an {@link MBean} cannot be registered
 */
public class MException extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	public MException(Throwable cause)
	{
		super(cause);
	}

	public MException(String message, MBeanException cause)
	{
		super(message, cause);
	}
}

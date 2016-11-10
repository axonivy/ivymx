package com.axonivy.jmx.util;

import java.util.Collection;

import javax.management.openmbean.CompositeData;
import javax.management.openmbean.TabularData;

/**
 * Utility class that provides useful functions to manipulate JMX objects
 */
public class MUtils
{
  @SuppressWarnings("unchecked")
  public static Collection<CompositeData> toRows(TabularData tabularData)
  {
    return (Collection<CompositeData>) tabularData.values();
  }
}

package com.axonivy.jmx.example1;

import com.axonivy.jmx.MBean;
import com.axonivy.jmx.MBeans;

@MBean("GettingStarted:name=Person")
public class Person
{
  public static void main(String[] args)
  {
    Person person = new Person();
    MBeans.registerMBeanFor(person);
  }
}

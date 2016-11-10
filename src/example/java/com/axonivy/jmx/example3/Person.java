package com.axonivy.jmx.example3;

import com.axonivy.jmx.MAttribute;
import com.axonivy.jmx.MBean;
import com.axonivy.jmx.MBeans;

@MBean("GettingStarted:name=Person")
public class Person
{
  @MAttribute(isWritable = true)
  private String firstName = "Reto";

  public static void main(String[] args)
  {
    Person person = new Person();
    MBeans.registerMBeanFor(person);
  }
}

package com.axonivy.jmx.example5;

import com.axonivy.jmx.MBean;
import com.axonivy.jmx.MBeans;

@MBean("GettingStarted:type=Person,name=#{firstName}")
public class Person
{
  private String firstName;

  public Person(String firstName)
  {
    this.firstName = firstName;
  }

  public static void main(String[] args)
  {
    Person reto = new Person("Reto");
    MBeans.registerMBeanFor(reto);

    Person flavio = new Person("Flavio");
    MBeans.registerMBeanFor(flavio);
  }
}

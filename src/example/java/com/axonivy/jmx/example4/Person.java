package com.axonivy.jmx.example4;

import com.axonivy.jmx.MBean;
import com.axonivy.jmx.MBeans;
import com.axonivy.jmx.MOperation;

@MBean("GettingStarted:name=Person")
public class Person
{
  private String firstName = "Reto";

  @MOperation
  private void print()
  {
    System.out.println(firstName);
  }

  public static void main(String[] args)
  {
    Person person = new Person();
    MBeans.registerMBeanFor(person);
  }
}

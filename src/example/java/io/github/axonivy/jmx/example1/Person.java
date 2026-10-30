package io.github.axonivy.jmx.example1;

import io.github.axonivy.jmx.MBean;
import io.github.axonivy.jmx.MBeans;

@MBean("GettingStarted:name=Person")
public class Person {
  public static void main(String[] args) {
    Person person = new Person();
    MBeans.registerMBeanFor(person);
  }
}

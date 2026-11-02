package io.github.axonivy.jmx.example2;

import io.github.axonivy.jmx.MAttribute;
import io.github.axonivy.jmx.MBean;
import io.github.axonivy.jmx.MBeans;

@MBean("GettingStarted:name=Person")
public class Person {
  @MAttribute
  private String firstName = "Reto";

  public static void main(String[] args) {
    Person person = new Person();
    MBeans.registerMBeanFor(person);
  }
}

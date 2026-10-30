package io.github.axonivy.jmx.example4;

import io.github.axonivy.jmx.MBean;
import io.github.axonivy.jmx.MBeans;
import io.github.axonivy.jmx.MOperation;

@MBean("GettingStarted:name=Person")
public class Person {
  private String firstName = "Reto";

  @MOperation
  private void print() {
    System.out.println(firstName);
  }

  public static void main(String[] args) {
    Person person = new Person();
    MBeans.registerMBeanFor(person);
  }
}

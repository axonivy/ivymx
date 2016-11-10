package com.axonivy.jmx.example6;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import com.axonivy.jmx.MAttribute;
import com.axonivy.jmx.MBean;
import com.axonivy.jmx.MCompositionReference;
import com.axonivy.jmx.MInclude;
import com.axonivy.jmx.MOperation;
import com.axonivy.jmx.MOperation.Impact;
import com.axonivy.jmx.MSizeAttribute;

@MBean(value = "GettingStarted:type=Person,name=#{firstName}", description = "This is #{firstName} #{name} living in #{home.city}")
public class Person
{
  private String firstName;

  @MAttribute(name = "lastName", description = "Last name of the person", isWritable = true)
  private String name;

  // The @MInclude annotation can be used to add attributes and operations that are declared on an other class to the current MBean.
  @MInclude
  private Age age;

  // The @MCompositionReference can be used to reference another MBean that is also registered/unregistered if the current MBean is registered/unregistered.
  @MCompositionReference(concatName = true)
  private Address home;

  @MAttribute
  //The @MSizeAttribute can be used to add an attribute that contains the size of a List or Map or the length of a String.
  @MSizeAttribute(name = "numberOfChildren")
  private List<Child> children;

  public Person(String firstName, String name, Age age, Address home, List<Child> children)
  {
    this.firstName = firstName;
    this.name = name;
    this.age = age;
    this.home = home;
    this.children = children;
  }

  @MAttribute(description = "First name of the person", isWritable = true)
  public String getFirstName()
  {
    return firstName;
  }

  public void setFirstName(String firstName)
  {
    this.firstName = firstName;
  }

  @MOperation(description = "Prints the firstName and lastName to system out", impact = Impact.INFO)
  public void print()
  {
    System.out.print(firstName);
    System.out.print(" ");
    System.out.println(name);
  }

  public static void main(String[] args)
  {
    PersonRegistry registry = new PersonRegistry();
    Person reto = new Person("Reto", "Weiss", new Age(LocalDate.of(1972, 6, 10)),
            new Address("Home", "Baarerstrasse 12", 6300, "Zug", "Switzerland"),
            Arrays.asList(new Child("Anna", 2001), new Child("Toni", 2003), new Child("Gisela", 2007)));
    registry.addPerson(reto);

    Person flavio = new Person("Flavio", "Sadeghi", new Age(LocalDate.of(1986, 7, 23)),
            new Address("Business", "Baarerstrasse 12", 6300, "Zug", "Switzerland"),
            Arrays.asList(new Child("Mirco", 2013)));
    registry.addPerson(flavio);
  }
}

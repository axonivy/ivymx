package com.axonivy.jmx.example6;

import java.util.HashMap;
import java.util.Map;

import com.axonivy.jmx.MCollections;

public class PersonRegistry
{
  // Every person put to these collection will be registered as MBean!
  private Map<String, Person> persons = MCollections.managedMap(new HashMap<>());

  public void addPerson(Person person)
  {
    persons.put(person.getFirstName(), person);
  }
}

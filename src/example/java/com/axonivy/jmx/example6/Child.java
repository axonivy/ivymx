package com.axonivy.jmx.example6;

import com.axonivy.jmx.MComposite;
import com.axonivy.jmx.MItem;

//The annotation @MComposite declares a complex attribute type
@MComposite
public class Child
{
  // The @MItem annotation is used to add properties to the complex type.
  @MItem(description = "Name of the child")
  private String name;
  @MItem(description = "Year of birth of the child")
  private int yearOfBirth;

  public Child(String name, int yearOfBirth)
  {
    this.name = name;
    this.yearOfBirth = yearOfBirth;
  }
}

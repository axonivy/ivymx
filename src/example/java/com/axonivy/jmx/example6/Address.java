package com.axonivy.jmx.example6;

import com.axonivy.jmx.MAttribute;
import com.axonivy.jmx.MBean;

@MBean(value = "address=#{type}", description = "A postal address")
public class Address
{
  @MAttribute
  private String type;
  @MAttribute
  private String street;
  @MAttribute
  private int zip;
  @MAttribute
  private String city;
  @MAttribute
  private String country;

  public Address(String type, String street, int zip, String city, String country)
  {
    this.type = type;
    this.street = street;
    this.zip = zip;
    this.city = city;
    this.country = country;
  }
}

package com.axonivy.jmx.example6;

import java.time.LocalDate;

import com.axonivy.jmx.MAttribute;

public class Age
{
  private LocalDate birthday;

  public Age(LocalDate birthday)
  {
    this.birthday = birthday;
  }

  @MAttribute(description = "Age in years of the person")
  public int getAge()
  {
    return birthday.until(LocalDate.now()).getYears();
  }
}

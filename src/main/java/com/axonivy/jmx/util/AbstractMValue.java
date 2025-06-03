package com.axonivy.jmx.util;

import org.apache.commons.lang3.StringUtils;

/**
 * Abstract managed value
 */
public class AbstractMValue {
  private final String name;
  private final String description;

  public AbstractMValue(String name) {
    this(name, name);
  }

  public AbstractMValue(String name, String description) {
    this.name = StringUtils.uncapitalize(name);
    this.description = description;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public String getCapitalizedName() {
    return StringUtils.capitalize(name);
  }
}

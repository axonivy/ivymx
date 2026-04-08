[![ivymx version][ivymx]][ivymx-central]

# IVYMX - AXON IVY Java Management Extension

## Introduction

Simply annotate your POJO's to export them as JMX MBeans!

## Usage

```java
@MBean("Example:name=Person")
public class Person
{
  public static void main(String[] args)
  {
    Person person = new Person();
    MBeans.registerMBeanFor(person);
  }
}
```  

## Features

* Annotate your POJO's to export them as JMX MBeans.
* Annotations to add attributes and operations to your MBeans.
* Simple API to register and unregister your POJO's as MBeans.
* Use EL like expressions to name and describe your MBeans.
* Automatically register your MBeans if they are added to a collection.
* Annotations to define complex JMX data types.
* Cache attribute values

## Documentation

* [Getting Started](http://axonivy.github.io/ivymx/GettingStarted.html)
* [JavaDoc](http://axonivy.github.io/ivymx/apidocs/index.html)

## Maven

```xml
<dependency>
  <groupId>com.axonivy</groupId>
  <artifactId>ivymx</artifactId>
  <version>2.0.0</version>
</dependency>
```

## Authors

[ivyTeam](https://developer.axonivy.com/)

[![Axon Ivy](https://www.axonivy.com/hubfs/brand/axonivy-logo-black.svg)](http://www.axonivy.com)

## License

The Apache License, Version 2.0

[ivymx]: https://img.shields.io/maven-metadata/v.svg?label=ivymx&logo=apachemaven&metadataUrl=https%3A%2F%2Frepo1.maven.org%2Fmaven2%2Fcom%2Faxonivy%2Fivymx%2Fmaven-metadata.xml
[ivymx-central]: https://repo1.maven.org/maven2/com/axonivy/ivymx/


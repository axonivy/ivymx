# IVYMX - AXON IVY Java Management Extension

[![ivymx version][ivymx]][ivymx-central]
[![CI-Build][ci-build]][ci-build-workflow]

[![ivymx 2.x version][ivymx-2x]][ivymx-2x-central]

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

- Annotate your POJO's to export them as JMX MBeans.
- Annotations to add attributes and operations to your MBeans.
- Simple API to register and unregister your POJO's as MBeans.
- Use EL like expressions to name and describe your MBeans.
- Automatically register your MBeans if they are added to a collection.
- Annotations to define complex JMX data types.
- Cache attribute values

## Documentation

- [Getting Started][getting-started]
- [JavaDoc][javadoc]

## Maven

### Versions from 3.x

Version 3.0.0 has not been released yet. Its Maven coordinates and Java packages use the new `io.github.axonivy` namespace.

Development snapshots use these coordinates:

```xml
<dependency>
  <groupId>io.github.axonivy</groupId>
  <artifactId>ivymx</artifactId>
  <version>3.0.0-SNAPSHOT</version>
</dependency>
```

To resolve snapshots, add the Maven Central snapshot repository:

```xml
<repositories>
  <repository>
    <id>central.snapshots</id>
    <url>https://central.sonatype.com/repository/maven-snapshots/</url>
    <releases>
      <enabled>false</enabled>
    </releases>
    <snapshots>
      <enabled>true</enabled>
    </snapshots>
  </repository>
</repositories>
```

### Older Releases

Stable 2.x and earlier releases remain available under [`com.axonivy:ivymx`][ivymx-2x-central] and use Java packages in the `com.axonivy` namespace. The latest stable release is:

```xml
<dependency>
  <groupId>com.axonivy</groupId>
  <artifactId>ivymx</artifactId>
  <version>2.0.2</version>
</dependency>
```

## Authors

[ivyTeam][ivyteam]

[![Axon Ivy][axonivy-logo]][axonivy-site]

## License

The Apache License, Version 2.0

[ivymx]: https://img.shields.io/maven-central/v/io.github.axonivy/ivymx.svg?label=ivymx&logo=apachemaven
[ivymx-central]: https://central.sonatype.com/artifact/io.github.axonivy/ivymx/
[ivymx-2x]: https://img.shields.io/maven-central/v/com.axonivy/ivymx.svg?label=ivymx%202.x&logo=apachemaven
[ivymx-2x-central]: https://central.sonatype.com/artifact/com.axonivy/ivymx/
[ci-build]: https://github.com/axonivy/ivymx/actions/workflows/ci.yml/badge.svg
[ci-build-workflow]: https://github.com/axonivy/ivymx/actions/workflows/ci.yml
[getting-started]: http://axonivy.github.io/ivymx/GettingStarted.html
[javadoc]: http://axonivy.github.io/ivymx/apidocs/index.html
[ivyteam]: https://developer.axonivy.com/
[axonivy-logo]: https://www.axonivy.com/hubfs/brand/axonivy-logo-black.svg
[axonivy-site]: http://www.axonivy.com

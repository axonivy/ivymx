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
  <version>1.2.3</version>
</dependency>
```

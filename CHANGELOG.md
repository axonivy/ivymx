# Changelog

## 1.2.4

Add method EventCounter#increase(long delta)
Support Instant, LocalDateTime, OffsetDateTime, and ZonedDateTime types

Update dependencies:
- Log4j2 to 2.20.0
- Slf4j to 2.0.9
- Apache commons-lang to 3.13.0
- AssertJ to 3.24.2

## 1.2.3

- Fix issue #27 MBeans.unregisterAllMBeans throws exception if an MBean could not be registered before because an error in resolving the object name
- Fix issue #28 MMap.put should first unregister the bean that is already in the map before registering the new bean

Update dependencies:
- Log4j2 to 2.17.2
- Slf4j to 1.7.36
- Apache commons-lang to 3.12.0
- JUnit4 to 4.13.2
- JMockit to 1.49 
- AssertJ to 3.22.0 

## 1.2.2

### Changed

- Update dependency to Log4j2 from 2.13.3 to 2.16.0 because of CVE-2021-44228

## 1.2.1

### Changed

- Do not analyze methods auto generated by javac (synthetic methods). See issue #7.

## 1.2

### Changed
 
- Default error strategy from THROW_RUNTIME_EXCEPTION_ERROR_STRATEGY to LOG_ERROR_STRATEGY

## 1.1

### Added

- Method based attribute caching with new @MCache annotation

## 1.0.1

### Changed

- Quote property values in MBean Names if they contain special characters like ,:="\n
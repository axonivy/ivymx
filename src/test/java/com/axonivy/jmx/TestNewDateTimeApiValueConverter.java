package com.axonivy.jmx;

import static org.assertj.core.api.Assertions.assertThat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanException;
import javax.management.MalformedObjectNameException;
import javax.management.ReflectionException;
import org.assertj.core.api.Assertions;
import org.assertj.core.data.TemporalUnitOffset;
import org.junit.Test;

public class TestNewDateTimeApiValueConverter extends BaseMTest<TestNewDateTimeApiValueConverter.TestBean>
{
  private static final long DATE_ONE_MILLISECOND = 1L;
  private static final TemporalUnitOffset ONE_MILLISECOND = Assertions.within(1, ChronoUnit.MILLIS);

  @MBean("Test:type=TestType")
  public static class TestBean
  {
    private Instant instant;
    private LocalDateTime localDateTime;
    private OffsetDateTime offsetDateTime;
    private ZonedDateTime zonedDateTime;

    @MAttribute(isWritable=true)
    Instant getInstant()
    {
      return instant;
    }

    void setInstant(Instant instant)
    {
      this.instant = instant;
    }

    @MAttribute(isWritable=true)
    LocalDateTime getLocalDateTime()
    {
      return localDateTime;
    }

    void setLocalDateTime(LocalDateTime localDateTime)
    {
      this.localDateTime = localDateTime;
    }

    @MAttribute(isWritable=true)
    OffsetDateTime getOffsetDateTime()
    {
      return offsetDateTime;
    }

    void setOffsetDateTime(OffsetDateTime offsetDateTime)
    {
      this.offsetDateTime = offsetDateTime;
    }

    @MAttribute(isWritable=true)
    ZonedDateTime getZonedDateTime()
    {
      return zonedDateTime;
    }

    void setZonedDateTime(ZonedDateTime zonedDateTime)
    {
      this.zonedDateTime = zonedDateTime;
    }
  }


  public TestNewDateTimeApiValueConverter() throws MalformedObjectNameException
  {
    super(new TestBean(), "Test:type=TestType");
  }

  @Test
  public void instantInfo() throws IntrospectionException, InstanceNotFoundException, ReflectionException
  {
    MBeanAttributeInfo attributeInfo = getAttributeInfo("instant");
    assertThat(attributeInfo.getDescription()).isEqualTo("instant");
    assertThat(attributeInfo.getType()).isEqualTo("java.util.Date");
    assertThat(attributeInfo.isReadable()).isEqualTo(true);
    assertThat(attributeInfo.isWritable()).isEqualTo(true);
    assertThat(attributeInfo.isIs()).isEqualTo(false);
  }

  @Test
  public void readInstant() throws AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException
  {
    Instant now = Instant.now();
    testBean.instant = now;
    assertThat((Date)getAttribute("instant")).isCloseTo(Date.from(now), DATE_ONE_MILLISECOND);
  }

  @Test
  public void writeInstant() throws InstanceNotFoundException, InvalidAttributeValueException, AttributeNotFoundException, ReflectionException, MBeanException
  {
    Instant now = Instant.now();
    setAttribute("instant", Date.from(now));
    assertThat(testBean.instant).isCloseTo(now, ONE_MILLISECOND);
  }

  @Test
  public void localDateTimeInfo() throws IntrospectionException, InstanceNotFoundException, ReflectionException
  {
    MBeanAttributeInfo attributeInfo = getAttributeInfo("localDateTime");
    assertThat(attributeInfo.getDescription()).isEqualTo("localDateTime");
    assertThat(attributeInfo.getType()).isEqualTo("java.util.Date");
    assertThat(attributeInfo.isReadable()).isEqualTo(true);
    assertThat(attributeInfo.isWritable()).isEqualTo(true);
    assertThat(attributeInfo.isIs()).isEqualTo(false);
  }

  @Test
  public void readLocalDateTime() throws AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException
  {
    LocalDateTime now = LocalDateTime.now();
    testBean.localDateTime = now;
    assertThat((Date)getAttribute("localDateTime")).isCloseTo(Date.from(now.atZone(ZoneId.systemDefault()).toInstant()), DATE_ONE_MILLISECOND);
  }

  @Test
  public void writeLocalDateTime() throws InstanceNotFoundException, InvalidAttributeValueException, AttributeNotFoundException, ReflectionException, MBeanException
  {
    LocalDateTime now = LocalDateTime.now();
    setAttribute("localDateTime", Date.from(now.atZone(ZoneId.systemDefault()).toInstant()));
    assertThat(testBean.localDateTime).isCloseTo(now, ONE_MILLISECOND);
  }

  @Test
  public void offsetDateTimeInfo() throws IntrospectionException, InstanceNotFoundException, ReflectionException
  {
    MBeanAttributeInfo attributeInfo = getAttributeInfo("offsetDateTime");
    assertThat(attributeInfo.getDescription()).isEqualTo("offsetDateTime");
    assertThat(attributeInfo.getType()).isEqualTo("java.util.Date");
    assertThat(attributeInfo.isReadable()).isEqualTo(true);
    assertThat(attributeInfo.isWritable()).isEqualTo(true);
    assertThat(attributeInfo.isIs()).isEqualTo(false);
  }

  @Test
  public void readOffsetDateTime() throws AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException
  {
    OffsetDateTime now = OffsetDateTime.now();
    testBean.offsetDateTime = now;
    assertThat((Date)getAttribute("offsetDateTime")).isCloseTo(Date.from(now.toInstant()), DATE_ONE_MILLISECOND);
  }

  @Test
  public void writeOffsetDateTime() throws InstanceNotFoundException, InvalidAttributeValueException, AttributeNotFoundException, ReflectionException, MBeanException
  {
    OffsetDateTime now = OffsetDateTime.now();
    setAttribute("offsetDateTime", Date.from(now.toInstant()));
    assertThat(testBean.offsetDateTime).isCloseTo(now, ONE_MILLISECOND);
  }

  @Test
  public void zonedDateTimeInfo() throws IntrospectionException, InstanceNotFoundException, ReflectionException
  {
    MBeanAttributeInfo attributeInfo = getAttributeInfo("zonedDateTime");
    assertThat(attributeInfo.getDescription()).isEqualTo("zonedDateTime");
    assertThat(attributeInfo.getType()).isEqualTo("java.util.Date");
    assertThat(attributeInfo.isReadable()).isEqualTo(true);
    assertThat(attributeInfo.isWritable()).isEqualTo(true);
    assertThat(attributeInfo.isIs()).isEqualTo(false);
  }

  @Test
  public void readZonedDateTime() throws AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException
  {
    ZonedDateTime now = ZonedDateTime.now();
    testBean.zonedDateTime = now;
    assertThat((Date)getAttribute("zonedDateTime")).isCloseTo(Date.from(now.toInstant()), DATE_ONE_MILLISECOND);
  }

  @Test
  public void writeZonedDateTime() throws InstanceNotFoundException, InvalidAttributeValueException, AttributeNotFoundException, ReflectionException, MBeanException
  {
    ZonedDateTime now = ZonedDateTime.now();
    setAttribute("zonedDateTime", Date.from(now.toInstant()));
    assertThat(testBean.zonedDateTime).isCloseTo(now, ONE_MILLISECOND);
  }

  @Test
  public void readPacificZonedDateTime() throws AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException
  {
    ZonedDateTime pacificTime = ZonedDateTime.of(LocalDateTime.of(1972, 6, 10, 9, 13, 15), ZoneId.of("America/Los_Angeles"));
    testBean.zonedDateTime = pacificTime;
    assertThat((Date)getAttribute("zonedDateTime")).isCloseTo(Date.from(pacificTime.toInstant()), DATE_ONE_MILLISECOND);
  }

  @Test
  public void writePacificZonedDateTime() throws InstanceNotFoundException, InvalidAttributeValueException, AttributeNotFoundException, ReflectionException, MBeanException
  {
    ZonedDateTime pacificTime = ZonedDateTime.of(LocalDateTime.of(1972, 6, 10, 9, 13, 15), ZoneId.of("America/Los_Angeles"));
    setAttribute("zonedDateTime", Date.from(pacificTime.toInstant()));
    assertThat(testBean.zonedDateTime).isCloseTo(pacificTime, ONE_MILLISECOND);
  }
}

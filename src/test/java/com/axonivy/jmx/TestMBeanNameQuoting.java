package com.axonivy.jmx;

import static org.assertj.core.api.Assertions.assertThat;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.junit.Test;

public class TestMBeanNameQuoting
{
  @Test
  public void quotedBean() throws MalformedObjectNameException
  {
    assertThat(isRegistered("Test:type=\",=:\\n \\\" hi\"")).isFalse();
    MBeans.registerMBeanFor(new QuotedMBean());
    assertThat(isRegistered("Test:type=\",=:\\n \\\" hi\"")).isTrue();
  }

  @Test
  public void notQuotedBeanFromElExpression() throws MalformedObjectNameException
  {
    assertNameNotQuoted("hello world");
  }

  @Test
  public void quotedBeanFromElExpressionWithComma() throws MalformedObjectNameException
  {
    assertNameQuoted("hello,world");
  }
  
  @Test
  public void quotedBeanFromElExpressionWithEqual() throws MalformedObjectNameException
  {
    assertNameQuoted("hello=world");
  }

  @Test
  public void quotedBeanFromElExpressionWithCollon() throws MalformedObjectNameException
  {
    assertNameQuoted("hello:world");
  }

  @Test
  public void quotedBeanFromElExpressionWithNewLine() throws MalformedObjectNameException
  {
    assertNameQuoted("hello\nworld");
  }

  @Test
  public void quotedBeanFromElExpressionWithQuote() throws MalformedObjectNameException
  {
    assertNameQuoted("hello\"world");
  }

  @Test
  public void notQuotedBeanFromElExpressionThatIsAlreadyQuoted() throws MalformedObjectNameException
  {
    assertNameNotQuoted("\"hello,=:\\n\\\"world\""); 
  }

  private static void assertNameNotQuoted(String name) throws MalformedObjectNameException
  {
    assertName(name, name);
  }

  private void assertNameQuoted(String name) throws MalformedObjectNameException
  {
    assertName(name, ObjectName.quote(name));
  }
  
  private static void assertName(String name, String mBeanName) throws MalformedObjectNameException
  {
    assertThat(isRegistered("Test:name="+mBeanName)).isFalse();
    MBeans.registerMBeanFor(new NameMBean(name));
    assertThat(isRegistered("Test:name="+mBeanName)).isTrue();
  }

  @MBean(value="Test:type=\",=:\\n \\\" hi\"")
  private static final class QuotedMBean
  {
  }

  @MBean(value="Test:name=#{name}")
  private static final class NameMBean
  {
    @SuppressWarnings("unused")
    private String name;
    
    private NameMBean(String name)
    {
      this.name = name;
    }
  }

  private static boolean isRegistered(String name) throws MalformedObjectNameException
  {
    return MBeans.getMBeanServer().isRegistered(new ObjectName(name));
  }
}

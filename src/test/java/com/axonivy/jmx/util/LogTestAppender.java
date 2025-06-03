/**
 *
 */
package com.axonivy.jmx.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.StringWriter;

import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.WriterAppender;
import org.apache.log4j.spi.LoggingEvent;

/**
 * Instances of the class LogTestAppender are intended to be used in UnitTests.
 * They can serve to detect exceptions which are logged but never make it
 * to the surface.
 *
 * <p>Example: During <i>postCommit()</i>, exceptions may happen.
 * However, because commit() has already finished, it cannot be rollbacked
 * anymore, forwarding the exception does not help the situation. It is
 * therefory quietly suppressed, but logged as "FATAL" using the persistence
 * layer's package logger.<br>
 * The only way to detect such an exception is by appending a LogTestAppender
 * with the minimum level FATAL and then check at the end of the test, if
 * anything has been logged.
 *
 * @author kvg
 */
public class LogTestAppender extends WriterAppender {
  /** Log level threshold (all below are suppressed) */
  private final Level fLevel;

  /** Log output recorder */
  private final StringWriter fWriter;

  /**
   * Creates a new TestAppender which will only log events that are above
   * or equal to the given log level. <b>Note:</b> Be aware, that the setting
   * of any log filters using <code>Logger.denyAllAllow()</code> may prevent
   * events of arriving at the appender at all. It is thus recommended to set
   * a minimum log level, even for denyAllAllow().
   *
   * @param logLevel
   */
  public LogTestAppender(Level logLevel) {
    fLevel = logLevel;
    fWriter = new StringWriter();

    setWriter(fWriter);
    setLayout(new PatternLayout("[%c, %p] : %m%n"));
    setName(getClass().getSimpleName() + " <" + logLevel + ">");
  }

  /**
   * Only appends events with level greater or equal to internal level.
   * @see org.apache.log4j.WriterAppender#append(org.apache.log4j.spi.LoggingEvent)
   */
  @Override
  public void append(LoggingEvent logEvent) {
    if (logEvent.getLevel().isGreaterOrEqual(fLevel)) {
      super.append(logEvent);
    }
  }

  /**
   * @return true if internal recorder (output buffer) is not empty
   */
  public boolean isEmpty() {
    fWriter.flush();
    return fWriter.getBuffer().length() == 0;
  }

  /**
   * @throws AssertionError if internal recorder (output buffer) is not empty
   */
  public void assertEmpty() throws AssertionError {
    assertThat(getRecording())
        .as("Expected no logs for Logger (Level <" + fLevel + ">)")
        .isEmpty();
  }

  /**
   * @return recorded log output
   */
  public String getRecording() {
    fWriter.flush();
    return fWriter.toString();
  }

  /**
   * Resets the internal recorder (i.e. deletes all recorded log output).
   * @see org.apache.log4j.WriterAppender#reset()
   */
  @Override
  public void reset() {
    fWriter.getBuffer().setLength(0);
  }

  /**
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return getName();
  }
}

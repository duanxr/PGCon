
package com.duanxr.pgcon.log;

import javafx.application.Platform;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.spi.ExtendedLogger;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author 段然 2022/7/28
 */
@Component
public class GuiLogger implements Logger {

  private static final String FQCN = GuiLogger.class.getName();
  private static final ExtendedLogger LOGGER = (ExtendedLogger) org.apache.logging.log4j.LogManager.getLogger(
      GuiLogger.class);
  private final String context;
  private final GuiLog guiLog;

  @Autowired
  public GuiLogger(GuiLog guiLog) {
    this.guiLog = guiLog;
    this.context = "";
  }

  public GuiLogger(GuiLog guiLog, String context) {
    this.guiLog = guiLog;
    this.context = context;
  }

  private void logConsole(String fqcn, Level level, String message) {
    LOGGER.logIfEnabled(fqcn == null ? FQCN : fqcn, level, null, message);
  }

  private String formatConsole(String msg, Object... args) {
    return MessageFormatter.arrayFormat(msg, args).getMessage();
  }

  public void logGUI(GuiLogRecord record) {
    Platform.runLater(() -> guiLog.offer(record));
  }

  private String formatGUI(String msg, Object... args) {
    String message = MessageFormatter.arrayFormat(msg, args).getMessage();
    if (args != null && args.length != 0 && args[args.length - 1] instanceof Throwable) {
      message += ":" + ((Throwable) args[args.length - 1]).getMessage();
    }
    return message;
  }

  private void debug(String fqcn, String msg, Object... args) {
    logConsole(fqcn, Level.DEBUG, formatConsole(msg, args));
    logGUI(new GuiLogRecord(GuiLogLevel.DEBUG, context, formatGUI(msg, args)));
  }

  private void info(String fqcn, String msg, Object... args) {
    logConsole(fqcn, Level.INFO, formatConsole(msg, args));
    logGUI(new GuiLogRecord(GuiLogLevel.INFO, context, formatGUI(msg, args)));
  }

  private void warn(String fqcn, String msg, Object... args) {
    logConsole(fqcn, Level.WARN, formatConsole(msg, args));
    logGUI(new GuiLogRecord(GuiLogLevel.WARN, context, formatGUI(msg, args)));
  }

  private void error(String fqcn, String msg, Object... args) {
    logConsole(fqcn, Level.ERROR, formatConsole(msg, args));
    logGUI(new GuiLogRecord(GuiLogLevel.ERROR, context, formatGUI(msg, args)));
  }

  @Override
  public void debug(String msg, Object... args) {
    debug(null, msg, args);
  }

  @Override
  public void info(String msg, Object... args) {
    info(null, msg, args);
  }

  @Override
  public void warn(String msg, Object... args) {
    warn(null, msg, args);
  }

  @Override
  public void error(String msg, Object... args) {
    error(null, msg, args);
  }

  public GuiLog getLog() {
    return guiLog;
  }

  public Logger getEndPoint(Class<?> clazz) {
    final String endPointFullName = clazz.getName();
    return new Logger() {
      @Override
      public void debug(String msg, Object... args) {
        GuiLogger.this.debug(endPointFullName, msg, args);
      }

      @Override
      public void info(String msg, Object... args) {
        GuiLogger.this.info(endPointFullName, msg, args);
      }

      @Override
      public void warn(String msg, Object... args) {
        GuiLogger.this.warn(endPointFullName, msg, args);
      }

      @Override
      public void error(String msg, Object... args) {
        GuiLogger.this.error(endPointFullName, msg, args);
      }
    };
  }
}
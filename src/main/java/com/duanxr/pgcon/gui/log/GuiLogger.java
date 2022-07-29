
package com.duanxr.pgcon.gui.log;

import javafx.application.Platform;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.helpers.MessageFormatter;

/**
 * @author 段然 2022/7/28
 */
@Slf4j
public class GuiLogger {

  private final GuiLog guiLog;
  private final String context;

  public GuiLogger(GuiLog guiLog, String context) {
    this.guiLog = guiLog;
    this.context = context;
  }

  public void log(GuiLogRecord record) {
    Platform.runLater(()->guiLog.offer(record));
  }

  private String format(String msg, Object... args) {
    String message = MessageFormatter.arrayFormat(msg, args).getMessage();
    if (args != null && args.length != 0 && args[args.length - 1] instanceof Throwable) {
      message += ":" + ((Throwable) args[args.length - 1]).getMessage();
    }
    return message;
  }

  public void debug(String msg, Object... args) {
    log.debug(msg, args);
    log(new GuiLogRecord(GuiLogLevel.DEBUG, context, format(msg, args)));
  }


  public void info(String msg, Object... args) {
    log.info(msg, args);
    log(new GuiLogRecord(GuiLogLevel.INFO, context, format(msg, args)));
  }

  public void warn(String msg, Object... args) {
    log.warn(msg, args);
    log(new GuiLogRecord(GuiLogLevel.WARN, context, format(msg, args)));
  }

  public void error(String msg, Object... args) {
    log.error(msg, args);
    log(new GuiLogRecord(GuiLogLevel.ERROR, context, format(msg, args)));
  }

  public GuiLog getLog() {
    return guiLog;
  }
}
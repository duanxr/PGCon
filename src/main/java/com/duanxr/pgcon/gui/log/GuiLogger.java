
package com.duanxr.pgcon.gui.log;

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
    guiLog.offer(record);
  }

  public void debug(String msg, Object... args) {
    String message = MessageFormatter.arrayFormat(msg, args).getMessage();
    log.debug(msg, args);
    log(new GuiLogRecord(GuiLogLevel.DEBUG, context, msg));
  }

  public void info(String msg, Object... args) {
    String message = MessageFormatter.arrayFormat(msg, args).getMessage();
    log.info(msg, args);
    log(new GuiLogRecord(GuiLogLevel.INFO, context, message));
  }

  public void warn(String msg, Object... args) {
    String message = MessageFormatter.arrayFormat(msg, args).getMessage();
    log.warn(msg, args);
    log(new GuiLogRecord(GuiLogLevel.WARN, context, msg));
  }

  public void error(String msg, Object... args) {
    String message = MessageFormatter.arrayFormat(msg, args).getMessage();
    log.error(msg, args);
    log(new GuiLogRecord(GuiLogLevel.ERROR, context, msg));
  }

  public GuiLog getLog() {
    return guiLog;
  }
}
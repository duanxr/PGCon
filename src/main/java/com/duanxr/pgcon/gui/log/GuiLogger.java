
package com.duanxr.pgcon.gui.log;

import javafx.application.Platform;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author 段然 2022/7/28
 */
@Log4j2
@Component
public class GuiLogger {

  private final GuiLog guiLog;
  private final String context;

  @Autowired
  public GuiLogger(GuiLog guiLog) {
    this.guiLog = guiLog;
    this.context = "";
  }

  public GuiLogger(GuiLog guiLog, String context) {
    this.guiLog = guiLog;
    this.context = context;
  }

  public void log(GuiLogRecord record) {
    Platform.runLater(() -> guiLog.offer(record));
  }

  private String format(String msg, Object... args) {
    String message = MessageFormatter.arrayFormat(msg, args).getMessage();
    if (args != null && args.length != 0 && args[args.length - 1] instanceof Throwable) {
      message += ":" + ((Throwable) args[args.length - 1]).getMessage();
    }
    return message;
  }

  //TOOD: 改为PATh
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
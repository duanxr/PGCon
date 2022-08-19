package com.duanxr.pgcon.log;

import java.util.Date;

/**
 * @author 段然 2022/7/28
 */
public class GuiLogRecord {
  private final Date timestamp;
  private final GuiLogLevel level;
  private final String context;
  private final String message;

  public GuiLogRecord(GuiLogLevel level, String context, String message) {
    this.timestamp = new Date();
    this.level = level;
    this.context = context;
    this.message = message;
  }

  public Date getTimestamp() {
    return timestamp;
  }

  public GuiLogLevel getLevel() {
    return level;
  }

  public String getContext() {
    return context;
  }

  public String getMessage() {
    return message;
  }
}


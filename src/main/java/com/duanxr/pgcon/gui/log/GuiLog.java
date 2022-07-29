package com.duanxr.pgcon.gui.log;

import java.util.Collection;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * @author 段然 2022/7/28
 */
public class GuiLog {
  private static final int MAX_LOG_ENTRIES = 10000;
  private final BlockingDeque<GuiLogRecord> log = new LinkedBlockingDeque<>(MAX_LOG_ENTRIES);

  public void drainTo(Collection<? super GuiLogRecord> collection) {
    log.drainTo(collection);
  }

  public void offer(GuiLogRecord record) {
    log.offer(record);
  }
}

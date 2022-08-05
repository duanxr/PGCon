package com.duanxr.pgcon.core;

import lombok.extern.slf4j.Slf4j;

/**
 * @author 段然 2022/7/30
 */
@Slf4j
public class DaemonTask implements Runnable {

  private final String name;
  private final Runnable runnable;

  public DaemonTask(String name, Runnable runnable) {
    this.name = name;
    this.runnable = runnable;
  }

  @Override
  public void run() {
    try {
      while (!Thread.currentThread().isInterrupted()) {
        runnable.run();
      }
    } catch (Exception e) {
      if (!(e instanceof InterruptedException)) {
        log.error("Daemon task {} error", name, e);
      }
    }
  }

  public static DaemonTask of(String name, Runnable runnable) {
    return new DaemonTask(name, runnable);
  }
}

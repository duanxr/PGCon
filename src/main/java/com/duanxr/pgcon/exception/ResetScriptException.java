package com.duanxr.pgcon.exception;

import lombok.experimental.StandardException;

/**
 * @author 段然 2022/7/28
 */
@StandardException
public class ResetScriptException extends RuntimeException {

  private Runnable runnable;

  public Runnable getRunnable() {
    return this.runnable;
  }
  public ResetScriptException setRunnable(Runnable runnable) {
    this.runnable = runnable;
    return this;
  }
}

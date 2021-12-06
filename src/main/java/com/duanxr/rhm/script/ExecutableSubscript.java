package com.duanxr.rhm.script;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Duanran 2019/12/19
 */
@Slf4j
public abstract class ExecutableSubscript implements Subscript {

  @Override
  public Object call() {
    try {
      execute();
    } catch (InterruptedException ignored) {
    } catch (Exception e) {
      log.error("ExecutableSubscript Exception.", e);
    }
    return null;
  }

  protected abstract void execute() throws InterruptedException;
}

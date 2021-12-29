package com.duanxr.pgcon.core.script;

import com.duanxr.pgcon.core.PGCon;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * @author 段然 2021/12/7
 */
@Slf4j
public abstract class RunnableScript extends PGScript {

  private final AtomicBoolean running;

  protected RunnableScript(PGCon pg) {
    super(pg);
    this.running = new AtomicBoolean(false);
    pg.getControlPanel().addScript(this);
  }


  @Override
  public void run() {
    try {
      init();
      log.info("开始执行脚本:{}", getName());
      running.set(true);
      controller.clear();
      while (running.get()) {
        execute();
      }
      log.info("脚本执行结束:{}", getName());
    } catch (Exception e) {
      log.error(
          "An error occurred while executing the script, please report this to the developer. Trying to save Switch video.",
          e);
      controller.capture();
    }
  }

  protected void init() throws Exception {
  }

  protected void clear() throws Exception {
  }

  protected abstract void execute() throws Exception;

  @Override
  @SneakyThrows
  public void stop() {
    running.set(false);
    controller.clear();
    clear();
  }

}

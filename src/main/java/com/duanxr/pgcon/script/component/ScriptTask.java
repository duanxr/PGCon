package com.duanxr.pgcon.script.component;

import com.duanxr.pgcon.script.api.MainScript;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.extern.slf4j.Slf4j;

/**
 * @author 段然 2022/7/26
 */
@Slf4j
public class ScriptTask implements Runnable {

  private final AtomicBoolean running;
  private final MainScript mainScript;

  ScriptTask(MainScript mainScript) {
    this.mainScript = mainScript;
    this.running = new AtomicBoolean(mainScript.isLoop());
  }

  public void stop() {
    running.set(false);
    mainScript.clear();
  }

  @Override
  public void run() {
    log.info("Script {} started.", mainScript.getScriptName());
    do {
      try {
        mainScript.execute();
      } catch (InterruptedException e) {
        log.error("Script {} interrupted.", mainScript.getScriptName());
        break;
      } catch (Exception e) {
        log.error("Script {} trow a exception.", mainScript.getScriptName(), e);
      }
    } while (running.get());
    log.info("Script {} stopped.", mainScript.getScriptName());
  }
}
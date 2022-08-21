package com.duanxr.pgcon.script.component;

import com.duanxr.pgcon.exception.InterruptScriptException;
import com.duanxr.pgcon.exception.ResetScriptException;
import com.duanxr.pgcon.script.api.Script;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * @author 段然 2022/7/26
 */
@Slf4j
public class ScriptTask implements Runnable {

  private final Runnable callback;
  private final AtomicBoolean running;
  private final Script<Object> script;
  private volatile Runnable reset;

  public ScriptTask(Script<Object> script) {
    this(script, null);
  }

  ScriptTask(Script<Object> script, Runnable callback) {
    this.script = script;
    this.callback = callback;
    this.running = new AtomicBoolean(false);
  }

  @Override
  @SneakyThrows
  public void run() {
    running.set(script.getInfo().isLoop());
    final String scriptName = script.getInfo().getDescription();
    log.info("Script {} launched.", scriptName);
    try {
      do {
        try {
          reset();
          script.execute();
        } catch (InterruptScriptException e) {
          log.error("Script {} interrupted.", scriptName, e);
          break;
        } catch (ResetScriptException e) {
          log.warn("Script {} reset.", scriptName, e);
          this.reset = e.getRunnable();
        } catch (Exception e) {
          log.error("Script {} trow a exception.", scriptName, e);
          throw e;
        }
      } while (running.get() && !Thread.interrupted());
    } finally {
      script.destroy();
      log.info("Script {} stopped.", scriptName);
      callback();
    }
  }

  private void reset() {
    if (reset != null) {
      reset.run();
      reset = null;
    }
  }

  private void callback() {
    if (callback != null) {
      callback.run();
    }
  }

  public void stop() {
    running.set(false);
  }

}
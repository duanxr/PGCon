package com.duanxr.pgcon.script.component;

import com.duanxr.pgcon.exception.InterruptScriptException;
import com.duanxr.pgcon.exception.ResetScriptException;
import com.duanxr.pgcon.script.api.Script;
import java.util.concurrent.atomic.AtomicBoolean;
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
  public void run() {
    running.set(script.getInfo().isLoop());
    final String scriptName = script.getInfo().getName();
    log.info("Script {} launched.", scriptName);
    do {
      try {
        reset();
        script.execute();
      } catch (InterruptedException | InterruptScriptException e) {
        log.error("Script {} interrupted.", scriptName, e);
        break;
      } catch (ResetScriptException e) {
        log.warn("Script {} reset.", scriptName, e);
        this.reset = e.getRunnable();
      } catch (Exception e) {
        log.error("Script {} trow a exception.", scriptName, e);
        break;
      }
    } while (running.get());
    script.destroy();
    log.info("Script {} stopped.", scriptName);
    callback();
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
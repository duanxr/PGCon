package com.duanxr.pgcon.script.component;

import com.duanxr.pgcon.exception.AbortScriptException;
import com.duanxr.pgcon.exception.ResetScriptException;
import com.duanxr.pgcon.script.api.Script;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.extern.slf4j.Slf4j;

/**
 * @author 段然 2022/7/26
 */
@Slf4j
public class ScriptTask implements Runnable {
  private final Script<?> script;
  private final Runnable callback;
  private final AtomicBoolean running;
  ScriptTask(Script<Object> script, Runnable callback) {
    this.script = script;
    this.callback = callback;
    this.running = new AtomicBoolean(false);
  }
  ScriptTask(Script<Object> script) {
    this(script, null);
  }

  @Override
  public void run() {
    running.set(script.getInfo().isLoop());
    final String scriptName = script.getInfo().getName();
    log.info("Script {} launched.", scriptName);
    do {
      try {
        script.execute();
      } catch (InterruptedException e) {
        log.error("Script {} interrupted.", scriptName);
        break;
      } catch (ResetScriptException e) {
        log.warn("Script {} reset.", scriptName, e);
        script.reset();
        break;
      } catch (AbortScriptException e) {
        log.error("Script {} abort.", scriptName, e);
        break;
      } catch (Exception e) {
        log.error("Script {} trow a exception.", scriptName, e);
      }
    } while (running.get());
    script.destroy();
    log.info("Script {} stopped.", scriptName);
    if (callback != null) {
      callback.run();
    }
  }

  public void stop() {
    running.set(false);
  }

}
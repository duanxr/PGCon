package com.duanxr.pgcon.script.engine;

import com.duanxr.pgcon.core.detect.api.ImageCompare;
import com.duanxr.pgcon.core.detect.api.OCR;
import com.duanxr.pgcon.exception.InterruptScriptException;
import com.duanxr.pgcon.exception.ResetScriptException;
import com.duanxr.pgcon.log.Logger;
import com.duanxr.pgcon.output.action.NintendoSwitchStandardButton;
import com.duanxr.pgcon.output.action.NintendoSwitchStandardStick;
import com.duanxr.pgcon.output.api.Button;
import com.duanxr.pgcon.output.api.Stick;
import com.duanxr.pgcon.script.api.Script;
import com.duanxr.pgcon.script.api.ScriptInfo;
import com.duanxr.pgcon.script.component.ScriptCache;
import com.duanxr.pgcon.script.component.ScriptTask;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.function.Supplier;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

/**
 * @author 段然 2019/12/25
 */
@Setter
public abstract class PGConScriptEngineV1<T> extends BasicScriptEngine<T> {

  @Getter(value = AccessLevel.PROTECTED, lazy = true)
  private final Logger logger = getLoggerEndPoint();

  protected PGConScriptEngineV1(ScriptInfo<T> scriptInfo) {
    super(scriptInfo);
  }

  protected void debug(String msg, Object... args) {
    getLogger().debug(msg, args);
  }

  protected void info(String msg, Object... args) {
    getLogger().info(msg, args);
  }

  protected void warn(String msg, Object... args) {
    getLogger().warn(msg, args);
  }

  protected void error(String msg, Object... args) {
    getLogger().error(msg, args);
  }

  protected ImageCompare.Result detect(ImageCompare.Param param) {
    return components.getDetectService().detect(param);
  }

  protected OCR.Result detect(OCR.Param param) {
    return components.getDetectService().detect(param);
  }

  protected void press(Button action) {
    components.getControllerService().press(action);
  }

  protected void hold(Button action) {
    components.getControllerService().hold(action);
  }

  protected void hold(Button action, int time) {
    components.getControllerService().hold(action, time);
  }

  protected void release(Button action) {
    components.getControllerService().release(action);
  }

  protected void press(Stick action) {
    components.getControllerService().press(action);
  }

  protected void hold(Stick action) {
    components.getControllerService().hold(action);
  }

  protected void hold(Stick action, int time) {
    components.getControllerService().hold(action, time);
  }

  protected void release(Stick action) {
    components.getControllerService().release(action);
  }

  protected <D> D until(Supplier<D> supplier, Function<D, Boolean> checker) {
    return until(supplier, checker, null, null, null);
  }

  protected <D> D until(Supplier<D> supplier, Function<D, Boolean> checker, Runnable action) {
    return until(supplier, checker, action, null, null);
  }

  protected <D> D until(Supplier<D> supplier, Function<D, Boolean> checker, Runnable action,
      Long maxMillis) {
    return until(supplier, checker, action, maxMillis, null);
  }

  protected <D> D until(Supplier<D> supplier, Function<D, Boolean> checker, Runnable action,
      Long maxMillis, Runnable reset) {
    D d = supplier.get();
    long start = System.currentTimeMillis();
    while (!checker.apply(d)) {
      if (maxMillis != null && System.currentTimeMillis() - start > maxMillis) {
        throw new ResetScriptException().setRunnable(reset);
      }
      if (action != null) {
        action.run();
      }
      d = supplier.get();
    }
    return d;
  }

  protected void push(String message) {
    try {
      components.getNotifyService().push(getInfo().getDescription(), message);
    } catch (Exception e) {
      error("push error", e);
    }
  }

  protected void async(Runnable runnable) {
    components.getExecutorService().submit(runnable);
  }

  protected <R> Future<R> async(Callable<R> callable) {
    return components.getExecutorService().submit(callable);
  }

  @SneakyThrows
  protected void sleep(long millis) {
    Thread.sleep(millis);
  }

  private Logger getLoggerEndPoint() {
    return components.getGuiLogger().getEndPoint(PGConScriptEngineV1.class);
  }

  protected void script(String scriptName) {
    ScriptCache<Object> scriptCache = components.getScriptManager().getScriptByName(scriptName);
    if (scriptCache == null) {
      throw new InterruptScriptException("script " + scriptName + " not found");
    }
    new ScriptTask(scriptCache.getScript()).run();
  }

  protected void script(Class<Script<Object>> scriptClass) {
    ScriptCache<Object> scriptCache = components.getScriptManager().getScriptByClass(scriptClass);
    if (scriptCache == null) {
      throw new InterruptScriptException("script " + scriptClass + " not found");
    }
    new ScriptTask(scriptCache.getScript()).run();
  }

}

package com.duanxr.pgcon.script.engine;

import com.duanxr.pgcon.core.detect.api.ImageCompare;
import com.duanxr.pgcon.core.detect.api.OCR;
import com.duanxr.pgcon.exception.InterruptScriptException;
import com.duanxr.pgcon.exception.ResetScriptException;
import com.duanxr.pgcon.log.Logger;
import com.duanxr.pgcon.output.action.ButtonAction;
import com.duanxr.pgcon.output.action.StickAction;
import com.duanxr.pgcon.script.api.Script;
import com.duanxr.pgcon.script.api.ScriptInfo;
import com.duanxr.pgcon.script.component.ScriptCache;
import com.duanxr.pgcon.script.component.ScriptTask;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.function.Supplier;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

/**
 * @author 段然 2021/12/29
 */
@Setter
public abstract class PGConScriptEngineV1<T> extends BasicScriptEngine<T> {

  @Getter(value = AccessLevel.PRIVATE, lazy = true)
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

  protected Long detectLong(OCR.Param param, Long timeout, Runnable reset) {
    return until(() -> detect(param),
        input -> input.getTextAsNumber() != null,
        () -> sleep(30), timeout, reset).getTextAsNumber();
  }

  protected Long detectLong(OCR.Param param, Long timeout) {
    return detectLong(param, timeout, null);
  }

  protected Long detectLong(OCR.Param param) {
    return detectLong(param, null);
  }

  protected Long detectAccurateLong(OCR.Param param, int count, Long timeout, Runnable reset) {
    Map<Long, Integer> countMap = new HashMap<>();
    return until(() -> detect(param),
        input -> {
          Long number = input.getTextAsNumber();
          return number != null
              && countMap.compute(number, (k, v) -> v == null ? 1 : v + 1) >= count;
        },
        () -> sleep(30), timeout, reset).getTextAsNumber();
  }

  protected Long detectAccurateLong(OCR.Param param, int count, Long timeout) {
    return detectAccurateLong(param, count, timeout, null);
  }

  protected Long detectAccurateLong(OCR.Param param, int count) {
    return detectAccurateLong(param, count, null);
  }

  protected void press(ButtonAction action) {
    components.getControllerService().press(action);
  }

  protected void hold(ButtonAction action) {
    components.getControllerService().hold(action);
  }

  protected void hold(ButtonAction action, int time) {
    components.getControllerService().hold(action, time);
  }

  protected void release(ButtonAction action) {
    components.getControllerService().release(action);
  }

  protected void press(StickAction action) {
    components.getControllerService().press(action);
  }

  protected void hold(StickAction action) {
    components.getControllerService().hold(action);
  }

  protected void hold(StickAction action, int time) {
    components.getControllerService().hold(action, time);
  }

  protected void release(StickAction action) {
    components.getControllerService().release(action);
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
      action.run();
      d = supplier.get();
    }
    return d;
  }

  protected void push(String message) {
    try {
      components.getNotifyService().push(getInfo().getName(), message);
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

  protected void script(String script) {
    ScriptCache<Object> scriptCache = components.getScriptManager().getScript(script);
    if (scriptCache == null) {
      throw new InterruptScriptException("script " + script + " not found");
    }
    new ScriptTask(scriptCache.getScript()).run();
  }

}

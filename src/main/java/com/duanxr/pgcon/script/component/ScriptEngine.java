package com.duanxr.pgcon.script.component;

import com.duanxr.pgcon.algo.detect.api.ImageCompare;
import com.duanxr.pgcon.algo.detect.api.OCR;
import com.duanxr.pgcon.output.Controller;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.function.Supplier;
import lombok.Setter;
import lombok.SneakyThrows;

/**
 * @author 段然 2021/12/29
 */
@Setter
public abstract class ScriptEngine {

  protected OCR ocr;
  protected Controller controller;
  protected ImageCompare imageCompare;
  protected ExecutorService executorService;
  protected ScriptManager scriptManager;

  protected ScriptEngine() {
  }

  @SneakyThrows
  protected void sleep(long millis) {
    Thread.sleep(millis);
  }

  protected <D> D until(Supplier<D> supplier, Function<D, Boolean> checker, Runnable action) {
    D d = supplier.get();
    while (!checker.apply(d)) {
      action.run();
      d = supplier.get();
    }
    return d;
  }

  protected <D> D until(Supplier<D> supplier, Function<D, Boolean> checker, Runnable action,
      long maxMillis) {
    D d = supplier.get();
    long limit = System.currentTimeMillis() + maxMillis;
    while (!checker.apply(d)) {
      if (System.currentTimeMillis() > limit) {
        return null;
      }
      action.run();
      d = supplier.get();
    }
    return d;
  }

  protected <D> D until(Supplier<D> supplier, Function<D, Boolean> checker, Runnable action,
      int maxTimes) {
    D d = supplier.get();
    int times = 0;
    while (!checker.apply(d)) {
      if (times >= maxTimes) {
        return null;
      }
      action.run();
      d = supplier.get();
      times++;
    }
    return d;
  }

  @SneakyThrows
  protected void script(String script) {
    Objects.requireNonNull(scriptManager.getScripts().get(script),
        "cannot find subscript " + script).execute();
  }

  protected Long ocrNumber(OCR.Param param, int length) {
    return until(() -> ocr.detect(param),
        input -> input.getTextWithoutSpace().length() == length &&  input.getTextAsNumber() != null,
        () -> sleep(200)).getTextAsNumber();
  }

  protected Long ocrNumber(OCR.Param param) {
    return until(() -> ocr.detect(param),
        input -> input.getTextAsNumber() != null,
        () -> sleep(200)).getTextAsNumber();
  }


  protected void async(Runnable runnable) {
    executorService.submit(runnable);
  }

  protected <R> Future<R> async(Callable<R> callable) {
    return executorService.submit(callable);
  }


}

package com.duanxr.pgcon.script;

import com.duanxr.pgcon.core.detect.api.ImageCompare;
import com.duanxr.pgcon.core.detect.api.OCR;
import com.duanxr.pgcon.output.Controller;
import com.duanxr.pgcon.output.action.ButtonAction;
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
  protected ImageCompare imageCompare;
  protected Controller controller;
  protected ExecutorService executorService;

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

  protected Long ocrNumber(OCR.Param param, int length) {
    return parseNumber(until(() -> ocr.detect(param),
        input -> input.getText().length() == length && parseNumber(input.getText()) != null,
        () -> sleep(200)).getText());
  }

  protected Long ocrNumber(OCR.Param param) {
    return parseNumber(until(() -> ocr.detect(param),
        input -> parseNumber(input.getText()) != null,
        () -> sleep(200)).getText());
  }

  private Long parseNumber(String str) {
    try {
      return Long.valueOf(str);
    } catch (Exception ignored) {
      return null;
    }
  }

  protected void async(Runnable runnable) {
    executorService.submit(runnable);
  }

  protected <R> Future<R> async(Callable<R> callable) {
    return executorService.submit(callable);
  }
}

package com.duanxr.pgcon.script;

import com.duanxr.pgcon.core.detect.api.ImageCompare;
import com.duanxr.pgcon.core.detect.api.OCR;
import com.duanxr.pgcon.output.Controller;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
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

  protected void async(Runnable runnable) {
    executorService.submit(runnable);
  }

  protected <R> Future<R> async(Callable<R> callable) {
    return executorService.submit(callable);
  }
}

package com.duanxr.pgcon.script.engine;

import com.duanxr.pgcon.core.detect.api.ImageCompare;
import com.duanxr.pgcon.core.detect.api.OCR;
import com.duanxr.pgcon.core.model.Area;
import com.duanxr.pgcon.exception.TimeOutException;
import com.duanxr.pgcon.gui.display.DrawEvent;
import com.duanxr.pgcon.gui.display.impl.Rectangle;
import com.duanxr.pgcon.gui.display.impl.Text;
import com.duanxr.pgcon.output.action.ButtonAction;
import com.duanxr.pgcon.output.action.StickAction;
import com.duanxr.pgcon.script.api.BasicScriptEngine;
import com.duanxr.pgcon.util.LogUtil;
import java.awt.Color;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.function.Supplier;
import lombok.Setter;
import lombok.SneakyThrows;

/**
 * @author 段然 2021/12/29
 */
@Setter
public abstract class PGConScriptEngineV1<T> extends BasicScriptEngine<T> {
  protected PGConScriptEngineV1(ScriptInfo<T> scriptInfo) {
    super(scriptInfo);
  }
  protected void info(String msg, Object... args) {
    components.getGuiLogger().info(msg, args);
  }

  protected void warn(String msg, Object... args) {
    components.getGuiLogger().warn(msg, args);
  }

  protected ImageCompare.Result detect(ImageCompare.Param param) {
    if (components.getEnableDebug().get()) {
      long start = System.currentTimeMillis();
      drawImageCompareParam(param);
      ImageCompare.Result detect = components.getDetectService().detect(param);
      drawImageCompareResult(param, detect);
      debug("image compare {} similarity: {} , cost {} ms", param.hashCode(),
          detect.getSimilarity(), System.currentTimeMillis() - start);
      return detect;
    } else {
      return components.getDetectService().detect(param);
    }
  }

  private void drawImageCompareParam(ImageCompare.Param param) {
    Area rect = getDebugRectArea(param.getArea());
    components.getDisplayService().draw(
        new DrawEvent("RECT_" + param, new Rectangle(
            rect,
            new Color(84, 216, 255, 190),
            3000)));
  }

  private void drawImageCompareResult(ImageCompare.Param param, ImageCompare.Result detect) {
    String similarity = LogUtil.format("%.02f", detect.getSimilarity()).toString();
    Area rect = getDebugRectArea(param.getArea());
    components.getDisplayService().draw(
        new DrawEvent("TEXT_" + param, new Text(
            rect, similarity,
            new Color(14, 37, 45, 255),
            14, 3000)));
  }

  protected void debug(String msg, Object... args) {
    components.getGuiLogger().debug(msg, args);
  }

  private Area getDebugRectArea(Area rect) {
    return Area.ofRect(rect.getX() / 2, rect.getY() / 2, rect.getWidth() / 2, rect.getHeight() / 2);
  }

  protected void press(ButtonAction action) {
    components.getController().press(action);
  }

  protected void hold(ButtonAction action) {
    components.getController().hold(action);
  }

  protected void hold(ButtonAction action, int time) {
    components.getController().hold(action, time);
  }

  protected void release(ButtonAction action) {
    components.getController().release(action);
  }

  protected void press(StickAction action) {
    components.getController().press(action);
  }

  protected void hold(StickAction action) {
    components.getController().hold(action);
  }

  protected void hold(StickAction action, int time) {
    components.getController().hold(action, time);
  }

  protected void release(StickAction action) {
    components.getController().release(action);
  }

  protected <D> D until(Supplier<D> supplier, Function<D, Boolean> checker, Runnable action,
      long maxMillis) {
    D d = supplier.get();
    long limit = System.currentTimeMillis() + maxMillis;
    while (!checker.apply(d)) {
      if (System.currentTimeMillis() > limit) {
        throw new TimeOutException();
      }
      action.run();
      d = supplier.get();
    }
    return d;
  }

  protected <D> D until(Supplier<D> supplier, Function<D, Boolean> checker, Runnable action) {
    D d = supplier.get();
    while (!checker.apply(d)) {
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

  protected void error(String msg, Object... args) {
    components.getGuiLogger().error(msg, args);
  }

  protected Long numberOcr(OCR.Param param, int times) {
    return until(() -> detect(param),
        input -> input.getTextAsNumber() != null,
        () -> sleep(50), times).getTextAsNumber();
  }

  protected <D> D until(Supplier<D> supplier, Function<D, Boolean> checker, Runnable action,
      int maxTimes) {
    D d = supplier.get();
    int times = 0;
    while (!checker.apply(d)) {
      if (times >= maxTimes) {
        throw new TimeOutException();
      }
      action.run();
      d = supplier.get();
      times++;
    }
    return d;
  }

  protected OCR.Result detect(OCR.Param param) {
    if (components.getEnableDebug().get()) {
      long start = System.currentTimeMillis();
      drawOcrParam(param);
      OCR.Result detect = components.getDetectService().detect(param);
      drawOcrResult(param, detect);
      debug("ocr {} detected: {} , confidence: {}, cost {} ms", param.hashCode(),
          detect.getTextWithoutSpace(), detect.getConfidence(), System.currentTimeMillis() - start);
      return detect;
    } else {
      return components.getDetectService().detect(param);
    }
  }

  @SneakyThrows
  protected void sleep(long millis) {
    Thread.sleep(millis);
  }

  private void drawOcrParam(OCR.Param param) {

    Area rect = getDebugRectArea(param.getArea());
    components.getDisplayService().draw(
        new DrawEvent("RECT_" + param, new Rectangle(
            rect,
            new Color(237, 224, 77, 190),
            3000)));
  }

  private void drawOcrResult(OCR.Param param, OCR.Result detect) {
    String result = detect.getTextWithoutSpace();
    Area rect = getDebugRectArea(param.getArea());
    components.getDisplayService().draw(
        new DrawEvent("TEXT_" + param, new Text(
            rect, result,
            new Color(66, 60, 19, 255),
            14, 3000)));
  }

  protected void async(Runnable runnable) {
    components.getExecutorService().submit(runnable);
  }

  protected <R> Future<R> async(Callable<R> callable) {
    return components.getExecutorService().submit(callable);
  }

  protected void script(String name) {

  }

}

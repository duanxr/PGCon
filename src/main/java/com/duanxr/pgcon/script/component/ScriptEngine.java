package com.duanxr.pgcon.script.component;

import com.duanxr.pgcon.algo.detect.api.ImageCompare;
import com.duanxr.pgcon.algo.detect.api.OCR;
import com.duanxr.pgcon.algo.model.Area;
import com.duanxr.pgcon.core.ScriptManager;
import com.duanxr.pgcon.gui.exception.GuiAlertException;
import com.duanxr.pgcon.core.DisplayHandler;
import com.duanxr.pgcon.gui.display.DrawEvent;
import com.duanxr.pgcon.gui.display.impl.Rectangle;
import com.duanxr.pgcon.gui.display.impl.Text;
import com.duanxr.pgcon.gui.log.GuiLogger;
import com.duanxr.pgcon.output.Controller;
import com.duanxr.pgcon.output.action.ButtonAction;
import com.duanxr.pgcon.output.action.StickAction;
import com.duanxr.pgcon.script.api.Script;
import com.duanxr.pgcon.util.LogUtil;
import java.awt.Color;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.function.Supplier;
import lombok.Setter;
import lombok.SneakyThrows;

/**
 * @author 段然 2021/12/29
 */
@Setter
public abstract class ScriptEngine {

  protected static ButtonAction A = ButtonAction.A;
  protected static ButtonAction B = ButtonAction.B;
  protected static ButtonAction X = ButtonAction.X;
  protected static ButtonAction Y = ButtonAction.Y;
  protected static ButtonAction L = ButtonAction.L;
  protected static ButtonAction R = ButtonAction.R;
  protected static ButtonAction ZL = ButtonAction.ZL;
  protected static ButtonAction ZR = ButtonAction.ZR;
  protected static ButtonAction L_STICK = ButtonAction.L_STICK;
  protected static ButtonAction R_STICK = ButtonAction.R_STICK;
  protected static ButtonAction D_TOP = ButtonAction.D_TOP;
  protected static ButtonAction D_BOTTOM = ButtonAction.D_BOTTOM;
  protected static ButtonAction D_LEFT = ButtonAction.D_LEFT;
  protected static ButtonAction D_RIGHT = ButtonAction.D_RIGHT;
  protected static ButtonAction PLUS = ButtonAction.PLUS;
  protected static ButtonAction MINUS = ButtonAction.MINUS;
  protected static ButtonAction CAPTURE = ButtonAction.CAPTURE;
  protected static ButtonAction HOME = ButtonAction.HOME;
  protected static StickAction L_TOP = StickAction.L_TOP;
  protected static StickAction L_BOTTOM = StickAction.L_BOTTOM;
  protected static StickAction L_LEFT = StickAction.L_LEFT;
  protected static StickAction L_RIGHT = StickAction.L_RIGHT;
  protected static StickAction R_TOP = StickAction.R_TOP;
  protected static StickAction R_BOTTOM = StickAction.R_BOTTOM;
  protected static StickAction R_LEFT = StickAction.R_LEFT;
  protected static StickAction R_RIGHT = StickAction.R_RIGHT;
  protected static StickAction L_TOP_RIGHT = StickAction.L_TOP_RIGHT;
  protected static StickAction L_BOTTOM_RIGHT = StickAction.L_BOTTOM_RIGHT;
  protected static StickAction L_BOTTOM_LEFT = StickAction.L_BOTTOM_LEFT;
  protected static StickAction L_TOP_LEFT = StickAction.L_TOP_LEFT;
  protected static StickAction R_TOP_RIGHT = StickAction.R_TOP_RIGHT;
  protected static StickAction R_BOTTOM_RIGHT = StickAction.R_BOTTOM_RIGHT;
  protected static StickAction R_BOTTOM_LEFT = StickAction.R_BOTTOM_LEFT;
  protected static StickAction R_TOP_LEFT = StickAction.R_TOP_LEFT;
  protected static StickAction L_CENTER = StickAction.L_CENTER;
  protected static StickAction R_CENTER = StickAction.R_CENTER;
  protected AtomicBoolean enableDebug;
  private Controller controller;
  private DisplayHandler displayHandler;
  private ExecutorService executorService;
  private GuiLogger guiLogger;
  private ImageCompare imageCompare;
  private OCR ocr;
  private ScriptManager scriptManager;

  protected ScriptEngine() {
  }

  protected void info(String msg, Object... args) {
    guiLogger.info(msg, args);
  }

  protected void warn(String msg, Object... args) {
    guiLogger.warn(msg, args);
  }

  protected void error(String msg, Object... args) {
    guiLogger.error(msg, args);
  }

  protected ImageCompare.Result imageCompare(ImageCompare.Param param) {
    if (enableDebug.get()) {
      long start = System.currentTimeMillis();
      drawImageCompareParam(param);
      ImageCompare.Result detect = imageCompare.detect(param);
      drawImageCompareResult(param, detect);
      debug("image compare {} similarity: {} , cost {} ms", param.hashCode(), detect.getSimilarity(), System.currentTimeMillis() - start);
      return detect;
    } else {
      return imageCompare.detect(param);
    }
  }

  private void drawImageCompareResult(ImageCompare.Param param, ImageCompare.Result detect) {
    String similarity = LogUtil.format("%.02f", detect.getSimilarity()).toString();
    Area rect = getDebugRectArea(param.getArea());
    displayHandler.draw(
        new DrawEvent("TEXT_" + param, new Text(
            rect, similarity,
            new Color(14, 37, 45, 255),
            14, 3000)));
  }

  private Area getDebugRectArea(Area rect) {
    return Area.ofRect(rect.getX() / 2, rect.getY() / 2, rect.getWidth() / 2, rect.getHeight() / 2);
  }

  private void drawImageCompareParam(ImageCompare.Param param) {
    Area rect = getDebugRectArea(param.getArea());
    displayHandler.draw(
        new DrawEvent("RECT_" + param, new Rectangle(
            rect,
            new Color(84, 216, 255, 190),
            3000)));
  }

  protected void debug(String msg, Object... args) {
    guiLogger.debug(msg, args);
  }

  protected void press(ButtonAction action) {
    controller.press(action);
  }

  protected void hold(ButtonAction action) {
    controller.hold(action);
  }

  protected void hold(ButtonAction action, int time) {
    controller.hold(action, time);
  }

  protected void release(ButtonAction action) {
    controller.release(action);
  }

  protected void press(StickAction action) {
    controller.press(action);
  }

  protected void hold(StickAction action) {
    controller.hold(action);
  }

  protected void hold(StickAction action, int time) {
    controller.hold(action, time);
  }

  protected void release(StickAction action) {
    controller.release(action);
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
    Script subScript = scriptManager.getScripts().get(script);
    if (subScript == null) {
      throw new GuiAlertException("cannot find script: " + script);
    }
    subScript.execute();
  }
//todo arrcur
  protected Long ocrNumber(OCR.Param param, int length) {
    return until(() -> ocr(param),
        input -> input.getTextWithoutSpace().length() == length && input.getTextAsNumber() != null,
        () -> sleep(200)).getTextAsNumber();
  }

  protected <D> D until(Supplier<D> supplier, Function<D, Boolean> checker, Runnable action) {
    D d = supplier.get();
    while (!checker.apply(d)) {
      action.run();
      d = supplier.get();
    }
    return d;
  }

  protected OCR.Result ocr(OCR.Param param) {
    if (enableDebug.get()) {
      long start = System.currentTimeMillis();
      drawOCRParam(param);
      OCR.Result detect = ocr.detect(param);
      drawOCRResult(param, detect);
      debug("ocr {} detected: {} , confidence: {}, cost {} ms", param.hashCode(),
          detect.getTextWithoutSpace(), detect.getConfidence(), System.currentTimeMillis() - start);
      return detect;
    } else {
      return ocr.detect(param);
    }
  }

  private void drawOCRResult(OCR.Param param, OCR.Result detect) {
    String result = detect.getTextWithoutSpace();
    Area rect = getDebugRectArea(param.getArea());
    displayHandler.draw(
        new DrawEvent("TEXT_" + param, new Text(
            rect, result,
            new Color(66, 60, 19, 255),
            14, 3000)));
  }

  private void drawOCRParam(OCR.Param param) {

    Area rect = getDebugRectArea(param.getArea());
    displayHandler.draw(
        new DrawEvent("RECT_" + param, new Rectangle(
            rect,
            new Color(237, 224, 77, 190),
            3000)));
  }

  @SneakyThrows
  protected void sleep(long millis) {
    Thread.sleep(millis);
  }

  protected Long ocrNumber(OCR.Param param) {
    return until(() -> ocr(param),
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

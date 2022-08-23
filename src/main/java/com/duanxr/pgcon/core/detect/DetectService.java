package com.duanxr.pgcon.core.detect;

import com.duanxr.pgcon.core.detect.api.ImageCompare;
import com.duanxr.pgcon.core.detect.api.OCR;
import com.duanxr.pgcon.core.model.Area;
import com.duanxr.pgcon.gui.display.DisplayService;
import com.duanxr.pgcon.gui.display.DrawEvent;
import com.duanxr.pgcon.gui.display.impl.Rectangle;
import com.duanxr.pgcon.gui.display.impl.Text;
import com.duanxr.pgcon.log.GuiLogger;
import com.duanxr.pgcon.util.StringFormatUtil;
import java.awt.Color;
import java.util.concurrent.atomic.AtomicBoolean;
import org.springframework.stereotype.Component;

/**
 * @author 段然 2022/8/16
 */
@Component
public class DetectService {

  private final DisplayService displayService;
  private final AtomicBoolean enableDebug;
  private final GuiLogger guiLogger;
  private final ImageCompare imageCompare;
  private final OCR ocr;

  public DetectService(DisplayService displayService, AtomicBoolean enableDebug,
      ImageCompare imageCompare, GuiLogger guiLogger, OCR ocr) {
    this.displayService = displayService;
    this.enableDebug = enableDebug;
    this.imageCompare = imageCompare;
    this.guiLogger = guiLogger;
    this.ocr = ocr;
  }

  public OCR.Result detect(OCR.Param param) {
    if (enableDebug.get()) {
      long start = System.currentTimeMillis();
      drawDebugInfo(param);
      OCR.Result detect = ocr.detect(param);
      drawDebugInfo(param, detect);
      guiLogger.debug("ocr {} detected: {} , confidence: {}, cost {} ms", param.hashCode(),
          detect.getTextWithoutSpace(), detect.getConfidence(), System.currentTimeMillis() - start);
      return detect;
    } else {
      return ocr.detect(param);
    }
  }

  private void drawDebugInfo(OCR.Param param) {
    Area rect = displayService.inputToScreen(param.getArea());
    displayService.draw(
        new DrawEvent("RECT_" + param, new Rectangle(
            rect,
            new Color(237, 224, 77, 190),
            3000)));
  }

  private void drawDebugInfo(OCR.Param param, OCR.Result detect) {
    String result = detect.getTextWithoutSpace();
    Area rect = displayService.inputToScreen(param.getArea());
    displayService.draw(
        new DrawEvent("TEXT_" + param, new Text(
            rect, result,
            new Color(66, 60, 19, 255),
            14, 3000)));
  }

  public ImageCompare.Result detect(ImageCompare.Param param) {
    if (enableDebug.get()) {
      long start = System.currentTimeMillis();
      drawDebugInfo(param);
      ImageCompare.Result detect = imageCompare.detect(param);
      drawDebugInfo(param, detect);
      String similarity = StringFormatUtil.formatObj("%.02f", detect.getSimilarity()).toString();
      guiLogger.debug("image compare {} similarity: {}, cost {} ms", param.hashCode(),
          similarity, System.currentTimeMillis() - start);
      return detect;
    } else {
      return imageCompare.detect(param);
    }
  }

  private void drawDebugInfo(ImageCompare.Param param) {
    Area rect = displayService.inputToScreen(param.getArea());
    displayService.draw(
        new DrawEvent("RECT_" + param, new Rectangle(
            rect,
            new Color(84, 216, 255, 190),
            3000)));
  }

  private void drawDebugInfo(ImageCompare.Param param, ImageCompare.Result detect) {
    String similarity = StringFormatUtil.formatObj("%.0f%%", detect.getSimilarity() * 100).toString();
    Area rect = displayService.inputToScreen(param.getArea());
    displayService.draw(
        new DrawEvent("TEXT_" + param, new Text(
            rect, similarity,
            new Color(14, 37, 45, 255),
            14, 3000)));
  }


}

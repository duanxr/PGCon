package com.duanxr.pgcon.core.detect.impl;

import static org.bytedeco.javacpp.lept.pixDestroy;

import com.duanxr.pgcon.input.component.FrameManager;
import com.duanxr.pgcon.input.component.FrameManager.CachedFrame;
import com.duanxr.pgcon.core.detect.api.OCR;
import com.duanxr.pgcon.core.detect.model.Area;
import com.duanxr.pgcon.util.ImageUtil;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.lept.PIX;
import org.bytedeco.javacpp.tesseract.TessBaseAPI;
import org.opencv.core.Mat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author 段然 2021/12/29
 */
@Component
public class TesseractOCR implements OCR {

  private final TessBaseAPI tessBaseAPI = new TessBaseAPI();
  private final FrameManager frameManager;

  @Autowired
  public TesseractOCR(FrameManager frameManager) {
    this.frameManager = frameManager;
  }

  private String doDetect(Mat target, Method method) {
    PIX pix = ImageUtil.matToPix(target);
    String text = ocr(pix);
    pixDestroy(pix);
    return text;
  }

  private synchronized String ocr(PIX pix) {
    tessBaseAPI.SetImage(pix);
    BytePointer outText = tessBaseAPI.GetUTF8Text();
    String text = outText.getString();
    outText.deallocate();
    return text;
  }

  private Mat getTarget(CachedFrame cachedFrame, Area area) {
    Mat originMat = cachedFrame.getMat();
    return area == null ? originMat : ImageUtil.splitMat(originMat, area);
  }

  @Override
  public Result detect(Param param) {
    Area area = param.getArea();
    Method method = param.getMethod();
    return detectNow(area, method);
  }

  private Result detectNow(Area area, Method method) {
    CachedFrame cachedFrame = frameManager.get();
    Mat targetMat = getTarget(cachedFrame, area);
    String text = doDetect(targetMat, method);
    return Result.builder().text(text).timestamp(cachedFrame.getTimestamp()).build();
  }
}

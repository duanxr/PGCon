package com.duanxr.pgcon.core.detect.impl;


import com.duanxr.pgcon.core.ResourceManager;
import com.duanxr.pgcon.input.component.FrameManager;
import com.duanxr.pgcon.input.component.FrameManager.CachedFrame;
import com.duanxr.pgcon.core.detect.api.OCR;
import com.duanxr.pgcon.core.detect.model.Area;
import com.duanxr.pgcon.util.DebugUtil;
import com.duanxr.pgcon.util.ImageUtil;
import com.google.common.base.Strings;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.tesseract.TessBaseAPI;
import org.opencv.core.Mat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author 段然 2021/12/29
 */
@Component
@Slf4j
public class TesseractOCR implements OCR {

  private final Map<Method,TessBaseAPI> apiMap;
  private final FrameManager frameManager;
  private final  ResourceManager resourceManager;

  @Autowired
  public TesseractOCR(FrameManager frameManager, ResourceManager resourceManager) {
    this.frameManager = frameManager;
    this.resourceManager = resourceManager;
    this.apiMap = new HashMap<>();
    initApi(apiMap, Method.ENG, "D:/DuanXR/Project/DuanXR/PGCon/src/main/resources/tessdata/");
    initApi(apiMap, Method.CHS, "D:/DuanXR/Project/DuanXR/PGCon/src/main/resources/tessdata/");
    initApi(apiMap, Method.NMU, "D:/DuanXR/Project/DuanXR/PGCon/src/main/resources/tessdata/");

  }

  private void initApi(Map<Method, TessBaseAPI> apiMap, Method method, String path) {
    TessBaseAPI tessBaseAPI = new TessBaseAPI();
    File file = resourceManager.getFile(path);
    long length = file.length();
    int init = tessBaseAPI.Init(file.getAbsolutePath(), method.name().toLowerCase());
    if (init != 0) {
      log.error("Could not initialize tesseract.");
    }
    apiMap.put(method, tessBaseAPI);
  }

  private String doDetect(Mat target, Method method) {
    try {
      String text = ocr(target,apiMap.get(method));
      return Strings.nullToEmpty(text).trim();
    } catch (Exception e) {
      log.error("", e);
      return "";
    }
  }

  private synchronized String ocr(Mat mat, TessBaseAPI tessBaseAPI) {
    tessBaseAPI.SetImage(ImageUtil.matToPix(mat));
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
    try {
      return detectNow(area, method);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  private Result detectNow(Area area, Method method) {
    CachedFrame cachedFrame = frameManager.get();
    Mat targetMat = getTarget(cachedFrame, area);
    String text = doDetect(targetMat, method);
    return Result.builder().text(text).timestamp(cachedFrame.getTimestamp()).build();
  }
}

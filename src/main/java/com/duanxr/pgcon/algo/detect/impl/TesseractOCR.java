package com.duanxr.pgcon.algo.detect.impl;


import com.duanxr.pgcon.gui.component.ResourceManager;
import com.duanxr.pgcon.input.component.FrameManager;
import com.duanxr.pgcon.input.component.FrameManager.CachedFrame;
import com.duanxr.pgcon.algo.detect.api.OCR;
import com.duanxr.pgcon.algo.detect.model.Area;
import com.duanxr.pgcon.util.ImageConvertUtil;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.google.common.base.Strings;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.math3.util.Pair;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.leptonica.PIX;
import org.bytedeco.tesseract.TessBaseAPI;
import org.bytedeco.tesseract.global.tesseract;
import org.opencv.core.Mat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author 段然 2021/12/29
 */
@Component
@Slf4j
public class TesseractOCR implements OCR {

  private static final Integer DEFAULT_OCR_ENGINE_MODE = tesseract.OEM_LSTM_ONLY;
  private static final Integer DEFAULT_PAGE_SEG_MODE = tesseract.PSM_SINGLE_LINE;
  private static final String DEFAULT_TESSDATA_PATH = "D:/DuanXR/Project/DuanXR/PGCon/src/main/resources/tessdata/";
  private static final String TESSEDIT_CHAR_BLACKLIST = "tessedit_char_blacklist";
  private static final String TESSEDIT_CHAR_WHITELIST = "tessedit_char_whitelist";
  private final Map<Method, TessBaseAPI> defaultApiMap;
  private final FrameManager frameManager;
  private final ResourceManager resourceManager;
  private final LoadingCache<ApiConfig, TessBaseAPI> specialApiMap;

  @Autowired
  public TesseractOCR(FrameManager frameManager, ResourceManager resourceManager) {
    this.frameManager = frameManager;
    this.resourceManager = resourceManager;
    this.defaultApiMap = new HashMap<>();
    this.specialApiMap = Caffeine.newBuilder().build(this::createTessBaseAPI);
    for (Method method : Method.values()) {
      ApiConfig config = ApiConfig.builder().method(method).build();
      defaultApiMap.put(method, createTessBaseAPI(config));
    }
  }

  private TessBaseAPI createTessBaseAPI(ApiConfig apiConfig) {
    TessBaseAPI tessBaseAPI = new TessBaseAPI();
    String path = ObjectUtils.firstNonNull(apiConfig.getPath(), DEFAULT_TESSDATA_PATH);
    String method = apiConfig.getMethod().name().toLowerCase();
    Integer pageSegMode = ObjectUtils.firstNonNull(apiConfig.getPageSegMode(),
        DEFAULT_PAGE_SEG_MODE);
    Integer ocrEngineMode = ObjectUtils.firstNonNull(apiConfig.getOcrEngineMode(),
        DEFAULT_OCR_ENGINE_MODE);
    tessBaseAPI.SetPageSegMode(pageSegMode);
    if (!Strings.isNullOrEmpty(apiConfig.getWhitelist())) {
      tessBaseAPI.SetVariable(TESSEDIT_CHAR_WHITELIST, apiConfig.getWhitelist());
    }
    if (!Strings.isNullOrEmpty(apiConfig.getBlacklist())) {
      tessBaseAPI.SetVariable(TESSEDIT_CHAR_BLACKLIST, apiConfig.getBlacklist());
    }
    int initResult = tessBaseAPI.Init(resourceManager.getFile(path).getAbsolutePath(), method,
        ocrEngineMode);
    if (initResult != 0) {
      log.error("Could not initialize tesseract! config: {}", apiConfig);
      return null;
    }
    return tessBaseAPI;
  }

  @Override
  public Result detect(Param param) {
    CachedFrame cachedFrame = getFrame();
    PIX image = getImage(cachedFrame, param.getArea());
    TessBaseAPI tessBaseAPI = getApi(param.getApiConfig());
    Pair<String, Integer> result = getText(tessBaseAPI, image);
    return warpResult(result, cachedFrame);
  }

  private CachedFrame getFrame() {
    return frameManager.get();
  }

  private PIX getImage(CachedFrame cachedFrame, Area area) {
    Mat imageMat = splitMat(cachedFrame, area);
    return ImageConvertUtil.matToPix(imageMat);
  }

  private TessBaseAPI getApi(ApiConfig apiConfig) {
    return isDefaultApi(apiConfig) ? defaultApiMap.get(apiConfig.getMethod())
        : specialApiMap.get(apiConfig);
  }

  private synchronized Pair<String, Integer> getText(TessBaseAPI tessBaseAPI, PIX image) {
    try {
      tessBaseAPI.SetImage(image);
      BytePointer outText = tessBaseAPI.GetUTF8Text();
      String text = outText.getString();
      int conf = tessBaseAPI.MeanTextConf();
      outText.deallocate();
      return Pair.create(text, conf);
    } catch (Exception e) {
      log.error("ocr error", e);
      return Pair.create("", 0);
    }
  }

  private Result warpResult(Pair<String, Integer> result, CachedFrame cachedFrame) {
    return Result.builder().text(result.getFirst()).confidence(result.getSecond())
        .cachedFrame(cachedFrame).build();
  }

  private Mat splitMat(CachedFrame cachedFrame, Area area) {
    Mat originMat = cachedFrame.getMat();
    return area == null ? originMat : ImageConvertUtil.splitMat(originMat, area);
  }

  private boolean isDefaultApi(ApiConfig apiConfig) {
    return Strings.isNullOrEmpty(apiConfig.getPath()) && Strings.isNullOrEmpty(
        apiConfig.getWhitelist()) && Strings.isNullOrEmpty(apiConfig.getBlacklist())
        && Objects.isNull(apiConfig.getOcrEngineMode()) && Objects.isNull(
        apiConfig.getPageSegMode());
  }

  private void initApi(Map<Method, TessBaseAPI> apiMap, Method method, String path) {
    TessBaseAPI tessBaseAPI = new TessBaseAPI();
    File file = resourceManager.getFile(path);
    long length = file.length();
    int init = tessBaseAPI.Init(file.getAbsolutePath(), method.name().toLowerCase(),
        DEFAULT_OCR_ENGINE_MODE);
    if (init != 0) {
      log.error("Could not initialize tesseract.");
    }
    tessBaseAPI.SetPageSegMode(DEFAULT_PAGE_SEG_MODE);
    apiMap.put(method, tessBaseAPI);
  }

  @PreDestroy
  public void close() {
    defaultApiMap.values().forEach(TessBaseAPI::close);
    specialApiMap.asMap().values().forEach(TessBaseAPI::close);
  }
}

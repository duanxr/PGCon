package com.duanxr.pgcon.core.detect.impl;


import com.duanxr.pgcon.input.FrameCacheService;
import com.duanxr.pgcon.input.FrameCacheService.CachedFrame;
import com.duanxr.pgcon.core.detect.api.OCR;
import com.duanxr.pgcon.core.model.Area;
import com.duanxr.pgcon.core.preprocessing.PreProcessorConfig;
import com.duanxr.pgcon.core.preprocessing.PreprocessorFactory;
import com.duanxr.pgcon.util.ImageUtil;
import com.duanxr.pgcon.util.MatUtil;
import com.duanxr.pgcon.util.TesseractDataLoadUtil;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.google.common.base.Strings;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.PreDestroy;
import lombok.SneakyThrows;
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

@Slf4j
@Component
public class TesseractOCR extends ImageDetector<OCR.Result, OCR.Param> implements OCR {
  public static final Integer DEFAULT_OCR_ENGINE_MODE = tesseract.OEM_DEFAULT;
  public static final Integer DEFAULT_PAGE_SEG_MODE = tesseract.PSM_RAW_LINE;
  private static final String TESSEDIT_CHAR_BLACKLIST = "tessedit_char_blacklist";
  private static final String TESSEDIT_CHAR_WHITELIST = "tessedit_char_whitelist";
  private final Map<Method, TessBaseAPI> defaultApiMap;
  private final LoadingCache<ApiConfig, TessBaseAPI> specialApiMap;

  @Autowired
  public TesseractOCR(PreprocessorFactory preprocessorFactory, FrameCacheService frameCacheService) {
    super(frameCacheService, preprocessorFactory);
    this.defaultApiMap = new HashMap<>();
    this.specialApiMap = Caffeine.newBuilder().build(this::createTessBaseAPI);
    for (Method method : Method.values()) {
      ApiConfig config = ApiConfig.builder().method(method).build();
      defaultApiMap.put(method, createTessBaseAPI(config));
    }
  }

  @SneakyThrows
  private TessBaseAPI createTessBaseAPI(ApiConfig apiConfig) {
    TessBaseAPI tessBaseAPI = new TessBaseAPI();
    String path = TesseractDataLoadUtil.loadResourceTesseractData(apiConfig.getMethod().name().toLowerCase());
    String method = apiConfig.getMethod().name().toLowerCase();
    Integer pageSegMode = ObjectUtils.firstNonNull(apiConfig.getPageSegMode(),
        DEFAULT_PAGE_SEG_MODE);
    Integer ocrEngineMode = ObjectUtils.firstNonNull(apiConfig.getOcrEngineMode(),
        DEFAULT_OCR_ENGINE_MODE);
    tessBaseAPI.SetPageSegMode(pageSegMode);
    int initResult = tessBaseAPI.Init(path, method, ocrEngineMode);
    if (initResult != 0) {
      log.error("Could not initialize tesseract! config: {}", apiConfig);
      return null;
    }
    if (!Strings.isNullOrEmpty(apiConfig.getWhitelist())) {
      tessBaseAPI.SetVariable(TESSEDIT_CHAR_WHITELIST, apiConfig.getWhitelist());
    }
    if (!Strings.isNullOrEmpty(apiConfig.getBlacklist())) {
      tessBaseAPI.SetVariable(TESSEDIT_CHAR_BLACKLIST, apiConfig.getBlacklist());
    }

    return tessBaseAPI;
  }

  @Override
  public Result detect(Param param) {
    TessBaseAPI tessBaseAPI = getApi(param.getApiConfig());
    Area area = param.getArea();
    List<PreProcessorConfig> preProcessors = param.getPreProcessors();
    CachedFrame cachedFrame = getImage();
    Mat targetMat = getTarget(cachedFrame, area, !preProcessors.isEmpty());
    targetMat = tryPreProcess(targetMat, preProcessors);
    PIX image = ImageUtil.matToPix(targetMat);
    Pair<String, Integer> result = getText(tessBaseAPI, image);
    return warpResult(result, cachedFrame,param);
  }

  private CachedFrame getFrame() {
    return frameCacheService.getFrame();
  }

  private Mat getTarget(CachedFrame cachedFrame, Area area, boolean deepCopy) {
    return area == null ? deepCopy ? MatUtil.deepCopy(cachedFrame.getMat())
        : cachedFrame.getMat()
        : deepCopy ? MatUtil.deepSplit(cachedFrame.getMat(), area)
            : MatUtil.split(cachedFrame.getMat(), area);
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

  private Result warpResult(Pair<String, Integer> result, CachedFrame cachedFrame, Param param) {
    return Result.builder().text(result.getFirst()).confidence(result.getSecond())
        .cachedFrame(cachedFrame).param(param).build();
  }

  private Mat splitMat(CachedFrame cachedFrame, Area area) {
    Mat originMat = cachedFrame.getMat();
    return area == null ? originMat : MatUtil.split(originMat, area);
  }

  private boolean isDefaultApi(ApiConfig apiConfig) {
    return Strings.isNullOrEmpty(apiConfig.getWhitelist()) && Strings.isNullOrEmpty(apiConfig.getBlacklist())
        && Objects.isNull(apiConfig.getOcrEngineMode()) && Objects.isNull(apiConfig.getPageSegMode());
  }

  @PreDestroy
  public void close() {
    defaultApiMap.values().forEach(TessBaseAPI::close);
    specialApiMap.asMap().values().forEach(TessBaseAPI::close);
  }
}

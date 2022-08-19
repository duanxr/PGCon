package com.duanxr.pgcon.core.detect.api;

import com.duanxr.pgcon.input.FrameCacheService;
import com.duanxr.pgcon.core.detect.api.OCR.Param;
import com.duanxr.pgcon.core.detect.api.OCR.Result;
import com.duanxr.pgcon.core.model.Area;
import com.duanxr.pgcon.core.preprocessing.PreProcessorConfig;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Singular;

/**
 * @author 段然 2021/12/28
 */
public interface OCR extends Detector<Result, Param> {

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  class Param {

    @NonNull
    private Area area;

    @Singular
    private List<PreProcessorConfig> preProcessors;

    @NonNull
    private ApiConfig apiConfig;

  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  class ApiConfig {

    @NonNull
    private Method method;
    private String whitelist;
    private String blacklist;
    private Integer pageSegMode;
    private Integer ocrEngineMode;
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  class Result {

    @NonNull
    private String text;

    @NonNull
    private Integer confidence;

    @NonNull
    private FrameCacheService.CachedFrame cachedFrame;

    public String getTextWithoutSpace() {
      return text.trim().replaceAll(" ", "").replaceAll("\n", "");
    }

    public Long getTextAsNumber() {
      try {
        return Long.parseLong(getTextWithoutSpace());
      } catch (NumberFormatException e) {
        return null;
      }
    }

    public String getTextWithoutSpaceAndNumber() {
      return getTextWithoutSpace().replaceAll("\\d", "");
    }

    public String getTextWithoutSpaceAndNumberAndComma() {
      return getTextWithoutSpaceAndNumber().replaceAll(",", "");
    }

    public String getTextWithoutSpaceAndNumberAndCommaAndDot() {
      return getTextWithoutSpaceAndNumberAndComma().replaceAll("\\.", "");
    }
  }

  enum Method {
    NMU,
    ENG,
    CHS,
    ;
  }
}


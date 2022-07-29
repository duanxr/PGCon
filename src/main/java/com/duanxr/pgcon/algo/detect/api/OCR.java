package com.duanxr.pgcon.algo.detect.api;

import com.duanxr.pgcon.algo.detect.model.Area;
import com.duanxr.pgcon.component.FrameManager;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

/**
 * @author 段然 2021/12/28
 */
public interface OCR extends Detector<OCR.Result, OCR.Param> {

  @Data
  @Builder
  class Param {

    @NonNull
    private Area area;

    @NonNull
    private ApiConfig apiConfig;

  }

  @Data
  @Builder
  @ToString
  @EqualsAndHashCode
  class ApiConfig {

    private String path;
    @NonNull
    private Method method;
    private String whitelist;
    private String blacklist;
    private Integer pageSegMode;
    private Integer ocrEngineMode;
  }

  @Data
  @Builder
  class Result {

    @NonNull
    private String text;

    @NonNull
    private Integer confidence;

    @NonNull
    private FrameManager.CachedFrame cachedFrame;

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
    public String getTextAsUpperCase() {
      return getTextWithoutSpaceAndNumberAndCommaAndDot().toUpperCase();
    }

    public String getTextAsLowerCase() {
      return getTextWithoutSpaceAndNumberAndCommaAndDot().toLowerCase();
    }

  }

  enum Method {
    NMU,
    ENG,
    CHS,
    ;
  }
}


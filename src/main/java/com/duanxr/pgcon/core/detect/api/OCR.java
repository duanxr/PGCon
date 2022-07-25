package com.duanxr.pgcon.core.detect.api;

import com.duanxr.pgcon.core.detect.model.Area;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

/**
 * @author 段然 2021/12/28
 */
public interface OCR extends Detector<OCR.Result, OCR.Param> {

  @Data
  @Builder
  class Param {

    private Area area;

    @NonNull
    private Method method;

  }

  @Data
  @Builder
  class Result {

    @NonNull
    private String text;

    @NonNull
    private Long timestamp;
  }

  enum Method {
    NMU,
    ENG,
    CHS,
    ;
  }
}


package com.duanxr.pgcon.algo.detect.api;

import com.duanxr.pgcon.algo.detect.model.Area;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

/**
 * @author 段然 2021/12/28
 */
public interface ImageCompare extends Detector<ImageCompare.Result, ImageCompare.Param> {

  @Data
  @Builder
  class Param {

    @NonNull
    private Area area;

    @NonNull
    private String template;

    private String mask;

    @NonNull
    private Method method;

  }

  @Data
  @Builder
  class Result {

    @NonNull
    private Double similarity;

    @NonNull
    private Long timestamp;
  }


  enum Method {
    ORB,
    TM_SQDIFF,
    TM_CCORR,
    TM_CCOEFF;
  }
}


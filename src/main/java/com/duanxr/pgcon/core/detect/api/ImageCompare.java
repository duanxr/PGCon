package com.duanxr.pgcon.core.detect.api;

import com.duanxr.pgcon.core.detect.api.ImageCompare.Param;
import com.duanxr.pgcon.core.detect.api.ImageCompare.Result;
import com.duanxr.pgcon.core.model.Area;
import com.duanxr.pgcon.core.preprocessing.PreProcessorConfig;
import com.duanxr.pgcon.input.FrameCacheService;
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
public interface ImageCompare extends Detector<Result, Param> {

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  class Param {

    @NonNull
    private Area area;

    @NonNull
    private String template;

    private String mask;

    @Singular
    private List<PreProcessorConfig> preProcessors;

    @NonNull
    private Method method;

  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  class Result {

    @NonNull
    private Double similarity;

    @NonNull
    private Param param;

    @NonNull
    private FrameCacheService.CachedFrame cachedFrame;
  }


  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  class ResultList {

    @Singular
    private List<Result> results;

    public Result getMax() {
      if (results == null || results.isEmpty()) {
        return null;
      }
      Result max = results.get(0);
      for (Result result : results) {
        if (result.getSimilarity() > max.getSimilarity()) {
          max = result;
        }
      }
      return max;
    }

    public Result getMin() {
      if (results == null || results.isEmpty()) {
        return null;
      }
      Result min = results.get(0);
      for (Result result : results) {
        if (result.getSimilarity() < min.getSimilarity()) {
          min = result;
        }
      }
      return min;
    }


  }


  enum Method {
    ORB,
    TM_SQDIFF,
    TM_CCORR,
    TM_CCOEFF;
  }
}


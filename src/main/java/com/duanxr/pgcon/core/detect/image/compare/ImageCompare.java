package com.duanxr.pgcon.core.detect.image.compare;

import com.duanxr.pgcon.core.detect.Area;
import com.duanxr.pgcon.core.detect.image.compare.ImageCompare.Result.Similarity;
import java.util.List;
import java.util.concurrent.Future;
import java.util.function.Function;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

/**
 * @author 段然 2021/12/28
 */
public interface ImageCompare {

  Result detect(Param area);

  Future<Result> asyncDetect(Param area);

  @Data
  @Builder
  class Param {

    @NonNull
    private Area area;
    @NonNull
    private String image;
    private String mask;
    private Method method;
    private Period period;

    @Data
    @Builder
    public static class Period {

      @NonNull
      private Integer frames;
      private Function<Similarity, Boolean> checker;
    }
  }

  @Data
  @Builder
  class Result {

    @NonNull
    private List<Similarity> all;
    @NonNull
    private Similarity max;
    @NonNull
    private Similarity min;
    @NonNull
    private Similarity avg;

    @Data
    @Builder
    public static class Similarity {

      @NonNull
      private Float point;
      @NonNull
      private Long timestamp;
    }

  }

  enum Method {
    ORB,
    TEMPLATE_MATCHING,
    ;
  }

}

package com.duanxr.pgcon.core.detect.ocr;

import com.duanxr.pgcon.core.detect.Area;
import java.util.List;
import java.util.concurrent.Future;
import java.util.function.Function;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

/**
 * @author 段然 2021/12/28
 */
public interface OCR {

  List<Result> detect(Param area);

  Future<List<Result>> asyncDetect(Param area);

  @Data
  @Builder
  class Param {

    @NonNull
    private Area area;
    private Method method;
    private Period period;

    @Data
    @Builder
    public static class Period {

      @NonNull
      private Integer frames;
      @NonNull
      private Function<Result, Boolean> checker;
    }
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


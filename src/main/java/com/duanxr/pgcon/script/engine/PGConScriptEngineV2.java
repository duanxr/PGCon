package com.duanxr.pgcon.script.engine;

import com.duanxr.pgcon.core.detect.api.ImageCompare;
import com.duanxr.pgcon.core.detect.api.ImageCompare.Result;
import com.duanxr.pgcon.core.detect.api.ImageCompare.ResultList;
import com.duanxr.pgcon.core.detect.api.ImageCompare.ResultList.ResultListBuilder;
import com.duanxr.pgcon.core.detect.api.OCR;
import com.duanxr.pgcon.script.api.ScriptInfo;
import com.google.common.base.Strings;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import lombok.Setter;
import lombok.SneakyThrows;

/**
 * @author 段然 2021/12/29
 */
@Setter
public abstract class PGConScriptEngineV2<T> extends PGConScriptEngineV1<T> {

  protected PGConScriptEngineV2(ScriptInfo<T> scriptInfo) {
    super(scriptInfo);
  }

  @SneakyThrows
  protected ImageCompare.ResultList detect(ImageCompare.Param... params) {
    List<Future<Result>> detectList = new ArrayList<>(params.length);
    for (ImageCompare.Param param : params) {
      detectList.add(async(() -> detect(param)));
    }
    ResultListBuilder builder = ResultList.builder();
    for (Future<Result> resultFuture : detectList) {
      builder.result(resultFuture.get());
    }
    return builder.build();
  }

  protected Long detectNumber(OCR.Param param, Long timeout, Runnable reset) {
    return until(() -> detect(param),
        input -> input.getTextAsNumber() != null,
        () -> sleep(30), timeout, reset).getTextAsNumber();
  }

  protected Long detectNumber(OCR.Param param, Long timeout) {
    return detectNumber(param, timeout, null);
  }

  protected Long detectNumber(OCR.Param param) {
    return detectNumber(param, null);
  }

  protected Long detectAccurateNumber(OCR.Param param, int count, Long timeout, Runnable reset) {
    Map<Long, Integer> countMap = new HashMap<>();
    return until(() -> detect(param),
        input -> {
          Long number = input.getTextAsNumber();
          return number != null
              && countMap.compute(number, (k, v) -> v == null ? 1 : v + 1) >= count;
        },
        () -> sleep(30), timeout, reset).getTextAsNumber();
  }

  protected Long detectAccurateNumber(OCR.Param param, int count, Long timeout) {
    return detectAccurateNumber(param, count, timeout, null);
  }

  protected Long detectAccurateNumber(OCR.Param param, int count) {
    return detectAccurateNumber(param, count, null);
  }

  protected String detectAccurate(OCR.Param param, int count, Long timeout, Runnable reset) {
    Map<String, Integer> countMap = new HashMap<>();
    return until(() -> detect(param),
        input -> {
          String text = input.getTextWithoutSpace();
          return !Strings.isNullOrEmpty(text)
              && countMap.compute(text, (k, v) -> v == null ? 1 : v + 1) >= count;
        },
        () -> sleep(30), timeout, reset).getTextWithoutSpace();
  }

  protected String detectAccurate(OCR.Param param, int count, Long timeout) {
    return detectAccurate(param, count, timeout, null);
  }

  protected String detectAccurate(OCR.Param param, int count) {
    return detectAccurate(param, count, null);
  }
}

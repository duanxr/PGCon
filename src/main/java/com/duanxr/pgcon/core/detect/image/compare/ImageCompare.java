package com.duanxr.pgcon.core.detect.image.compare;

import com.duanxr.pgcon.core.detect.Area;
import com.duanxr.pgcon.core.detect.DetectResult;
import com.duanxr.pgcon.core.detect.Detector;
import java.util.concurrent.Future;

/**
 * @author 段然 2021/12/6
 */
public class ImageCompare implements Detector<Area, IcParam, IcResult> {

  @Override
  public DetectResult<IcResult> detect(Area area) {
    return null;
  }

  @Override
  public Future<DetectResult<IcResult>> asyncDetect(Area area) {
    return null;
  }

  @Override
  public DetectResult<IcResult> detect(Area area, IcParam icParam) {
    return null;
  }

  @Override
  public Future<DetectResult<IcResult>> asyncDetect(Area area, IcParam icParam) {
    return null;
  }

  @Override
  public DetectResult<IcResult> detect(Area area, IcParam icParam, long timeoutMillis) {
    return null;
  }

  @Override
  public Future<DetectResult<IcResult>> asyncDetect(Area area, IcParam icParam,
      long timeoutMillis) {
    return null;
  }
}

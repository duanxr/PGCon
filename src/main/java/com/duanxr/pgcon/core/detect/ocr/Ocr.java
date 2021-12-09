package com.duanxr.pgcon.core.detect.ocr;

import com.duanxr.pgcon.core.detect.Area;
import com.duanxr.pgcon.core.detect.DetectResult;
import com.duanxr.pgcon.core.detect.Detector;
import java.util.concurrent.Future;
import org.springframework.stereotype.Component;

/**
 * @author 段然 2021/12/6
 */
@Component
public class Ocr implements Detector<Area, OcrParam, OcrResult> {

  @Override
  public DetectResult<OcrResult> detect(Area area) {
    return null;
  }

  @Override
  public Future<DetectResult<OcrResult>> asyncDetect(Area area) {
    return null;
  }

  @Override
  public DetectResult<OcrResult> detect(Area area, OcrParam ocrParam) {
    return null;
  }

  @Override
  public Future<DetectResult<OcrResult>> asyncDetect(Area area, OcrParam ocrParam) {
    return null;
  }

  @Override
  public DetectResult<OcrResult> detect(Area area, OcrParam ocrParam, long timeoutMillis) {
    return null;
  }

  @Override
  public Future<DetectResult<OcrResult>> asyncDetect(Area area, OcrParam ocrParam,
      long timeoutMillis) {
    return null;
  }
}

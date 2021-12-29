package com.duanxr.pgcon.core.detect.ocr;

import static org.bytedeco.javacpp.lept.pixDestroy;

import com.duanxr.pgcon.core.PGPool;
import com.duanxr.pgcon.core.detect.FrameCache;
import com.duanxr.pgcon.core.detect.FrameReceiver;
import com.duanxr.pgcon.event.FrameEvent;
import com.duanxr.pgcon.util.ImageUtil;
import com.google.common.eventbus.EventBus;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;
import java.util.function.Function;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.lept.PIX;
import org.bytedeco.javacpp.tesseract.TessBaseAPI;
import org.opencv.core.Mat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author 段然 2021/12/29
 */
@Component
public class TesseractOCR implements OCR {

  private final FrameCache frameCache;
  private final EventBus eventBus;
  private final PGPool pgPool;

  @Autowired
  public TesseractOCR(FrameCache frameCache, EventBus eventBus,
      PGPool pgPool) {
    this.frameCache = frameCache;
    this.eventBus = eventBus;
    this.pgPool = pgPool;
  }

  @Override
  public Future<List<Result>> asyncDetect(Param param) {
    return pgPool.getExecutors().submit(() -> detect(param));
  }

  @Override
  public List<Result> detect(Param param) {
    return param.getPeriod() == null || param.getPeriod().getFrames() == 0 ?
        detectNow(param) : detectPeriod(param);
  }

  private List<Result> detectPeriod(Param param) {
    List<Result> list = new ArrayList<>(param.getPeriod().getFrames());
    Function<Result, Boolean> checker = param.getPeriod().getChecker();
    if (param.getPeriod().getFrames() < 0) {
      List<FrameEvent> eventList = frameCache.get(-param.getPeriod().getFrames());
      for (FrameEvent frame : eventList) {
        Mat originMat = ImageUtil.bufferedImageToMat(frame.getFrame());
        Mat targetMat = ImageUtil.splitMat(originMat, param.getArea());
        String text = doDetect(targetMat, param.getMethod());
        Result result = Result.builder().text(text).timestamp(frame.getTimestamp()).build();
        list.add(result);
        if (checker.apply(result)) {
          break;
        }
      }
    } else {
      FrameReceiver receiver = new FrameReceiver(eventBus, param.getPeriod().getFrames()) {
        @Override
        public void receive(FrameEvent frame) {
          Mat originMat = ImageUtil.bufferedImageToMat(frame.getFrame());
          Mat targetMat = ImageUtil.splitMat(originMat, param.getArea());
          String text = doDetect(targetMat, param.getMethod());
          Result result = Result.builder().text(text).timestamp(frame.getTimestamp()).build();
          list.add(result);
          if (checker.apply(result)) {
            super.breakReceive();
          }
        }
      };
    }
    return list;
  }

  private List<Result> detectNow(Param param) {
    FrameEvent frame = frameCache.get();
    Mat originMat = ImageUtil.bufferedImageToMat(frame.getFrame());
    Mat targetMat = ImageUtil.splitMat(originMat, param.getArea());
    String text = doDetect(targetMat, param.getMethod());
    return Collections.singletonList(
        Result.builder().text(text).timestamp(frame.getTimestamp()).build());
  }

  private String doDetect(Mat target, Method method) {
    //todo 复用?
    TessBaseAPI API = new TessBaseAPI();
    PIX pix = ImageUtil.matToPix(target);
    API.SetImage(pix);
    BytePointer outText = API.GetUTF8Text();
    String text = outText.getString();
    pixDestroy(pix);
    outText.deallocate();
    return text;
  }

}

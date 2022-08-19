package com.duanxr.pgcon.service;

import com.duanxr.pgcon.core.detect.api.ImageCompare;
import com.duanxr.pgcon.core.detect.api.OCR;
import org.springframework.stereotype.Component;

/**
 * @author 段然 2022/8/16
 */
@Component
public class DetectService {

  private final ImageCompare imageCompare;
  private final OCR ocr;

  public DetectService(ImageCompare imageCompare, OCR ocr) {
    this.imageCompare = imageCompare;
    this.ocr = ocr;
  }

  public OCR.Result detect(OCR.Param param) {
    return ocr.detect(param);
  }

  public ImageCompare.Result detect(ImageCompare.Param param) {
    return imageCompare.detect(param);
  }
}

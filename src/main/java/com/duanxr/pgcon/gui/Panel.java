package com.duanxr.pgcon.gui;

import com.duanxr.pgcon.core.detect.api.OCR;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author 段然 2022/7/27
 */
@Slf4j
@Component
public class Panel {

  private final OCR ocr;

  public Panel(OCR ocr) {
    log.info("Panel created. {}", ocr);
    this.ocr = ocr;
  }

}

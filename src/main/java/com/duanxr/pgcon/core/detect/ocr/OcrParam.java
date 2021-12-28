package com.duanxr.pgcon.core.detect.ocr;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 段然 2021/12/6
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OcrParam {

  private String text;
  private Integer OcrType;
  private Float similarity;
  private Integer timeoutMillis;
  private Integer retryTimes;
}

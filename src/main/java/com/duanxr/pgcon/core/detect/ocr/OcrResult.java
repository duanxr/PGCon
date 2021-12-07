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
public class OcrResult {

  private String text;
  private float similarity;
}

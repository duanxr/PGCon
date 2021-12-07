package com.duanxr.pgcon.core.detect.image.compare;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 段然 2021/12/6
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IcParam {

  private String image;
  private int imageType;
  private float similarity;
}

package com.duanxr.pgcon.core.detect.image.compare;

import com.duanxr.pgcon.core.detect.Area;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 段然 2021/12/6
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IcResult {

  private Area imageLocation;
  private float similarity;
}

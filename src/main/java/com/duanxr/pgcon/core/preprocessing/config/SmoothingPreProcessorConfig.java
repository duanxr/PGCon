package com.duanxr.pgcon.core.preprocessing.config;

import com.duanxr.pgcon.core.preprocessing.PreProcessorConfig;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author 段然 2022/8/1
 */
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SmoothingPreProcessorConfig extends PreProcessorConfig {

  private int size = 5;

  private double sigmaColor = 75;

  private double sigmaSpace = 75;

  private SmoothingType type = SmoothingType.GAUSSIAN;
  public enum SmoothingType {
    //CONVOLUTION,
    AVERAGING,
    GAUSSIAN,
    MEDIAN,
    BILATERAL
  }

}

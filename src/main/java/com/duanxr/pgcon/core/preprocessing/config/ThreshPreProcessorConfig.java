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
public class ThreshPreProcessorConfig extends PreProcessorConfig {

  private double binaryThreshold = 0.5;
  private boolean inverse = false;
  private ThreshType threshType = ThreshType.BINARY;
  private int adaptiveThreshC = 2;
  private int adaptiveBlockSize = 11;

  public enum ThreshType {
    BINARY(0),
    TOZERO(3),

    TRUNC(2),
    OTSU(8),

    ADAPTIVE_THRESH_MEAN_C(0, true),
    ADAPTIVE_THRESH_GAUSSIAN_C(1, true),

    ;

    @Getter
    private final boolean isAdaptive;
    @Getter
    private final int cvVal;

    ThreshType(int cvVal) {
      this.cvVal = cvVal;
      this.isAdaptive = false;
    }

    ThreshType(int cvVal, boolean isAdaptive) {
      this.cvVal = cvVal;
      this.isAdaptive = isAdaptive;
    }
  }
}

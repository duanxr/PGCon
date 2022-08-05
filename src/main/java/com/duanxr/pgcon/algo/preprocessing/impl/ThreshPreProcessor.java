package com.duanxr.pgcon.algo.preprocessing.impl;

import com.duanxr.pgcon.algo.preprocessing.config.ThreshPreProcessorConfig;
import com.duanxr.pgcon.algo.preprocessing.config.ThreshPreProcessorConfig.ThreshType;
import com.duanxr.pgcon.algo.preprocessing.PreProcessor;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

/**
 * @author 段然 2022/8/1
 */
public class ThreshPreProcessor implements PreProcessor {

  private static final int THRESH_MAX_VAL = 255;
  private final ThreshPreProcessorConfig threshConfig;

  public ThreshPreProcessor(ThreshPreProcessorConfig threshConfig) {
    this.threshConfig = threshConfig;
  }

  @Override
  public Mat preProcess(Mat src) {
    if (threshConfig.isEnable()) {
      Imgproc.cvtColor(src, src, Imgproc.COLOR_BGR2GRAY);
      ThreshType threshType = threshConfig.getThreshType();
      if (!threshType.isAdaptive()) {
        int thresh = (int) Math.round(threshConfig.getBinaryThreshold() * THRESH_MAX_VAL);
        Imgproc.threshold(src, src, thresh, THRESH_MAX_VAL, threshType.getCvVal());
      } else {
        Imgproc.adaptiveThreshold(src, src, THRESH_MAX_VAL, threshType.getCvVal(),
            Imgproc.THRESH_BINARY,
            threshConfig.getAdaptiveBlockSize(), threshConfig.getAdaptiveThreshC());
      }
      if (threshConfig.isInverse()) {
        Core.bitwise_not(src, src);
      }
    }
    return src;
  }
}


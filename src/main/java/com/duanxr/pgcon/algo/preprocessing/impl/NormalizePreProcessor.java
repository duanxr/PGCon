package com.duanxr.pgcon.algo.preprocessing.impl;

import com.duanxr.pgcon.algo.preprocessing.PreProcessor;
import com.duanxr.pgcon.algo.preprocessing.config.NormalizePreProcessorConfig;
import com.duanxr.pgcon.algo.preprocessing.config.ThreshPreProcessorConfig.ThreshType;
import com.duanxr.pgcon.util.ImageConvertUtil;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

/**
 * @author 段然 2022/8/1
 */
public class NormalizePreProcessor implements PreProcessor {
  private final NormalizePreProcessorConfig normalizePreProcessorConfig;

  public NormalizePreProcessor(NormalizePreProcessorConfig normalizePreProcessorConfig) {
    this.normalizePreProcessorConfig = normalizePreProcessorConfig;
  }

  @Override
  public Mat preProcess(Mat src) {
    if (normalizePreProcessorConfig.isEnable()) {
      ImageConvertUtil.BGR2GRAY(src);
      Core.normalize(src,src);
    }
    return src;
  }
}


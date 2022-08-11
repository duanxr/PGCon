package com.duanxr.pgcon.core.preprocessing.impl;

import com.duanxr.pgcon.core.preprocessing.PreProcessor;
import com.duanxr.pgcon.core.preprocessing.config.NormalizePreProcessorConfig;
import com.duanxr.pgcon.util.MatUtil;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

/**
 * @author 段然 2022/8/1
 */
public class NormalizePreProcessor implements PreProcessor {
  private final NormalizePreProcessorConfig normalizePreProcessorConfig;

  public NormalizePreProcessor(NormalizePreProcessorConfig normalizePreProcessorConfig) {
    this.normalizePreProcessorConfig = normalizePreProcessorConfig;
  }

  @Override
  public Mat preProcess(Mat mat) {
    if (normalizePreProcessorConfig.isEnable()) {
      mat = MatUtil.toGrayMat(mat);
      Core.normalize(mat,mat,0,255, Core.NORM_MINMAX, CvType.CV_8U);
    }
    return mat;
  }
}


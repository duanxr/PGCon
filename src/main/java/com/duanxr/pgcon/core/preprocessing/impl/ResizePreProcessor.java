package com.duanxr.pgcon.core.preprocessing.impl;

import com.duanxr.pgcon.core.preprocessing.PreProcessor;
import com.duanxr.pgcon.core.preprocessing.config.ResizePreProcessorConfig;
import com.duanxr.pgcon.util.ImageResizeUtil;
import org.opencv.core.Mat;

/**
 * @author 段然 2022/8/1
 */
public class ResizePreProcessor implements PreProcessor {

  private final ResizePreProcessorConfig processorConfig;

  public ResizePreProcessor(ResizePreProcessorConfig processorConfig) {
    this.processorConfig = processorConfig;
  }

  @Override
  public Mat preProcess(Mat mat) {
    if (processorConfig.isEnable()) {
      mat = ImageResizeUtil.resize(mat,
          (int) (mat.width() * processorConfig.getScale()),
          (int) (mat.height() * processorConfig.getScale()));
    }
    return mat;
  }
}


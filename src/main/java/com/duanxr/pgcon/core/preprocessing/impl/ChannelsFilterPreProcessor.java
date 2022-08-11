package com.duanxr.pgcon.core.preprocessing.impl;

import com.duanxr.pgcon.core.preprocessing.PreProcessor;
import com.duanxr.pgcon.core.preprocessing.config.ChannelsFilterPreProcessorConfig;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

/**
 * @author 段然 2022/8/1
 */
public class ChannelsFilterPreProcessor implements PreProcessor {

  private final ChannelsFilterPreProcessorConfig filterConfig;
  private final Mat transformInfo3;

  public ChannelsFilterPreProcessor(ChannelsFilterPreProcessorConfig filterConfig) {
    this.filterConfig = filterConfig;
    transformInfo3 = new Mat(3, 3, CvType.CV_32F);
    transformInfo3.put(0, 0, filterConfig.getBlueWeight(), 0, 0);
    transformInfo3.put(1, 0, 0, filterConfig.getGreenWeight(), 0);
    transformInfo3.put(2, 0, 0, 0, filterConfig.getRedWeight());
  }

  @Override
  public Mat preProcess(Mat mat) {
    if (filterConfig.isEnable() && mat.channels() == 3) {
      Core.transform(mat, mat, transformInfo3);
    }
    return mat;
  }

}

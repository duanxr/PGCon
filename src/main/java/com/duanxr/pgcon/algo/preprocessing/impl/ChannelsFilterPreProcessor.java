package com.duanxr.pgcon.algo.preprocessing.impl;

import com.duanxr.pgcon.algo.preprocessing.config.ChannelsFilterPreProcessorConfig;
import com.duanxr.pgcon.algo.preprocessing.PreProcessor;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

/**
 * @author 段然 2022/8/1
 */
public class ChannelsFilterPreProcessor implements PreProcessor {
  private final ChannelsFilterPreProcessorConfig filterConfig;
  private final Mat transformInfo;
  public ChannelsFilterPreProcessor(ChannelsFilterPreProcessorConfig filterConfig) {
    this.filterConfig = filterConfig;
    transformInfo = new Mat(3, 3, CvType.CV_32F);
    transformInfo.put(0, 0, filterConfig.getBlueWeight(), 0, 0);
    transformInfo.put(1, 0, 0, filterConfig.getGreenWeight(), 0);
    transformInfo.put(2, 0, 0, 0, filterConfig.getRedWeight());
  }

  @Override
  public Mat preProcess(Mat src) {
    if (filterConfig.isEnable()) {
      Core.transform(src, src, transformInfo);
    }
    return src;
  }

}

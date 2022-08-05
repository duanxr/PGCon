package com.duanxr.pgcon.algo.preprocessing.impl;

import com.duanxr.pgcon.algo.preprocessing.PreProcessor;
import com.duanxr.pgcon.algo.preprocessing.config.ChannelsFilterPreProcessorConfig;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

/**
 * @author 段然 2022/8/1
 */
public class ChannelsFilterPreProcessor implements PreProcessor {

  private final ChannelsFilterPreProcessorConfig filterConfig;
  private final Mat transformInfo3;
  private final Mat transformInfo4;

  public ChannelsFilterPreProcessor(ChannelsFilterPreProcessorConfig filterConfig) {
    this.filterConfig = filterConfig;
    transformInfo3 = new Mat(3, 3, CvType.CV_32F);
    transformInfo3.put(0, 0, filterConfig.getBlueWeight(), 0, 0);
    transformInfo3.put(1, 0, 0, filterConfig.getGreenWeight(), 0);
    transformInfo3.put(2, 0, 0, 0, filterConfig.getRedWeight());

    transformInfo4 = new Mat(3, 3, CvType.CV_32F);
    transformInfo4.put(0, 0, 1, 0, 0, 0);
    transformInfo4.put(1, 0, 0, filterConfig.getBlueWeight(), 0, 0);
    transformInfo4.put(2, 0, 0, 0, filterConfig.getGreenWeight(), 0);
    transformInfo4.put(3, 0, 0, 0, 0, filterConfig.getRedWeight());
  }

  @Override
  public Mat preProcess(Mat src) {
    if (filterConfig.isEnable()) {
      if (src.channels() == 3) {
        Core.transform(src, src, transformInfo3);
      } else if (src.channels() == 4) {
        Core.transform(src, src, transformInfo4);
      }
    }
    return src;
  }

}

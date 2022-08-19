package com.duanxr.pgcon.core.detect.impl;

import com.duanxr.pgcon.input.FrameCacheService;
import com.duanxr.pgcon.input.FrameCacheService.CachedFrame;
import com.duanxr.pgcon.core.detect.api.Detector;
import com.duanxr.pgcon.core.preprocessing.PreProcessor;
import com.duanxr.pgcon.core.preprocessing.PreProcessorConfig;
import com.duanxr.pgcon.core.preprocessing.PreprocessorFactory;
import java.util.List;
import org.opencv.core.Mat;

/**
 * @author 段然 2022/8/4
 */
public abstract class ImageDetector<R, P> implements Detector<R, P> {
  protected final FrameCacheService frameCacheService;
  protected final PreprocessorFactory preprocessorFactory;

  protected ImageDetector(FrameCacheService frameCacheService, PreprocessorFactory preprocessorFactory) {
    this.frameCacheService = frameCacheService;
    this.preprocessorFactory = preprocessorFactory;
  }

  protected CachedFrame getImage() {
    return frameCacheService.getFrame();
  }

  protected CachedFrame getNewImage() {
    return frameCacheService.getNewFrame();
  }

  protected Mat tryPreProcess(Mat mat, List<PreProcessorConfig> preProcessorConfigs) {
    if (preProcessorConfigs != null && !preProcessorConfigs.isEmpty()) {
      List<PreProcessor> preProcessors = preprocessorFactory.getPreProcessors(preProcessorConfigs);
      for (PreProcessor preProcessor : preProcessors) {
        mat = preProcessor.preProcess(mat);
      }
    }
    return mat;
  }

}

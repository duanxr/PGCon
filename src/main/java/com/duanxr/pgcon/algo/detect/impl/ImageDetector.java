package com.duanxr.pgcon.algo.detect.impl;

import com.duanxr.pgcon.core.FrameManager;
import com.duanxr.pgcon.core.FrameManager.CachedFrame;
import com.duanxr.pgcon.algo.detect.api.Detector;
import com.duanxr.pgcon.algo.preprocessing.PreProcessor;
import com.duanxr.pgcon.algo.preprocessing.PreProcessorConfig;
import com.duanxr.pgcon.algo.preprocessing.PreprocessorFactory;
import java.util.List;
import org.opencv.core.Mat;

/**
 * @author 段然 2022/8/4
 */
public abstract class ImageDetector<R, P> implements Detector<R, P> {
  protected final FrameManager frameManager;
  protected final PreprocessorFactory preprocessorFactory;

  protected ImageDetector(FrameManager frameManager, PreprocessorFactory preprocessorFactory) {
    this.frameManager = frameManager;
    this.preprocessorFactory = preprocessorFactory;
  }

  protected CachedFrame getImage() {
    return frameManager.getFrame();
  }

  protected CachedFrame getNewImage() {
    return frameManager.getNewFrame();
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

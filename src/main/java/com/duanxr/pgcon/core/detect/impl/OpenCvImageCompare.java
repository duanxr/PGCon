package com.duanxr.pgcon.core.detect.impl;

import com.duanxr.pgcon.component.FrameManager;
import com.duanxr.pgcon.component.FrameManager.CachedFrame;
import com.duanxr.pgcon.core.detect.api.ImageCompare;
import com.duanxr.pgcon.core.model.Area;
import com.duanxr.pgcon.core.preprocessing.PreProcessorConfig;
import com.duanxr.pgcon.core.preprocessing.PreprocessorFactory;
import com.duanxr.pgcon.util.ImageConvertUtil;
import com.google.common.base.Strings;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import org.opencv.core.Core;
import org.opencv.core.Core.MinMaxLocResult;
import org.opencv.core.DMatch;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.ORB;
import org.opencv.imgproc.Imgproc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author 段然 2021/12/6
 */
@Component
public class OpenCvImageCompare extends
    ImageDetector<ImageCompare.Result, ImageCompare.Param> implements ImageCompare {

  private final Map<Method, BiFunction<Mat, Mat, Double>> methods;

  @Autowired
  public OpenCvImageCompare(PreprocessorFactory preprocessorFactory, FrameManager frameManager) {
    super(frameManager, preprocessorFactory);
    this.methods = new HashMap<>();
    methods.put(Method.ORB, this::orb);
    methods.put(Method.TM_SQDIFF, this::templateMatchingSQDIFF);
    methods.put(Method.TM_CCORR, this::templateMatchingCCOERR);
    methods.put(Method.TM_CCOEFF, this::templateMatchingCCOEFF);
  }

  @Override
  public Result detect(Param param) {
    Mat temple = ImageConvertUtil.matFromJson(param.getTemplate());
    Mat mask = Strings.isNullOrEmpty(param.getMask()) ? null
        : ImageConvertUtil.matFromJson(param.getMask());
    Area area = param.getArea();
    Method method = param.getMethod();
    List<PreProcessorConfig> preProcessors = param.getPreProcessors();
    return detectNow(temple, mask, area, preProcessors, method);
  }

  private Result detectNow(Mat temple, Mat mask, Area area, List<PreProcessorConfig> preProcessors,
      Method method) {
    CachedFrame cachedFrame = getImage();
    Mat targetMat = getTarget(cachedFrame, area, !preProcessors.isEmpty());
    targetMat = tryPreProcess(targetMat, preProcessors);
    Double score = doDetect(temple, targetMat, method);
    return Result.builder().similarity(score).timestamp(cachedFrame.getTimestamp()).build();
  }

  private Double doDetect(Mat temple, Mat target, Method method) {
    return methods.get(method).apply(temple, target);
  }

  private Double orb(Mat temple, Mat target) {
    int retVal = 0;
    MatOfKeyPoint keyPoint1 = new MatOfKeyPoint();
    MatOfKeyPoint keyPoint2 = new MatOfKeyPoint();
    Mat descriptors1 = new Mat();
    Mat descriptors2 = new Mat();
    ORB detector = ORB.create();
    detector.detect(temple, keyPoint1);
    detector.detect(target, keyPoint2);
    detector.compute(temple, keyPoint1, descriptors1);
    detector.compute(target, keyPoint2, descriptors2);
    DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING);
    MatOfDMatch matches = new MatOfDMatch();
    if (descriptors2.cols() == descriptors1.cols()) {
      matcher.match(descriptors1, descriptors2, matches);
      DMatch[] match = matches.toArray();
      double maxDist = 0;
      double minDist = 100;
      for (int i = 0; i < descriptors1.rows(); i++) {
        double dist = match[i].distance;
        if (dist < minDist) {
          minDist = dist;
        }
        if (dist > maxDist) {
          maxDist = dist;
        }
      }
      for (int i = 0; i < descriptors1.rows(); i++) {
        if (match[i].distance <= 10) {
          retVal++;
        }
      }
    }
    return retVal / 100D;
  }

  private Double templateMatchingSQDIFF(Mat temple, Mat target) {
    Mat result = templateMatching(temple, target, Imgproc.TM_SQDIFF_NORMED);
    MinMaxLocResult minMaxLocResult = Core.minMaxLoc(result);
    return -minMaxLocResult.minVal;
  }

  private Double templateMatchingCCOERR(Mat temple, Mat target) {
    Mat result = templateMatching(temple, target, Imgproc.TM_CCORR_NORMED);
    MinMaxLocResult minMaxLocResult = Core.minMaxLoc(result);
    return minMaxLocResult.maxVal;
  }

  private Double templateMatchingCCOEFF(Mat temple, Mat target) {
    Mat result = templateMatching(temple, target, Imgproc.TM_CCOEFF_NORMED);
    MinMaxLocResult minMaxLocResult = Core.minMaxLoc(result);
    return minMaxLocResult.maxVal;
  }

  private Mat templateMatching(Mat temple, Mat target, int method) {
    Mat result = new Mat();
    Imgproc.matchTemplate(target, temple, result, method);
    return result;
  }

  private Mat getTarget(CachedFrame cachedFrame, Area area, boolean deepCopy) {
    return area == null ? deepCopy ? ImageConvertUtil.deepCopyMat(cachedFrame.getMat())
        : cachedFrame.getMat()
        : deepCopy ? ImageConvertUtil.deepSplitMat(cachedFrame.getMat(), area)
            : ImageConvertUtil.splitMat(cachedFrame.getMat(), area);
  }

}

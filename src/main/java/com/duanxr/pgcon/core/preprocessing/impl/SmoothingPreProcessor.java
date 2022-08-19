package com.duanxr.pgcon.core.preprocessing.impl;

import com.duanxr.pgcon.core.preprocessing.PreProcessor;
import com.duanxr.pgcon.core.preprocessing.config.SmoothingPreProcessorConfig;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

/**
 * @author 段然 2022/8/1
 */
public class SmoothingPreProcessor implements PreProcessor {

  private final SmoothingPreProcessorConfig processorConfig;

  public SmoothingPreProcessor(SmoothingPreProcessorConfig processorConfig) {
    this.processorConfig = processorConfig;
  }

  @Override
  public Mat preProcess(Mat mat) {
    if (processorConfig.isEnable()) {
      switch (processorConfig.getType()) {
        //case CONVOLUTION -> mat = convolutionFilter(mat);
        case AVERAGING -> mat = averagingFilter(mat);
        case MEDIAN -> mat = medianBlur(mat);
        case BILATERAL -> mat = bilateralFilter(mat);
        case GAUSSIAN -> mat = gaussianBlur(mat);
        default -> throw new IllegalArgumentException(
            "Unknown smoothing type: " + processorConfig.getType());
      }
    }
    return mat;
  }

  private Mat gaussianBlur(Mat mat) {
    Mat dst = new Mat();
    Imgproc.GaussianBlur(mat, dst, new Size(processorConfig.getSize(),
        processorConfig.getSigmaColor()),0,0);
    return dst;
  }

  private Mat convolutionFilter(Mat mat) {
    Mat dst = new Mat();
    Mat k = new Mat(processorConfig.getSize(), processorConfig.getSize(), CvType.CV_32F,
        new Scalar(1, 1, 1, 1));
    Imgproc.filter2D(mat, dst, -1, k);
    return dst;
  }

  private Mat averagingFilter(Mat mat) {
    Mat dst = new Mat();
    Imgproc.blur(mat, dst, new Size(processorConfig.getSize(), processorConfig.getSize()));
    return dst;
  }

  private Mat bilateralFilter(Mat mat) {
    Mat dst = new Mat();
    Imgproc.bilateralFilter(mat, dst, processorConfig.getSize(),
        processorConfig.getSigmaColor(), processorConfig.getSigmaSpace());
    return dst;
  }

  private Mat medianBlur(Mat mat) {
    Mat dst = new Mat();
    Imgproc.medianBlur(mat, dst, processorConfig.getSize());
    return dst;
  }

}


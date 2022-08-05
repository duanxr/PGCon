package com.duanxr.pgcon.core.preprocessing.impl;

import com.duanxr.pgcon.core.preprocessing.PreProcessor;
import com.duanxr.pgcon.core.preprocessing.config.ColorPickFilterPreProcessorConfig;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

/**
 * @author 段然 2022/8/1
 */
public class ColorPickFilterPreProcessor implements PreProcessor {

  private final ColorPickFilterPreProcessorConfig filterConfig;

  private final double hue;
  private final double saturation;
  private final double value;

  public ColorPickFilterPreProcessor(ColorPickFilterPreProcessorConfig filterConfig) {
    this.filterConfig = filterConfig;
    this.hue = filterConfig.getTargetColor().getHue() / 360.0 * 180.0;
    this.saturation = filterConfig.getTargetColor().getSaturation() * 255.0;
    this.value = filterConfig.getTargetColor().getBrightness() * 255.0;
  }

  @Override
  public Mat preProcess(Mat src) {
    if (filterConfig.isEnable() && src.channels() == 3) {
      Mat hsv = new Mat();
      Mat mask = new Mat();
      Mat black = new Mat(src.size(), src.type(), new Scalar(0));
      Imgproc.cvtColor(src, hsv, Imgproc.COLOR_BGR2HSV);
      double hr = filterConfig.getHueRange() * 180.0;
      double sr = filterConfig.getSaturationRange() * 255.0;
      double vr = filterConfig.getValueRange() * 255.0;
      Scalar lower = new Scalar(Math.max(hue - hr, 0.0),
          Math.max(saturation - sr, 0.0),
          Math.max(value - vr, 0.0));
      Scalar higher = new Scalar(Math.min(hue + hr, 180.0),
          Math.min(saturation + sr, 255.0),
          Math.min(value + vr, 255.0));
      Core.inRange(hsv, lower, higher, mask);
      if (hue - hr < 0) {
        lower = new Scalar(180 + hue - hr,
            Math.max(saturation - sr, 0.0),
            Math.max(value - vr, 0.0));
        higher = new Scalar(180.0,
            Math.min(saturation + sr, 255.0),
            Math.min(value + vr, 255.0));
        Mat mask2 = new Mat();
        Core.inRange(hsv, lower, higher, mask2);
        Core.bitwise_or(mask, mask2, mask);
      }
      if (filterConfig.isInverse()) {
        Core.bitwise_not(mask, mask);
      }
      Core.bitwise_and(src, black, src, mask);
    }
    return src;
  }

}

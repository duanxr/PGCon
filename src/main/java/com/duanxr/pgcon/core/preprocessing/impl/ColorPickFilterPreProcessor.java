package com.duanxr.pgcon.core.preprocessing.impl;

import com.duanxr.pgcon.core.preprocessing.PreProcessor;
import com.duanxr.pgcon.core.preprocessing.config.ColorPickFilterPreProcessorConfig;
import com.duanxr.pgcon.core.preprocessing.config.ColorPickFilterPreProcessorConfig.MaskType;
import com.duanxr.pgcon.util.ColorDifferenceUtil;
import com.duanxr.pgcon.util.MatUtil;
import javafx.scene.paint.Color;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

/**
 * @author 段然 2022/8/1
 */
public class ColorPickFilterPreProcessor implements PreProcessor {

  private final ColorPickFilterPreProcessorConfig filterConfig;

  public ColorPickFilterPreProcessor(ColorPickFilterPreProcessorConfig filterConfig) {
    this.filterConfig = filterConfig;
  }

  @Override
  public Mat preProcess(Mat mat) {
    if (filterConfig.isEnable() && mat.channels() == 3) {
      byte[] targetLab = getLabTarget(filterConfig.getTargetColor());
      Mat lab = new Mat(mat.size(), mat.type());
      Mat cover = getCover(mat);
      Mat mask = new Mat(mat.size(), CvType.CV_8UC1, new Scalar(0));
      Imgproc.cvtColor(mat, lab, Imgproc.COLOR_BGR2Lab);
      byte[] matLab = new byte[3];
      double range = getRange();
      for (int row = 0; row < lab.rows(); row++) {
        for (int col = 0; col < lab.cols(); col++) {
          lab.get(row, col, matLab);
          double distance = calcDistance(matLab, targetLab);
          if (distance < range) {
            mask.put(row, col, 255);
          }
        }
      }
      if (filterConfig.isInverse()) {
        Core.bitwise_not(mask, mask);
      }
      cover.copyTo(mat, mask);
    }
    return mat;
  }

  private byte[] getLabTarget(Color color) {
    Mat target = new Mat(1, 1, CvType.CV_8UC3);
    target.put(0, 0, color.getRed() * 100, color.getGreen() * 255, color.getBlue() * 255);
    Imgproc.cvtColor(target, target, Imgproc.COLOR_RGB2Lab);
    byte[] targetLab = new byte[3];
    target.get(0, 0, targetLab);
    return targetLab;
  }

  private Mat getCover(Mat mat) {
    return filterConfig.getMaskType() == MaskType.GARY ? get3ChannelsGary(MatUtil.toGray(mat))
        : filterConfig.getMaskType() == MaskType.BLACK ? new Mat(mat.size(), mat.type(),
            new Scalar(0)) : new Mat(mat.size(), mat.type(), new Scalar(255, 255, 255, 255));
  }

  private double getRange() {
    return Math.round(filterConfig.getRange() * 100);
  }

  private double calcDistance(byte[] matLab, byte[] targetLab) {
    return switch (filterConfig.getPickType()) {
      case CMC -> ColorDifferenceUtil.deltaECMC(
          Byte.toUnsignedInt(targetLab[0]), Byte.toUnsignedInt(targetLab[1]),
          Byte.toUnsignedInt(targetLab[2]), Byte.toUnsignedInt(matLab[0]),
          Byte.toUnsignedInt(matLab[1]), Byte.toUnsignedInt(matLab[2]));
      case CIE94 -> ColorDifferenceUtil.deltaECIE94(
          Byte.toUnsignedInt(targetLab[0]), Byte.toUnsignedInt(targetLab[1]),
          Byte.toUnsignedInt(targetLab[2]), Byte.toUnsignedInt(matLab[0]),
          Byte.toUnsignedInt(matLab[1]), Byte.toUnsignedInt(matLab[2]));
      case CIEDE2000 -> ColorDifferenceUtil.deltaECIEDE2000(
          Byte.toUnsignedInt(targetLab[0]), Byte.toUnsignedInt(targetLab[1]),
          Byte.toUnsignedInt(targetLab[2]), Byte.toUnsignedInt(matLab[0]),
          Byte.toUnsignedInt(matLab[1]), Byte.toUnsignedInt(matLab[2]));
      case CIE76 -> ColorDifferenceUtil.deltaECIE76(
          Byte.toUnsignedInt(targetLab[0]), Byte.toUnsignedInt(targetLab[1]),
          Byte.toUnsignedInt(targetLab[2]), Byte.toUnsignedInt(matLab[0]),
          Byte.toUnsignedInt(matLab[1]), Byte.toUnsignedInt(matLab[2]));
    };
  }

  private Mat get3ChannelsGary(Mat mat) {
    Imgproc.cvtColor(mat, mat, Imgproc.COLOR_GRAY2BGR);
    return mat;
  }

}

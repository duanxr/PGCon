package com.duanxr.pgcon.util;

import com.duanxr.pgcon.core.model.Area;
import lombok.experimental.UtilityClass;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

/**
 * @author 段然 2021/12/29
 */
@UtilityClass
public class MatUtil {

  public static Mat deepSplit(Mat mat, Area area) {
    ImageUtil.checkChannel(mat);
    return mat.submat(new Rect(area.getX(), area.getY(), area.getWidth(), area.getHeight()));
  }

  public static Mat split(Mat mat, Area area) {
    ImageUtil.checkChannel(mat);
    return new Mat(mat, new Rect(area.getX(), area.getY(), area.getWidth(), area.getHeight()));
  }

  public static Mat toMask(Mat mat) {
    ImageUtil.checkChannel(mat);
    Mat mask = deepCopy(mat);
    Core.bitwise_not(mask, mask);
    return mask;
  }

  public static Mat deepCopy(Mat mat) {
    ImageUtil.checkChannel(mat);
    return mat.submat(new Rect(0, 0, mat.cols(), mat.rows()));
  }
  public Mat toGray(Mat mat) {
    ImageUtil.checkChannel(mat);
    int channels = mat.channels();
    if (channels == 1) {
      return mat;
    }
    Mat newMat = new Mat(mat.rows(), mat.cols(), CvType.CV_8UC1);
    int code = channels == 3 ? Imgproc.COLOR_BGR2GRAY : Imgproc.COLOR_BGRA2GRAY;
    Imgproc.cvtColor(mat, newMat, code);
    return newMat;
  }


}

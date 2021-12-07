package com.duanxr.pgcon.util;


import static com.duanxr.pgcon.util.ConstantConfig.SIZE;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

/**
 * @author Duanran 2019/12/13
 */
public class MatUtil {

  /**
   * 返回引用而非新的矩阵
   */
  public static Mat split(Mat origin, int left, int top, int right, int bottom) {
    Rect rectCrop = new Rect(left, top, right - left, bottom - top);
    return origin.submat(rectCrop);
  }

  public static Mat toMask(Mat origin) {
    Mat mask = new Mat();
    origin.copyTo(mask);
    Core.bitwise_not(mask, mask);
    return mask;
  }

  public static void resize(Mat origin) {
    Imgproc.resize(origin, origin, SIZE);
  }

  public static BufferedImage toBufferedImage(Mat mat) {
    int type = BufferedImage.TYPE_BYTE_GRAY;
    if (mat.channels() > 1) {
      type = BufferedImage.TYPE_3BYTE_BGR;
    }
    byte[] bytes = toByteArray(mat);
    BufferedImage image = new BufferedImage(mat.cols(), mat.rows(), type);
    final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
    System.arraycopy(bytes, 0, targetPixels, 0, bytes.length);
    return image;
  }

  public static byte[] toByteArray(Mat mat) {
    int bufferSize = mat.channels() * mat.cols() * mat.rows();
    byte[] bytes = new byte[bufferSize];
    mat.get(0, 0, bytes);
    return bytes;
  }
}

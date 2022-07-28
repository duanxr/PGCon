package com.duanxr.pgcon.util;


import com.duanxr.pgcon.algo.detect.model.Area;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.nio.ByteBuffer;
import org.bytedeco.leptonica.PIX;
import org.bytedeco.leptonica.global.lept;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

/**
 * @author Duanran 2019/12/13
 */
public class ImageConvertUtil {

  /**
   * 返回引用而非新的矩阵
   */
  public static Mat splitMat(Mat origin, Area area) {
    return origin.submat(new Rect(area.getX(), area.getY(), area.getWidth(), area.getHeight()));
  }

  public static PIX matToPix(Mat mat) {
    Mat gray = new Mat();
    Imgproc.cvtColor(mat, gray, Imgproc.COLOR_RGB2GRAY);
    MatOfByte bytes = new MatOfByte();
    Imgcodecs.imencode(".tif", gray, bytes);
    ByteBuffer buff = ByteBuffer.wrap(bytes.toArray());
    return lept.pixReadMem(buff,buff.capacity());
  }

  public static Mat matToMask(Mat origin) {
    Mat mask = new Mat();
    origin.copyTo(mask);
    Core.bitwise_not(mask, mask);
    return mask;
  }
  public static BufferedImage matToBufferedImage(Mat mat) {
    int type = BufferedImage.TYPE_BYTE_GRAY;
    if (mat.channels() > 1) {
      type = BufferedImage.TYPE_3BYTE_BGR;
    }
    byte[] bytes = matToByteArray(mat);
    BufferedImage image = new BufferedImage(mat.cols(), mat.rows(), type);
    final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
    System.arraycopy(bytes, 0, targetPixels, 0, bytes.length);
    return image;
  }

  public static byte[] matToByteArray(Mat mat) {
    int bufferSize = mat.channels() * mat.cols() * mat.rows();
    byte[] bytes = new byte[bufferSize];
    mat.get(0, 0, bytes);
    return bytes;
  }

  public static Mat bufferedImageToMat(BufferedImage bufferedImage) {
    Mat mat = new Mat(bufferedImage.getHeight(), bufferedImage.getWidth(), CvType.CV_8UC3);
    byte[] data = ((DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData();
    mat.put(0, 0, data);
    return mat;
  }
}
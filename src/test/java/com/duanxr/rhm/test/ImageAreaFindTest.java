package com.duanxr.rhm.test;

import com.duanxr.rhm.config.ConstantConfig;
import com.duanxr.rhm.cache.entity.CachedImageArea;
import com.duanxr.rhm.util.MatUtil;
import nu.pattern.OpenCV;
import org.opencv.core.Core;
import org.opencv.core.Core.MinMaxLocResult;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

public class ImageAreaFindTest {

  static Mat originMat = null;

  public static void main(String[] args) throws Exception {
    OpenCV.loadShared();

    String origin = "C:\\Users\\mytq\\Desktop\\13\\2019121319552301-B8FAEF4816CAC2B76D11869B05CA7601.jpg";
    String template = "D:\\DuanXR\\Project\\DuanXR\\relentless-hatching-machine\\src\\src\\main\\resources\\img\\"
        + "rotom_done.png";
    Boolean hasMask = Boolean.FALSE;
    Boolean cap = Boolean.TRUE;

    CachedImageArea detectionImageArea = searchFeature(origin, template, hasMask, cap);

    if (detectionImageArea != null) {
      System.out.println(detectionImageArea.getTop() + "," +
          detectionImageArea.getBottom() + "," + detectionImageArea.getLeft() + ","
          + detectionImageArea.getRight());
      Imgproc.rectangle(originMat, detectionImageArea.getLeftTopPoint(),
          detectionImageArea.getRightBottomPoint(),
          new Scalar(255, 0, 0));
    }
    TestUtil.displayImage(originMat);
  }

  public static CachedImageArea searchFeature(String origin, String template,
      boolean hasMask, Boolean cap) {
    long startTime = System.currentTimeMillis();
    try {
      if (cap) {
        originMat = new Mat();
        VideoCapture videoCapture = new VideoCapture(1);
        boolean opened = videoCapture.isOpened();
        videoCapture.read(originMat);
        MatUtil.resize(originMat);
      } else {
        originMat = Imgcodecs.imread(origin, Imgcodecs.CV_LOAD_IMAGE_COLOR);
      }
      Mat templateMat = Imgcodecs.imread(template, Imgcodecs.CV_LOAD_IMAGE_COLOR);
      Mat result = new Mat();
      if (hasMask) {
        Mat mask = new Mat();
        templateMat.copyTo(mask);
        Core.bitwise_not(mask, mask);
        Imgproc.matchTemplate(originMat, templateMat, result, Imgproc.TM_CCORR_NORMED, mask);
      } else {
        Imgproc.matchTemplate(originMat, templateMat, result, Imgproc.TM_CCORR_NORMED);
      }
      MinMaxLocResult minMaxLocResult = Core.minMaxLoc(result);
      Point leftTopLoc = minMaxLocResult.maxLoc;
      Point rightBottomLoc = new Point(leftTopLoc.x + templateMat.cols(),
          leftTopLoc.y + templateMat.rows());
      if (minMaxLocResult.maxVal > ConstantConfig.TEMPLATE_MATCH_THRESHOLD) {
        System.out.println(minMaxLocResult.maxVal);
        return new CachedImageArea(
            (int) leftTopLoc.x, (int) leftTopLoc.y, (int) rightBottomLoc.x, (int) rightBottomLoc.y);
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      long estimatedTime = System.currentTimeMillis() - startTime;
      System.out.println("estimatedTime=" + estimatedTime + "ms");
    }
    return null;
  }

}

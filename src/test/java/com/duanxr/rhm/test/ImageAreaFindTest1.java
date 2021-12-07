package com.duanxr.rhm.test;

import com.duanxr.pgcon.util.MatUtil;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import nu.pattern.OpenCV;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

public class ImageAreaFindTest1 {

  static Mat originMat = null;

  private void fastNMS(Mat result) {
    List<double[]> list = new ArrayList<>(result.cols()* result.rows());
    for (int r = 0; r < result.rows(); r++) {
      for (int c = 0; c < result.cols(); c++) {
        double[] doubles = new double[3];
        doubles[0] = result.get(r, c)[0];
        doubles[1] = r;
        doubles[2] = c;
        list.add(doubles);
      }
    }
    list.sort(Comparator.comparingDouble(o -> o[0]));

  }


  public static void main(String[] args) throws Exception {
    OpenCV.loadShared();

    String origin = "D:\\gs.jpg";
    String template =
        "D:\\3n.png";
    Boolean hasMask = Boolean.FALSE;
    Boolean cap = Boolean.FALSE;

    List<int[]> list = searchFeature(origin, template, hasMask, cap);

    //CachedImageArea detectionImageArea = (CachedImageArea) list;

   /* if (detectionImageArea != null) {
      System.out.println(detectionImageArea.getTop() + "," +
          detectionImageArea.getBottom() + "," + detectionImageArea.getLeft() + ","
          + detectionImageArea.getRight());
      Imgproc.rectangle(originMat, detectionImageArea.getLeftTopPoint(),
          detectionImageArea.getRightBottomPoint(),
          new Scalar(255, 0, 0));
    }*/
    TestUtil.displayImage(originMat);
  }

  public static List<int[]> searchFeature(String origin, String template,
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
        originMat = Imgcodecs.imread(origin, Imgcodecs.CV_LOAD_IMAGE_ANYCOLOR);
      }
      Mat templateMat = Imgcodecs.imread(template, Imgcodecs.CV_LOAD_IMAGE_ANYCOLOR);
      Mat result = new Mat();
      if (hasMask) {
        Mat mask = new Mat();
        templateMat.copyTo(mask);
        Core.bitwise_not(mask, mask);
        Imgproc.matchTemplate(originMat, templateMat, result, Imgproc.TM_SQDIFF_NORMED, mask);
      } else {
        Imgproc.matchTemplate(originMat, templateMat, result, Imgproc.TM_SQDIFF_NORMED);
      }
      List<int[]> list = new ArrayList<>();
      for (int r = 0; r < result.rows(); r++) {
        for (int c = 0; c < result.cols(); c++) {
          //System.out.println(result.get(r, c)[0]);
          if (result.get(r, c)[0] < 0.05D) {
            list.add(new int[]{r, c});
          }
        }
      }
      for (int[] doubles : list) {
        Point leftTopLoc = new Point(doubles[1] + (templateMat.cols() / 2D),
            doubles[0] + (templateMat.rows() / 2D));
        //System.out.println(Arrays.toString(doubles));
        Imgproc.circle(
            originMat,                 //Matrix obj of the image
            leftTopLoc,    //Center of the circle
            1,                    //Radius
            new Scalar(0, 0, 255),  //Scalar object for color
            1                      //Thickness of the circle
        );
      }

    /*  MinMaxLocResult minMaxLocResult = Core.minMaxLoc(result);
      Point leftTopLoc = minMaxLocResult.maxLoc;
      Point rightBottomLoc = new Point(leftTopLoc.x + templateMat.cols(),
          leftTopLoc.y + templateMat.rows());
      if (minMaxLocResult.maxVal > ConstantConfig.TEMPLATE_MATCH_THRESHOLD) {
        System.out.println(minMaxLocResult.maxVal);
        return new CachedImageArea(
            (int) leftTopLoc.x, (int) leftTopLoc.y, (int) rightBottomLoc.x, (int) rightBottomLoc.y);
      }*/
      return list;
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      long estimatedTime = System.currentTimeMillis() - startTime;
      System.out.println("estimatedTime=" + estimatedTime + "ms");
    }
    return null;
  }

}

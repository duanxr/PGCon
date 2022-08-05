package com.duanxr.pgcon.test.algo;

import com.duanxr.pgcon.core.model.Area;
import com.duanxr.pgcon.test.util.TestUtil;
import java.util.ArrayList;
import java.util.List;
import lombok.SneakyThrows;
import nu.pattern.OpenCV;
import org.opencv.core.Core;
import org.opencv.core.KeyPoint;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

public class ImageAreaFindTest {

  static Mat originMat = null;

  public static void main(String[] args) throws Exception {
    OpenCV.loadLocally();

    String origin = "D:\\IMG_0052.MP4";
    String template = "D:\\2.png";
    Boolean hasMask = Boolean.FALSE;
    Boolean cap = Boolean.FALSE;

    Area detectionImageArea = searchFeature(origin, template, hasMask, cap);

   /* if (detectionImageArea != null) {
      Imgproc.rectangle(originMat, new Point(detectionImageArea.getX(), detectionImageArea.getY()),
          new Point(detectionImageArea.getX() + detectionImageArea.getWeight(),
              detectionImageArea.getY() + detectionImageArea.getHigh()),
          new Scalar(255, 0, 0));
    }*/
    TestUtil.displayImage(originMat);
  }

  @SneakyThrows
  public static Area searchFeature(String origin, String template,
      boolean hasMask, Boolean cap) {
    long startTime = System.currentTimeMillis();
    try {
      originMat = new Mat();
      VideoCapture videoCapture = new VideoCapture(origin);
      System.out.println(videoCapture.isOpened());
      List<Mat> results = new ArrayList<>();
      Mat templateMat = Imgcodecs.imread(template, Imgcodecs.IMREAD_COLOR);
      double[][] doubles = null;
      for (int i = 0; i < 30; i++) {
        videoCapture.read(originMat);
        TestUtil.displayImage(originMat);
        Mat result = new Mat();
        if (hasMask) {
          Mat mask = new Mat();
          templateMat.copyTo(mask);
          Core.bitwise_not(mask, mask);
          Imgproc.matchTemplate(originMat, templateMat, result, Imgproc.TM_CCORR_NORMED, mask);
        } else {
          Imgproc.matchTemplate(originMat, templateMat, result, Imgproc.TM_CCORR_NORMED);
        }
        int cols = result.cols();
        int rows = result.rows();
        if (doubles == null) {
          doubles = new double[cols][rows];
        }
        for (int x = 0; x < cols; x++) {
          for (int y = 0; y < rows; y++) {
            double[] p = result.get(y, x);
            if (p[0] > doubles[x][y]) {
              doubles[x][y] = p[0];
            }
          }
        }
      }
      List<KeyPoint> keyPoints = new ArrayList<>();
      for (int i = 0, doublesLength = doubles.length; i < doublesLength; i++) {
        double[] r = doubles[i];
        for (int j = 0; j < r.length; j++) {
          if (doubles[i][j] > 0.97) {
            keyPoints.add(new KeyPoint(i, j, 1));
            Imgproc.circle(originMat,
                new Point(i + (templateMat.cols() / 2D), j + (templateMat.rows() / 2D)), 5,
                new Scalar(0, 255, 255), 1);
          }
        }

      }
      List<KeyPoint> ssc = ssc(keyPoints,144,0.2f,originMat.cols(),originMat.rows());
      for (KeyPoint keyPoint : ssc) {
        Imgproc.circle(originMat,
            new Point(keyPoint.pt.x + (templateMat.cols() / 2D), keyPoint.pt.y + (templateMat.rows() / 2D)), 5,
            new Scalar(0, 0, 255), 1);
      }
      //MinMaxLocResult minMaxLocResult = Core.minMaxLoc(result);
      /*Point leftTopLoc = minMaxLocResult.maxLoc;
      Point rightBottomLoc = new Point(leftTopLoc.x + templateMat.cols(),
          leftTopLoc.y + templateMat.rows());*/

     /* if (minMaxLocResult.maxVal > 0.95) {
        System.out.println(minMaxLocResult.maxVal);
        return new Area(
            (int) leftTopLoc.x, (int) leftTopLoc.y, (int) rightBottomLoc.x - (int) leftTopLoc.x,
            (int) rightBottomLoc.y - (int) leftTopLoc.y);
      }*/
    } catch (
        Exception e) {
      e.printStackTrace();
    } finally {
      long estimatedTime = System.currentTimeMillis() - startTime;
      System.out.println("estimatedTime=" + estimatedTime + "ms");
    }
    return null;
  }

  private static void fasterNMS(Mat result) {
  }

  private static List<KeyPoint> ssc(final List<KeyPoint> keyPoints,
      final int numRetPoints, final float tolerance,
      final int cols, final int rows) {

    // Several temp expression variables to simplify equation solution
    int expression1 = rows + cols + 2 * numRetPoints;
    long expression2 = ((long) 4 * cols + (long) 4 * numRetPoints
        + (long) 4 * rows * numRetPoints
        + (long) rows * rows + (long) cols * cols - (long) 2 * rows * cols
        + (long) 4 * rows * cols * numRetPoints);
    double expression3 = Math.sqrt(expression2);
    double expression4 = (double) numRetPoints - 1;

    // first solution
    double solution1 = -Math.round((expression1 + expression3) / expression4);
    // second solution
    double solution2 = -Math.round((expression1 - expression3) / expression4);

    // binary search range initialization with positive solution
    int high = (int) ((solution1 > solution2) ? solution1 : solution2);
    int low = (int) Math.floor(
        Math.sqrt((double) keyPoints.size() / numRetPoints));
    int width;
    int prevWidth = -1;

    ArrayList<Integer> resultVec = new ArrayList<>();
    boolean complete = false;
    int kMin = Math.round(numRetPoints - (numRetPoints * tolerance));
    int kMax = Math.round(numRetPoints + (numRetPoints * tolerance));

    ArrayList<Integer> result = new ArrayList<>(keyPoints.size());
    while (!complete) {
      width = low + (high - low) / 2;

      // needed to reassure the same radius is not repeated again
      if (width == prevWidth || low > high) {
        // return the keypoints from the previous iteration
        resultVec = result;
        break;
      }
      result.clear();
      double c = (double) width / 2; // initializing Grid
      int numCellCols = (int) Math.floor(cols / c);
      int numCellRows = (int) Math.floor(rows / c);

      // Fill temporary boolean array
      boolean[][] coveredVec = new boolean[numCellRows + 1][numCellCols + 1];

      // Perform square suppression
      for (int i = 0; i < keyPoints.size(); i++) {
        // get position of the cell current point is located at
        int row = (int) Math.floor(keyPoints.get(i).pt.y / c);
        int col = (int) Math.floor(keyPoints.get(i).pt.x / c);
        if (!coveredVec[row][col]) { // if the cell is not covered
          result.add(i);

          // get range which current radius is covering
          int rowMin = (int) (((row - (int) Math.floor(width / c)) >= 0)
              ? (row - Math.floor(width / c)) : 0);
          int rowMax = (int) (((row + Math.floor(width / c)) <= numCellRows)
              ? (row + Math.floor(width / c)) : numCellRows);
          int colMin = (int) (((col - Math.floor(width / c)) >= 0)
              ? (col - Math.floor(width / c)) : 0);
          int colMax = (int) (((col + Math.floor(width / c)) <= numCellCols)
              ? (col + Math.floor(width / c)) : numCellCols);

          // cover cells within the square bounding box with width w
          for (int rowToCov = rowMin; rowToCov <= rowMax; rowToCov++) {
            for (int colToCov = colMin; colToCov <= colMax; colToCov++) {
              if (!coveredVec[rowToCov][colToCov]) {
                coveredVec[rowToCov][colToCov] = true;
              }
            }
          }
        }
      }

      // solution found
      if (result.size() >= kMin && result.size() <= kMax) {
        resultVec = result;
        complete = true;
      } else if (result.size() < kMin) {
        high = width - 1; // update binary search range
      } else {
        low = width + 1; // update binary search range
      }
      prevWidth = width;
    }

    // Retrieve final keypoints
    List<KeyPoint> kp = new ArrayList<>();
    for (int i : resultVec) {
      kp.add(keyPoints.get(i));
    }

    return kp;
  }
}

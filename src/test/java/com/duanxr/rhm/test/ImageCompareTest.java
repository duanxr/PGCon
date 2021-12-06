package com.duanxr.rhm.test;
import java.io.IOException;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.DMatch;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.imgcodecs.Imgcodecs;
import org.scijava.nativelib.NativeLoader;

/**
 * @author Duanran 2019/12/13
 */
public class ImageCompareTest {

  public static void main(String[] args) throws IOException {
    //设置图像路径
    String filename1 = "C:\\Users\\mytq\\Desktop\\1.jpg";
    String filename2 = "C:\\Users\\mytq\\Desktop\\2.jpg";

    int ret;
    ret = compareFeature(filename1, filename2);

    if (ret > 0) {
      System.out.println("Two images are same.");
    } else {
      System.out.println("Two images are different.");
    }
  }

  /**
   * 使用特征映射比较两个图像是否相似
   * @author minikim
   * @param filename1 - the first image
   * @param filename2 - the second image
   * @return integer - count that has the similarity within images
   */
  public static int compareFeature(String filename1, String filename2) {
    int retVal = 0;
    long startTime = System.currentTimeMillis();
    nu.pattern.OpenCV.loadShared();
    /*System.loadLibrary(Core.NATIVE_LIBRARY_NAME);*/

    // 加载图像以进行比较
    Mat img1 = Imgcodecs.imread(filename1, Imgcodecs.CV_LOAD_IMAGE_COLOR);
    Mat img2 = Imgcodecs.imread(filename2, Imgcodecs.CV_LOAD_IMAGE_COLOR);

    // 声明图像的关键点
    MatOfKeyPoint keypoints1 = new MatOfKeyPoint();
    MatOfKeyPoint keypoints2 = new MatOfKeyPoint();
    Mat descriptors1 = new Mat();
    Mat descriptors2 = new Mat();

    // ORB关键点检测器和描述符提取器的定义
    FeatureDetector detector = FeatureDetector.create(FeatureDetector.ORB);
    DescriptorExtractor extractor = DescriptorExtractor.create(DescriptorExtractor.ORB);

    //检测关键点
    detector.detect(img1, keypoints1);
    detector.detect(img2, keypoints2);

    //提取描述符
    extractor.compute(img1, keypoints1, descriptors1);
    extractor.compute(img2, keypoints2, descriptors2);

    // 描述符匹配
    DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING);

    //两个图像的匹配点
    MatOfDMatch matches = new MatOfDMatch();
//  System.out.println("Type of Image1= " + descriptors1.type() + ", Type of Image2= " + descriptors2.type());
//  System.out.println("Cols of Image1= " + descriptors1.cols() + ", Cols of Image2= " + descriptors2.cols());

    // 避免断言失败
    // Assertion failed (type == src2.type() && src1.cols == src2.cols && (type == CV_32F || type == CV_8U)
    if (descriptors2.cols() == descriptors1.cols()) {
      matcher.match(descriptors1, descriptors2 ,matches);

      // 检查关键点的
      DMatch[] match = matches.toArray();
      double max_dist = 0; double min_dist = 100;

      for (int i = 0; i < descriptors1.rows(); i++) {
        double dist = match[i].distance;
        if( dist < min_dist ) min_dist = dist;
        if( dist > max_dist ) max_dist = dist;
      }
      System.out.println("max_dist=" + max_dist + ", min_dist=" + min_dist);

      //提取良好的图像（距离小于10）
      for (int i = 0; i < descriptors1.rows(); i++) {
        if (match[i].distance <= 10) {
          retVal++;
        }
      }
      System.out.println("matching count=" + retVal);
    }

    long estimatedTime = System.currentTimeMillis() - startTime;
    System.out.println("estimatedTime=" + estimatedTime + "ms");

    return retVal;
  }
}

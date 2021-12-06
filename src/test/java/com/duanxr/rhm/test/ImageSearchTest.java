/*
package com.duanxr.nug.test;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.duanxr.nug.execute.detection.ImageTemplateCache;
import com.duanxr.nug.execute.detection.CachedImageArea;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import nu.pattern.OpenCV;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.opencv.core.Core;
import org.opencv.core.Core.MinMaxLocResult;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

*/
/**
 * @author Duanran 2019/12/13
 *//*

public class ImageSearchTest {

  private static ImageTemplateCache imageAreaCache = new ImageTemplateCache();
  private static String ss = "[{\"top\":123,\"left\":48,\"bottom\":169,\"right\":104},{\"top\":488,\"left\":48,\"bottom\":534,\"right\":104},{\"top\":213,\"left\":48,\"bottom\":259,\"right\":104},{\"top\":398,\"left\":48,\"bottom\":444,\"right\":104},{\"top\":400,\"left\":336,\"bottom\":446,\"right\":392},{\"top\":214,\"left\":427,\"bottom\":260,\"right\":483},{\"top\":485,\"left\":336,\"bottom\":531,\"right\":392},{\"top\":33,\"left\":133,\"bottom\":79,\"right\":189},{\"top\":218,\"left\":336,\"bottom\":264,\"right\":392},{\"top\":307,\"left\":48,\"bottom\":353,\"right\":104},{\"top\":310,\"left\":336,\"bottom\":356,\"right\":392},{\"top\":577,\"left\":48,\"bottom\":623,\"right\":104},{\"top\":124,\"left\":336,\"bottom\":170,\"right\":392},{\"top\":40,\"left\":579,\"bottom\":86,\"right\":635},{\"top\":491,\"left\":789,\"bottom\":537,\"right\":845},{\"top\":307,\"left\":517,\"bottom\":353,\"right\":573},{\"top\":122,\"left\":517,\"bottom\":168,\"right\":573},{\"top\":306,\"left\":427,\"bottom\":352,\"right\":483},{\"top\":400,\"left\":608,\"bottom\":446,\"right\":664},{\"top\":586,\"left\":689,\"bottom\":632,\"right\":745},{\"top\":215,\"left\":789,\"bottom\":261,\"right\":845},{\"top\":304,\"left\":699,\"bottom\":350,\"right\":755},{\"top\":583,\"left\":436,\"bottom\":629,\"right\":492},{\"top\":309,\"left\":789,\"bottom\":355,\"right\":845},{\"top\":123,\"left\":608,\"bottom\":169,\"right\":664},{\"top\":400,\"left\":517,\"bottom\":446,\"right\":573},{\"top\":491,\"left\":517,\"bottom\":537,\"right\":573},{\"top\":213,\"left\":699,\"bottom\":259,\"right\":755},{\"top\":491,\"left\":608,\"bottom\":537,\"right\":664},{\"top\":125,\"left\":789,\"bottom\":171,\"right\":845},{\"top\":401,\"left\":789,\"bottom\":447,\"right\":845},{\"top\":491,\"left\":699,\"bottom\":537,\"right\":755},{\"top\":397,\"left\":427,\"bottom\":443,\"right\":483},{\"top\":309,\"left\":608,\"bottom\":355,\"right\":664},{\"top\":215,\"left\":608,\"bottom\":261,\"right\":664},{\"top\":489,\"left\":427,\"bottom\":535,\"right\":483},{\"top\":213,\"left\":517,\"bottom\":259,\"right\":573},{\"top\":397,\"left\":699,\"bottom\":443,\"right\":755},{\"top\":123,\"left\":699,\"bottom\":169,\"right\":755},{\"top\":122,\"left\":427,\"bottom\":168,\"right\":483}]\n";
  static List<CachedImageArea> list = new ArrayList<>();

  public static void main(String[] args) throws Exception {
    OpenCV.loadShared();
    HashSet<CachedImageArea> value = new HashSet<>();
    JSONArray objects = JSONArray.parseArray(ss);
    for (int i = 0; i < objects.size(); i++) {
      JSONObject jsonObject = objects.getJSONObject(i);
      CachedImageArea cachedDetectionImageArea = new CachedImageArea(jsonObject.getInteger("left"),
          jsonObject.getInteger("top"), jsonObject.getInteger("right"),
          jsonObject.getInteger("bottom"));
      value.add(cachedDetectionImageArea);
    }
    //设置图像路径
    String origin = "C:\\Users\\mytq\\Desktop\\13\\2019121319552301-B8FAEF4816CAC2B76D11869B05CA7601.jpg";
    String template = "C:\\Users\\mytq\\Desktop\\t.png";
    Mat templateMat = Imgcodecs.imread(template, Imgcodecs.CV_LOAD_IMAGE_COLOR);
    Mat mask = new Mat();
    templateMat.copyTo(mask);
    Core.bitwise_not(mask, mask);
    imageCache.setTemplate(templateMat);
    imageCache.setMask(mask);
    File file = new File("C:\\Users\\mytq\\Desktop\\13");
    Iterator<File> iterator = FileUtils
        .iterateFiles(file, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
    List<String> strings = new ArrayList<>();
    while (iterator.hasNext()) {
      File f = iterator.next();
      strings.add(f.getAbsolutePath());
    }
    ExecutorService executorService = Executors.newFixedThreadPool(400);
    long startTime = System.currentTimeMillis();
    int i = 0;
    while (i < strings.size()) {
      String f = strings.get(i);
      Mat originMat = Imgcodecs.imread(f, Imgcodecs.CV_LOAD_IMAGE_COLOR);
      for (CachedImageArea im : test) {
        Mat split = originMat.submat(im.getTop(), im.getBottom(), im.getLeft(), im.getRight());
        executorService.execute(() -> {
          if (compare(split)) {
            list.add(im);
          }
        });
      }
      i++;
    }
    long estimatedTime = System.currentTimeMillis() - startTime;
    System.out.println("estimatedTime=" + estimatedTime + "ms");
    Mat originMat = Imgcodecs.imread(origin, Imgcodecs.CV_LOAD_IMAGE_COLOR);
    JSONArray jsonArray = new JSONArray();
    for (CachedImageArea cachedDetectionImageArea : list) {
      Imgproc.rectangle(originMat, cachedDetectionImageArea.getLeftTopPoint(), cachedDetectionImageArea
              .getRightBottomPoint(),
          new Scalar(255, 0, 0));
      JSONObject jsonObject = new JSONObject();
      jsonObject.put("left", cachedDetectionImageArea.getLeft() - 5);
      jsonObject.put("right", cachedDetectionImageArea.getRight() + 5);
      jsonObject.put("top", cachedDetectionImageArea.getTop() - 5);
      jsonObject.put("bottom", cachedDetectionImageArea.getBottom() + 5);
      jsonArray.add(jsonObject);
    }
    System.out.println(jsonArray.toJSONString());

    TestUtil.displayImage(originMat);
  }

  public static boolean searchFeature(String origin, String template) {
    long startTime = System.currentTimeMillis();
    try {
      // 加载图像以进行比较

    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      long estimatedTime = System.currentTimeMillis() - startTime;
      System.out.println("estimatedTime=" + estimatedTime + "ms");
    }
    return false;
  }


  private static boolean compare(Mat originMat) {
    Mat result = new Mat();
    Imgproc.matchTemplate(originMat, imageCaceh.getTemplate(), result, Imgproc.TM_CCORR_NORMED,
        imageCache.getMask());
    MinMaxLocResult minMaxLocResult = Core.minMaxLoc(result);
    Point leftTopLoc = minMaxLocResult.maxLoc;
    Point rightBottomLoc = new Point(leftTopLoc.x + imageCache.getTemplate().cols(),
        leftTopLoc.y + imageCache.getTemplate().rows());
    return minMaxLocResult.maxVal > 0.95f;
  }
}
*/

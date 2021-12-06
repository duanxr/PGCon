/*
package com.duanxr.rhm.test;

import static org.bytedeco.javacpp.lept.pixDestroy;

import com.duanxr.rhm.cache.image.CachedImageArea;
import com.duanxr.rhm.script.pokemon.swsh.detection.DateChangeLocation;
import com.duanxr.rhm.util.MatUtil;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import lombok.extern.slf4j.Slf4j;
import nu.pattern.OpenCV;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.lept.PIX;
import org.bytedeco.javacpp.tesseract.TessBaseAPI;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;

@Slf4j
public class ImageOcrTest {

  static Mat originMat = null;
  private static final TessBaseAPI API = new TessBaseAPI();

  public static void main(String[] args) throws Exception {
    OpenCV.loadShared();
    String origin = "C:\\Users\\mytq\\Desktop\\13\\2019121319552301-B8FAEF4816CAC2B76D11869B05CA7601.jpg";
    Boolean cap = Boolean.TRUE;
    Map<String, CachedImageArea> hashMap = new HashMap<>();

    // Initialize tesseract-ocr with English, without specifying tessdata path
    if (API.Init("src/src/main/resources/tessdata", "ENG") != 0) {
      System.err.println("Could not initialize tesseract.");
      System.exit(1);
    }


    for (DateChangeLocation location : DateChangeLocation.values()) {
      hashMap.put(location.name(), location.area);
    }
    Map<String, String> map = areaOcr(origin, hashMap, cap);
    log.info(String.valueOf(map));
    API.End();
    TestUtil.displayImage(originMat);
  }

  public static Map<String, String> areaOcr(String origin,
      Map<String, CachedImageArea> map, Boolean cap) {
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
      Map<String, String> result = new HashMap<>();
      for (Entry<String, CachedImageArea> entry : map
          .entrySet()) {
        CachedImageArea value = entry.getValue();
        Mat sub = MatUtil
            .split(originMat, value.getLeft(), value.getTop(), value.getRight(), value.getBottom());
        PIX pix = MatUtil.convert(sub);
        BytePointer outText;
        API.SetImage(pix);
        outText = API.GetUTF8Text();
        String string = outText.getString();
        pixDestroy(pix);
        outText.deallocate();
        result.put(entry.getKey(),string);
      }
      return result;
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      long estimatedTime = System.currentTimeMillis() - startTime;
      System.out.println("estimatedTime=" + estimatedTime + "ms");
    }
    return null;
  }

}
*/

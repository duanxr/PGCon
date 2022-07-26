package d;

import com.duanxr.pgcon.util.ImageUtil;
import java.io.File;
import lombok.extern.slf4j.Slf4j;
import nu.pattern.OpenCV;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.tesseract.TessBaseAPI;
import org.bytedeco.tesseract.global.tesseract;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

/**
 * @author 段然 2022/7/26
 */
@Slf4j
public class Test {

  public static void main(String[] args) {
    OpenCV.loadLocally();
    TessBaseAPI tessBaseAPI = new TessBaseAPI();
    File file = new File("D:/DuanXR/Project/DuanXR/PGCon/src/main/resources/tessdata/");
    long length = file.length();
    int init = tessBaseAPI.Init(file.getAbsolutePath(), "eng",tesseract.OEM_LSTM_ONLY);
    if (init != 0) {
      log.error("Could not initialize tesseract.");
    }
    String path ="C:\\tmp\\PGCon15701522213659889941.png";
    Mat imread = Imgcodecs.imread(path, Imgcodecs.IMREAD_COLOR);
    tessBaseAPI.SetImage(ImageUtil.matToPix(imread));
    tessBaseAPI.SetPageSegMode(tesseract.PSM_SINGLE_LINE);
    BytePointer outText = tessBaseAPI.GetUTF8Text();
    String text = outText.getString();
    outText.deallocate();
    System.out.printf("%s", text);
  }
}

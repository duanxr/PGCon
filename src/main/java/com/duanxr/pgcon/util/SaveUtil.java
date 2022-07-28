package com.duanxr.pgcon.util;

import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.opencv.core.Mat;

/**
 * @author 段然 2022/7/26
 */
@Slf4j
public class SaveUtil {

  @SneakyThrows
  public static File saveTempImage(BufferedImage image) {
    File file = File.createTempFile("PGCon", ".png");
    ImageIO.write(image, "png", file);
    return file;
  }

  @SneakyThrows
  public static File saveImage(BufferedImage image) {
    File file = new File("images/" + System.currentTimeMillis() + ".png");
    if (!file.exists()) {
      file.mkdirs();
    }
    ImageIO.write(image, "png", file);
    return file;
  }


  public static void saveTempImage(Mat m) {
    saveTempImage(ImageConvertUtil.matToBufferedImage(m));
  }


}
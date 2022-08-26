package com.duanxr.pgcon.util;

import static com.duanxr.pgcon.config.ConstantConfig.IMAGES_PATH;

import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.opencv.core.Mat;

/**
 * @author 段然 2022/7/26
 */
@Slf4j
@UtilityClass
public class FileSaveUtil {
  @SneakyThrows
  public static File saveTempImage(BufferedImage image) {
    File file = File.createTempFile("PGCon", ".png");
    ImageIO.write(image, "png", file);
    return file;
  }
  @SneakyThrows
  public static File saveImage(BufferedImage image) {
    File file = new File(IMAGES_PATH + System.currentTimeMillis() + ".png");
    if (!file.exists()) {
      boolean mkdirs = file.mkdirs();
      if (!mkdirs) {
        log.warn("Cannot create image folder");
      }
    }
    ImageIO.write(image, "png", file);
    return file;
  }

  public static File saveTempImage(Mat m) {
    return saveImage(ImageUtil.matToBufferedImage(m));
  }


}
package com.duanxr.pgcon.gui.component;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.springframework.stereotype.Component;

/**
 * @author 段然 2022/7/25
 */
@Component
public class ResourceManager {

  LoadingCache<String, Mat> imageCache = Caffeine.newBuilder()
      .maximumSize(1000)
      .build(this::loadImage);

  @SneakyThrows
  private Mat loadImage(String path) {
    try (InputStream resourceAsStream = ResourceManager.class.getResourceAsStream(path)) {
      if (resourceAsStream == null) {
        return readImage(path);
      } else {
        File tempFile = File.createTempFile("PGCon", "tmp");
        tempFile.deleteOnExit();
        IOUtils.copy(resourceAsStream, new FileOutputStream(tempFile));
        return readImage(tempFile.getAbsolutePath());
      }
    }
  }

  private Mat readImage(String path) {
    return Imgcodecs.imread(path, Imgcodecs.IMREAD_COLOR);
  }


  public Mat getImage(String path) {
    return imageCache.get(path);
  }

  @SneakyThrows
  public File getFile(String path) {
    try (InputStream resourceAsStream = ResourceManager.class.getResourceAsStream(path)) {
      if (resourceAsStream == null) {
        return new File(path);
      } else {
        File tempFile = File.createTempFile("PGCon", "tmp");
        tempFile.deleteOnExit();
        IOUtils.copy(resourceAsStream, new FileOutputStream(tempFile));
        return tempFile;
      }
    }
  }
}

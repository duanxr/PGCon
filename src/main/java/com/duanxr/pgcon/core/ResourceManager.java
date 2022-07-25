package com.duanxr.pgcon.core;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
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
      .build(key -> Imgcodecs.imread(key, Imgcodecs.IMREAD_COLOR));

  public Mat getImage(String path) {
    return Imgcodecs.imread(path, Imgcodecs.IMREAD_COLOR);
  }

}

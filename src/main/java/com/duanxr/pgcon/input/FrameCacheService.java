package com.duanxr.pgcon.input;

import com.duanxr.pgcon.config.InputConfig;
import com.duanxr.pgcon.util.ImageUtil;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import java.awt.image.BufferedImage;
import java.util.concurrent.TimeUnit;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.opencv.core.Mat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author 段然 2021/12/28
 */
@Slf4j
@Component
public class FrameCacheService {
  private final LoadingCache<BufferedImage, Mat> matCache;
  private CachedFrame cachedFrame;
  @Autowired
  public FrameCacheService(InputConfig inputConfig) {
    this.matCache = Caffeine.newBuilder().maximumSize(inputConfig.getCacheSize())
        .expireAfterAccess(5, TimeUnit.SECONDS).build(ImageUtil::bufferedImageToMat);
  }

  public void setFrame(BufferedImage frame) {
    CachedFrame cachedFrame = new CachedFrame(frame, System.currentTimeMillis());
    synchronized (this) {
      this.cachedFrame = cachedFrame;
      this.notifyAll();
    }
  }

  public CachedFrame getFrame() {
    return this.cachedFrame;
  }

  @SneakyThrows
  public CachedFrame getNewFrame() {
    synchronized (this) {
      this.wait();
      return this.cachedFrame;
    }
  }

  @AllArgsConstructor
  public class CachedFrame {
    private final BufferedImage bufferedImage;
    private final long timestamp;

    public BufferedImage getImage() {
      return bufferedImage;
    }

    public long getTimestamp() {
      return timestamp;
    }

    public Mat getMat() {
      return matCache.get(bufferedImage);
    }

    @Override
    public int hashCode() {
      return System.identityHashCode(bufferedImage);
    }

  }
}

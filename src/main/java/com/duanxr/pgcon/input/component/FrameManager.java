package com.duanxr.pgcon.input.component;

import com.duanxr.pgcon.config.InputConfig;
import com.duanxr.pgcon.event.FrameEvent;
import com.duanxr.pgcon.util.ImageUtil;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.google.common.eventbus.Subscribe;
import java.awt.image.BufferedImage;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opencv.core.Mat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author 段然 2021/12/28
 */
@Slf4j
@Component
public class FrameManager {

  private final LoadingCache<BufferedImage, Mat> matCache;
  private CachedFrame cachedFrame;

  @Autowired
  public FrameManager(InputConfig inputConfig) {
    this.matCache = Caffeine.newBuilder().maximumSize(inputConfig.getCacheSize())
        .expireAfterAccess(30, TimeUnit.SECONDS).build(ImageUtil::bufferedImageToMat);
  }

  public void setFrame(BufferedImage frame) {
    this.cachedFrame = new CachedFrame(frame, System.currentTimeMillis());
  }

  public CachedFrame get() {
    return this.cachedFrame;
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

package com.duanxr.pgcon.input.component;

import com.duanxr.pgcon.config.InputConfig;
import com.duanxr.pgcon.event.FrameEvent;
import com.duanxr.pgcon.util.ImageUtil;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.google.common.eventbus.EventBus;
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
import org.opencv.core.Mat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author 段然 2021/12/28
 */
@Component
public class FrameManager {

  private final LoadingCache<BufferedImage, Mat> matLoadingCache;

  private final ExecutorService executorService;
  private final ArrayDeque<CachedFrame> cache;
  private final ReadWriteLock readWriteLock;
  private final InputConfig inputConfig;

  private final EventBus eventBus;

  @Autowired
  public FrameManager(ExecutorService executorService, EventBus eventBus, InputConfig inputConfig) {
    this.executorService = executorService;
    this.eventBus = eventBus;
    this.inputConfig = inputConfig;
    this.readWriteLock = new ReentrantReadWriteLock();
    this.cache = new ArrayDeque<>();
    this.matLoadingCache = Caffeine.newBuilder().maximumSize(inputConfig.getCacheSize())
        .expireAfterAccess(10,
            TimeUnit.SECONDS).build(ImageUtil::bufferedImageToMat);
    eventBus.register(this);
  }

  @Subscribe
  public void onFrame(FrameEvent frame) {
    executorService.execute(() -> {
      readWriteLock.writeLock().lock();
      CachedFrame cachedFrame = new CachedFrame(matLoadingCache, frame);
      cache.addFirst(cachedFrame);
      while (cache.size() > inputConfig.getCacheSize()) {
        cache.removeLast();
      }
      readWriteLock.writeLock().unlock();
      eventBus.post(cachedFrame);
    });
  }

  public CachedFrame get() {
    readWriteLock.readLock().lock();
    CachedFrame frame = cache.getFirst();
    readWriteLock.readLock().unlock();
    return frame;
  }

  public List<CachedFrame> get(int size) {
    readWriteLock.readLock().lock();
    size = Math.min(size, cache.size());
    List<CachedFrame> frames = new ArrayList<>(size);
    Iterator<CachedFrame> iterator = cache.iterator();
    while (iterator.hasNext() && size-- > 0) {
      frames.add(iterator.next());
    }
    readWriteLock.readLock().unlock();
    return frames;
  }

  public static class CachedFrame {

    private final LoadingCache<BufferedImage, Mat> matLoadingCache;
    private final BufferedImage bufferedImage;
    private final long timestamp;

    public CachedFrame(LoadingCache<BufferedImage, Mat> matLoadingCache, FrameEvent frameEvent) {
      this.matLoadingCache = matLoadingCache;
      this.bufferedImage = frameEvent.getFrame();
      this.timestamp = frameEvent.getTimestamp();
    }

    public BufferedImage getImage() {
      return bufferedImage;
    }

    public long getTimestamp() {
      return timestamp;
    }

    public Mat getMat() {
      return matLoadingCache.get(bufferedImage);
    }

  }
}

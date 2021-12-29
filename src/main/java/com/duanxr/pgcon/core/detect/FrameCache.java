package com.duanxr.pgcon.core.detect;

import com.duanxr.pgcon.config.InputConfig;
import com.duanxr.pgcon.event.FrameEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author 段然 2021/12/28
 */
@Component
public class FrameCache {

  private final ArrayDeque<FrameEvent> cache;
  private final ExecutorService executorService;
  private final ReadWriteLock readWriteLock;
  private final InputConfig inputConfig;


  @Autowired
  public FrameCache(EventBus eventBus, InputConfig inputConfig) {
    this.inputConfig = inputConfig;
    this.executorService = Executors.newCachedThreadPool();
    this.readWriteLock = new ReentrantReadWriteLock();
    this.cache = new ArrayDeque<>();
    eventBus.register(this);
  }

  @Subscribe
  public void onFrame(FrameEvent frame) {
    executorService.execute(() -> {
      readWriteLock.writeLock().lock();
      cache.addFirst(frame);
      while (cache.size() > inputConfig.getCacheSize()) {
        cache.removeLast();
      }
      readWriteLock.writeLock().unlock();
    });
  }

  public FrameEvent get() {
    readWriteLock.readLock().lock();
    FrameEvent frame = cache.getFirst();
    readWriteLock.readLock().unlock();
    return frame;
  }

  public List<FrameEvent> get(int size) {
    readWriteLock.readLock().lock();
    size = Math.min(size, cache.size());
    List<FrameEvent> frames = new ArrayList<>(size);
    Iterator<FrameEvent> iterator = cache.iterator();
    while (iterator.hasNext() && size-- > 0) {
      frames.add(iterator.next());
    }
    readWriteLock.readLock().unlock();
    return frames;
  }
}

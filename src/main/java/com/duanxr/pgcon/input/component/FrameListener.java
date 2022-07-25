package com.duanxr.pgcon.input.component;

import com.duanxr.pgcon.input.component.FrameManager.CachedFrame;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import lombok.SneakyThrows;

public abstract class FrameListener {

  private final EventBus eventBus;
  private final ExecutorService executorService;
  protected final CountDownLatch countDownLatch;

  public FrameListener(EventBus eventBus, int size,
      ExecutorService executorService) {
    this.executorService = executorService;
    this.countDownLatch = new CountDownLatch(size);
    this.eventBus = eventBus;
    eventBus.register(this);
  }

  @Subscribe
  public void onFrame(CachedFrame cachedFrame) {
    executorService.execute(() -> {
      receive(cachedFrame);
      countDownLatch.countDown();
      if (countDownLatch.getCount() == 0) {
        eventBus.unregister(this);
      }
    });
  }

  protected void stopListening() {
    eventBus.unregister(this);
    for (long i = 0; i < countDownLatch.getCount(); i++) {
      countDownLatch.countDown();
    }
  }

  public abstract void receive(CachedFrame cachedFrame);

  @SneakyThrows
  public void await() {
    countDownLatch.await();
  }
}
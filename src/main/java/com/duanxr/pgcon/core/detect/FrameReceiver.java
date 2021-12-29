package com.duanxr.pgcon.core.detect;

import com.duanxr.pgcon.core.PGPool;
import com.duanxr.pgcon.event.FrameEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import java.util.concurrent.CountDownLatch;
import lombok.SneakyThrows;

public abstract class FrameReceiver {

  private final EventBus eventBus;
  protected final CountDownLatch countDownLatch;

  public FrameReceiver(EventBus eventBus, int size) {
    this.countDownLatch = new CountDownLatch(size);
    this.eventBus = eventBus;
    eventBus.register(this);
  }

  @Subscribe
  public void onFrame(FrameEvent event) {
    receive(event);
    countDownLatch.countDown();
    if (countDownLatch.getCount() == 0) {
      eventBus.unregister(this);
    }
  }

  protected void breakReceive() {
    eventBus.unregister(this);
    for (long i = 0; i < countDownLatch.getCount(); i++) {
      countDownLatch.countDown();
    }
  }

  public abstract void receive(FrameEvent frame);

  @SneakyThrows
  public void await() {
    countDownLatch.await();
  }
}
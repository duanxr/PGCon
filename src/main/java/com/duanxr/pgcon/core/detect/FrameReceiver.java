package com.duanxr.pgcon.core.detect;

import com.duanxr.pgcon.core.ComponentManager;
import com.duanxr.pgcon.event.FrameEvent;
import com.google.common.eventbus.Subscribe;
import java.util.concurrent.CountDownLatch;
import lombok.SneakyThrows;

public abstract class FrameReceiver {

  private final ComponentManager componentManager;
  protected final CountDownLatch countDownLatch;

  public FrameReceiver(ComponentManager componentManager, int size) {
    this.countDownLatch = new CountDownLatch(size);
    this.componentManager = componentManager;
    componentManager.getEventBus().register(this);
  }

  @Subscribe
  public void onFrame(FrameEvent event) {
    receive(event);
    countDownLatch.countDown();
    if (countDownLatch.getCount() == 0) {
      componentManager.getEventBus().unregister(this);
    }
  }

  protected void breakReceive() {
    componentManager.getEventBus().unregister(this);
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
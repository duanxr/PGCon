package com.duanxr.rhm.core.execute.daemon;

import com.google.common.util.concurrent.FutureCallback;
import java.util.concurrent.CountDownLatch;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * @author Duanran 2019/12/17
 */
public class DaemonCallback implements FutureCallback<Object> {

  private CountDownLatch countDownLatch;

  public DaemonCallback() {
    countDownLatch = new CountDownLatch(1);
  }

  @Override
  public void onSuccess(@Nullable Object result) {
    countDownLatch.countDown();
  }

  @Override
  public void onFailure(Throwable t) {
    countDownLatch.countDown();
  }

  public void await() throws InterruptedException {
    countDownLatch.await();
  }
}

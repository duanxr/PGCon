package com.duanxr.rhm.core.execute.daemon;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import java.util.concurrent.Callable;

/**
 * @author Duanran 2019/12/17
 */
public class DaemonCallable implements Callable<Object> {

  private final Callable<?> holderCallable;

  private final ListeningExecutorService scriptExecutorService;

  private ListenableFuture<?> submit;

  private boolean keepRun;

  public DaemonCallable(Callable<?> holderCallable,
      ListeningExecutorService scriptExecutorService) {
    this.holderCallable = holderCallable;
    this.scriptExecutorService = scriptExecutorService;
    keepRun = true;
  }

  @Override
  public Object call() throws InterruptedException {
    while (keepRun) {
      DaemonCallback daemonCallback = new DaemonCallback();
      run(daemonCallback);
      daemonCallback.await();
    }
    return null;
  }

  private synchronized void run(DaemonCallback daemonCallback) {
    if (keepRun) {
      submit = scriptExecutorService.submit(holderCallable);
      Futures.addCallback(submit, daemonCallback, scriptExecutorService);
    }
  }

  public synchronized void cancel() {
    if (submit != null) {
      submit.cancel(true);
    }
    keepRun = false;
  }

}

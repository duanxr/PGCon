package com.duanxr.pgcon.script.component;

import com.duanxr.pgcon.output.Controller;
import com.duanxr.pgcon.script.api.MainScript;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import java.util.concurrent.Executors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author Duanran 2019/12/13
 */
@Slf4j
@Component
public class ScriptRunner {

  private final ListeningExecutorService listeningExecutorService;
  private ListenableFuture<?> currentScriptListenable;
  private ScriptTask currentScriptTask;
  private final Controller controller;
  public ScriptRunner(Controller controller) {
    this.controller = controller;
    listeningExecutorService = MoreExecutors.listeningDecorator(
        Executors.newSingleThreadExecutor());
  }

  public synchronized void run(MainScript mainScript,Runnable callback) {
    stop();
    currentScriptTask = new ScriptTask(mainScript,callback);
    currentScriptListenable = listeningExecutorService.submit(currentScriptTask);
  }

  public synchronized boolean isRunning() {
    return currentScriptTask != null;
  }
  public synchronized void stop() {
    if (currentScriptTask != null) {
      currentScriptTask.stop();
      currentScriptTask = null;
      currentScriptListenable.cancel(true);
      currentScriptListenable = null;
      controller.clear();
    }
  }


}

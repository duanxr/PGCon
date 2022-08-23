package com.duanxr.pgcon.script.component;

import com.duanxr.pgcon.script.api.Script;
import com.duanxr.pgcon.output.ControllerService;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import java.util.concurrent.ExecutorService;
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
  private final ControllerService controllerService;
  public ScriptRunner(ControllerService controllerService, ExecutorService executorService) {
    this.controllerService = controllerService;
    this.listeningExecutorService = MoreExecutors.listeningDecorator(executorService);
  }
  public synchronized void run(Script<Object> script) {
    stop();
    script.load();
    currentScriptTask = new ScriptTask(script);
    currentScriptListenable = listeningExecutorService.submit(currentScriptTask);
  }
  public synchronized void run(Script<Object> script,Runnable callback) {
    stop();
    script.load();
    currentScriptTask = new ScriptTask(script,callback);
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
      controllerService.clear();
    }
  }

}

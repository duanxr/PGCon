package com.duanxr.pgcon.script.component;

import com.duanxr.pgcon.script.api.MainScript;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import java.util.concurrent.Executors;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author Duanran 2019/12/13
 */
@Slf4j
@Service
public class ScriptRunner {

  @Getter
  private final ListeningExecutorService listeningExecutorService;

  private ListenableFuture<?> currentScriptSubmit;

  public ScriptRunner() {
    this.listeningExecutorService = MoreExecutors.listeningDecorator(Executors.newCachedThreadPool());
  }

  public synchronized void runScript(MainScript mainScript) {
    stopScript();
    currentScriptSubmit = listeningExecutorService.submit(mainScript);
  }

  public synchronized void stopScript() {
    if (currentScriptSubmit != null) {
      currentScriptSubmit.cancel(true);
      currentScriptSubmit=null;
    }
  }
}

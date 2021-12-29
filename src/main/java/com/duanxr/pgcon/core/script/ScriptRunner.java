package com.duanxr.pgcon.core.script;

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
  private final ListeningExecutorService scriptExecutorService;

  private Script currentScript;

  private ListenableFuture<?> currentScriptSubmit;

  public ScriptRunner() {
    this.scriptExecutorService = MoreExecutors.listeningDecorator(Executors.newCachedThreadPool());
  }

  public synchronized void runScript(Script script) {
    stopScript();
    currentScript =script;
    currentScriptSubmit = scriptExecutorService.submit(script);
  }

  public synchronized void stopScript() {
    if (currentScriptSubmit != null) {
      currentScript.stop();
      currentScriptSubmit.cancel(true);
      currentScriptSubmit=null;
    }
  }
}

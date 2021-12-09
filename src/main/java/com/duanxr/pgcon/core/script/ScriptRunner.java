package com.duanxr.pgcon.core.script;

import static com.duanxr.pgcon.util.ConstantConfig.TEMPLATE_MATCH_THREADS;

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
  private ListeningExecutorService scriptExecutorService;

  private Script currentScript;

  private ListenableFuture<?> currentScriptSubmit;

  public ScriptRunner() {
    this.scriptExecutorService = MoreExecutors.listeningDecorator(
        Executors.newFixedThreadPool(TEMPLATE_MATCH_THREADS));
  }

  public synchronized void runScript(Script script) {
    stopScript();
    ListenableFuture<?> submit = scriptExecutorService.submit(script);
  }

  public synchronized void stopScript() {
    if (currentScriptSubmit != null) {
      currentScriptSubmit.cancel(true);
    }
  }
}

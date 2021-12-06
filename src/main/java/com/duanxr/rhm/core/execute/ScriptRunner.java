package com.duanxr.rhm.core.execute;

import static com.duanxr.rhm.config.ConstantConfig.TEMPLATE_MATCH_THREADS;

import com.duanxr.rhm.core.execute.daemon.DaemonCache;
import com.duanxr.rhm.core.execute.daemon.DaemonCallable;
import com.duanxr.rhm.script.ExecutableScript;
import com.duanxr.rhm.script.Script;
import com.duanxr.rhm.script.Subscript;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import java.util.ArrayList;
import java.util.List;
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

  private Script currentExecutableScript;

  private List<DaemonCache> daemonCacheList;

  public ScriptRunner() {
    this.scriptExecutorService = MoreExecutors
        .listeningDecorator(Executors.newFixedThreadPool(TEMPLATE_MATCH_THREADS));
    this.daemonCacheList = new ArrayList<>();
  }

  public synchronized void runScript(Script script) {
    stopScript();
    List<Subscript> subscriptList = script.getSubscript();
    for (Subscript subscript : subscriptList) {
      DaemonCallable daemonCallable = new DaemonCallable(subscript, scriptExecutorService);
      ListenableFuture<?> submit = scriptExecutorService.submit(daemonCallable);
      daemonCacheList.add(new DaemonCache(daemonCallable, submit));
    }
  }

  public synchronized void stopScript() {
    for (DaemonCache daemonCache : daemonCacheList) {
      daemonCache.getDaemonCallable().cancel();
      daemonCache.getSubmit().cancel(true);
    }
    daemonCacheList.clear();
  }
}

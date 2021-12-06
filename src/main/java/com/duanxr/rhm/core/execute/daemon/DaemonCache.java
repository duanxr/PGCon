package com.duanxr.rhm.core.execute.daemon;

import com.google.common.util.concurrent.ListenableFuture;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Duanran 2019/12/17
 */
@Data
@AllArgsConstructor
public class DaemonCache {

  private DaemonCallable daemonCallable;
  private ListenableFuture<?> submit;

}

package com.duanxr.pgcon.core.detect;

import java.util.concurrent.Future;

/**
 * @author 段然 2021/12/6
 */
public interface Detector<A, T, R>{

  DetectResult<R> detect(A a);

  Future<DetectResult<R>> asyncDetect(A a);

  DetectResult<R> detect(A a, T t);

  Future<DetectResult<R>> asyncDetect(A a, T t);

  DetectResult<R> detect(A a, T t, long timeoutMillis);

  Future<DetectResult<R>> asyncDetect(A a, T t, long timeoutMillis);

}

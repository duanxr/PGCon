package com.duanxr.pgcon.algo.detect.api;

/**
 * @author 段然 2022/7/25
 */
public interface Detector<R, P> {

  R detect(P param);

}

package com.duanxr.pgcon.core.detect.api;

import java.util.List;

/**
 * @author 段然 2022/7/25
 */
public interface Detector<R, P> {

  R detect(P param);

}

package com.duanxr.rhm.cache.loadable;

import com.duanxr.rhm.core.parser.image.define.DefineImageOcrCondition;
import com.duanxr.rhm.core.parser.image.models.ImageOcrApi;

/**
 * @author Duanran 2019/12/21
 */
public class LoadableDefineImageOcrCondition extends LoadableDefineImageOcr implements
    DefineImageOcrCondition {

  protected static final int TARGET_NUMBER = 0;

  protected String target;

  public LoadableDefineImageOcrCondition(String name, String target,
      ImageOcrApi imageOcrApi, int top, int bottom, int left, int right) {
    super(name, TARGET_NUMBER, imageOcrApi, top, bottom, left, right);
    this.target = target;
  }

  @Override
  public boolean isHit(String result) {
    return target.equals(result);
  }
}

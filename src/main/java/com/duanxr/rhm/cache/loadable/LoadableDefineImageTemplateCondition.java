package com.duanxr.rhm.cache.loadable;

import com.duanxr.rhm.core.parser.image.define.DefineImageTemplateCondition;

/**
 * @author Duanran 2019/12/21
 */
public class LoadableDefineImageTemplateCondition extends
    LoadableDefineImageTemplate implements
    DefineImageTemplateCondition {

  protected static final int TARGET_NUMBER = 0;

  public LoadableDefineImageTemplateCondition(String name, String path, boolean hasMask, int top,
      int bottom,
      int left, int right) {
    super(name, TARGET_NUMBER, path, hasMask, top, bottom, left, right);
  }

  @Override
  public boolean isHit(int index) {
    return index == TARGET_NUMBER;
  }
}

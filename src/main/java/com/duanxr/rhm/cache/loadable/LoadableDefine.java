package com.duanxr.rhm.cache.loadable;

import com.duanxr.rhm.core.parser.image.define.Define;

/**
 * @author Duanran 2019/12/21
 */
public class LoadableDefine implements Define {

  protected String name;

  public LoadableDefine(String name) {
    this.name = name;
  }

  @Override
  public String getName() {
    return this.name;
  }
}
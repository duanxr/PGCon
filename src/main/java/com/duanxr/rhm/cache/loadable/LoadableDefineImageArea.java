package com.duanxr.rhm.cache.loadable;

import com.duanxr.rhm.cache.entity.CachedImageArea;
import com.duanxr.rhm.cache.ImageAreaCache;
import com.duanxr.rhm.core.parser.image.define.DefineImageArea;

/**
 * @author Duanran 2019/12/21
 */
public class LoadableDefineImageArea extends LoadableDefine implements DefineImageArea {

  protected CachedImageArea cachedImageArea;

  public LoadableDefineImageArea(String name, int number, int top, int bottom, int left, int right) {
    super(name);
    this.cachedImageArea = new CachedImageArea(left, top, right, bottom);
    this.cachedImageArea.setNumber(number);
    ImageAreaCache.loadCache(this);
  }

  @Override
  public CachedImageArea loadImageArea() {
    return this.cachedImageArea;
  }

  @Override
  public int getNumber() {
    return this.cachedImageArea.getNumber();
  }
}
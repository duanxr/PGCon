package com.duanxr.rhm.cache.loadable;

import com.duanxr.rhm.cache.ImageTemplateCache;
import com.duanxr.rhm.cache.entity.CachedImageTemplate;
import com.duanxr.rhm.core.parser.image.define.DefineImageTemplate;
import com.duanxr.rhm.util.MatLoadUtil;
import com.duanxr.rhm.util.MatUtil;

/**
 * @author Duanran 2019/12/21
 */
public class LoadableDefineImageTemplate extends LoadableDefineImageArea implements
    DefineImageTemplate {

  protected CachedImageTemplate cachedImageTemplate;

  public LoadableDefineImageTemplate(String name, int number, String path, boolean hasMask, int top,
      int bottom, int left, int right) {
    super(name, number, top, bottom, left, right);
    this.cachedImageTemplate = new CachedImageTemplate();
    this.cachedImageTemplate.setName(name);
    this.cachedImageTemplate.setTemplate(MatLoadUtil.loadByResourcesPath(path));
    if (hasMask) {
      cachedImageTemplate.setMask(MatUtil.toMask(cachedImageTemplate.getTemplate()));
    }
    ImageTemplateCache.loadCache(this);
  }

  @Override
  public CachedImageTemplate loadImageTemplate() {
    return cachedImageTemplate;
  }
}

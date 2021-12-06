package com.duanxr.rhm.cache.loadable;

import com.duanxr.rhm.core.parser.image.define.DefineImageOcr;
import com.duanxr.rhm.core.parser.image.models.ImageOcrApi;

/**
 * @author Duanran 2019/12/21
 */
public class LoadableDefineImageOcr extends LoadableDefineImageArea implements
    DefineImageOcr {

  protected ImageOcrApi imageOcrApi;

  public LoadableDefineImageOcr(String name, int number, ImageOcrApi imageOcrApi, int top,
      int bottom, int left, int right) {
    super(name, number, top, bottom, left, right);
    this.imageOcrApi = imageOcrApi;
  }

  @Override
  public ImageOcrApi getOcrType() {
    return this.imageOcrApi;
  }

}

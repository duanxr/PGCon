package com.duanxr.rhm.util;

import com.duanxr.rhm.cache.entity.CachedImageArea;
import com.duanxr.rhm.cache.entity.CachedImageTemplate;
import java.io.IOException;
import org.opencv.core.Mat;

/**
 * @author Duanran 2019/12/17
 */
public class DetectionImageFactory {

  public static DetectionImageBuilder build(String name) {
    return new DetectionImageBuilder(name);
  }

  public static class DetectionImageBuilder {

    private CachedImageTemplate cachedImageTemplate;

    private DetectionImageBuilder(String name) {
      cachedImageTemplate = new CachedImageTemplate();
      cachedImageTemplate.setName(name);
    }

    public DetectionImageBuilder template(String resourcePath) throws IOException {
      Mat mat = MatLoadUtil.loadByResourcesPath(resourcePath);
      cachedImageTemplate.setTemplate(mat);
      return this;
    }

    public DetectionImageBuilder mask() {
      Mat mask = MatUtil.toMask(cachedImageTemplate.getTemplate());
      cachedImageTemplate.setMask(mask);
      return this;
    }

    public DetectionImageBuilder addArea(CachedImageArea area) {
      return this;
    }

    public CachedImageTemplate build() {
      return cachedImageTemplate;
    }
  }
}

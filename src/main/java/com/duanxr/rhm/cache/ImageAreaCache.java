package com.duanxr.rhm.cache;

import com.duanxr.rhm.cache.entity.CachedImageArea;
import com.duanxr.rhm.core.parser.image.define.DefineImageArea;
import com.google.common.collect.ArrayListMultimap;
import java.util.List;

/**
 * @author Duanran 2019/12/13
 */
public class ImageAreaCache {

  private static ArrayListMultimap<String, CachedImageArea> cacheMap = ArrayListMultimap
      .create();

  public static void set(String name, CachedImageArea detectionImageArea) {
    cacheMap.put(name, detectionImageArea);
  }

  public static void remove(String name) {
    cacheMap.removeAll(name);
  }

  public static List<CachedImageArea> get(String name) {
    return cacheMap.get(name);
  }

  public static void loadCache(DefineImageArea defineImageArea) {
    CachedImageArea cachedImageArea = defineImageArea.loadImageArea();
    String name = defineImageArea.getName();
    set(name, cachedImageArea);
  }

}

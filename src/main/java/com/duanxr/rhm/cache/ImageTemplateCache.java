package com.duanxr.rhm.cache;

import com.duanxr.rhm.cache.entity.CachedImageTemplate;
import com.duanxr.rhm.core.parser.image.define.DefineImageTemplate;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Duanran 2019/12/13
 */
public class ImageTemplateCache {

  private static Map<String, CachedImageTemplate> cacheMap = new HashMap<>();

  public static void set(String name, CachedImageTemplate cachedImageTemplate) {
    cacheMap.put(cachedImageTemplate.getName(), cachedImageTemplate);
  }

  public static void remove(CachedImageTemplate cachedImageTemplate) {
    cacheMap.remove(cachedImageTemplate.getName());
  }

  public static CachedImageTemplate get(String name) {
    return cacheMap.get(name);
  }

  public static void loadCache(DefineImageTemplate defineImageTemplate) {
    CachedImageTemplate cachedImageTemplate = defineImageTemplate.loadImageTemplate();
    String name = defineImageTemplate.getName();
    set(name, cachedImageTemplate);
  }
}

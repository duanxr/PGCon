/*
package com.duanxr.rhm.script.pokemon.swsh.detection;

import com.duanxr.rhm.cache.entity.CachedImageTemplate;
import com.duanxr.rhm.cache.ImageTemplateCache;
import com.duanxr.rhm.util.DetectionImageFactory;
import com.duanxr.rhm.util.DetectionImageFactory.DetectionImageBuilder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

*/
/**
 * @author Duanran 2019/12/17
 *//*

@Slf4j
@AllArgsConstructor
public class BoxArrowLocationCache {

  private static final String IMG_BOX_ARROW = "/img/box_arrow.png";
  public static String boxArrow = "BoxArrow";
  private static BoxArrowLocation[][] locationMap = new BoxArrowLocation[7][7];

  private static CachedImageTemplate cachedImageTemplate;

  static {
    try {
      DetectionImageBuilder builder = DetectionImageFactory.build(boxArrow)
          .template(IMG_BOX_ARROW).mask();
      for (BoxArrowLocation location : BoxArrowLocation.values()) {
        locationMap[location.col][location.row] = location;
        builder
            .addArea(location.area);
      }
      cachedImageTemplate = builder.build();
      ImageTemplateCache.set(cachedImageTemplate);
    } catch (Exception e) {
      log.error("BoxArrowLocationCache Init Exception.", e);
    }
  }

}
*/

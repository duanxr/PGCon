package com.duanxr.rhm.script.pokemon.swsh.define;

import com.duanxr.rhm.cache.entity.CachedImageArea;
import com.duanxr.rhm.cache.loadable.LoadableDefineImageOcr;
import com.duanxr.rhm.core.parser.image.define.DefineImageOcr;
import com.duanxr.rhm.core.parser.image.models.ImageOcrApi;

/**
 * @author Duanran 2019/12/21
 */
public enum SwshDefineImageOcr implements DefineImageOcr {

  INGAME_MENU_POKEBOX_BOXNAME("INGAME_MENU_POKEBOX_BOXNAME", 0, ImageOcrApi.CHS, 71,107,574,640),
  INGAME_MENU_POKEBOX_POKE_ABILITY("INGAME_MENU_POKEBOX_POKE_ABILITY", 0, ImageOcrApi.CHS, 400,440,991,1211),
  INGAME_MENU_POKEBOX_POKE_NUMBER("INGAME_MENU_POKEBOX_POKE_NUMBER", 0, ImageOcrApi.NUM, 73,97,933,978),



  MENU_DATE_CHANGE_DAY("MENU_DATE_CHANGE_DAY", 0, ImageOcrApi.NUM, 436, 503, 504, 584),
  MENU_DATE_CHANGE_MONTH("MENU_DATE_CHANGE_MONTH", 0, ImageOcrApi.NUM, 436, 503, 374, 454),
  MENU_DATE_CHANGE_YEAR("MENU_DATE_CHANGE_YEAR", 0, ImageOcrApi.NUM, 433, 506, 172, 314);

  private LoadableDefineImageOcr loadableDefineImageOcr;

  SwshDefineImageOcr(String name, int number, ImageOcrApi imageOcrApi, int top,
      int bottom, int left, int right) {
    loadableDefineImageOcr = new LoadableDefineImageOcr(name, number, imageOcrApi, top, bottom,
        left, right);
  }

  @Override
  public CachedImageArea loadImageArea() {
    return loadableDefineImageOcr.loadImageArea();
  }

  @Override
  public int getNumber() {
    return loadableDefineImageOcr.getNumber();
  }

  @Override
  public String getName() {
    return loadableDefineImageOcr.getName();
  }

  @Override
  public ImageOcrApi getOcrType() {
    return loadableDefineImageOcr.getOcrType();
  }
}

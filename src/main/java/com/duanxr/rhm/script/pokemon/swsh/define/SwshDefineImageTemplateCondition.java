package com.duanxr.rhm.script.pokemon.swsh.define;

import com.duanxr.rhm.cache.entity.CachedImageArea;
import com.duanxr.rhm.cache.entity.CachedImageTemplate;
import com.duanxr.rhm.cache.loadable.LoadableDefineImageTemplateCondition;
import com.duanxr.rhm.core.parser.image.define.DefineImageTemplateCondition;
import com.duanxr.rhm.core.parser.image.models.ImageOcrApi;

/**
 * @author Duanran 2019/12/21
 */
public enum SwshDefineImageTemplateCondition implements DefineImageTemplateCondition {

  //提前量450
  EGG_TALK_ENABLE_N("EGG_TALK_ENABLE_N", "/img/EGG_TALK_ENABLE_N.png", false, 83,221,736-450,910-450),
  EGG_TALK_ENABLE("EGG_TALK_ENABLE", "/img/EGG_TALK_ENABLE.png", false, 83,221,736-450,910-450),

  BATTLE_RUN_SELECTED("BATTLE_RUN_SELECTED", "/img/BATTLE_SELECTED.png", true, 642, 708, 994, 1056),
  BATTLE_ATTACK_SELECTED("BATTLE_ATTACK_SELECTED", "/img/BATTLE_SELECTED.png", true, 410, 474, 989,
      1056),

  ROTOM_LOTTERY_SELECTED("ROTOM_LOTTERY_SELECTED", "/img/ROTOM_LOTTERY_SELECTED.png", true, 379,
      436, 842, 898),
  ROTOM_DONE("ROTOM_DONE", "/img/ROTOM_DONE.png", false, 576, 699, 257, 616),

  MENU_DATE("MENU_DATE", "/img/MENU_DATE.png", false, 24, 76, 68, 214),
  MENU_SETTING("MENU_SETTING", "/img/MENU_SETTING.png", false, 21, 79, 63, 191),
  MAIN_PAGE("MAIN_PAGE", "/img/MAIN_PAGE_A.png", false, 665, 701, 1132, 1168);

  private LoadableDefineImageTemplateCondition defineImageTemplateCondition;

  SwshDefineImageTemplateCondition(String name, String path, boolean hasMask, int top, int bottom,
      int left, int right) {
    defineImageTemplateCondition = new LoadableDefineImageTemplateCondition(name, path, hasMask,
        top, bottom,
        left, right);
  }

  @Override
  public boolean isHit(int index) {
    return defineImageTemplateCondition.isHit(index);
  }

  @Override
  public CachedImageTemplate loadImageTemplate() {
    return defineImageTemplateCondition.loadImageTemplate();
  }

  @Override
  public CachedImageArea loadImageArea() {
    return defineImageTemplateCondition.loadImageArea();
  }

  @Override
  public int getNumber() {
    return defineImageTemplateCondition.getNumber();
  }

  @Override
  public String getName() {
    return defineImageTemplateCondition.getName();
  }
}

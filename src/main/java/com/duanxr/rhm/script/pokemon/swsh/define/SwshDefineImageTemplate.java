package com.duanxr.rhm.script.pokemon.swsh.define;

import com.duanxr.rhm.cache.entity.CachedImageArea;
import com.duanxr.rhm.cache.entity.CachedImageTemplate;
import com.duanxr.rhm.cache.loadable.LoadableDefineImageTemplate;
import com.duanxr.rhm.core.parser.image.define.DefineImageTemplate;

/**
 * @author Duanran 2019/12/21
 */
public enum SwshDefineImageTemplate implements DefineImageTemplate {

  INGAME_MENU_SETTING_SELECTED("INGAME_MENU_SELECTED",9, "/img/BATTLE_SELECTED.png", true, 371,439,995,1069),
  INGAME_MENU_VS_SELECTED("INGAME_MENU_SELECTED",8, "/img/BATTLE_SELECTED.png", true, 370,433,771,836),
  INGAME_MENU_GIFT_SELECTED("INGAME_MENU_SELECTED",7, "/img/BATTLE_SELECTED.png", true, 367,439,540,597),
  INGAME_MENU_PLAY_SELECTED("INGAME_MENU_SELECTED",6, "/img/BATTLE_SELECTED.png", true, 370,439,305,362),
  INGAME_MENU_MAP_SELECTED("INGAME_MENU_SELECTED",5, "/img/BATTLE_SELECTED.png", true, 368,437,70,129),
  INGAME_MENU_SAVE_SELECTED("INGAME_MENU_SELECTED",4, "/img/BATTLE_SELECTED.png", true, 124,203,997,1072),
  INGAME_MENU_CARD_SELECTED("INGAME_MENU_SELECTED",3, "/img/BATTLE_SELECTED.png", true, 127,201,769,835),
  INGAME_MENU_BAG_SELECTED("INGAME_MENU_SELECTED",2, "/img/BATTLE_SELECTED.png", true, 124,198,529,606),
  INGAME_MENU_POKE_SELECTED("INGAME_MENU_SELECTED",1, "/img/BATTLE_SELECTED.png", true, 126,202,293,372),
  INGAME_MENU_DEX_SELECTED("INGAME_MENU_SELECTED",0, "/img/BATTLE_SELECTED.png", true, 133,196,63,135);

  private LoadableDefineImageTemplate loadableDefineImageTemplate;

  SwshDefineImageTemplate(String name, int number, String path, boolean hasMask, int top,
      int bottom, int left, int right) {
    loadableDefineImageTemplate = new LoadableDefineImageTemplate(name, number, path, hasMask,
        top, bottom, left, right);
  }

  @Override
  public CachedImageTemplate loadImageTemplate() {
    return loadableDefineImageTemplate.loadImageTemplate();
  }

  @Override
  public CachedImageArea loadImageArea() {
    return loadableDefineImageTemplate.loadImageArea();
  }

  @Override
  public int getNumber() {
    return loadableDefineImageTemplate.getNumber();
  }

  @Override
  public String getName() {
    return loadableDefineImageTemplate.getName();
  }
}

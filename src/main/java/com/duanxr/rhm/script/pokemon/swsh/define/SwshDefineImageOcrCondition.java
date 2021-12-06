package com.duanxr.rhm.script.pokemon.swsh.define;

import com.duanxr.rhm.cache.entity.CachedImageArea;
import com.duanxr.rhm.cache.loadable.LoadableDefineImageOcrCondition;
import com.duanxr.rhm.core.parser.image.define.DefineImageOcrCondition;
import com.duanxr.rhm.core.parser.image.models.ImageOcrApi;

/**
 * @author Duanran 2019/12/21
 */
public enum SwshDefineImageOcrCondition implements DefineImageOcrCondition {


  BATTLE_IN("BATTLE_IN", "战斗", ImageOcrApi.CHS, 416,468,1055,1122),

  EGG_TALK_START("EGG_TALK_START", "欢迎光临寄放屋", ImageOcrApi.CHS, 586,639,268,529),
  EGG_TURN_BACK("EGG_TURN_BACK", "远处的拳关市竞技场", ImageOcrApi.CHS, 644,686,272,600),

  ROTOM_LOTTERY_STARTED("ROTOM_LOTTERY_STARTED", "现在我们连接到", ImageOcrApi.CHS, 583,639,272,533),
  ROTOM_MENU("ROTOM_MENU", "宝可帮帮忙", ImageOcrApi.CHS, 425,469,885,1035),
  ROTOM_START("ROTOM_START", "要做什么洛米", ImageOcrApi.CHS, 638,689,265,491),

  INGAME_MENU_POKE("INGAME_MENU_POKE", "前往盒子", ImageOcrApi.CHS, 646,685,1095,1222),

  INGAME_MENU_POKEBOX_POKEINFO_1("INGAME_MENU_POKEBOX_POKEINFO_1", "特性", ImageOcrApi.CHS, 404,439,908,962),


  MENU_DATE_CHANGE_ENABLE("MENU_DATE_CHANGE_ENABLE", "开启", ImageOcrApi.CHS, 150,183,997,1052),
  MENU_DATE_CHANGING("MENU_DATE_CHANGING", "现在的日期与时间", ImageOcrApi.CHS, 270, 300, 30, 270);
  private LoadableDefineImageOcrCondition loadableDefineImageOcrCondition;

  SwshDefineImageOcrCondition(String name, String target,
      ImageOcrApi imageOcrApi, int top, int bottom, int left, int right) {
    loadableDefineImageOcrCondition = new LoadableDefineImageOcrCondition(name, target,
        imageOcrApi, top,
        bottom,
        left, right);
  }

  @Override
  public CachedImageArea loadImageArea() {
    return loadableDefineImageOcrCondition.loadImageArea();
  }

  @Override
  public int getNumber() {
    return loadableDefineImageOcrCondition.getNumber();
  }

  @Override
  public String getName() {
    return loadableDefineImageOcrCondition.getName();
  }

  @Override
  public ImageOcrApi getOcrType() {
    return loadableDefineImageOcrCondition.getOcrType();
  }

  @Override
  public boolean isHit(String result) {
    return loadableDefineImageOcrCondition.isHit(result);
  }
}

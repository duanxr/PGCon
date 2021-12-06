package com.duanxr.rhm.script.pokemon.swsh.define;

import com.duanxr.rhm.cache.entity.CachedImageArea;
import com.duanxr.rhm.cache.entity.CachedImageTemplate;
import com.duanxr.rhm.cache.loadable.LoadableDefineImageTemplate;
import com.duanxr.rhm.core.parser.image.define.DefineImageTemplate;

/**
 * @author Duanran 2019/12/21
 */
public enum SwshDefinePokebox implements DefineImageTemplate {

  HOLD_HEAD(false, false, 0, 0, 28, 94, 133, 189),
  BOX_HEAD(false, true, 1, 0, 35, 101, 579, 635),
  BOX_VIEW(false, true, 1, 6, 578, 644, 436, 492),
  BOX_SEARCH(false, true, 5, 6, 581, 647, 689, 745),

  HOLD_1(true, false, 0, 1, 118, 184, 48, 104),
  HOLD_2(true, false, 0, 2, 208, 274, 48, 104),
  HOLD_3(true, false, 0, 3, 302, 368, 48, 104),
  HOLD_4(true, false, 0, 4, 393, 459, 48, 104),
  HOLD_5(true, false, 0, 5, 483, 549, 48, 104),
  HOLD_6(true, false, 0, 6, 572, 638, 48, 104),

  BOX_1_1(true, true, 1, 1, 119, 185, 336, 392),
  BOX_1_2(true, true, 1, 2, 213, 279, 336, 392),
  BOX_1_3(true, true, 1, 3, 305, 371, 336, 392),
  BOX_1_4(true, true, 1, 4, 395, 461, 336, 392),
  BOX_1_5(true, true, 1, 5, 480, 546, 336, 392),

  BOX_2_1(true, true, 2, 1, 117, 183, 427, 483),
  BOX_2_2(true, true, 2, 2, 209, 275, 427, 483),
  BOX_2_3(true, true, 2, 3, 301, 367, 427, 483),
  BOX_2_4(true, true, 2, 4, 392, 458, 427, 483),
  BOX_2_5(true, true, 2, 5, 484, 550, 427, 483),

  BOX_3_1(true, true, 3, 1, 117, 183, 517, 573),
  BOX_3_2(true, true, 3, 2, 208, 274, 517, 573),
  BOX_3_3(true, true, 3, 3, 302, 368, 517, 573),
  BOX_3_4(true, true, 3, 4, 395, 461, 517, 573),
  BOX_3_5(true, true, 3, 5, 486, 552, 517, 573),

  BOX_4_1(true, true, 4, 1, 118, 184, 608, 664),
  BOX_4_2(true, true, 4, 2, 210, 276, 608, 664),
  BOX_4_3(true, true, 4, 3, 304, 370, 608, 664),
  BOX_4_4(true, true, 4, 4, 395, 461, 608, 664),
  BOX_4_5(true, true, 4, 5, 486, 552, 608, 664),

  BOX_5_1(true, true, 5, 1, 118, 184, 699, 755),
  BOX_5_2(true, true, 5, 2, 208, 274, 699, 755),
  BOX_5_3(true, true, 5, 3, 299, 365, 699, 755),
  BOX_5_4(true, true, 5, 4, 392, 458, 699, 755),
  BOX_5_5(true, true, 5, 5, 486, 552, 699, 755),

  BOX_6_1(true, true, 6, 1, 120, 186, 789, 845),
  BOX_6_2(true, true, 6, 2, 210, 276, 789, 845),
  BOX_6_3(true, true, 6, 3, 304, 370, 789, 845),
  BOX_6_4(true, true, 6, 4, 396, 462, 789, 845),
  BOX_6_5(true, true, 6, 5, 486, 552, 789, 845);

  public boolean canPutPokemon;
  public boolean inBox;
  public int col;
  public int row;
  private LoadableDefineImageTemplate loadableDefineImageTemplate;

  SwshDefinePokebox(boolean canPutPokemon, boolean inBox, int col, int row, int top,
      int bottom, int left, int right) {
    loadableDefineImageTemplate = new LoadableDefineImageTemplate("POKE_BOX_LOCATION",
        col * 10 + row, "/img/BOX_ARROW.png", true,
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

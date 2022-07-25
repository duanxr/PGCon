package com.duanxr.pgcon.output.action;

import static com.duanxr.pgcon.output.action.StickBaseAction.LX_CENTER;
import static com.duanxr.pgcon.output.action.StickBaseAction.LX_MAX;
import static com.duanxr.pgcon.output.action.StickBaseAction.LX_MIN;
import static com.duanxr.pgcon.output.action.StickBaseAction.LY_CENTER;
import static com.duanxr.pgcon.output.action.StickBaseAction.LY_MAX;
import static com.duanxr.pgcon.output.action.StickBaseAction.LY_MIN;
import static com.duanxr.pgcon.output.action.StickBaseAction.RX_CENTER;
import static com.duanxr.pgcon.output.action.StickBaseAction.RX_MAX;
import static com.duanxr.pgcon.output.action.StickBaseAction.RX_MIN;
import static com.duanxr.pgcon.output.action.StickBaseAction.RY_CENTER;
import static com.duanxr.pgcon.output.action.StickBaseAction.RY_MAX;

import lombok.Getter;

/**
 * @author Duanran 2019/12/17
 */
@Getter
public enum StickAction {

  L_TOP(LX_CENTER, LY_MIN,true),
  L_TOP_RIGHT(LX_MAX, LY_MIN,true),
  L_RIGHT(LX_MAX, LY_CENTER,true),
  L_BOTTOM_RIGHT(LX_MAX, LY_MAX,true),
  L_BOTTOM(LX_CENTER, LY_MAX,true),
  L_BOTTOM_LEFT(LX_MIN, LY_MAX,true),
  L_LEFT(LX_MIN, LY_CENTER,true),
  L_TOP_LEFT(LX_MIN, LY_MIN,true),
  L_CENTER(LX_CENTER, LY_CENTER,true),

  R_TOP(RX_CENTER, LY_MIN,false),
  R_TOP_RIGHT(RX_MAX, LY_MIN,false),
  R_RIGHT(RX_MAX, RY_CENTER,false),
  R_BOTTOM_RIGHT(RX_MAX, RY_MAX,false),
  R_BOTTOM(RX_CENTER, RY_MAX,false),
  R_BOTTOM_LEFT(RX_MIN, RY_MAX,false),
  R_LEFT(RX_MIN, RY_CENTER,false),
  R_TOP_LEFT(RX_MIN, LY_MIN,false),
  R_CENTER(RX_CENTER, RY_CENTER,false);

  private final StickBaseAction actionX;
  private final StickBaseAction actionY;
  private final boolean isLeft;

  StickAction(StickBaseAction actionX, StickBaseAction actionY, boolean isLeft) {
    this.actionX = actionX;
    this.actionY = actionY;
    this.isLeft = isLeft;
  }
}

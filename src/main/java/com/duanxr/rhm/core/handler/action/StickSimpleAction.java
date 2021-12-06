package com.duanxr.rhm.core.handler.action;

import static com.duanxr.rhm.core.handler.action.StickAction.LX_CENTER;
import static com.duanxr.rhm.core.handler.action.StickAction.LX_MAX;
import static com.duanxr.rhm.core.handler.action.StickAction.LX_MIN;
import static com.duanxr.rhm.core.handler.action.StickAction.LY_CENTER;
import static com.duanxr.rhm.core.handler.action.StickAction.LY_MAX;
import static com.duanxr.rhm.core.handler.action.StickAction.LY_MIN;
import static com.duanxr.rhm.core.handler.action.StickAction.RX_CENTER;
import static com.duanxr.rhm.core.handler.action.StickAction.RX_MAX;
import static com.duanxr.rhm.core.handler.action.StickAction.RX_MIN;
import static com.duanxr.rhm.core.handler.action.StickAction.RY_CENTER;
import static com.duanxr.rhm.core.handler.action.StickAction.RY_MAX;

import lombok.Getter;

/**
 * @author Duanran 2019/12/17
 */
@Getter
public enum StickSimpleAction {

  L_TOP(LX_CENTER, LY_MIN),
  L_TOP_RIGHT(LX_MAX, LY_MIN),
  L_RIGHT(LX_MAX, LY_CENTER),
  L_BOTTOM_RIGHT(LX_MAX, LY_MAX),
  L_BOTTOM(LX_CENTER, LY_MAX),
  L_BOTTOM_LEFT(LX_MIN, LY_MAX),
  L_LEFT(LX_MIN, LY_CENTER),
  L_TOP_LEFT(LX_MIN, LY_MIN),
  L_CENTER(LX_CENTER, LY_CENTER),

  R_TOP(RX_CENTER, LY_MIN),
  R_TOP_RIGHT(RX_MAX, LY_MIN),
  R_RIGHT(RX_MAX, RY_CENTER),
  R_BOTTOM_RIGHT(RX_MAX, RY_MAX),
  R_BOTTOM(RX_CENTER, RY_MAX),
  R_BOTTOM_LEFT(RX_MIN, RY_MAX),
  R_LEFT(RX_MIN, RY_CENTER),
  R_TOP_LEFT(RX_MIN, LY_MIN),
  R_CENTER(RX_CENTER, RY_CENTER);

  private StickAction actionX;
  private StickAction actionY;

  StickSimpleAction(StickAction actionX, StickAction actionY) {
    this.actionX = actionX;
    this.actionY = actionY;
  }
}

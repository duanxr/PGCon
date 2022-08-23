package com.duanxr.pgcon.output.action;

import static com.duanxr.pgcon.output.action.NintendoSwitchBaseStick.LX_CENTER;
import static com.duanxr.pgcon.output.action.NintendoSwitchBaseStick.LX_MAX;
import static com.duanxr.pgcon.output.action.NintendoSwitchBaseStick.LX_MIN;
import static com.duanxr.pgcon.output.action.NintendoSwitchBaseStick.LY_CENTER;
import static com.duanxr.pgcon.output.action.NintendoSwitchBaseStick.LY_MAX;
import static com.duanxr.pgcon.output.action.NintendoSwitchBaseStick.LY_MIN;
import static com.duanxr.pgcon.output.action.NintendoSwitchBaseStick.RX_CENTER;
import static com.duanxr.pgcon.output.action.NintendoSwitchBaseStick.RX_MAX;
import static com.duanxr.pgcon.output.action.NintendoSwitchBaseStick.RX_MIN;
import static com.duanxr.pgcon.output.action.NintendoSwitchBaseStick.RY_CENTER;
import static com.duanxr.pgcon.output.action.NintendoSwitchBaseStick.RY_MAX;
import static com.duanxr.pgcon.output.action.Sticks.LEFT;
import static com.duanxr.pgcon.output.action.Sticks.RIGHT;

import com.duanxr.pgcon.output.api.Stick;
import lombok.Getter;

/**
 * @author Duanran 2019/12/17
 */
@Getter
public enum NintendoSwitchStandardStick implements Stick {
  L_TOP(LEFT, LX_CENTER, LY_MIN),
  L_TOP_RIGHT(LEFT, LX_MAX, LY_MIN),
  L_RIGHT(LEFT, LX_MAX, LY_CENTER),
  L_BOTTOM_RIGHT(LEFT, LX_MAX, LY_MAX),
  L_BOTTOM(LEFT, LX_CENTER, LY_MAX),
  L_BOTTOM_LEFT(LEFT, LX_MIN, LY_MAX),
  L_LEFT(LEFT, LX_MIN, LY_CENTER),
  L_TOP_LEFT(LEFT, LX_MIN, LY_MIN),
  L_CENTER(LEFT, LX_CENTER, LY_CENTER),
  R_TOP(RIGHT, RX_CENTER, LY_MIN),
  R_TOP_RIGHT(RIGHT, RX_MAX, LY_MIN),
  R_RIGHT(RIGHT, RX_MAX, RY_CENTER),
  R_BOTTOM_RIGHT(RIGHT, RX_MAX, RY_MAX),
  R_BOTTOM(RIGHT, RX_CENTER, RY_MAX),
  R_BOTTOM_LEFT(RIGHT, RX_MIN, RY_MAX),
  R_LEFT(RIGHT, RX_MIN, RY_CENTER),
  R_TOP_LEFT(RIGHT, RX_MIN, LY_MIN),
  R_CENTER(RIGHT, RX_CENTER, RY_CENTER);
  private final Sticks stick;
  private final byte xCommandPGCon;
  private final byte yCommandPGCon;
  private final byte xCommandEasyCon;
  private final byte yCommandEasyCon;
  NintendoSwitchStandardStick(Sticks stick, NintendoSwitchBaseStick actionX,NintendoSwitchBaseStick actionY) {
    this.stick = stick;
    this.xCommandPGCon = actionX.getCommandPGCon();
    this.yCommandPGCon = actionY.getCommandPGCon();
    this.xCommandEasyCon = actionX.getCommandEasyCon();
    this.yCommandEasyCon = actionY.getCommandEasyCon();
  }
}

package com.duanxr.rhm.core.handler.action;

import lombok.Getter;

/**
 * @author Duanran 2019/12/17
 */
@Getter
public enum StickAction {

  LX_MIN(37),
  LX_MAX(38),
  LX_CENTER(39),
  LY_MIN(40),
  LY_MAX(41),
  LY_CENTER(42),
  RX_MIN(43),
  RX_MAX(44),
  RX_CENTER(45),
  RY_MIN(46),
  RY_MAX(47),
  RY_CENTER(48);

  private byte command;

  StickAction(int command) {
    this.command = (byte) command;
  }
}

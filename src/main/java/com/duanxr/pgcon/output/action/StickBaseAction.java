package com.duanxr.pgcon.output.action;

import lombok.Getter;

/**
 * @author Duanran 2019/12/17
 */
@Getter
public enum StickBaseAction {

  LX_MIN(37,0),
  LX_MAX(38,-1),
  LX_CENTER(39,-128),
  LY_MIN(40,0),
  LY_MAX(41,-1),
  LY_CENTER(42,-128),
  RX_MIN(43,0),
  RX_MAX(44,-1),
  RX_CENTER(45,-128),
  RY_MIN(46,0),
  RY_MAX(47,-1),
  RY_CENTER(48,-128);

  private byte pgcCommand;
  private byte scCommand;

  StickBaseAction(int command, int scCommand) {
    this.pgcCommand = (byte) command;
    this.scCommand = (byte) scCommand;
  }
}

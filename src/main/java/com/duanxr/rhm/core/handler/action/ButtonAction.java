package com.duanxr.rhm.core.handler.action;

import lombok.Getter;

/**
 * @author Duanran 2019/12/13
 */
@Getter
public enum ButtonAction {

  A(1, 2),
  B(3, 4),
  X(5, 6),
  Y(7, 8),
  L(9, 10),
  R(11, 12),
  ZL(13, 14),
  ZR(15, 16),
  L_STICK(17, 18),
  R_STICK(19, 20),
  D_TOP(21, 22),
  D_BOTTOM(23, 24),
  D_LEFT(25, 26),
  D_RIGHT(27, 28),
  PLUS(29, 30),
  MINUS(31, 32),
  HOME(33, 34),
  CAPTURE(35, 36);

  private byte downCommand;
  private byte upCommand;

  ButtonAction(int downCommand, int upCommand) {
    this.downCommand = (byte) downCommand;
    this.upCommand = (byte) upCommand;
  }
}

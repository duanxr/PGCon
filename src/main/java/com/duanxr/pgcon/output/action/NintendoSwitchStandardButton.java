package com.duanxr.pgcon.output.action;


import com.duanxr.pgcon.output.api.Button;
import lombok.Getter;

/**
 * @author Duanran 2019/12/13
 */
@Getter
public enum NintendoSwitchStandardButton implements Button {

  A(1, 2, 0x04, false),
  B(3, 4, 0x02, false),
  X(5, 6, 0x08, false),
  Y(7, 8, 0x01, false),
  L(9, 10, 0x10, false),
  R(11, 12, 0x20, false),
  ZL(13, 14, 0x40, false),
  ZR(15, 16, 0x80, false),
  L_STICK(17, 18, 0x400, false),
  R_STICK(19, 20, 0x800, false),
  D_TOP(21, 22, 0x00, true),
  D_BOTTOM(23, 24, 0x04, true),
  D_LEFT(25, 26, 0x06, true),
  D_RIGHT(27, 28, 0x02, true),
  PLUS(29, 30, 0x200, false),
  MINUS(31, 32, 0x100, false),
  HOME(33, 34, 0x1000, false),
  CAPTURE(35, 36, 0x2000, false);

  private final byte holdCommandPGCon;
  private final byte releaseCommandPGCon;
  private final int commandEasyCon;
  private final boolean isHat;

  NintendoSwitchStandardButton(int holdCommandPGCon, int releaseCommandPGCon, int commandEasyCon,
      boolean isHat) {
    this.holdCommandPGCon = (byte) holdCommandPGCon;
    this.releaseCommandPGCon = (byte) releaseCommandPGCon;
    this.commandEasyCon = commandEasyCon;
    this.isHat = isHat;
  }


}

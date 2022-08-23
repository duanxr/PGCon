package com.duanxr.pgcon.output.api;

/**
 * @author 段然 2022/8/23
 */
public interface Button {
  byte getHoldCommandPGCon();
  byte getReleaseCommandPGCon();
  int getCommandEasyCon();
  boolean isHat();

  int ordinal();
}

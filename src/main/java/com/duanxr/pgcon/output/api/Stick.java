package com.duanxr.pgcon.output.api;

import com.duanxr.pgcon.output.action.Sticks;

/**
 * @author 段然 2022/8/23
 */
public interface Stick {
  Sticks getStick();

  byte getXCommandPGCon();

  byte getYCommandPGCon();

  byte getXCommandEasyCon();

  byte getYCommandEasyCon();
}

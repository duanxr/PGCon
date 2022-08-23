package com.duanxr.pgcon.output.api;

import com.duanxr.pgcon.output.action.NintendoSwitchStandardButton;
import com.duanxr.pgcon.output.action.NintendoSwitchStandardStick;

/**
 * @author Duanran 2019/12/17
 */
public interface Protocol {

  void hold(Button buttonType);

  void release(Button buttonType);

  void set(Stick stickSimpleAction);

  void set(boolean isLeft, double degrees);

  void clear();

  boolean isConnected();
}

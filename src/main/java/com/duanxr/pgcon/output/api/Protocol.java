package com.duanxr.pgcon.output.api;

import com.duanxr.pgcon.output.action.ButtonAction;
import com.duanxr.pgcon.output.action.StickAction;

/**
 * @author Duanran 2019/12/17
 */
public interface Protocol {

  void hold(ButtonAction buttonType);

  void release(ButtonAction buttonType);

  void set(StickAction stickSimpleAction);

  void set(boolean isLeft, double degrees);

  void clear();

  boolean isConnected();
}

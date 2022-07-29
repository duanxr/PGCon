package com.duanxr.pgcon.output.api;

import com.duanxr.pgcon.output.action.ButtonAction;
import com.duanxr.pgcon.output.action.PressAction;
import com.duanxr.pgcon.output.action.StickAction;
import lombok.SneakyThrows;

/**
 * @author Duanran 2019/12/17
 */
public interface Protocol {

  void hold(ButtonAction buttonType);

  void release(ButtonAction buttonType);

  void set(StickAction stickSimpleAction);

  void clear();

  boolean isConnected();
}

package com.duanxr.pgcon.output;

import com.duanxr.pgcon.output.action.ButtonAction;
import com.duanxr.pgcon.output.action.PressAction;
import com.duanxr.pgcon.output.action.StickAction;
import lombok.SneakyThrows;

/**
 * @author Duanran 2019/12/17
 */
public interface Protocol {
  //TODO 添加虚拟按键自动释放功能

  void send(ButtonAction buttonType, PressAction pressAction);

  void send(StickAction stickSimpleAction, PressAction pressAction);

  void clear();

}

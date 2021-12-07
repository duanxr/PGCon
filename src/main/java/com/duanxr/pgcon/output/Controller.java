package com.duanxr.pgcon.output;

import com.duanxr.pgcon.output.action.ButtonAction;
import com.duanxr.pgcon.output.action.PressAction;
import com.duanxr.pgcon.output.action.StickAction;
import lombok.Setter;

/**
 * @author 段然 2021/12/7
 */
public class Controller {

  @Setter
  private Protocol protocol;

  public void press(ButtonAction buttonType) {
    protocol.send(buttonType, PressAction.PRESS);
  }

  public void hold(ButtonAction buttonType) {
    protocol.send(buttonType, PressAction.HOLD);
  }

  public void release(ButtonAction buttonType) {
    protocol.send(buttonType, PressAction.RELEASE);
  }

  public void press(StickAction stickAction) {
    protocol.send(stickAction, PressAction.PRESS);
  }

  public void hold(StickAction stickAction) {
    protocol.send(stickAction, PressAction.HOLD);
  }

  public void release(StickAction stickAction) {
    protocol.send(stickAction, PressAction.RELEASE);
  }

  public void clear() {
    protocol.clear();
  }

  public void capture() {
    try {
      protocol.send(ButtonAction.CAPTURE, PressAction.HOLD);
      Thread.sleep(5000);
      protocol.send(ButtonAction.CAPTURE, PressAction.RELEASE);
    } catch (Exception ignore) {
    }
  }
}

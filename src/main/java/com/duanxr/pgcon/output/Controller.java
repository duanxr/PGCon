package com.duanxr.pgcon.output;


import com.duanxr.pgcon.config.OutputConfig;
import com.duanxr.pgcon.output.action.ButtonAction;
import com.duanxr.pgcon.output.action.PressAction;
import com.duanxr.pgcon.output.action.StickAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author 段然 2021/12/7
 */
@Component
public class Controller {

  private final OutputConfig outputConfig;

  private Protocol protocol;

  @Autowired
  public Controller(OutputConfig outputConfig) {
    this.outputConfig = outputConfig;
  }

  public void press(ButtonAction buttonType) {
    protocol.send(buttonType, PressAction.PRESS, outputConfig.getPressTime());
  }

  public void press(ButtonAction buttonType, int time) {
    protocol.send(buttonType, PressAction.PRESS, time);
  }

  public void hold(ButtonAction buttonType) {
    protocol.send(buttonType, PressAction.HOLD, 0);
  }

  public void release(ButtonAction buttonType) {
    protocol.send(buttonType, PressAction.RELEASE, 0);
  }

  public void press(StickAction stickAction) {
    protocol.send(stickAction, PressAction.PRESS, outputConfig.getPressTime());
  }

  public void press(StickAction stickAction, int time) {
    protocol.send(stickAction, PressAction.PRESS, time);
  }

  public void hold(StickAction stickAction) {
    protocol.send(stickAction, PressAction.HOLD, 0);
  }

  public void release(StickAction stickAction) {
    protocol.send(stickAction, PressAction.RELEASE, 0);
  }

  public void clear() {
    protocol.clear();
  }

  public void capture() {
    try {
      protocol.send(ButtonAction.CAPTURE, PressAction.PRESS, outputConfig.getCaptureTime());
    } catch (Exception ignore) {
    }
  }

  public void setProtocol(Protocol protocol) {
    //TODO CLOSE PORT CONNECTION
    this.protocol = protocol;
  }
}

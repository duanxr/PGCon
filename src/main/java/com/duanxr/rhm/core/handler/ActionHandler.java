package com.duanxr.rhm.core.handler;

import static com.duanxr.rhm.config.ConstantConfig.OUTPUT_HOLD_TIME;

import com.duanxr.rhm.core.handler.action.ButtonAction;
import com.duanxr.rhm.core.handler.action.PressAction;
import com.duanxr.rhm.core.handler.action.StickAction;
import com.duanxr.rhm.core.handler.action.StickSimpleAction;
import com.duanxr.rhm.io.output.ControllerOutput;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author Duanran 2019/12/17
 */
@Slf4j
@Service
public class ActionHandler {

  @Setter
  private ControllerOutput controllerOutput;

  public ActionHandler() {
  }

  @SneakyThrows
  public void sendAction(ButtonAction buttonType, PressAction pressAction) {
    switch (pressAction) {
      case DOWN:
        send(buttonType.getDownCommand());
        break;
      case UP:
        send(buttonType.getUpCommand());
        break;
      default:
        send(buttonType.getDownCommand());
        Thread.sleep(OUTPUT_HOLD_TIME);
        send(buttonType.getUpCommand());
        break;
    }
  }

  @SneakyThrows
  public void sendAction(StickSimpleAction stickSimpleAction, PressAction pressAction) {
    switch (pressAction) {
      case DOWN:
        send(stickSimpleAction.getActionX().getCommand());
        send(stickSimpleAction.getActionY().getCommand());
        break;
      case UP:
        if (stickSimpleAction.name().startsWith("L")) {
          send(StickAction.LX_CENTER.getCommand());
          send(StickAction.LY_CENTER.getCommand());
        } else {
          send(StickAction.RX_CENTER.getCommand());
          send(StickAction.RY_CENTER.getCommand());
        }
        break;
      default:
        send(stickSimpleAction.getActionX().getCommand());
        send(stickSimpleAction.getActionY().getCommand());
        Thread.sleep(OUTPUT_HOLD_TIME);
        if (stickSimpleAction.name().startsWith("L")) {
          send(StickAction.LX_CENTER.getCommand());
          send(StickAction.LY_CENTER.getCommand());
        } else {
          send(StickAction.RX_CENTER.getCommand());
          send(StickAction.RY_CENTER.getCommand());
        }
        break;
    }
  }

  public void sendAction(StickAction stickAction) {
    send(stickAction.getCommand());
  }

  @SneakyThrows
  private synchronized void send(int command) {
    if (controllerOutput != null) {
      controllerOutput.output(command);
    }
  }

  public void clear() {
    send(49);
  }
}

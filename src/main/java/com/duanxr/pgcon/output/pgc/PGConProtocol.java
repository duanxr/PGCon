package com.duanxr.pgcon.output.pgc;

import static com.duanxr.pgcon.util.ConstantConfig.OUTPUT_PRESS_TIME;

import com.duanxr.pgcon.output.Protocol;
import com.duanxr.pgcon.output.SerialPort;
import com.duanxr.pgcon.output.action.ButtonAction;
import com.duanxr.pgcon.output.action.PressAction;
import com.duanxr.pgcon.output.action.StickAction;
import lombok.SneakyThrows;

/**
 * @author 段然 2021/12/7
 */
public class PGConProtocol implements Protocol {

  private final SerialPort<Integer> pgConProtocol;

  @SneakyThrows
  public PGConProtocol(String portName){
    pgConProtocol= new PGConSerialPort(portName);
  }

  @Override
  @SneakyThrows
  public void send(ButtonAction buttonType, PressAction pressAction, int pressTime) {
    switch (pressAction) {
      case HOLD:
        send(buttonType.getPgcHoldCommand());
        break;
      case RELEASE:
        send(buttonType.getPgcReleaseCommand());
        break;
      default:
        send(buttonType.getPgcHoldCommand());
        Thread.sleep(pressTime);
        send(buttonType.getPgcReleaseCommand());
        break;
    }
  }

  @Override
  @SneakyThrows
  public void send(StickAction stickAction, PressAction pressAction, int pressTime) {
    switch (pressAction) {
      case HOLD:
        send(stickAction.getActionX().getPgcCommand());
        send(stickAction.getActionY().getPgcCommand());
        break;
      case RELEASE:
        if (stickAction.isLeft()) {
          send(StickAction.L_CENTER.getActionX().getPgcCommand());
          send(StickAction.L_CENTER.getActionY().getPgcCommand());
        } else {
          send(StickAction.R_CENTER.getActionX().getPgcCommand());
          send(StickAction.R_CENTER.getActionY().getPgcCommand());
        }
        break;
      default:
        send(stickAction.getActionX().getPgcCommand());
        send(stickAction.getActionY().getPgcCommand());
        Thread.sleep(pressTime);
        if (stickAction.isLeft()) {
          send(StickAction.L_CENTER.getActionX().getPgcCommand());
          send(StickAction.L_CENTER.getActionY().getPgcCommand());
        } else {
          send(StickAction.R_CENTER.getActionX().getPgcCommand());
          send(StickAction.R_CENTER.getActionY().getPgcCommand());
        }
        break;
    }
  }

  @Override
  public void clear() {
    send(49);
  }

  private void send(int pgcCommand) {
    pgConProtocol.sendCommand(pgcCommand);
  }

}

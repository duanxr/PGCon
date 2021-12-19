package com.duanxr.pgcon.output.sc;

import com.duanxr.pgcon.output.Protocol;
import com.duanxr.pgcon.output.SerialPort;
import com.duanxr.pgcon.output.action.ButtonAction;
import com.duanxr.pgcon.output.action.PressAction;
import com.duanxr.pgcon.output.action.StickAction;
import lombok.SneakyThrows;

/**
 * @author 段然 2021/12/7
 */
public class SerialConProtocol implements Protocol {

  private final SerialPort<byte[]> pgConProtocol;

  @SneakyThrows
  public SerialConProtocol(String portName) {
    pgConProtocol = new SerialConSerialPort(portName);
  }

  @Override
  @SneakyThrows
  public void send(ButtonAction buttonType, PressAction pressAction, int pressTime) {
    switch (pressAction) {
      case HOLD:
        sendButton(buttonType);
        break;
      case RELEASE:
        reset();
        break;
      default:
        sendButton(buttonType);
        Thread.sleep(pressTime);
        reset();
        break;
    }
  }


  private void sendButton(ButtonAction buttonType) {
    int scCommand = buttonType.getScCommand();
    byte[] bytes = getDefault();
    if (buttonType.isHat()) {
      bytes[2] = (byte) scCommand;
    } else {
      if (scCommand <= 0x80) {
        bytes[1] = (byte) scCommand;
      } else {
        bytes[0] = (byte) (scCommand >> 8);
      }
    }
    pgConProtocol.sendCommand(bytes);
  }

  private void sendStick(StickAction stickAction) {
    byte[] bytes = getDefault();
    if (stickAction.isLeft()) {
      bytes[3] = stickAction.getActionX().getScCommand();
      bytes[4] = stickAction.getActionY().getScCommand();
    } else {
      bytes[5] = stickAction.getActionX().getScCommand();
      bytes[6] = stickAction.getActionY().getScCommand();
    }
    pgConProtocol.sendCommand(bytes);
  }

  private void reset() {
    pgConProtocol.sendCommand(getDefault());
  }


  @Override
  @SneakyThrows
  public void send(StickAction stickAction, PressAction pressAction, int pressTime) {
    switch (pressAction) {
      case HOLD:
        sendStick(stickAction);
        break;
      case RELEASE:
        reset();
        break;
      default:
        sendStick(stickAction);
        Thread.sleep(pressTime);
        reset();
        break;
    }
  }


  @Override
  public void clear() {
    pgConProtocol.checkConnection();
  }

  private byte[] getDefault() {
    return new byte[]{0, 0, 8, -128, -128, -128, -128};
  }

}

package com.duanxr.pgcon.output.sc;

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
public class SerialConProtocol implements Protocol {

  private final SerialPort<byte[]> pgConProtocol;

  @SneakyThrows
  public SerialConProtocol(String portName) {
    pgConProtocol = new SerialConSerialPort(portName);
  }

  @Override
  @SneakyThrows
  public void send(ButtonAction buttonType, PressAction pressAction) {
    switch (pressAction) {
      case HOLD:
        sendButton(buttonType);
        break;
      case RELEASE:
        reset();
        break;
      default:
        sendButton(buttonType);
        Thread.sleep(OUTPUT_PRESS_TIME);
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
  public void send(StickAction stickAction, PressAction pressAction) {
    switch (pressAction) {
      case HOLD:
        sendStick(stickAction);
        break;
      case RELEASE:
        reset();
        break;
      default:
        sendStick(stickAction);
        Thread.sleep(OUTPUT_PRESS_TIME);
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

  @SneakyThrows
  public static void main(String[] args) {
    SerialConProtocol serialConProtocol = new SerialConProtocol("COM15");
    serialConProtocol.clear();
    serialConProtocol.send(StickAction.R_TOP, PressAction.PRESS);
    Thread.sleep(500);
    serialConProtocol.send(StickAction.R_BOTTOM, PressAction.PRESS);
    Thread.sleep(500);
    serialConProtocol.send(StickAction.R_LEFT, PressAction.PRESS);
    Thread.sleep(500);
    serialConProtocol.send(StickAction.R_RIGHT, PressAction.PRESS);
    Thread.sleep(500);
    serialConProtocol.send(StickAction.R_TOP_LEFT, PressAction.PRESS);
    Thread.sleep(500);
    serialConProtocol.send(StickAction.R_TOP_RIGHT, PressAction.PRESS);
    Thread.sleep(500);
    serialConProtocol.send(StickAction.R_BOTTOM_LEFT, PressAction.PRESS);
    Thread.sleep(500);
    serialConProtocol.send(StickAction.R_BOTTOM_RIGHT, PressAction.PRESS);
    Thread.sleep(1000);
    System.exit(0);
  }

}

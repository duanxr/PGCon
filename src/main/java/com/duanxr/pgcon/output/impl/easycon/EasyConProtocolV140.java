package com.duanxr.pgcon.output.impl.easycon;

import com.duanxr.pgcon.output.action.ButtonAction;
import com.duanxr.pgcon.output.action.StickAction;
import com.duanxr.pgcon.output.api.Protocol;
import com.duanxr.pgcon.output.api.SerialPort;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * @author 段然 2021/12/7
 */
@Slf4j
public class EasyConProtocolV140 implements Protocol {

  private final SerialPort<byte[]> serialPort;

  private final byte[] currentBytes = getDefault();

  @SneakyThrows
  public EasyConProtocolV140(String portName) {
    serialPort = new EasyConSerialPort(portName, 9600);
  }

  @Override
  public synchronized void hold(ButtonAction buttonType) {
    int command = buttonType.getEcCommand();
    byte[] bytes = currentBytes;
    if (buttonType.isHat()) {
      bytes[2] = (byte) command;
    } else {
      if (command <= 0x80) {
        bytes[1] |= (byte) command;
      } else {
        bytes[0] |= (byte) (command >> 8);
      }
    }
    serialPort.sendCommand(bytes);
  }

  @Override
  public synchronized void release(ButtonAction buttonType) {
    int command = buttonType.getEcCommand();
    byte[] bytes = currentBytes;
    if (buttonType.isHat()) {
      bytes[2] = 8;
    } else {
      if (command <= 0x80) {
        bytes[1] &= ~(byte) command;
      } else {
        bytes[0] &= ~(byte) (command >> 8);
      }
    }
    serialPort.sendCommand(bytes);
  }

  @Override
  public synchronized void set(StickAction action) {
    byte[] bytes = currentBytes;
    if (action.isLeft()) {
      bytes[3] = action.getActionX().getEcCommand();
      bytes[4] = action.getActionY().getEcCommand();
    } else {
      bytes[5] = action.getActionX().getEcCommand();
      bytes[6] = action.getActionY().getEcCommand();
    }
    serialPort.sendCommand(bytes);
  }

  @Override
  public void set(boolean isLeft, double degrees) {
    byte[] bytes = currentBytes;
    int ix = 128;
    int iy = 128;
    ix += 127 * -Math.sin(Math.toRadians(-degrees));
    iy += 127 * -Math.cos(Math.toRadians(-degrees));
    byte x = (byte) ix;
    byte y = (byte) iy;
    if (isLeft) {
      bytes[3] = x;
      bytes[4] = y;
    } else {
      bytes[5] = x;
      bytes[6] = y;
    }
    serialPort.sendCommand(bytes);
  }

  @Override
  public void clear() {
    serialPort.sendCommand(getDefault());
    serialPort.close();
  }

  @Override
  public boolean isConnected() {
    return serialPort.checkConnection();
  }


  private byte[] getDefault() {
    return new byte[]{0, 0, 8, -128, -128, -128, -128};
  }

}

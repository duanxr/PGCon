package com.duanxr.pgcon.output.impl.ec;

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
public class EasyConProtocolV147 implements Protocol {

  private final SerialPort<byte[]> serialPort;

  private final byte[] currentBytes = getDefault();

  @SneakyThrows
  public EasyConProtocolV147(String portName) {
    serialPort = new EasyConSerialPort(portName, 115200);
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
      bytes[3] = action.getActionX().getScCommand();
      bytes[4] = action.getActionY().getScCommand();
    } else {
      bytes[5] = action.getActionX().getScCommand();
      bytes[6] = action.getActionY().getScCommand();
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

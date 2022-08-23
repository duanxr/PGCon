package com.duanxr.pgcon.output.impl.easycon;

import com.duanxr.pgcon.output.action.NintendoSwitchStandardButton;
import com.duanxr.pgcon.output.action.NintendoSwitchStandardStick;
import com.duanxr.pgcon.output.action.Sticks;
import com.duanxr.pgcon.output.api.Button;
import com.duanxr.pgcon.output.api.Protocol;
import com.duanxr.pgcon.output.api.SerialPort;
import com.duanxr.pgcon.output.api.Stick;
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
  public synchronized void hold(Button buttonType) {
    int command = buttonType.getCommandEasyCon();
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
  public synchronized void release(Button buttonType) {
    int command = buttonType.getCommandEasyCon();
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
  public synchronized void set(Stick action) {
    byte[] bytes = currentBytes;
    if (action.getStick() == Sticks.LEFT) {
      bytes[3] = action.getXCommandEasyCon();
      bytes[4] = action.getYCommandEasyCon();
    } else {
      bytes[5] = action.getXCommandEasyCon();
      bytes[6] = action.getYCommandEasyCon();
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

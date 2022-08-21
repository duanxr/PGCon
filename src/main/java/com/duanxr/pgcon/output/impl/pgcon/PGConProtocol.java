package com.duanxr.pgcon.output.impl.pgcon;

import com.duanxr.pgcon.output.api.Protocol;
import com.duanxr.pgcon.output.api.SerialPort;
import com.duanxr.pgcon.output.action.ButtonAction;
import com.duanxr.pgcon.output.action.StickAction;
import lombok.SneakyThrows;

/**
 * @author 段然 2021/12/7
 */
public class PGConProtocol implements Protocol {

  private final SerialPort<Integer> serialPort;

  @SneakyThrows
  public PGConProtocol(String portName) {
    serialPort = new PGConSerialPort(portName, 9600);
  }

  @Override
  public void hold(ButtonAction buttonType) {
    send(buttonType.getPcHoldCommand());
  }

  @Override
  public void release(ButtonAction buttonType) {
    send(buttonType.getPcReleaseCommand());
  }

  @Override
  public void set(StickAction action) {
    send(action.getActionX().getPgcCommand());
    send(action.getActionY().getPgcCommand());
  }

  @Override
  public void set(boolean isLeft, double degrees) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void clear() {
    send(49);
    serialPort.close();
  }

  @Override
  public boolean isConnected() {
      return serialPort.checkConnection();
  }

  private void send(int pgcCommand) {
    serialPort.sendCommand(pgcCommand);
  }

}

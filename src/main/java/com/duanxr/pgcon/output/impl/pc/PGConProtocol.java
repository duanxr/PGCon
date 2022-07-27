package com.duanxr.pgcon.output.impl.pc;

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
  public PGConProtocol(String portName, int baudRate) {
    serialPort = new PGConSerialPort(portName, baudRate);
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
  public void clear() {
    send(49);
  }

  @Override
  public String getName() {
    return "PGCon";
  }

  private void send(int pgcCommand) {
    serialPort.sendCommand(pgcCommand);
  }

}

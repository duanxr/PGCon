package com.duanxr.pgcon.output.api;

/**
 * @author 段然 2021/12/7
 */
public interface SerialPort<C> {

  void sendCommand(C command);

  boolean checkConnection();

  void close();

}

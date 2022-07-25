package com.duanxr.pgcon.output.api;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import purejavacomm.CommPortIdentifier;

/**
 * @author 段然 2021/12/7
 */
public interface SerialPort<C> {

  void sendCommand(C command);

  boolean checkConnection();

}

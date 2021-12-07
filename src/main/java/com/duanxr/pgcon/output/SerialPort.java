package com.duanxr.pgcon.output;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import purejavacomm.CommPortIdentifier;

/**
 * @author 段然 2021/12/7
 */
public interface SerialPort<C> {

  boolean sendCommand(C command);

  boolean checkConnection();

  static List<String> getSerialList() {
    Enumeration<?> allPorts = CommPortIdentifier.getPortIdentifiers();
    List<String> portIdentifierList = new ArrayList<>();
    while (allPorts.hasMoreElements()) {
      CommPortIdentifier commPortIdentifier = (CommPortIdentifier) allPorts.nextElement();
      portIdentifierList.add(commPortIdentifier.getName());
    }
    return portIdentifierList;
  }

}

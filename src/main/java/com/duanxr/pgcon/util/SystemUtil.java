package com.duanxr.pgcon.util;

import com.github.sarxos.webcam.Webcam;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;
import purejavacomm.CommPortIdentifier;

/**
 * @author 段然 2022/7/25
 */
public class SystemUtil {
  public static List<String> getCameraList() {
    List<Webcam> list = Webcam.getWebcams();
    return list.isEmpty() ? Collections.emptyList() : list.stream().map(Webcam::getName).collect(
        Collectors.toList());
  }
  public static List<String> getSerialList() {
    Enumeration<?> allPorts = CommPortIdentifier.getPortIdentifiers();
    List<String> portIdentifierList = new ArrayList<>();
    while (allPorts.hasMoreElements()) {
      CommPortIdentifier commPortIdentifier = (CommPortIdentifier) allPorts.nextElement();
      portIdentifierList.add(commPortIdentifier.getName());
    }
    return portIdentifierList;
  }
}

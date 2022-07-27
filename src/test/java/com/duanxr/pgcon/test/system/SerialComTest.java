package com.duanxr.pgcon.test.system;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import purejavacomm.CommPortIdentifier;
import purejavacomm.NoSuchPortException;
import purejavacomm.PortInUseException;
import purejavacomm.SerialPort;
import purejavacomm.UnsupportedCommOperationException;


public class SerialComTest {

  public static void main(String[] args) throws Exception {
    CommPortIdentifier portIdentifier = null;
    Enumeration<?> allPorts
        = CommPortIdentifier.getPortIdentifiers();
    while (allPorts.hasMoreElements()) {
      portIdentifier
          = (CommPortIdentifier) allPorts.nextElement();
      System.out.println("portName:" + portIdentifier.getName());
    }
    CommPortIdentifier identifier = null;
    try {
      identifier = CommPortIdentifier.getPortIdentifier("COM8");
    } catch (NoSuchPortException e) {
      e.printStackTrace();
    }
    SerialPort serialPort = null;
    try {
      serialPort
          = (SerialPort) identifier.open("SerialControllerOutput", 1000);
    } catch (PortInUseException e) {
      e.printStackTrace();
    }
    try {
      serialPort.setSerialPortParams(
          9600,
          SerialPort.DATABITS_8,
          SerialPort.STOPBITS_1,
          SerialPort.PARITY_NONE
      );
    } catch (UnsupportedCommOperationException e) {
      e.printStackTrace();
    }
    OutputStream outputStream = serialPort.getOutputStream();
    InputStream inputStream = serialPort.getInputStream();

    outputStream.write(0);
    outputStream.flush();
    Thread.sleep(1000);
    byte[] cache = new byte[1024];
    int availableBytes = inputStream.available();
    while (availableBytes > 0) {
      inputStream.read(cache);
      for (int j = 0; j < cache.length && j < availableBytes; j++) {
        System.out.print((char) cache[j]);
      }
      System.out.println();
      availableBytes = inputStream.available();
    }
    Thread.sleep(100);
    for (int i = 1; i < 50; i++) {
      outputStream.write(i);
      outputStream.flush();
      Thread.sleep(200);
    }
    System.out.println("6666");
    outputStream.close();
    serialPort.close();
  }

  private static int get(int i) {
    return i + 1;
  }
}
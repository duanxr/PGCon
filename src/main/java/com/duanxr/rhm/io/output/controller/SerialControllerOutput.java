package com.duanxr.rhm.io.output.controller;

import static com.duanxr.rhm.config.ConstantConfig.OUTPUT_SERIAL_BAUD_RATE;

import com.duanxr.rhm.exception.OutputInvalidException;
import com.duanxr.rhm.io.output.ControllerOutput;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import purejavacomm.CommPortIdentifier;
import purejavacomm.SerialPort;

/**
 * @author Duanran 2019/12/18
 */
@Slf4j
public class SerialControllerOutput implements ControllerOutput {

  private final Executor listenThread;
  private String portName;
  private CommPortIdentifier commPortIdentifier;
  private SerialPort serialPort;
  private OutputStream outputStream;
  private InputStream inputStream;
  private Semaphore semaphore;

  public SerialControllerOutput(String portName) throws Exception {
    this.portName = portName;
    semaphore = new Semaphore(0);
    listenThread = Executors.newSingleThreadExecutor();
    listenThread.execute(this::listen);
    commPortIdentifier = CommPortIdentifier.getPortIdentifier(portName);
    serialPort = (SerialPort) commPortIdentifier.open("SerialControllerOutput", 500);
    serialPort.setSerialPortParams(
        OUTPUT_SERIAL_BAUD_RATE,
        SerialPort.DATABITS_8,
        SerialPort.STOPBITS_1,
        SerialPort.PARITY_NONE
    );
    outputStream = serialPort.getOutputStream();
    inputStream = serialPort.getInputStream();
    validTest();
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

  private void listen() {
    while (true) {
      try {
        semaphore.acquire();
        if (inputStream != null && inputStream.available() > 0) {
          byte[] bytes = IOUtils.toByteArray(inputStream, inputStream.available());
          log.info("listen:{}", new String(bytes));
        }
      } catch (Exception e) {
        log.error("listen exception.", e);
      }
    }
  }

  private synchronized void validTest() throws Exception {
    outputStream.write(0);
    outputStream.write(49);
    outputStream.flush();
    Thread.sleep(100);
    if (inputStream.available() == 0) {
      throw new OutputInvalidException();
    } else {
      byte[] bytes = IOUtils.toByteArray(inputStream, inputStream.available());
      log.info("output valid:{}", new String(bytes));
    }
  }

  @Override
  public void output(int command) {
    try {
      //log.info("c:{}",command);
      outputStream.write(command);
      outputStream.flush();
    } catch (Exception e) {
      log.error("output exception.", e);
    }
  }

  @Override
  protected void finalize() throws Throwable {
    try {
      if (outputStream != null) {
        outputStream.close();
      }
    } catch (Exception ignored) {
    }
    try {
      if (outputStream != null) {
        inputStream.close();
      }
    } catch (Exception ignored) {
    }
    super.finalize();
  }

}


package com.duanxr.pgcon.output.impl.pgcon;


import com.duanxr.pgcon.output.api.SerialPort;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import javax.annotation.PreDestroy;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import purejavacomm.CommPortIdentifier;

/**
 * @author Duanran 2019/12/18
 */
@Slf4j
public class PGConSerialPort implements SerialPort<Integer> {

  private final OutputStream outputStream;
  private final InputStream inputStream;
  private final Semaphore semaphore;
  private final purejavacomm.SerialPort serialPort;

  public PGConSerialPort(String portName, int baudRate) throws Exception {
    semaphore = new Semaphore(0);
    CommPortIdentifier commPortIdentifier = CommPortIdentifier.getPortIdentifier(portName);
    serialPort = (purejavacomm.SerialPort) commPortIdentifier.open("PGCon",
        1000);
    serialPort.setSerialPortParams(
        baudRate,
        purejavacomm.SerialPort.DATABITS_8,
        purejavacomm.SerialPort.STOPBITS_1,
        purejavacomm.SerialPort.PARITY_NONE
    );
    outputStream = serialPort.getOutputStream();
    inputStream = serialPort.getInputStream();
    Executors.newSingleThreadExecutor().execute(this::listen);
  }

  @Override
  @SneakyThrows
  public synchronized void sendCommand(Integer command) {
    outputStream.write(command);
    outputStream.flush();
  }

  @Override
  @SneakyThrows
  public synchronized boolean checkConnection() {
    outputStream.write(0);
    outputStream.write(49);
    outputStream.flush();
    Thread.sleep(100);
    return inputStream.available() == 0;
  }

  @SneakyThrows
  private void listen() {
    while (!Thread.currentThread().isInterrupted()) {
      semaphore.acquire();
      if (inputStream != null && inputStream.available() > 0) {
        byte[] bytes = IOUtils.toByteArray(inputStream, inputStream.available());
      }
    }
  }

  @PreDestroy
  @SneakyThrows
  public void close() {
    try {
      if (outputStream != null) {
        outputStream.close();
      }
      if (outputStream != null) {
        inputStream.close();
      }
      serialPort.close();
    } catch (Exception ignored) {
    }
  }

}


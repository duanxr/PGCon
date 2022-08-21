package com.duanxr.pgcon.output.impl.easycon;


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
public class EasyConSerialPort implements SerialPort<byte[]> {

  private final purejavacomm.SerialPort serialPort;
  private final OutputStream outputStream;
  private final InputStream inputStream;
  private final Semaphore semaphore;

  public EasyConSerialPort(String portName, int baudRate) throws Exception {
    semaphore = new Semaphore(0);
    CommPortIdentifier commPortIdentifier = CommPortIdentifier.getPortIdentifier(portName);
    serialPort = (purejavacomm.SerialPort) commPortIdentifier.open("SerialCon",
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
  public synchronized void sendCommand(byte[] command) {
    outputStream.write(convert(command));
    outputStream.flush();
  }

  private byte[] convert(byte[] command) {
    byte[] packet = new byte[8];
    int i = 0;
    long n = 0;
    int bits = 0;
    for (byte b : command) {
      n = (n << 8) | (b & 0xFF);
      bits += 8;
      while (bits >= 7) {
        bits -= 7;
        packet[i++] = ((byte) (n >>> bits));
        n &= (1L << bits) - 1;
      }
    }
    packet[7] = (byte) (packet[7] | 0x80);
    return packet;
  }

  @Override
  @SneakyThrows
  public synchronized boolean checkConnection() {
    outputStream.write(0xA5);
    outputStream.flush();
    Thread.sleep(50);
    outputStream.write(0x84);
    outputStream.flush();
    Thread.sleep(50);
    if (inputStream != null && inputStream.available() > 0) {
      byte[] bytes = IOUtils.toByteArray(inputStream, inputStream.available());
      return true;
    }
    return false;
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


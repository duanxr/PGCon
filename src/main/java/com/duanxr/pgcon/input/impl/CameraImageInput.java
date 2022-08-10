package com.duanxr.pgcon.input.impl;


import com.duanxr.pgcon.config.InputConfig;
import com.duanxr.pgcon.exception.GuiAlertException;
import com.duanxr.pgcon.input.api.ImageInput;
import com.github.sarxos.webcam.Webcam;
import com.google.common.base.Strings;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 * @author Duanran 2019/12/16
 */
public class CameraImageInput implements ImageInput<BufferedImage> {

  private volatile Webcam camera;

  public CameraImageInput(String device, InputConfig inputConfig) {
    camera = openCamera(device, inputConfig.getWidth(), inputConfig.getHeight());
  }

  private Webcam openCamera(String device, Integer width, Integer height) {
    if (!Strings.isNullOrEmpty(device)) {
      Webcam webcam;
      try {
        int which = Integer.parseInt(device);
        webcam = Webcam.getWebcams().get(which);
      } catch (NumberFormatException ignore) {
        webcam = findDevice(device);
      }
      if (webcam != null) {
        if (width >= 0 && height >= 0) {
          adjustResolution(webcam, width, height);
        }
        webcam.open();
        return webcam;
      }
    }
    throw new GuiAlertException("Can't open webcam with ID or name at " + device);
  }

  private Webcam findDevice(String device) {
    List<Webcam> found = Webcam.getWebcams();
    for (Webcam cam : found) {
      if (cam.getName().contains(device)) {
        return cam;
      }
    }
    return null;
  }

  public void adjustResolution(Webcam webcam, int desiredWidth, int desiredHeight) {
    Dimension[] sizes = webcam.getCustomViewSizes();
    Dimension match = null;
    for (Dimension dimension : sizes) {
      if (dimension.width == desiredWidth && dimension.height == desiredHeight) {
        match = dimension;
        break;
      }
    }
    if (match == null) {
      match = new Dimension(desiredWidth, desiredHeight);
      webcam.setCustomViewSizes(match);
    }
    webcam.setViewSize(match);
  }

  @Override
  public BufferedImage read() {
    if (camera != null && camera.isOpen()) {
      return camera.getImage();
    }
    return null;
  }

  public void close() {
    camera.close();
    camera = null;
  }
}

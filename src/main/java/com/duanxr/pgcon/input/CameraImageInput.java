package com.duanxr.pgcon.input;

import com.github.sarxos.webcam.Webcam;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

/**
 * @author Duanran 2019/12/16
 */
public class CameraImageInput implements ImageInput {

  private VideoCapture cameraCapture;

  public CameraImageInput(int cameraIndex) {
    cameraCapture = new VideoCapture(cameraIndex);
  }

  public static List<String> getCameraList() {
    List<Webcam> list = Webcam.getWebcams();
    return list.isEmpty() ? Collections.emptyList() : list.stream().map(Webcam::getName).collect(
        Collectors.toList());
  }

  public BufferedImage loadInput(Mat buffer) {
    cameraCapture.read(buffer);
    return null;
  }

  @Override
  public BufferedImage read() {
    return null;
  }
}

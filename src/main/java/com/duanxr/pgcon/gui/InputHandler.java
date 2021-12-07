/*
package com.duanxr.pgcon.gui;

import java.io.IOException;
import javafx.scene.effect.ImageInput;
import nu.pattern.OpenCV;
import org.opencv.core.Mat;
import org.springframework.stereotype.Service;

*/
/**
 * @author Duanran 2019/12/16
 *//*

@Service
public class InputHandler {

  private ImageInput imageInput;

  private Mat screenshotMat;

  private Mat streamMat;

  public InputHandler() {
    OpenCV.loadShared();
    this.imageInput = getNoInputHandler();
    this.screenshotMat = new Mat();
    this.streamMat = new Mat();
  }

  public static ImageInput getNoInputHandler() {
    try {
      return new StaticImageInput("/img/no_input.bmp");
    } catch (IOException e) {
      return null;
    }
  }

  public void setNewInput(ImageInput imageInput) {
    this.imageInput = imageInput;
    this.getStream();
    this.getScreenshot(true);
  }

  public synchronized Mat getStream() {
    this.imageInput.loadInput(streamMat);
    MatUtil.resize(streamMat);
    return streamMat;
  }

  public Mat getScreenshot(boolean needRefresh) {
    if (needRefresh) {
      synchronized (this) {
        this.streamMat.copyTo(screenshotMat);
      }
    }
    return screenshotMat;
  }

}
*/

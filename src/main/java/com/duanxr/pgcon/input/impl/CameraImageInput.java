package com.duanxr.pgcon.input.impl;

import boofcv.io.image.SimpleImageSequence;
import boofcv.io.wrapper.DefaultMediaManager;
import boofcv.struct.image.GrayF32;
import boofcv.struct.image.ImageType;
import com.duanxr.pgcon.config.GuiConfig;
import com.duanxr.pgcon.input.api.ImageInput;
import com.github.sarxos.webcam.Webcam;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Duanran 2019/12/16
 */
public class CameraImageInput implements ImageInput<BufferedImage> {
  private SimpleImageSequence<GrayF32> camera;

  public CameraImageInput(String device, GuiConfig guiConfig) {
    camera = DefaultMediaManager.INSTANCE.openCamera(device, guiConfig.getWidth(),
        guiConfig.getHeight(), ImageType.single(GrayF32.class));
    camera.setLoop(false);
  }

  @Override
  public BufferedImage read() {
    if (camera == null || !camera.hasNext()) {
      return null;
    }
    return camera.getGuiImage();
  }

  public void close() {
    camera.close();
    camera = null;
  }
}
package com.duanxr.pgcon.input;

import boofcv.io.image.SimpleImageSequence;
import boofcv.io.wrapper.DefaultMediaManager;
import boofcv.struct.image.GrayF32;
import boofcv.struct.image.ImageType;
import com.duanxr.pgcon.util.ConstantConfig;
import java.awt.image.BufferedImage;
import lombok.SneakyThrows;
import net.coobird.thumbnailator.Thumbnails;

/**
 * @author Duanran 2019/12/16
 */
public class CameraImageInput implements StreamImageInput<BufferedImage> {

  private SimpleImageSequence<GrayF32> camera;

  public CameraImageInput(String device) {
    camera = DefaultMediaManager.INSTANCE.openCamera(device, (int) ConstantConfig.SIZE.width,
        (int) ConstantConfig.SIZE.height, ImageType.single(GrayF32.class));
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

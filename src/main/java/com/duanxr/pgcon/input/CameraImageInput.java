package com.duanxr.pgcon.input;

import static com.duanxr.pgcon.util.ConstantConfig.INPUT_VIDEO_FRAME_CACHE_SIZE;

import boofcv.io.image.SimpleImageSequence;
import boofcv.io.wrapper.DefaultMediaManager;
import boofcv.struct.image.GrayF32;
import boofcv.struct.image.ImageType;
import com.duanxr.pgcon.util.ConstantConfig;
import java.awt.image.BufferedImage;

/**
 * @author Duanran 2019/12/16
 */
public class CameraImageInput implements StreamImageInput<BufferedImage> {

  private SimpleImageSequence<GrayF32> camera;
  private final BufferedImage[] frames;
  private int framesIndex;

  public CameraImageInput(String device) {
    camera = DefaultMediaManager.INSTANCE.openCamera(device, (int) ConstantConfig.SIZE.width,
        (int) ConstantConfig.SIZE.height, ImageType.single(GrayF32.class));
    frames = new BufferedImage[INPUT_VIDEO_FRAME_CACHE_SIZE];
    framesIndex = 0;
  }

  @Override
  public synchronized BufferedImage[] readCache() {
    BufferedImage[] cachedImages = new BufferedImage[frames.length];
    for (int i = framesIndex; i < cachedImages.length + framesIndex; i++) {
      cachedImages[i - framesIndex] = frames[i % frames.length];
    }
    return cachedImages;
  }

  @Override
  public synchronized BufferedImage read() {
    if (!camera.hasNext()) {
      return frames[framesIndex == 0 ? frames.length - 1 : framesIndex - 1];
    }
    BufferedImage image = camera.getGuiImage();
    camera.getGuiImage();
    if (framesIndex == frames.length) {
      framesIndex = 0;
    }
    frames[framesIndex++] = image;
    return image;
  }

  public synchronized void close() {
    camera.close();
    camera = null;
  }
}

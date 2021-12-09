package com.duanxr.pgcon.input;

import static com.duanxr.pgcon.util.ConstantConfig.INPUT_VIDEO_FRAME_CACHE_SIZE;

import boofcv.io.image.SimpleImageSequence;
import boofcv.io.wrapper.DefaultMediaManager;
import boofcv.struct.image.GrayF32;
import boofcv.struct.image.ImageType;
import com.duanxr.pgcon.util.ConstantConfig;

/**
 * @author Duanran 2019/12/16
 */
public class CameraImageInput implements StreamImageInput<GrayF32> {

  private SimpleImageSequence<GrayF32> camera;
  private final GrayF32[] frames;
  private int framesIndex;

  public CameraImageInput(String device) {
    camera = DefaultMediaManager.INSTANCE.openCamera(device, (int) ConstantConfig.SIZE.width,
        (int) ConstantConfig.SIZE.height, ImageType.single(GrayF32.class));
    frames = new GrayF32[INPUT_VIDEO_FRAME_CACHE_SIZE];
    framesIndex = 0;
  }

  @Override
  public synchronized GrayF32[] readCache() {
    GrayF32[] cachedImages = new GrayF32[frames.length];
    for (int i = framesIndex; i < cachedImages.length + framesIndex; i++) {
      cachedImages[i - framesIndex] = frames[i % frames.length];
    }
    return cachedImages;
  }

  @Override
  public synchronized GrayF32 read() {
    GrayF32 image = camera.next();
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

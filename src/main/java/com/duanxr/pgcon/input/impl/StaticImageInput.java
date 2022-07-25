package com.duanxr.pgcon.input.impl;

import com.duanxr.pgcon.input.api.ImageInput;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Objects;
import javax.imageio.ImageIO;
import lombok.SneakyThrows;

/**
 * @author Duanran 2019/12/16
 */
public class StaticImageInput implements ImageInput<BufferedImage> {

  private BufferedImage image;

  @SneakyThrows
  public StaticImageInput(File file) {
    image = ImageIO.read(file);
  }

  @SneakyThrows
  public StaticImageInput(String resourcePath) {
    image = ImageIO.read(Objects.requireNonNull(getClass().getResource(resourcePath)));
  }

  @Override
  public BufferedImage read() {
    return image;
  }

  @Override
  public void close() {
    image = null;
  }

}

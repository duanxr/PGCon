package com.duanxr.pgcon.input;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Objects;
import javax.imageio.ImageIO;
import lombok.SneakyThrows;

/**
 * @author Duanran 2019/12/16
 */
public class StaticImageInput implements ImageInput<BufferedImage> {

  private final BufferedImage image;

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

}

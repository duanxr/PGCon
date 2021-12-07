package com.duanxr.pgcon.input;

import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import lombok.SneakyThrows;

/**
 * @author Duanran 2019/12/16
 */
public class StaticImageInput implements ImageInput {

  private BufferedImage bufferedImage;

  @SneakyThrows
  public StaticImageInput(File file) {
    bufferedImage = ImageIO.read(file);
  }

  @SneakyThrows
  public StaticImageInput(String resourcePath) {
    bufferedImage = ImageIO.read(getClass().getResource(resourcePath));
  }

  @Override
  public BufferedImage read() {
    return bufferedImage;
  }

}

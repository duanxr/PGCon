package com.duanxr.pgcon.test;

import com.duanxr.pgcon.util.DebugUtil;
import com.duanxr.pgcon.util.ImageResizeUtil;
import com.duanxr.pgcon.util.ImageUtil;
import com.duanxr.pgcon.util.MatUtil;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import lombok.SneakyThrows;
import nu.pattern.OpenCV;
import org.opencv.core.Mat;

/**
 * @author 段然 2022/8/10
 */
public class ImageUtilTest {

  /**
   * game.png channels = 3
   * ImageUtil.bufferedImageToMat cost 1177 ms
   * ImageUtil.matToBufferedImage cost 1643 ms
   * ImageResizeUtil.resize(mat) x2 cost 27152 ms
   * ImageResizeUtil.resize(image) x2 cost 19903 ms
   * ImageResizeUtil.resize(mat) /2 cost 5327 ms
   * ImageResizeUtil.resize(image) /2 cost 7246 ms
   * MatUtil.toGrayMat cost 9 ms
   *
   * game.png channels = 1
   * MatUtil.toGrayMat cost 11 ms
   * ImageUtil.matToBufferedImage cost 860 ms
   * ImageResizeUtil.resize(mat) x2 cost 15538 ms
   * ImageResizeUtil.resize(image) x2 cost 13692 ms
   * ImageResizeUtil.resize(mat) /2 cost 2136 ms
   * ImageResizeUtil.resize(image) /2 cost 1707 ms
   */
  @SneakyThrows
  public static void main(String[] args) {
    OpenCV.loadLocally();
    BufferedImage image3 = ImageIO.read(new File("src\\test\\java\\resources\\game.png"));
    testC3(image3);
    //testC1(image3);
    BufferedImage image4 = ImageIO.read(new File("src\\test\\java\\resources\\png1.png"));
    //testC4(image4);
  }

  private static void testC1(BufferedImage bufferedImage) {
    //c=1
    long start = System.currentTimeMillis();
    Mat mat = ImageUtil.bufferedImageToMat(bufferedImage);
    for (int i = 0; i < 1000; i++) {
      mat = MatUtil.toGrayMat(mat);
    }
    long end = System.currentTimeMillis();
    System.out.printf("MatUtil.toGrayMat cost %d ms\n", end - start);

    start = System.currentTimeMillis();
    BufferedImage image = ImageUtil.matToBufferedImage(mat);
    for (int i = 0; i < 1000; i++) {
      ImageUtil.matToBufferedImage(mat);
    }
    end = System.currentTimeMillis();
    System.out.printf("ImageUtil.matToBufferedImage cost %d ms\n", end - start);

    start = System.currentTimeMillis();
    for (int i = 0; i < 1000; i++) {
      ImageResizeUtil.resize(mat, mat.width() * 2, mat.height() * 2);
    }
    end = System.currentTimeMillis();
    System.out.printf("ImageResizeUtil.resize(mat) x2 cost %d ms\n", end - start);

    start = System.currentTimeMillis();
    for (int i = 0; i < 1000; i++) {
      ImageResizeUtil.resize(image, image.getWidth() * 2, image.getHeight() * 2);
    }
    end = System.currentTimeMillis();
    System.out.printf("ImageResizeUtil.resize(image) x2 cost %d ms\n", end - start);

    start = System.currentTimeMillis();
    for (int i = 0; i < 1000; i++) {
      ImageResizeUtil.resize(mat, mat.width() / 2, mat.height() / 2);
    }
    end = System.currentTimeMillis();
    System.out.printf("ImageResizeUtil.resize(mat) /2 cost %d ms\n", end - start);

    start = System.currentTimeMillis();
    for (int i = 0; i < 1000; i++) {
      ImageResizeUtil.resize(image, image.getWidth() / 2, image.getHeight() / 2);
    }
    end = System.currentTimeMillis();
    System.out.printf("ImageResizeUtil.resize(image) /2 cost %d ms\n", end - start);
  }

  private static void testC3(BufferedImage bufferedImage) {
    //c=3
    Mat mat = ImageUtil.bufferedImageToMat(bufferedImage);
    long start = System.currentTimeMillis();
    for (int i = 0; i < 1000; i++) {
      //ImageUtil.bufferedImageToMat(bufferedImage);
    }
    long end = System.currentTimeMillis();
    System.out.printf("ImageUtil.bufferedImageToMat cost %d ms\n", end - start);

    BufferedImage image = ImageUtil.matToBufferedImage(mat);
    start = System.currentTimeMillis();
    for (int i = 0; i < 1000; i++) {
      //ImageUtil.matToBufferedImage(mat);
    }
    end = System.currentTimeMillis();
    System.out.printf("ImageUtil.matToBufferedImage cost %d ms\n", end - start);

    Mat resize1 = ImageResizeUtil.resize(mat, mat.width() * 2, mat.height() * 2);
    start = System.currentTimeMillis();
    for (int i = 0; i < 1000; i++) {
      //ImageResizeUtil.resize(mat, mat.width() * 2, mat.height() * 2);
    }
    end = System.currentTimeMillis();
    System.out.printf("ImageResizeUtil.resize(mat) x2 cost %d ms\n", end - start);

    BufferedImage resize = ImageResizeUtil.resize(image, image.getWidth() * 2,
        image.getHeight() * 2);
    start = System.currentTimeMillis();
    for (int i = 0; i < 1000; i++) {
      //ImageResizeUtil.resize(image, image.getWidth() * 2, image.getHeight() * 2);
    }
    end = System.currentTimeMillis();
    System.out.printf("ImageResizeUtil.resize(image) x2 cost %d ms\n", end - start);

    start = System.currentTimeMillis();
    for (int i = 0; i < 1000; i++) {
      //ImageResizeUtil.resize(mat, mat.width() / 2, mat.height() / 2);
    }
    end = System.currentTimeMillis();
    System.out.printf("ImageResizeUtil.resize(mat) /2 cost %d ms\n", end - start);

    start = System.currentTimeMillis();
    for (int i = 0; i < 1000; i++) {
      ImageResizeUtil.resize(image, image.getWidth() / 2, image.getHeight() / 2);
    }
    end = System.currentTimeMillis();
    System.out.printf("ImageResizeUtil.resize(image) /2 cost %d ms\n", end - start);

  }

  private static void testC4(BufferedImage bufferedImage) {
    Mat mat = ImageUtil.bufferedImageToMat(bufferedImage);
    BufferedImage image = ImageUtil.matToBufferedImage(mat);
    Mat resize = ImageResizeUtil.resize(mat, mat.width() / 1, mat.height() / 1);
    BufferedImage resize1 = ImageResizeUtil.resize(image, image.getWidth() / 1,
        image.getHeight() / 1);
    System.out.printf("");
  }

}

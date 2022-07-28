package com.duanxr.pgcon.util;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import lombok.SneakyThrows;
import net.coobird.thumbnailator.Thumbnails;

/**
 * @author 段然 2022/7/27
 */
public class ImageResizeUtil {

  /**
   * resize cost 95 ms
   */
  @SneakyThrows
  public static BufferedImage resizeV1(BufferedImage original, int newWidth, int newHeight) {
    return Thumbnails.of(original).size(newWidth, newHeight).asBufferedImage();
  }

  /**
   * resize cost 6 ms, the fastest so far,
   * <a
   * href="https://web.archive.org/web/20170809062128/http://willperone.net/Code/codescaling.php">...</a>
   * <a
   * href="https://stackoverflow.com/questions/42615441/convert-2d-pixel-array-into-bufferedimage">...</a>
   * <a href="https://stackoverflow.com/questions/6524196/java-get-pixel-array-from-image">...</a>
   */
  @SneakyThrows
  public static BufferedImage resizeV2(BufferedImage original, int newWidth, int newHeight) {
    int[] rawInput = convertBufferedImageTo1D(original);
    int[] rawOutput = new int[newWidth * newHeight];
    // YD compensates for the x loop by subtracting the width back out
    int YD = (original.getHeight() / newHeight) * original.getWidth() - original.getWidth();
    int YR = original.getHeight() % newHeight;
    int XD = original.getWidth() / newWidth;
    int XR = original.getWidth() % newWidth;
    int outOffset = 0;
    int inOffset = 0;
    for (int y = newHeight, YE = 0; y > 0; y--) {
      for (int x = newWidth, XE = 0; x > 0; x--) {
        rawOutput[outOffset++] = rawInput[inOffset];
        inOffset += XD;
        XE += XR;
        if (XE >= newWidth) {
          XE -= newWidth;
          inOffset++;
        }
      }
      inOffset += YD;
      YE += YR;
      if (YE >= newHeight) {
        YE -= newHeight;
        inOffset += original.getWidth();
      }
    }
    return convert1DToBufferedImage(rawOutput, newWidth, newHeight);
  }

  private static int[] convertBufferedImageTo1D(BufferedImage image) {
    byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
    boolean hasAlphaChannel = image.getAlphaRaster() != null;
    int[] result = new int[pixels.length];
    int index = 0;
    if (hasAlphaChannel) {
      int pixelLength = 4;
      for (int pixel = 0; pixel + 3 < pixels.length; pixel += pixelLength) {
        int argb = 0;
        argb += (((int) pixels[pixel] & 0xff) << 24); // alpha
        argb += ((int) pixels[pixel + 1] & 0xff); // blue
        argb += (((int) pixels[pixel + 2] & 0xff) << 8); // green
        argb += (((int) pixels[pixel + 3] & 0xff) << 16); // red
        result[index++] = argb;
      }
    } else {
      int pixelLength = 3;
      for (int pixel = 0; pixel + 2 < pixels.length; pixel += pixelLength) {
        int argb = 0;
        argb += 16777216; // 255 alpha
        argb += ((int) pixels[pixel] & 0xff); // blue
        argb += (((int) pixels[pixel + 1] & 0xff) << 8); // green
        argb += (((int) pixels[pixel + 2] & 0xff) << 16); // red
        result[index++] = argb;
      }
    }
    return result;
  }

  private static BufferedImage convert1DToBufferedImage(int[] pixelData, int width, int height) {
    BufferedImage outputImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);
    int[] outputImagePixelData = ((DataBufferInt) outputImage.getRaster()
        .getDataBuffer()).getData();
    System.arraycopy(pixelData, 0, outputImagePixelData, 0, outputImagePixelData.length);
    return outputImage;
  }
}

package com.duanxr.pgcon.util;

import java.awt.image.BufferedImage;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBufferByte;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

/**
 * @author 段然 2022/7/27
 */
@UtilityClass
public class ImageResizeUtil {
  private final static int[] DEFAULT_OFFSET_1 = {0};
  private final static int[] DEFAULT_OFFSET_3 = {2, 1, 0};
  private final static int[] DEFAULT_OFFSET_4 = {3, 2, 1, 0};
  private static final int HEX_255 = 0xff;

  /**
   * resize cost 3.3 ms, the fastest so far, <a
   * href="https://web.archive.org/web/20170809062128/http://willperone.net/Code/codescaling.php">...</a>
   * <a
   * href="https://stackoverflow.com/questions/42615441/convert-2d-pixel-array-into-bufferedimage">...</a>
   * <a href="https://stackoverflow.com/questions/6524196/java-get-pixel-array-from-image">...</a>
   */
  @SneakyThrows
  public static BufferedImage resize(BufferedImage image, int newWidth, int newHeight) {
    int channels = image.getSampleModel().getNumBands();
    ImageUtil.checkChannel(image);
    int[] rawInput = convertBufferedImageToRaw(image);
    int[] rawOutput = resizeRaw(rawInput, image.getWidth(), image.getHeight(), newWidth, newHeight);
    return convertRawToBufferedImage(rawOutput, newWidth, newHeight, image);
  }

  private static int[] convertBufferedImageToRaw(BufferedImage image) {
    int channels = image.getSampleModel().getNumBands();
    byte[] imageData = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
    int[] raw = new int[imageData.length];
    convertBytesToInts(imageData, raw,
        ((ComponentSampleModel) image.getSampleModel()).getBandOffsets());
    return raw;
  }

  private int[] resizeRaw(int[] input, int oldWidth, int oldHeight, int newWidth, int newHeight) {
    int[] output = new int[newWidth * newHeight];
    int YD = (oldHeight / newHeight) * oldWidth - oldWidth;
    int YR = oldHeight % newHeight;
    int XD = oldWidth / newWidth;
    int XR = oldWidth % newWidth;
    int outOffset = 0;
    int inOffset = 0;
    for (int y = newHeight, YE = 0; y > 0; y--) {
      for (int x = newWidth, XE = 0; x > 0; x--) {
        output[outOffset++] = input[inOffset];
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
        inOffset += oldWidth;
      }
    }
    return output;
  }

  private static BufferedImage convertRawToBufferedImage(int[] raw, int width, int height,
      BufferedImage original) {
    int channels = original.getSampleModel().getNumBands();
    int type = channels == 3 ? BufferedImage.TYPE_3BYTE_BGR : channels == 1 ?
        BufferedImage.TYPE_BYTE_GRAY : BufferedImage.TYPE_4BYTE_ABGR;
    BufferedImage bufferedImage = new BufferedImage(width, height, type);
    byte[] imageData = ((DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData();
    convertIntsToBytes(raw, imageData, getDefaultOffset(channels));
    return bufferedImage;
  }

  private static void convertBytesToInts(byte[] bytes, int[] ints, int[] offsets) {
    int channels = offsets.length;
    if (channels == 3) {
      int index = 0;
      int limit = bytes.length - 2;
      for (int pixel = 0; pixel < limit; pixel += channels) {
        int rgb = bytes[pixel + offsets[0]] & HEX_255;
        rgb <<= 8;
        rgb |= bytes[pixel + offsets[1]] & HEX_255;
        rgb <<= 8;
        rgb |= bytes[pixel + offsets[2]] & HEX_255;
        ints[index++] = rgb;
      }
    } else if (channels == 4) {
      int index = 0;
      int limit = bytes.length - 3;
      for (int pixel = 0; pixel < limit; pixel += channels) {
        int rgb = bytes[pixel + offsets[0]] & HEX_255;
        rgb <<= 8;
        rgb |= bytes[pixel + offsets[1]] & HEX_255;
        rgb <<= 8;
        rgb |= bytes[pixel + offsets[2]] & HEX_255;
        rgb <<= 8;
        rgb |= bytes[pixel + offsets[3]] & HEX_255;
        ints[index++] = rgb;
      }
    } else if (channels == 1) {
      int limit = bytes.length;
      for (int i = 0; i < limit; i++) {
        ints[i] = bytes[i];
      }
    } else {
      throw new IllegalArgumentException("Unsupported channels" + channels);
    }
  }

  private void convertIntsToBytes(int[] ints, byte[] bytes, int[] offset) {
    int channels = offset.length;
    if (channels == 3) {
      int index = 0;
      int limit = bytes.length - 2;
      for (int pixel = 0; pixel < limit; pixel += channels) {
        int rgb = ints[index++];
        bytes[pixel + offset[2]] = (byte) (rgb & HEX_255);
        rgb >>>= 8;
        bytes[pixel + offset[1]] = (byte) (rgb & HEX_255);
        rgb >>>= 8;
        bytes[pixel + offset[0]] = (byte) (rgb & HEX_255);
      }
    } else if (channels == 4) {
      int index = 0;
      int limit = bytes.length - 3;
      for (int pixel = 0; pixel < limit; pixel += channels) {
        int rgb = ints[index++];
        bytes[pixel + offset[3]] = (byte) (rgb & HEX_255);
        rgb >>>= 8;
        bytes[pixel + offset[2]] = (byte) (rgb & HEX_255);
        rgb >>>= 8;
        bytes[pixel + offset[1]] = (byte) (rgb & HEX_255);
        rgb >>>= 8;
        bytes[pixel + offset[0]] = (byte) (rgb & HEX_255);

      }
    } else if (channels == 1) {
      int limit = ints.length;
      for (int i = 0; i < limit; i++) {
        bytes[i] = (byte) (ints[i] & HEX_255);
      }
    } else {
      throw new IllegalArgumentException("Unsupported channels");
    }
  }

  private static int[] getDefaultOffset(int channels) {
    return channels == 3 ? DEFAULT_OFFSET_3 : channels == 4 ? DEFAULT_OFFSET_4 : DEFAULT_OFFSET_1;
  }

  @SneakyThrows
  public static Mat resize(Mat mat, int newWidth, int newHeight) {
    int channels = mat.channels();
    ImageUtil.checkChannel(mat);
    int[] rawInput = convertMatToRaw(mat, getDefaultOffset(channels));
    int[] rawOutput = resizeRaw(rawInput, mat.cols(), mat.rows(), newWidth, newHeight);
    return convertRawToMat(rawOutput, newWidth, newHeight, channels);
  }

  private static int[] convertMatToRaw(Mat mat, int[] offset) {
    int channels = mat.channels();
    int matSize = mat.rows() * mat.cols() * channels;
    byte[] matData = new byte[matSize];
    mat.get(0, 0, matData);
    int[] raw = new int[matData.length];
    convertBytesToInts(matData, raw, offset);
    return raw;
  }

  private static Mat convertRawToMat(int[] raw, int newWidth, int newHeight, int channels) {
    Mat mat = new Mat(newHeight, newWidth, CvType.CV_8UC(channels));
    int matSize = mat.rows() * mat.cols() * mat.channels();
    byte[] matData = new byte[matSize];
    convertIntsToBytes(raw, matData, getDefaultOffset(channels));
    mat.put(0, 0, matData);
    return mat;
  }
}

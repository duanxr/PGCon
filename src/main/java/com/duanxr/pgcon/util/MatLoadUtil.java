package com.duanxr.pgcon.util;

import static com.duanxr.pgcon.util.ConstantConfig.LOAD_IMAGE_FLAG;

import java.io.InputStream;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;

/**
 * @author Duanran 2019/12/13
 */
public class MatLoadUtil {

  public static Mat loadByPath(String imagePath) {
    return Imgcodecs.imread(imagePath, LOAD_IMAGE_FLAG);
  }

  public static Mat loadByBytes(byte[] bytes) {
    return Imgcodecs.imdecode(new MatOfByte(bytes), LOAD_IMAGE_FLAG);
  }

  @SneakyThrows
  public static Mat loadByInputStream(InputStream inputStream) {
    byte[] bytes = IOUtils.toByteArray(inputStream);
    return Imgcodecs.imdecode(new MatOfByte(bytes), LOAD_IMAGE_FLAG);
  }

  @SneakyThrows
  public static Mat loadByResourcesPath(String resourcesPath) {
    try (InputStream inputStream = MatLoadUtil.class.getResourceAsStream(resourcesPath)) {
      byte[] bytes = IOUtils.toByteArray(inputStream);
      return Imgcodecs.imdecode(new MatOfByte(bytes), LOAD_IMAGE_FLAG);
    }
  }
}

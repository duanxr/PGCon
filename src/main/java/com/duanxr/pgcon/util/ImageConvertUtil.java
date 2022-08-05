package com.duanxr.pgcon.util;


import com.alibaba.fastjson.JSONObject;
import com.duanxr.pgcon.algo.model.Area;
import com.duanxr.pgcon.gui.exception.AbortScriptException;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.nio.ByteBuffer;
import javax.imageio.ImageIO;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import nu.pattern.OpenCV;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.SerializationUtils;
import org.bytedeco.leptonica.PIX;
import org.bytedeco.leptonica.global.lept;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

/**
 * @author Duanran 2019/12/13
 */
@UtilityClass
public class ImageConvertUtil {

  /**
   * 返回引用而非新的矩阵
   */
  public static Mat deepSplitMat(Mat origin, Area area) {
    return origin.submat(new Rect(area.getX(), area.getY(), area.getWidth(), area.getHeight()));
  }

  public static Mat deepCopyMat(Mat origin) {
    return origin.submat(new Rect(0, 0, origin.cols(), origin.rows()));
  }

  public static Mat splitMat(Mat origin, Area area) {
    return new Mat(origin, new Rect(area.getX(), area.getY(), area.getWidth(), area.getHeight()));
  }

  public static PIX matToPix(Mat mat) {
    if (mat.channels() == 3) {
      Mat gray = new Mat();
      Imgproc.cvtColor(mat, gray, Imgproc.COLOR_RGB2GRAY);
      mat = gray;
    } else if (mat.channels() == 4) {
      Mat gray = new Mat();
      Imgproc.cvtColor(mat, gray, Imgproc.COLOR_RGBA2GRAY);
      mat = gray;
    }
    MatOfByte bytes = new MatOfByte();
    Imgcodecs.imencode(".tif", mat, bytes);
    ByteBuffer buff = ByteBuffer.wrap(bytes.toArray());
    return lept.pixReadMem(buff, buff.capacity());
  }

  public static Mat matToMask(Mat origin) {
    Mat mask = new Mat();
    origin.copyTo(mask);
    Core.bitwise_not(mask, mask);
    return mask;
  }

  /**
   * 304 ms for 1920*1080 image by 100 times
   */
  public static BufferedImage matToBufferedImage(Mat mat) {
    int type = mat.channels() == 1 ? BufferedImage.TYPE_BYTE_GRAY
        : mat.channels() == 3 ? BufferedImage.TYPE_3BYTE_BGR : BufferedImage.TYPE_4BYTE_ABGR;
    byte[] bytes = matToByteArray(mat);
    BufferedImage image = new BufferedImage(mat.cols(), mat.rows(), type);
    final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
    System.arraycopy(bytes, 0, targetPixels, 0, bytes.length);
    return image;
  }


  /**
   * 149 ms for 1920*1080 image by 100 times
   */
  public static Mat bufferedImageToMat(BufferedImage bufferedImage) {
    int type = bufferedImage.getType() == BufferedImage.TYPE_BYTE_GRAY ? CvType.CV_8UC1 :
        bufferedImage.getType() == BufferedImage.TYPE_3BYTE_BGR ? CvType.CV_8UC3 : CvType.CV_8UC4;
    Mat mat = new Mat(bufferedImage.getHeight(), bufferedImage.getWidth(), type);
    byte[] data = ((DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData();
    int dataSize = mat.channels() * mat.cols() * mat.rows();
    if (dataSize == data.length) {
      mat.put(0, 0, data);
    } else {
      int originWidth = bufferedImage.getSampleModel().getWidth();
      int originHeight = bufferedImage.getSampleModel().getHeight();
      int subWidth = bufferedImage.getWidth();
      int subHeight = bufferedImage.getHeight();
      int subOffY = -bufferedImage.getRaster().getSampleModelTranslateY();
      int subOffX = -bufferedImage.getRaster().getSampleModelTranslateX();
      int channels = bufferedImage.getSampleModel().getNumBands();
      int length = (subWidth * channels);
      for (int i = 0; i < subHeight; i++) {
        int offset = ((i + subOffY) * originWidth + subOffX) * channels;
        mat.put(i, 0, data, offset, length);
      }
    }
    return mat;
  }


  public static byte[] matToByteArray(Mat mat) {
    int bufferSize = mat.channels() * mat.cols() * mat.rows();
    byte[] bytes = new byte[bufferSize];
    mat.get(0, 0, bytes);
    return bytes;
  }

  public static String matToJson(Mat mat) {
    JSONObject obj = new JSONObject();

    if (mat.isContinuous()) {
      int cols = mat.cols();
      int rows = mat.rows();
      int elemSize = (int) mat.elemSize();
      int type = mat.type();

      obj.put("rows", rows);
      obj.put("cols", cols);
      obj.put("type", type);

      String dataString;

      if (type == CvType.CV_32S || type == CvType.CV_32SC2 || type == CvType.CV_32SC3
          || type == CvType.CV_16S) {
        int[] data = new int[cols * rows * elemSize];
        mat.get(0, 0, data);
        dataString = new String(Base64.encodeBase64(SerializationUtils.serialize(data)));
      } else if (type == CvType.CV_32F || type == CvType.CV_32FC2) {
        float[] data = new float[cols * rows * elemSize];
        mat.get(0, 0, data);
        dataString = new String(Base64.encodeBase64(SerializationUtils.serialize(data)));
      } else if (type == CvType.CV_64F || type == CvType.CV_64FC2) {
        double[] data = new double[cols * rows * elemSize];
        mat.get(0, 0, data);
        dataString = new String(Base64.encodeBase64(SerializationUtils.serialize(data)));
      } else if (type == CvType.CV_8U) {
        byte[] data = new byte[cols * rows * elemSize];
        mat.get(0, 0, data);
        dataString = new String(Base64.encodeBase64(data));
      } else {
        throw new AbortScriptException("unknown type");
      }
      obj.put("data", dataString);

      return obj.toJSONString();
    } else {
      throw new AbortScriptException("Mat not continuous.");
    }
  }

  public static Mat matFromJson(String json) {
    JSONObject parse = JSONObject.parseObject(json);
    int rows = parse.getIntValue("rows");
    int cols = parse.getIntValue("cols");
    int type = parse.getIntValue("type");
    Mat mat = new Mat(rows, cols, type);
    String dataString = parse.getString("data");
    if (type == CvType.CV_32S || type == CvType.CV_32SC2 || type == CvType.CV_32SC3
        || type == CvType.CV_16S) {
      int[] data = SerializationUtils.deserialize(Base64.decodeBase64(dataString.getBytes()));
      mat.put(0, 0, data);
    } else if (type == CvType.CV_32F || type == CvType.CV_32FC2) {
      float[] data = SerializationUtils.deserialize(Base64.decodeBase64(dataString.getBytes()));
      mat.put(0, 0, data);
    } else if (type == CvType.CV_64F || type == CvType.CV_64FC2) {
      double[] data = SerializationUtils.deserialize(Base64.decodeBase64(dataString.getBytes()));
      mat.put(0, 0, data);
    } else if (type == CvType.CV_8U) {
      byte[] data = Base64.decodeBase64(dataString.getBytes());
      mat.put(0, 0, data);
    } else {
      throw new AbortScriptException("unknown mat type");
    }
    return mat;
  }

}

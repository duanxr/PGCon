package com.duanxr.pgcon.util;


import com.alibaba.fastjson.JSONObject;
import com.duanxr.pgcon.exception.AbortScriptException;
import java.awt.image.BufferedImage;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.SampleModel;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collections;
import lombok.experimental.UtilityClass;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.SerializationUtils;
import org.bytedeco.leptonica.PIX;
import org.bytedeco.leptonica.global.lept;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfInt;
import org.opencv.imgcodecs.Imgcodecs;

/**
 * @author Duanran 2019/12/13
 */
@UtilityClass
public class ImageUtil {

  private static final MatOfInt BGR2RGB = new MatOfInt(0, 2, 1, 1, 2, 0);
  private static final MatOfInt BGRA2ABGR = new MatOfInt(0, 1, 1, 2, 2, 3, 3, 0);
  private static final int[] CORRECT_COLOR_CHANNEL_BGR = {2, 1, 0};
  private static final int[] CORRECT_COLOR_CHANNEL_BGRA = {0, 3, 2, 1};

  public static PIX matToPix(Mat mat) {
    if (mat.channels() != 1) {
      mat = MatUtil.toGrayMat(mat);
    }
    MatOfByte bytes = new MatOfByte();
    Imgcodecs.imencode(".tif", mat, bytes);
    ByteBuffer buff = ByteBuffer.wrap(bytes.toArray());
    return lept.pixReadMem(buff, buff.capacity());
  }

  public static BufferedImage matToBufferedImage(Mat mat) {
    int channels = mat.channels();
    ImageUtil.checkChannel(mat);
    int type = channels == 1 ? BufferedImage.TYPE_BYTE_GRAY : channels == 3 ?
        BufferedImage.TYPE_3BYTE_BGR : BufferedImage.TYPE_4BYTE_ABGR;
    BufferedImage image = new BufferedImage(mat.cols(), mat.rows(), type);
    mat = correctColorChannel(mat);
    int bufferSize = mat.channels() * mat.cols() * mat.rows();
    byte[] bytes = new byte[bufferSize];
    mat.get(0, 0, bytes);
    byte[] data = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
    System.arraycopy(bytes, 0, data, 0, bytes.length);
    return image;
  }

  public void checkChannel(Mat mat) {
    int channels = mat.channels();
    if (channels != 1 && channels != 3 && channels != 4) {
      throw new IllegalArgumentException("mat channel must be 1, 3 or 4");
    }
  }

  private static Mat correctColorChannel(Mat mat) {
    if (mat.channels() != 4) {
      return mat;
    }
    Mat newMat = new Mat(mat.rows(), mat.cols(), mat.type());
    Core.mixChannels(Collections.singletonList(mat), Collections.singletonList(newMat), BGRA2ABGR);
    return newMat;
  }

  public static Mat bufferedImageToMat(BufferedImage bufferedImage) {
    ImageUtil.checkChannel(bufferedImage);
    int channels = bufferedImage.getSampleModel().getNumBands();
    Mat mat = new Mat(bufferedImage.getHeight(), bufferedImage.getWidth(), CvType.CV_8UC(channels));
    byte[] data = ((DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData();
    int dataSize = mat.channels() * mat.cols() * mat.rows();
    boolean isSubImage = dataSize != data.length;
    if (isSubImage) {
      int originWidth = bufferedImage.getSampleModel().getWidth();
      int originHeight = bufferedImage.getSampleModel().getHeight();
      int subWidth = bufferedImage.getWidth();
      int subHeight = bufferedImage.getHeight();
      int subOffY = -bufferedImage.getRaster().getSampleModelTranslateY();
      int subOffX = -bufferedImage.getRaster().getSampleModelTranslateX();
      int length = (subWidth * channels);
      for (int i = 0; i < subHeight; i++) {
        int offset = ((i + subOffY) * originWidth + subOffX) * channels;
        mat.put(i, 0, data, offset, length);
      }
    } else {
      mat.put(0, 0, data);
    }
    return correctColorChannel(mat, (ComponentSampleModel) bufferedImage.getSampleModel());
  }

  public void checkChannel(BufferedImage image) {
    int channels = image.getSampleModel().getNumBands();
    if (channels != 1 && channels != 3 && channels != 4) {
      throw new IllegalArgumentException("image channel must be 1, 3 or 4");
    }
    DataBuffer dataBuffer = image.getRaster().getDataBuffer();
    if (!(dataBuffer instanceof DataBufferByte)) {
      throw new IllegalArgumentException("Only byte buffered images are supported");
    }
    SampleModel sampleModel = image.getSampleModel();
    if (!(sampleModel instanceof ComponentSampleModel)) {
      throw new IllegalArgumentException("Only component sample models are supported");
    }
  }

  private static Mat correctColorChannel(Mat mat, ComponentSampleModel sampleModel) {
    int[] bandOffsets = sampleModel.getBandOffsets();
    if (bandOffsets.length == 3 && !Arrays.equals(bandOffsets, CORRECT_COLOR_CHANNEL_BGR)) {
      Mat newMat = new Mat(mat.rows(), mat.cols(), mat.type());
      MatOfInt matMixInfo = new MatOfInt(0, bandOffsets[2], 1, bandOffsets[1], 2, bandOffsets[0]);
      Core.mixChannels(Collections.singletonList(mat), Collections.singletonList(newMat),
          matMixInfo);
      return newMat;
    } else if (bandOffsets.length == 4 && !Arrays.equals(bandOffsets, CORRECT_COLOR_CHANNEL_BGRA)) {
      Mat newMat = new Mat(mat.rows(), mat.cols(), mat.type());
      MatOfInt matMixInfo = new MatOfInt(0, bandOffsets[0], 1, bandOffsets[3], 2, bandOffsets[2], 3,
          bandOffsets[1]);
      Core.mixChannels(Collections.singletonList(mat), Collections.singletonList(newMat),
          matMixInfo);
      return newMat;
    }
    return mat;
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
      int dataLength;
      if (type == CvType.CV_32S || type == CvType.CV_32SC2 || type == CvType.CV_32SC3
          || type == CvType.CV_16S) {
        int[] data = new int[cols * rows * elemSize];
        mat.get(0, 0, data);
        byte[] serialize = SerializationUtils.serialize(data);
        byte[] compress = CompressUtil.lz4Compress(serialize);
        dataLength = serialize.length;
        dataString = Base64.encodeBase64String(compress);
      } else if (type == CvType.CV_32F || type == CvType.CV_32FC2) {
        float[] data = new float[cols * rows * elemSize];
        mat.get(0, 0, data);
        byte[] serialize = SerializationUtils.serialize(data);
        byte[] compress = CompressUtil.lz4Compress(serialize);
        dataLength = serialize.length;
        dataString = Base64.encodeBase64String(compress);
      } else if (type == CvType.CV_64F || type == CvType.CV_64FC2) {
        double[] data = new double[cols * rows * elemSize];
        mat.get(0, 0, data);
        byte[] serialize = SerializationUtils.serialize(data);
        byte[] compress = CompressUtil.lz4Compress(serialize);
        dataLength = serialize.length;
        dataString = Base64.encodeBase64String(compress);
      } else if (type == CvType.CV_8U || type == CvType.CV_8UC2 || type == CvType.CV_8UC3
          || type == CvType.CV_8UC4) {
        byte[] data = new byte[cols * rows * elemSize];
        mat.get(0, 0, data);
        byte[] compress = CompressUtil.lz4Compress(data);
        dataLength = data.length;
        dataString = Base64.encodeBase64String(compress);
      } else {
        throw new AbortScriptException("unknown type");
      }
      obj.put("length", dataLength);
      obj.put("data", dataString);
      return obj.toJSONString();
    } else {
      throw new AbortScriptException("Mat not continuous.");
    }
  }

  public static Mat jsonToMat(String json) {
    JSONObject parse = JSONObject.parseObject(json);
    int rows = parse.getIntValue("rows");
    int cols = parse.getIntValue("cols");
    int type = parse.getIntValue("type");
    Mat mat = new Mat(rows, cols, type);
    String dataString = parse.getString("data");
    int dataLength = parse.getIntValue("length");
    if (type == CvType.CV_32S || type == CvType.CV_32SC2 || type == CvType.CV_32SC3
        || type == CvType.CV_16S) {
      int[] data = SerializationUtils.deserialize(
          CompressUtil.lz4Decompress(Base64.decodeBase64(dataString.getBytes()), dataLength));
      mat.put(0, 0, data);
    } else if (type == CvType.CV_32F || type == CvType.CV_32FC2) {
      float[] data = SerializationUtils.deserialize(
          CompressUtil.lz4Decompress(Base64.decodeBase64(dataString.getBytes()), dataLength));
      mat.put(0, 0, data);
    } else if (type == CvType.CV_64F || type == CvType.CV_64FC2) {
      double[] data = SerializationUtils.deserialize(
          CompressUtil.lz4Decompress(Base64.decodeBase64(dataString.getBytes()), dataLength));
      mat.put(0, 0, data);
    } else if (type == CvType.CV_8U || type == CvType.CV_8UC2 || type == CvType.CV_8UC3
        || type == CvType.CV_8UC4) {
      byte[] data = CompressUtil.lz4Decompress(Base64.decodeBase64(dataString.getBytes()),
          dataLength);
      mat.put(0, 0, data);
    } else {
      throw new AbortScriptException("unknown mat type");
    }
    return mat;
  }

}

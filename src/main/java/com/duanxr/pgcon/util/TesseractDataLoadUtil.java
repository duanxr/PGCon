package com.duanxr.pgcon.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import lombok.experimental.UtilityClass;

/**
 * @author 段然 2022/8/5
 */
@UtilityClass
public class TesseractDataLoadUtil {
  public static String tessdataTempFolder;

  public static String getDataPath(String language) throws IOException {

    if (tessdataTempFolder == null || !(new File(tessdataTempFolder)).exists()) {
      File temp = File.createTempFile("tessdata", "");
      if (!temp.exists()) {
        throw new IOException("Error creating temporary file " + temp.getAbsolutePath());
      }
      if (!(temp.delete())) {
        throw new IOException("Could not delete temp file: " + temp.getAbsolutePath());
      }
      if (!(temp.mkdir())) {
        throw new IOException("Could not create temp directory: " + temp.getAbsolutePath());
      }
      tessdataTempFolder = temp.getAbsolutePath();
      temp.deleteOnExit();
    }
    File tessDataFile = new File(tessdataTempFolder + "/tessdata");
    tessDataFile.mkdir();
    File langFile = new File(tessDataFile.getAbsolutePath() + "/" + language + ".traineddata");

    InputStream is = TesseractDataLoadUtil.class.getResourceAsStream(
        "/tessdata/" + language + ".traineddata");
    if (is == null) {
      throw new IOException("Training file does not contain " + language + ".traineddata");
    }

    byte[] buffer = new byte[1024];
    int length;
    OutputStream os = new FileOutputStream(langFile);
    try {
      while ((length = is.read(buffer)) != -1) {
        os.write(buffer, 0, length);
      }
    } finally {
      os.close();
      is.close();
    }
    return tessDataFile.getAbsolutePath();
  }

}

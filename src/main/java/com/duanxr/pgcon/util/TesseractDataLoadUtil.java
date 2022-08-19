package com.duanxr.pgcon.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.apache.commons.io.IOUtils;

/**
 * @author 段然 2022/8/5
 */
@UtilityClass
public class TesseractDataLoadUtil {
  public static volatile String tessdataTempFolder;
  public static String loadResourceTesseractData(String language) throws IOException {
    File tessDataFile = createTessdataTempFolder();
    File langFile = new File(tessDataFile.getAbsolutePath() + "/" + language + ".traineddata");
    @Cleanup InputStream inputStream = TesseractDataLoadUtil.class.getResourceAsStream(
        "/tessdata/" + language + ".traineddata");
    if (inputStream == null) {
      throw new IOException("Training file does not contain " + language + ".traineddata");
    }
    @Cleanup FileOutputStream outputStream = new FileOutputStream(langFile);
    IOUtils.copy(inputStream, outputStream);
    return tessDataFile.getAbsolutePath();
  }

  @SneakyThrows
  public static String loadFileTesseractData(String path, String language) {
    File tessDataFile = createTessdataTempFolder();
    File langFile = new File(tessDataFile.getAbsolutePath() + "/" + language + ".traineddata");
    @Cleanup InputStream inputStream = new FileInputStream(path);
    @Cleanup FileOutputStream outputStream = new FileOutputStream(langFile);
    IOUtils.copy(inputStream, outputStream);
    return tessDataFile.getAbsolutePath();
  }

  @SneakyThrows
  private synchronized File createTessdataTempFolder() {
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
    boolean mkdir = tessDataFile.mkdir();
    return tessDataFile;
  }



}

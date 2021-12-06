package com.duanxr.rhm.io.input.Image;

import com.duanxr.rhm.io.input.ImageInput;
import com.duanxr.rhm.util.MatLoadUtil;
import java.io.File;
import java.io.IOException;
import org.opencv.core.Mat;

/**
 * @author Duanran 2019/12/16
 */
public class StaticImageInput implements ImageInput {

  private Mat imageMat;

  public StaticImageInput(File file) {
    imageMat = MatLoadUtil.loadByPath(file.getAbsolutePath());
  }

  public StaticImageInput(String resourcePath) throws IOException {
    imageMat = MatLoadUtil.loadByResourcesPath(resourcePath);
  }

  @Override
  public void loadInput(Mat buffer) {
    imageMat.copyTo(buffer);
  }

}

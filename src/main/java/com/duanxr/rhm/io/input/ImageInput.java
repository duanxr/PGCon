package com.duanxr.rhm.io.input;

import org.opencv.core.Mat;

/**
 * @author Duanran 2019/12/16
 */
public interface ImageInput {

  void loadInput(Mat buffer);
}

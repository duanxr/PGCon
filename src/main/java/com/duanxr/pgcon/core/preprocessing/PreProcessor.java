package com.duanxr.pgcon.core.preprocessing;

import org.opencv.core.Mat;

/**
 * @author 段然 2022/8/1
 */
public interface PreProcessor {

  Mat preProcess(Mat src);


}

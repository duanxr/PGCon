package com.duanxr.pgcon.input.api;

import com.github.sarxos.webcam.Webcam;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.opencv.core.Mat;

/**
 * @author Duanran 2019/12/16
 */
public interface ImageInput<I> {

  I read();

  void close();
}

package com.duanxr.pgcon.input;

import com.github.sarxos.webcam.Webcam;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Duanran 2019/12/16
 */
public interface StreamImageInput<I> extends ImageInput<I> {

  static List<String> getCameraList() {
    List<Webcam> list = Webcam.getWebcams();
    return list.isEmpty() ? Collections.emptyList() : list.stream().map(Webcam::getName).collect(
        Collectors.toList());
  }

}

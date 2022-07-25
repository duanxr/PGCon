package com.duanxr.pgcon;

import nu.pattern.OpenCV;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author Duanran 2019/12/16
 */
@Configuration
@ComponentScan
public class Main {

  public static void main(String[] args) {
    OpenCV.loadLocally();
    new SpringApplicationBuilder(Main.class).headless(false).run(args);
  }

}

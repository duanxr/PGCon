package com.duanxr.pgcon;

import com.duanxr.pgcon.gui.PGConGUI;
import javafx.application.Application;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.opencv.opencv_java;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author Duanran 2019/12/16
 */
@Configuration
@SpringBootApplication
@EnableConfigurationProperties
public class PGConApplication {
  public static void main(String[] args) {
    Loader.load(opencv_java.class);
    Application.launch(PGConGUI.class, args);
  }

}

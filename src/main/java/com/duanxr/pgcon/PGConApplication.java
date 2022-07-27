package com.duanxr.pgcon;

import com.duanxr.pgcon.gui.Camera;
import com.duanxr.pgcon.gui.MainPanel;
import javafx.application.Application;
import nu.pattern.OpenCV;
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
    OpenCV.loadLocally();
    Application.launch(MainPanel.class, args);
  }

}
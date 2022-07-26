package com.duanxr.pgcon;

import nu.pattern.OpenCV;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author Duanran 2019/12/16
 */
@Configuration
@ComponentScan
@EnableConfigurationProperties
public class Application {

  public static void main(String[] args) {
    OpenCV.loadLocally();
    new SpringApplicationBuilder(Application.class).headless(false).run(args);
  }

}

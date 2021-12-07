package com.duanxr.pgcon;

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
    new SpringApplicationBuilder(Main.class).headless(false).run(args);
  }

}

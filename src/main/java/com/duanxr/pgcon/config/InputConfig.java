package com.duanxr.pgcon.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author 段然 2021/12/29
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "pgcon.input")
@EnableConfigurationProperties
public class InputConfig {

  private Integer width;

  private Integer height;

  private Integer ppi;

  private Integer frameInterval;

  private Integer cacheSize;

}

package com.duanxr.pgcon.config;

import com.duanxr.pgcon.output.api.Protocol;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 段然 2022/7/25
 */
@Configuration
public class BeanConfig {

  @Bean
  public ExecutorService executorService() {
    return Executors.newCachedThreadPool();
  }

  @Bean
  public AtomicBoolean enableDebug() {
    return new AtomicBoolean(false);
  }

  @Bean
  public AtomicBoolean frozenScreen() {
    return new AtomicBoolean(false);
  }

}

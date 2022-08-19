package com.duanxr.pgcon.config;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 段然 2022/7/25
 */
@Configuration
public class BeanConfig {

  @Bean
  public ExecutorService executorService() {
    ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(0, Integer.MAX_VALUE,
        60L, TimeUnit.SECONDS, new SynchronousQueue<>());
    threadPoolExecutor.setThreadFactory(
        new ThreadFactoryBuilder().setNameFormat("PGCon-%d").build());
    return threadPoolExecutor;
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

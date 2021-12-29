package com.duanxr.pgcon.core;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.Getter;
import org.springframework.stereotype.Component;

/**
 * @author 段然 2021/12/30
 */
@Component
public class PGPool {

  @Getter
  private final ExecutorService executors = Executors.newCachedThreadPool();
}

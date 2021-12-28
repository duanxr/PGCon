package com.duanxr.pgcon.core;

import com.google.common.eventbus.EventBus;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author 段然 2021/12/13
 */
@Getter
@Component
public class ComponentManager {

  private final EventBus eventBus;


  private final ExecutorService executors = Executors.newCachedThreadPool();

  public ComponentManager(EventBus eventBus) {
    this.eventBus = eventBus;
  }
}

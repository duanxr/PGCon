package com.duanxr.pgcon.component;

import com.duanxr.pgcon.log.GuiLogger;
import com.duanxr.pgcon.notification.NotifyService;
import com.duanxr.pgcon.service.Controller;
import com.duanxr.pgcon.service.DetectService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * @author 段然 2022/8/17
 */
@Data
@Component
public class PGConComponents {
  private final Controller controller;
  private final DetectService detectService;
  private final DisplayService displayService;
  private final AtomicBoolean enableDebug;
  private final ExecutorService executorService;
  private final GuiLogger guiLogger;
  private final NotifyService notifyService;
  public PGConComponents( AtomicBoolean enableDebug, Controller controller,
      DisplayService displayService, ExecutorService executorService, GuiLogger guiLogger,
     NotifyService notifyService, DetectService detectService) {
    this.detectService = detectService;
    this.enableDebug = enableDebug;
    this.controller = controller;
    this.displayService = displayService;
    this.executorService = executorService;
    this.guiLogger = guiLogger;
    this.notifyService = notifyService;
  }
}

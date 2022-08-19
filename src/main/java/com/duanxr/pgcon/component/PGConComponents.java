package com.duanxr.pgcon.component;

import com.duanxr.pgcon.gui.display.DisplayService;
import com.duanxr.pgcon.log.GuiLogger;
import com.duanxr.pgcon.notification.NotifyService;
import com.duanxr.pgcon.script.component.ScriptManager;
import com.duanxr.pgcon.output.ControllerService;
import com.duanxr.pgcon.core.detect.DetectService;
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
  private final ControllerService controllerService;
  private final DetectService detectService;
  private final DisplayService displayService;
  private final AtomicBoolean enableDebug;
  private final ExecutorService executorService;
  private final GuiLogger guiLogger;
  private final NotifyService notifyService;
  private final ScriptManager scriptManager;
  public PGConComponents( AtomicBoolean enableDebug, ControllerService controllerService,
      DisplayService displayService, ExecutorService executorService, GuiLogger guiLogger,
     NotifyService notifyService, DetectService detectService, ScriptManager scriptManager) {
    this.detectService = detectService;
    this.enableDebug = enableDebug;
    this.controllerService = controllerService;
    this.displayService = displayService;
    this.executorService = executorService;
    this.guiLogger = guiLogger;
    this.notifyService = notifyService;
    this.scriptManager = scriptManager;
  }
}

package com.duanxr.pgcon.core;

import com.duanxr.pgcon.config.InputConfig;
import com.duanxr.pgcon.config.OutputConfig;
import com.duanxr.pgcon.config.GuiConfig;
import com.duanxr.pgcon.core.detect.image.compare.ImageCompare;
import com.duanxr.pgcon.core.detect.ocr.OCR;
import com.duanxr.pgcon.core.script.ScriptLoader;
import com.duanxr.pgcon.gui.ControlPanel;
import com.duanxr.pgcon.output.Controller;
import com.google.common.eventbus.EventBus;
import java.util.concurrent.ExecutorService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author 段然 2021/12/13
 */
@Getter
@Component
public class PGCon {

  private final OCR ocr;
  private final EventBus eventBus;
  private final Controller controller;
  private final ControlPanel controlPanel;
  private final ImageCompare imageCompare;
  private final ScriptLoader scriptLoader;
  private final ExecutorService executors;

  private final GuiConfig guiConfig;
  private final InputConfig inputConfig;
  private final OutputConfig outPutConfig;


  @Autowired
  public PGCon(OCR ocr, EventBus eventBus,
      Controller controller, ControlPanel controlPanel,
      ImageCompare imageCompare, GuiConfig guiConfig,
      InputConfig inputConfig, OutputConfig outPutConfig,
      PGPool pgPool, ScriptLoader scriptLoader) {
    this.ocr = ocr;
    this.eventBus = eventBus;
    this.controller = controller;
    this.controlPanel = controlPanel;
    this.imageCompare = imageCompare;
    this.scriptLoader = scriptLoader;

    this.guiConfig = guiConfig;
    this.inputConfig = inputConfig;
    this.outPutConfig = outPutConfig;

    this.executors = pgPool.getExecutors();
  }
}

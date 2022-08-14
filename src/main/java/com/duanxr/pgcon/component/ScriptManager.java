package com.duanxr.pgcon.component;

import com.duanxr.pgcon.core.detect.api.ImageCompare;
import com.duanxr.pgcon.core.detect.api.OCR;
import com.duanxr.pgcon.gui.log.GuiLogger;
import com.duanxr.pgcon.notification.NotifyService;
import com.duanxr.pgcon.notification.impl.PushPlusNotifyService;
import com.duanxr.pgcon.output.Controller;
import com.duanxr.pgcon.script.api.MainScript;
import com.duanxr.pgcon.script.api.Script;
import com.duanxr.pgcon.script.component.ScriptEngine;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * @author 段然 2022/7/25
 */
@Component
public class ScriptManager {

  private final Controller controller;
  private final DisplayHandler displayHandler;
  private final AtomicBoolean enableDebug;
  private final ExecutorService executorService;
  private final ImageCompare imageCompare;
  private final List<MainScript> mainScriptList;
  private final OCR ocr;
  private final List<ScriptEngine> scriptEngineList;
  private final List<Script> scriptList;
  @Getter
  private Map<String, MainScript> mainScripts;
  @Getter
  private Map<String, Script> scripts;

  private NotifyService notifyService;
  @Autowired
  public ScriptManager(@Qualifier("enableDebug") AtomicBoolean enableDebug,
      List<ScriptEngine> scriptEngineList, List<MainScript> mainScriptList,
      List<Script> scriptList, OCR ocr, ImageCompare imageCompare, Controller controller,
      ExecutorService executorService, DisplayHandler displayHandler) {
    this.scriptEngineList = scriptEngineList;
    this.mainScriptList = mainScriptList;
    this.scriptList = scriptList;
    this.ocr = ocr;
    this.imageCompare = imageCompare;
    this.controller = controller;
    this.executorService = executorService;
    this.enableDebug = enableDebug;
    this.displayHandler = displayHandler;
    PushPlusNotifyService pushPlusNotifyService = new PushPlusNotifyService();
    //TODO
    pushPlusNotifyService.setToken("33242cfb6ef347909b11e0f0a96f8aac");
    pushPlusNotifyService.setChannel(PushPlusNotifyService.Channel.wechat);
    this.notifyService = pushPlusNotifyService;
  }

  @PostConstruct
  public void loadScripts() {
    scriptEngineList.forEach(this::inject);
    mainScripts = mainScriptList.stream()
        .collect(Collectors.toMap(Script::getScriptName, Function.identity()));
    scripts = scriptList.stream()
        .collect(Collectors.toMap(Script::getScriptName, Function.identity()));
  }

  private void inject(ScriptEngine scriptEngine) {
    scriptEngine.setEnableDebug(enableDebug);
    scriptEngine.setOcr(ocr);
    scriptEngine.setImageCompare(imageCompare);
    scriptEngine.setController(controller);
    scriptEngine.setScriptManager(this);
    scriptEngine.setDisplayHandler(displayHandler);
    scriptEngine.setExecutorService(executorService);
    scriptEngine.setNotifyService(notifyService);
  }

  public void register(GuiLogger guiLogger) {
    scriptEngineList.forEach(scriptEngine -> scriptEngine.setGuiLogger(guiLogger));
  }
}
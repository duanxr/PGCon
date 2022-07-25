package com.duanxr.pgcon.script.component;

import com.duanxr.pgcon.core.detect.api.ImageCompare;
import com.duanxr.pgcon.core.detect.api.OCR;
import com.duanxr.pgcon.output.Controller;
import com.duanxr.pgcon.script.api.MainScript;
import com.duanxr.pgcon.script.api.Script;
import com.duanxr.pgcon.script.ScriptEngine;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author 段然 2022/7/25
 */
@Component
public class ScriptManager {

  private final List<ScriptEngine> scriptEngineList;
  private final  List<MainScript> mainScriptList;
  private final List<Script> scriptList;
  private final OCR ocr;
  private final ImageCompare imageCompare;
  private final Controller controller;
  @Getter
  private Map<String, MainScript> mainScripts;
  @Getter
  private Map<String, Script> scripts;

  @Autowired
  public ScriptManager(List<ScriptEngine> scriptEngineList, List<MainScript> mainScriptList,
      List<Script> scriptList, OCR ocr, ImageCompare imageCompare, Controller controller) {
    this.scriptEngineList = scriptEngineList;
    this.mainScriptList = mainScriptList;
    this.scriptList = scriptList;
    this.ocr = ocr;
    this.imageCompare = imageCompare;
    this.controller = controller;
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
    scriptEngine.setOcr(ocr);
    scriptEngine.setImageCompare(imageCompare);
    scriptEngine.setController(controller);
  }
}
package com.duanxr.pgcon.component;

import com.duanxr.pgcon.script.component.CachedScript;
import com.duanxr.pgcon.script.component.ScriptLoader;
import com.duanxr.pgcon.script.component.ScriptParser;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author 段然 2022/7/25
 */
@Slf4j
@Component
public class ScriptService {
  @Getter
  private final Map<String, CachedScript> cachedScriptMap;
  private final ScriptLoader scriptLoader;
  private final ScriptParser scriptParser;

  @Autowired
  public ScriptService(ScriptLoader scriptLoader,
      ScriptParser scriptParser) {
    this.scriptLoader = scriptLoader;
    this.scriptParser = scriptParser;
    this.cachedScriptMap = new HashMap<>();
  }

  @PostConstruct
  public void loadScripts() {
    cachedScriptMap.clear();
    List<File> files = scriptLoader.loadScripts();
    if (!files.isEmpty()) {
      files.stream().map(scriptParser::parseScript).filter(Objects::nonNull)
          .forEach(cachedScript -> cachedScriptMap.put(cachedScript.getScriptName(), cachedScript));
    } else {
      log.error("no script file found");
    }
  }

}


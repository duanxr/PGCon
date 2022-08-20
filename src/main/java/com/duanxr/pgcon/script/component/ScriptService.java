package com.duanxr.pgcon.script.component;

import com.duanxr.pgcon.exception.AlertErrorException;
import com.duanxr.pgcon.log.GuiLogger;
import com.duanxr.pgcon.script.api.Script;
import java.io.File;
import java.util.List;
import java.util.Objects;
import lombok.SneakyThrows;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author 段然 2022/7/25
 */
@Component
public class ScriptService {

  private final GuiLogger guiLogger;
  private final ScriptLoader scriptLoader;
  private final ScriptParser scriptParser;
  private final ScriptManager scriptManager;

  @Autowired
  public ScriptService(GuiLogger guiLogger, ScriptLoader scriptLoader,
      ScriptManager scriptManager, ScriptParser scriptParser) {
    this.guiLogger = guiLogger;
    this.scriptLoader = scriptLoader;
    this.scriptParser = scriptParser;
    this.scriptManager = scriptManager;
  }

  public void loadScripts() {
    List<File> files = scriptLoader.loadScripts();
    if (!files.isEmpty()) {
      files.stream().map(scriptParser::parseScript)
          .filter(Objects::nonNull).forEach(scriptManager::putScript);
    } else {
      guiLogger.error("no script file found");
    }
  }

  @SneakyThrows
  public void reloadScripts(ScriptCache<Object> scriptCache) {
    Script<Object> script = scriptParser.compileScript(scriptCache.getScriptFile());
    if (script == null) {
      throw new AlertErrorException(
          "compile script " + scriptCache.getScriptFile().getName() + " failed");
    }
    try {
      BeanUtils.copyProperties(script.getInfo(), scriptCache.getScript().getInfo());
    } catch (Exception e) {
      throw new AlertErrorException(
          "copy properties from script " + scriptCache.getScriptFile().getName() + " failed");
    }
    scriptCache.setScript(script);
  }


}


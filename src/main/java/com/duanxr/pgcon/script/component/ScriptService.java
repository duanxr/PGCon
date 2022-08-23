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
  private final ScriptFinder scriptFinder;
  private final ScriptParser scriptParser;
  private final ScriptManager scriptManager;
  private final ScriptCompiler scriptCompiler;
  private final ScriptLoader scriptLoader;

  private ClassLoader scriptClassLoader;

  @Autowired
  public ScriptService(GuiLogger guiLogger, ScriptFinder scriptFinder,
      ScriptManager scriptManager, ScriptParser scriptParser, ScriptCompiler scriptCompiler,
      ScriptLoader scriptLoader) {
    this.guiLogger = guiLogger;
    this.scriptFinder = scriptFinder;
    this.scriptParser = scriptParser;
    this.scriptManager = scriptManager;
    this.scriptCompiler = scriptCompiler;
    this.scriptLoader = scriptLoader;
  }

  public void loadScripts() {
    scriptManager.clear();
    scriptClassLoader = this.getClass().getClassLoader();
    scriptClassLoader = scriptLoader.loadJars(scriptClassLoader);
    scriptClassLoader = scriptLoader.loadEngines(scriptClassLoader);
    List<File> files = scriptFinder.findScripts();
    if (files.isEmpty()) {
      guiLogger.error("no script file found");
    } else {
      files.stream().map(this::parseScript)
          .filter(Objects::nonNull).forEach(scriptManager::putScript);
    }
  }

  public ScriptCache<Object> parseScript(File scriptFile) {
    Script<Object> script = scriptLoader.loadScript(scriptClassLoader, scriptFile);
    if (script == null) {
      return null;
    }
    guiLogger.info("compile script {} success", scriptFile.getName());
    return ScriptCache.builder()
        .description(script.getInfo().getDescription())
        .script(script)
        .name(script.getClass().getName())
        .scriptFile(scriptFile)
        .build();
  }

  @SneakyThrows
  public void reloadScripts(ScriptCache<Object> scriptCache) {
    Script<Object> script = scriptLoader.loadScript(scriptClassLoader,scriptCache.getScriptFile());
    if (script.getInfo().getConfig() != null
        && scriptCache.getScript().getInfo().getConfig() != null) {
      try {
        BeanUtils.copyProperties(script.getInfo().getConfig(),
            scriptCache.getScript().getInfo().getConfig());
      } catch (Exception e) {
        throw new AlertErrorException(
            "copy properties from script " + scriptCache.getScriptFile().getName() + " failed", e);
      }
    }
    scriptCache.setScript(script);
  }


}


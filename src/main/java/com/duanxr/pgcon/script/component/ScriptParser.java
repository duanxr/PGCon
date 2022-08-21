package com.duanxr.pgcon.script.component;

import com.duanxr.pgcon.component.PGConComponents;
import com.duanxr.pgcon.log.GuiLogger;
import com.duanxr.pgcon.script.api.Script;
import com.duanxr.pgcon.script.engine.BasicScriptEngine;
import java.io.File;
import java.nio.charset.StandardCharsets;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.openhft.compiler.CompilerUtils;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;

/**
 * @author 段然 2022/8/18
 */
@Slf4j
@Component
public class ScriptParser {

  private final GuiLogger guiLogger;

  private final PGConComponents component;

  public ScriptParser(GuiLogger guiLogger, PGConComponents component) {
    this.guiLogger = guiLogger;
    this.component = component;
  }

  public ScriptCache<Object> parseScript(File scriptFile) {
    Script<Object> script = compileScript(scriptFile);
    if (script != null) {
      guiLogger.info("compile script {} success", scriptFile.getName());
      return ScriptCache.builder()
          .description(script.getInfo().getDescription())
          .script(script)
          .name(script.getClass().getName())
          .scriptFile(scriptFile)
          .build();
    }
    return null;
  }

  @SuppressWarnings("unchecked")
  public Script<Object> compileScript(File scriptFile) {
    Object scriptInstance = compileJava(scriptFile);
    if (scriptInstance != null) {
      if (checkEngine(scriptInstance)) {
        BasicScriptEngine<Object> script = (BasicScriptEngine<Object>) scriptInstance;
        script.setComponents(component);
        return script;
      } else {
        guiLogger.error("script {} didn't implement any ScriptEngine", scriptFile.getName());
      }
    }
    return null;
  }

  private boolean checkEngine(Object scriptInstance) {
    return scriptInstance instanceof BasicScriptEngine;
  }

  @SneakyThrows
  public Object compileJava(File file) {
    String name = file.getName();
    String className = name.substring(0, name.lastIndexOf("."));
    String code = null;
    Class<?> scriptClass = null;
    Object scriptInstance = null;
    try {
      code = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
    } catch (Exception e) {
      log.error("read script {} code error", className, e);
      return null;
    }
    try {
      scriptClass = CompilerUtils.CACHED_COMPILER.loadFromJava(new ScriptClassLoader(), className,
          code);
    } catch (ClassNotFoundException e) {
      log.error(
          "cannot found class {} in script {} , please check the script and make sure its name is the same as the class name",
          className, name, e);
      return null;
    } catch (Exception e) {
      log.error("compile script {} error", name, e);
      return null;
    }
    try {
      scriptInstance = scriptClass.getDeclaredConstructor().newInstance();
    } catch (Exception e) {
      log.error("create script {} instance error", name, e);
      return null;
    }
    return scriptInstance;
  }

}

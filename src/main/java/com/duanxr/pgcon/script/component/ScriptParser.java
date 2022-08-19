package com.duanxr.pgcon.script.component;

import com.duanxr.pgcon.component.PGConComponents;
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

  private final PGConComponents component;

  public ScriptParser(PGConComponents component) {
    this.component = component;
  }

  @SuppressWarnings("unchecked")
  public ScriptCache<Object> parseScript(File scriptFile) {
    String name = scriptFile.getName();
    Object scriptInstance = compileScript(scriptFile);
    if (scriptInstance != null) {
      if (checkEngine(scriptInstance)) {
        BasicScriptEngine<Object> script = (BasicScriptEngine<Object>) scriptInstance;
        script.setComponents(component);
        return ScriptCache.builder()
            .scriptName(script.getInfo().getName())
            .script(script)
            .scriptFile(scriptFile)
            .build();
      } else {
        log.error("script {} didn't implement any ScriptEngine", name);
      }
    }
    return null;
  }

  private boolean checkEngine(Object scriptInstance) {
    return scriptInstance instanceof BasicScriptEngine;
  }

  @SneakyThrows
  private Object compileScript(File file) {
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
      scriptClass = CompilerUtils.CACHED_COMPILER.loadFromJava(className, code);
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

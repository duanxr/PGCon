package com.duanxr.pgcon.script.component;

import com.duanxr.pgcon.component.PGConComponents;
import com.duanxr.pgcon.exception.LoadScriptException;
import com.duanxr.pgcon.log.GuiLogger;
import com.duanxr.pgcon.script.api.Script;
import com.duanxr.pgcon.script.component.loader.ScriptJavaClassLoader;
import com.duanxr.pgcon.script.component.loader.ScriptJarClassLoader;
import com.duanxr.pgcon.script.engine.BasicScriptEngine;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.SneakyThrows;
import net.openhft.compiler.CompilerUtils;
import org.springframework.stereotype.Component;

/**
 * @author 段然 2022/8/22
 */
@Component
public class ScriptLoader {

  private final PGConComponents component;
  private final GuiLogger guiLogger;
  private final ScriptCompiler scriptCompiler;
  private final ScriptFinder scriptFinder;
  public ScriptLoader(GuiLogger guiLogger, ScriptFinder scriptFinder,
      ScriptCompiler scriptCompiler, PGConComponents component) {
    this.component = component;
    this.guiLogger = guiLogger;
    this.scriptFinder = scriptFinder;
    this.scriptCompiler = scriptCompiler;
  }

  @SuppressWarnings("unchecked")
  public Script<Object> loadScript(ClassLoader parent, File scriptFile) {
    if (parent == null) {
      parent = this.getClass().getClassLoader();
    }
    Class<?> scriptClass = scriptCompiler.compile(new ScriptJavaClassLoader(parent), scriptFile);
    if (!BasicScriptEngine.class.isAssignableFrom(scriptClass)) {
      throw new LoadScriptException("script didn't implement any ScriptEngine");
    }
    BasicScriptEngine<Object> script = null;
    try {
      script = (BasicScriptEngine<Object>) scriptClass.getDeclaredConstructor().newInstance();
    } catch (Exception e) {
      throw new LoadScriptException(
          "can't construct script instance , please make sure script has a public no-arg constructor",
          e);
    }
    script.setComponents(component);
    return script;
  }

  @SneakyThrows
  public ClassLoader loadEngines(ClassLoader parent) {
    if (parent == null) {
      parent = this.getClass().getClassLoader();
    }
    List<File> engines = scriptFinder.findDependentScripts();
    if (!engines.isEmpty()) {
      ScriptJavaClassLoader scriptJavaClassLoader = new ScriptJavaClassLoader(parent);
      Set<String> folders = new HashSet<>();
      for (File engine : engines) {
        folders.add(engine.getParentFile().getAbsolutePath());
      }
      for (String folder : folders) {
        CompilerUtils.addClassPath(folder);
      }
      for (File engine : engines) {
        //scriptCompiler.compile(scriptJavaClassLoader, engine);
      }
      return scriptJavaClassLoader;
    }
    return parent;
  }

  public ClassLoader loadJars(ClassLoader parent) {
    if (parent == null) {
      parent = this.getClass().getClassLoader();
    }
    List<File> jars = scriptFinder.findDependentJars();
    if (!jars.isEmpty()) {
      List<URL> urlList = new ArrayList<>();
      for (File jar : jars) {
        guiLogger.debug("find dependent jar: {}", jar.getName());
        try {
          urlList.add(jar.toURI().toURL());
        } catch (Exception e) {
          guiLogger.error(
              "read dependent jar {} failed , please make sure you have permission to access it",
              jar.getName(), e);
        }
      }
      URL[] urls = new URL[urlList.size()];
      urls = urlList.toArray(urls);
      try {
        return new ScriptJarClassLoader(urls);
      } catch (Exception e) {
        guiLogger.error("load dependent jars failed", e);
      }
    }
    return this.getClass().getClassLoader();
  }

}

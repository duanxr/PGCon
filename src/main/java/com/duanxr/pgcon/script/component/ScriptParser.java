package com.duanxr.pgcon.script.component;

import com.duanxr.pgcon.component.PGConComponents;
import com.duanxr.pgcon.log.GuiLogger;
import com.duanxr.pgcon.script.api.Script;
import com.duanxr.pgcon.script.component.loader.ScriptJavaClassLoader;
import java.io.File;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author 段然 2022/8/18
 */
@Slf4j
@Component
public class ScriptParser {

  private final PGConComponents component;
  private final GuiLogger guiLogger;
  private final ScriptJavaClassLoader scriptJavaClassLoader;

  public ScriptParser(ScriptJavaClassLoader scriptJavaClassLoader, GuiLogger guiLogger,
      PGConComponents component) {
    this.scriptJavaClassLoader = scriptJavaClassLoader;
    this.guiLogger = guiLogger;
    this.component = component;
  }








}

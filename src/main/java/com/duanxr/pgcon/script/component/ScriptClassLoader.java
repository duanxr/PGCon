package com.duanxr.pgcon.script.component;

import org.springframework.stereotype.Component;

/**
 * @author 段然 2022/8/20
 */
@Component
public class ScriptClassLoader extends ClassLoader {

  public ScriptClassLoader() {
  }

  public ScriptClassLoader(ClassLoader parent) {
    super(parent);
  }
}

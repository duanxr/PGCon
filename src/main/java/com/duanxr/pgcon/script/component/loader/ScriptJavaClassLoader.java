package com.duanxr.pgcon.script.component.loader;

import org.springframework.stereotype.Component;

/**
 * @author 段然 2022/8/20
 */
@Component
public class ScriptJavaClassLoader extends ClassLoader {

  public ScriptJavaClassLoader() {
  }

  public ScriptJavaClassLoader(ClassLoader parent) {
    super(parent);
  }
}

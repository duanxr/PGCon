package com.duanxr.pgcon.script.component.loader;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * @author 段然 2022/8/22
 */
public class ScriptJarClassLoader extends URLClassLoader {

  public ScriptJarClassLoader(URL[] urls, ClassLoader parent) {
    super(urls, parent);
  }

  public ScriptJarClassLoader(URL[] urls) {
    super(urls);
  }
}

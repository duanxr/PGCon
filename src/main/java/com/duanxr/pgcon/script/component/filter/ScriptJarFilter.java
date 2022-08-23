package com.duanxr.pgcon.script.component.filter;

import java.io.File;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.springframework.stereotype.Component;

/**
 * @author 段然 2022/8/17
 */
@Component
public class ScriptJarFilter extends TrueFileFilter {

  @Override
  public boolean accept(File file) {
    return file.getName().endsWith(".jar");
  }
}

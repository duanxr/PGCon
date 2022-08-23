package com.duanxr.pgcon.script.component;

import static com.duanxr.pgcon.config.ConstantConfig.ENGINE_PATH;
import static com.duanxr.pgcon.config.ConstantConfig.LIB_PATH;
import static com.duanxr.pgcon.config.ConstantConfig.SCRIPT_PATH;

import com.duanxr.pgcon.script.component.filter.ScriptCodeFilter;
import com.duanxr.pgcon.script.component.filter.ScriptJarFilter;
import java.io.File;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.springframework.stereotype.Component;

/**
 * @author 段然 2022/8/18
 */
@Slf4j
@Component
public class ScriptFinder {

  private final ScriptCodeFilter scriptCodeFilter;
  private final ScriptJarFilter scriptJarFilter;

  public ScriptFinder(ScriptCodeFilter scriptCodeFilter, ScriptJarFilter scriptJarFilter) {
    this.scriptCodeFilter = scriptCodeFilter;
    this.scriptJarFilter = scriptJarFilter;
  }

  public List<File> findDependentJars() {
    return findFiles(new File(LIB_PATH), scriptJarFilter);
  }

  public List<File> findDependentScripts() {
    return findFiles(new File(ENGINE_PATH), scriptCodeFilter);
  }

  public List<File> findScripts() {
    return findFiles(new File(SCRIPT_PATH), scriptCodeFilter);
  }

  private List<File> findFiles(File folder, IOFileFilter filter) {
    if (!folder.exists()) {
      return Collections.emptyList();
    }
    Iterator<File> iterator = FileUtils.iterateFiles(folder, filter, TrueFileFilter.INSTANCE);
    Iterable<File> iterable = () -> iterator;
    Stream<File> stream = StreamSupport.stream(iterable.spliterator(), false);
    return stream.collect(Collectors.toList());
  }

}

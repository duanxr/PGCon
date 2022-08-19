package com.duanxr.pgcon.script.component;

import static com.duanxr.pgcon.config.ConstantConfig.SCRIPTS_PATH;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.springframework.stereotype.Component;

/**
 * @author 段然 2022/8/18
 */
@Slf4j
@Component
public class ScriptLoader {

  private final ScriptFilter scriptFilter;

  public ScriptLoader(ScriptFilter scriptFilter) {
    this.scriptFilter = scriptFilter;
  }

  public List<File> loadScripts() {
    File scriptFolder = new File(SCRIPTS_PATH);
    Iterator<File> iterator = FileUtils.iterateFiles(scriptFolder, scriptFilter,
        TrueFileFilter.INSTANCE);
    Iterable<File> iterable = () -> iterator;
    Stream<File> stream = StreamSupport.stream(iterable.spliterator(), false);
    return stream.collect(Collectors.toList());
  }

}

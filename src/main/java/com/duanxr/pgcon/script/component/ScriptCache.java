package com.duanxr.pgcon.script.component;

import com.duanxr.pgcon.script.api.Script;
import java.io.File;
import lombok.Builder;
import lombok.Data;

/**
 * @author 段然 2022/8/18
 */
@Data
@Builder
public class ScriptCache<T> {

  private String name;
  private String description;
  private Script<T> script;
  private File scriptFile;

}

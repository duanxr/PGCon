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
public class CachedScript {

  private String scriptName;
  private Script script;
  private File scriptFile;

}

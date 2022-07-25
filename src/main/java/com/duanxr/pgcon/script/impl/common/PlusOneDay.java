package com.duanxr.pgcon.script.impl.common;

import com.duanxr.pgcon.script.api.MainScript;
import com.duanxr.pgcon.script.ScriptEngine;
import org.springframework.stereotype.Component;

/**
 * @author 段然 2022/7/25
 */
@Component
public class PlusOneDay  extends ScriptEngine implements MainScript {
  @Override
  public String getScriptName() {
    return "Plus One Day";
  }

  @Override
  public void run() {
    //todo
    sleep(1000);
  }

}

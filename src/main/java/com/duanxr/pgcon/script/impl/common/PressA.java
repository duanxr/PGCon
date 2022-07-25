package com.duanxr.pgcon.script.impl.common;

import com.duanxr.pgcon.script.api.MainScript;
import com.duanxr.pgcon.output.action.ButtonAction;
import com.duanxr.pgcon.script.ScriptEngine;
import org.springframework.stereotype.Component;

/**
 * @author 段然 2021/12/9
 */
@Component
public class PressA extends ScriptEngine implements MainScript {

  @Override
  public void run() {
    controller.press(ButtonAction.A);
    sleep(100);
  }

  @Override
  public String getScriptName() {
    return "Press A";
  }
}

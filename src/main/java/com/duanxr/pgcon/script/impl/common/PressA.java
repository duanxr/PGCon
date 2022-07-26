package com.duanxr.pgcon.script.impl.common;

import com.duanxr.pgcon.script.api.MainScript;
import com.duanxr.pgcon.output.action.ButtonAction;
import com.duanxr.pgcon.script.component.ScriptEngine;
import org.springframework.stereotype.Component;

/**
 * @author 段然 2021/12/9
 */
@Component
public class PressA extends ScriptEngine implements MainScript {

  @Override
  public void execute() {
    controller.press(ButtonAction.A);
    sleep(150);
  }

  @Override
  public String getScriptName() {
    return "Press A";
  }
}

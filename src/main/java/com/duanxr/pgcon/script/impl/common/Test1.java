package com.duanxr.pgcon.script.impl.common;

import com.duanxr.pgcon.output.action.ButtonAction;
import com.duanxr.pgcon.script.ScriptEngine;
import com.duanxr.pgcon.script.api.MainScript;
import org.springframework.stereotype.Component;

/**
 * @author 段然 2021/12/9
 */
@Component
public class Test1 extends ScriptEngine implements MainScript {

  @Override
  public void run() {
    controller.press(ButtonAction.D_TOP);
    sleep(150);
    controller.press(ButtonAction.D_BOTTOM);
    sleep(150);
    controller.press(ButtonAction.D_RIGHT);
    sleep(150);
    controller.press(ButtonAction.D_LEFT);
    sleep(150);
  }

  @Override
  public String getScriptName() {
    return "Test1";
  }
}

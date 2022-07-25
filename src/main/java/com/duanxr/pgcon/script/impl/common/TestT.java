package com.duanxr.pgcon.script.impl.common;

import com.duanxr.pgcon.output.action.ButtonAction;
import com.duanxr.pgcon.output.action.StickAction;
import com.duanxr.pgcon.script.ScriptEngine;
import com.duanxr.pgcon.script.api.MainScript;
import org.springframework.stereotype.Component;

/**
 * @author 段然 2021/12/9
 */
@Component
public class TestT extends ScriptEngine implements MainScript {

  @Override
  public void run() {
    controller.press(StickAction.L_BOTTOM);
    sleep(1600);
    controller.press(StickAction.L_CENTER);
    /*sleep(150);
    controller.press(ButtonAction.D_RIGHT);
    sleep(600);
    controller.press(StickAction.L_BOTTOM);
    sleep(700);
    controller.press(StickAction.L_CENTER);*/
  }

  @Override
  public String getScriptName() {
    return "TestTTTT";
  }
}

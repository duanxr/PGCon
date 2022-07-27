package com.duanxr.pgcon.script.impl.common;

import com.duanxr.pgcon.output.action.ButtonAction;
import com.duanxr.pgcon.script.component.ScriptEngine;
import com.duanxr.pgcon.script.api.MainScript;

/**
 * @author 段然 2021/12/9
 */
//@Component
public class Test extends ScriptEngine implements MainScript {

  @Override
  public void execute() {
    controller.press(ButtonAction.A);
    sleep(150);
    controller.press(ButtonAction.B);
    sleep(150);
    controller.press(ButtonAction.X);
    sleep(150);
    controller.press(ButtonAction.Y);
    sleep(150);
    controller.press(ButtonAction.L);
    sleep(150);
    controller.press(ButtonAction.R);
    sleep(150);
    controller.press(ButtonAction.ZL);
    sleep(150);
    controller.press(ButtonAction.ZR);
    sleep(150);
    controller.press(ButtonAction.L_STICK);
    sleep(150);
    controller.press(ButtonAction.R_STICK);
    sleep(150);
    controller.press(ButtonAction.D_TOP);
    sleep(150);
    controller.press(ButtonAction.D_BOTTOM);
    sleep(150);
    controller.press(ButtonAction.D_RIGHT);
    sleep(150);
    controller.press(ButtonAction.D_LEFT);
    sleep(150);
    controller.press(ButtonAction.PLUS);
    sleep(150);
    controller.press(ButtonAction.MINUS);
    sleep(150);
    controller.press(ButtonAction.CAPTURE);
    sleep(5000);
    controller.press(ButtonAction.HOME);
  }

  @Override
  public String getScriptName() {
    return "Test";
  }

  @Override
  public boolean isLoop() {
    return false;
  }
}

package com.duanxr.pgcon.script.impl.common;

import com.duanxr.pgcon.output.action.ButtonAction;
import com.duanxr.pgcon.output.action.StickAction;
import com.duanxr.pgcon.script.api.MainScript;
import com.duanxr.pgcon.script.ScriptEngine;
import org.springframework.stereotype.Component;

/**
 * @author 段然 2022/7/25
 */
@Component
public class PlusOneDay extends ScriptEngine implements MainScript {

  @Override
  public String getScriptName() {
    return "Plus One Day";
  }

  @Override
  public void run() {
    //controller.press(ButtonAction.HOME);
    sleep(3000);
    controller.press(ButtonAction.D_BOTTOM);
    sleep(150);
    controller.press(ButtonAction.D_RIGHT);
    sleep(150);
    controller.press(ButtonAction.D_RIGHT);
    sleep(150);
    controller.press(ButtonAction.D_RIGHT);
    sleep(150);
    controller.press(ButtonAction.D_RIGHT);
    sleep(150);
    controller.press(ButtonAction.D_RIGHT);
    sleep(150);
    controller.press(ButtonAction.A);
    sleep(600);
    controller.press(StickAction.L_BOTTOM);
    sleep(1600);
    controller.press(StickAction.L_CENTER);
    sleep(150);
    controller.press(ButtonAction.D_RIGHT);
    sleep(600);
    controller.press(StickAction.L_BOTTOM);
    sleep(700);
    controller.press(StickAction.L_CENTER);

  }

}

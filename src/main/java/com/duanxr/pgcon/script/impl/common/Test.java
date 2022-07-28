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
    press(A);
    sleep(150);
    press(B);
    sleep(150);
    press(X);
    sleep(150);
    press(Y);
    sleep(150);
    press(L);
    sleep(150);
    press(R);
    sleep(150);
    press(ZL);
    sleep(150);
    press(ZR);
    sleep(150);
    press(L_STICK);
    sleep(150);
    press(R_STICK);
    sleep(150);
    press(D_TOP);
    sleep(150);
    press(D_BOTTOM);
    sleep(150);
    press(D_RIGHT);
    sleep(150);
    press(D_LEFT);
    sleep(150);
    press(PLUS);
    sleep(150);
    press(MINUS);
    sleep(150);
    press(CAPTURE);
    sleep(5000);
    press(HOME);
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

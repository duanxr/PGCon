package com.duanxr.pgcon.core.script.runnable.common;

import com.duanxr.pgcon.core.script.BaseScript;
import com.duanxr.pgcon.core.script.ScriptLoader;
import com.duanxr.pgcon.output.action.ButtonAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author 段然 2021/12/9
 */
@Component
public class PressA extends BaseScript {

  @Override
  protected void execute() throws Exception {
    controller.press(ButtonAction.A);
    Thread.sleep(100);
  }

  @Override
  public String name() {
    return "Press A";
  }

  public PressA(@Autowired ScriptLoader scriptLoader) {
    super(scriptLoader);
  }
}

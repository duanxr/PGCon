package com.duanxr.pgcon.core.script.runnable.common;

import com.duanxr.pgcon.core.PGCon;
import com.duanxr.pgcon.core.script.RunnableScript;
import com.duanxr.pgcon.core.script.ScriptLoader;
import com.duanxr.pgcon.output.action.ButtonAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author 段然 2021/12/9
 */
@Component
public class PressA extends RunnableScript {

  public PressA(@Autowired PGCon pg) {
    super(pg);
  }

  @Override
  public String getName() {
    return "Press A";
  }
  @Override
  protected void execute() {
    controller.press(ButtonAction.A);
    sleep(1000);
  }

}

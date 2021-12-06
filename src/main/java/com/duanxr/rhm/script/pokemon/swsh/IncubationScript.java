package com.duanxr.rhm.script.pokemon.swsh;


import static com.duanxr.rhm.script.pokemon.swsh.define.SwshDefineImageOcrCondition.EGG_TALK_START;
import static com.duanxr.rhm.script.pokemon.swsh.define.SwshDefineImageOcrCondition.EGG_TURN_BACK;
import static com.duanxr.rhm.script.pokemon.swsh.define.SwshDefineImageTemplateCondition.EGG_TALK_ENABLE;
import static com.duanxr.rhm.script.pokemon.swsh.define.SwshDefineImageTemplateCondition.EGG_TALK_ENABLE_N;

import com.duanxr.rhm.config.ConstantConfig;
import com.duanxr.rhm.core.handler.action.ButtonAction;
import com.duanxr.rhm.core.handler.action.StickSimpleAction;
import com.duanxr.rhm.script.ExecutableScript;
import com.duanxr.rhm.script.Subscript;

/**
 * @author Duanran 2019/12/17
 */
public class IncubationScript extends ExecutableScript {

  public IncubationScript() {
    subscriptList.add(new IncubationSubscript());
  }

  public class IncubationSubscript implements Subscript {

    @Override
    public Object call() {
      press(StickSimpleAction.L_LEFT, 450);
      press(StickSimpleAction.L_TOP, 450);
      press(StickSimpleAction.L_LEFT);
      waitText(EGG_TALK_START, () -> press(ButtonAction.A));
      sleep(800);
      press(ButtonAction.B);
      sleep(800);
      press(ButtonAction.B);
      sleep(800);
      press(ButtonAction.B);
      sleep(800);
      press(StickSimpleAction.L_TOP, 450);
      loop();
      return null;
    }

    private void loop() {
      sleep(300);
      press(StickSimpleAction.L_RIGHT);
      sleep(300);
      press(ButtonAction.PLUS);
      sleep(300);
      hold(StickSimpleAction.L_RIGHT);
      sleep(1000);
      waitText(EGG_TURN_BACK, () -> press(ButtonAction.A));
      release(StickSimpleAction.L_RIGHT);
      press(ButtonAction.B);
      sleep(300);
      press(ButtonAction.B);
      sleep(300);
      press(ButtonAction.PLUS);
      sleep(800);
      press(StickSimpleAction.L_TOP, 450);
      sleep(300);
      press(StickSimpleAction.L_LEFT, 400);
      sleep(800);
      press(ButtonAction.PLUS);
      sleep(300);
      hold(StickSimpleAction.L_LEFT);
      sleep(800);
      while (!isImageExits(0.9,EGG_TALK_ENABLE,EGG_TALK_ENABLE_N) ) {
      }
      release(StickSimpleAction.L_LEFT);
      press(ButtonAction.PLUS);
      sleep(300);
      hold(StickSimpleAction.L_TOP);
      waitText(EGG_TALK_START, () -> press(ButtonAction.A));
      release(StickSimpleAction.L_TOP);
      sleep(300);
      press(ButtonAction.B);
      sleep(800);
      press(ButtonAction.B);
      sleep(800);
      press(ButtonAction.B);
      sleep(800);
    }
  }
}

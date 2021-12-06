package com.duanxr.rhm.script.pokemon.swsh;


import com.duanxr.rhm.core.handler.action.ButtonAction;
import com.duanxr.rhm.script.ExecutableScript;
import com.duanxr.rhm.script.Subscript;

/**
 * @author Duanran 2019/12/17
 */
public class PressExecutableScript extends ExecutableScript {

  public PressExecutableScript(ButtonAction... buttonActions) {
    for (ButtonAction action : buttonActions) {
      subscriptList.add(new PressSubscript(action));
    }
  }

  public class PressSubscript implements Subscript {

    private ButtonAction action;

    public PressSubscript(ButtonAction action) {
      this.action = action;
    }

    @Override
    public Object call() {
      press(action);
      return null;
    }
  }
}

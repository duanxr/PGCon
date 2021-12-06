package com.duanxr.rhm.script.pokemon.swsh.define;

import com.duanxr.rhm.cache.loadable.LoadableScript;
import com.duanxr.rhm.core.handler.action.ButtonAction;
import com.duanxr.rhm.script.DefineScript;
import com.duanxr.rhm.script.Script;
import com.duanxr.rhm.script.pokemon.swsh.IncubationScript;
import com.duanxr.rhm.script.pokemon.swsh.PressExecutableScript;
import com.duanxr.rhm.script.pokemon.swsh.RotomLootExecutableScript;

/**
 * @author Duanran 2019/12/21
 */
public enum SwshDefineScript implements DefineScript {

  INCUBATION_SCRIPT("无情孵蛋", new IncubationScript()),
  ROTOM_LOOT_SCRIPT("狂日电鬼", new RotomLootExecutableScript()),
  PRESS_A_SCRIPT("A键连射", new PressExecutableScript(ButtonAction.A)),
  PRESS_B_SCRIPT("B键连射", new PressExecutableScript(ButtonAction.B));

  private LoadableScript loadableScript;

  SwshDefineScript(String name, Script script) {
    this.loadableScript = new LoadableScript(name, script);
  }

  @Override
  public String getName() {
    return this.loadableScript.getName();
  }

  @Override
  public Script getScript() {
    return loadableScript.getScript();
  }
}

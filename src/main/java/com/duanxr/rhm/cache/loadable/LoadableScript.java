package com.duanxr.rhm.cache.loadable;

import com.duanxr.rhm.cache.ScriptCache;
import com.duanxr.rhm.script.DefineScript;
import com.duanxr.rhm.script.Script;

/**
 * @author Duanran 2019/12/21
 */
public class LoadableScript implements DefineScript {

  protected Script script;

  protected String name;

  public LoadableScript(String name, Script script) {
    this.name = name;
    this.script = script;
    ScriptCache.load(this);
  }

  @Override
  public String getName() {
    return this.name;
  }

  @Override
  public Script getScript() {
    return this.script;
  }
}

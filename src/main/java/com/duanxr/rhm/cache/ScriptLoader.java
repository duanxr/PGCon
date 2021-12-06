package com.duanxr.rhm.cache;

import com.duanxr.rhm.core.execute.ScriptExecutor;
import com.duanxr.rhm.script.DefineScript;
import com.duanxr.rhm.script.pokemon.swsh.define.SwshDefineScript;
import org.springframework.stereotype.Component;

/**
 * @author Duanran 2019/12/18
 */
@Component
public class ScriptLoader {

  private final ScriptExecutor scriptExecutor;

  public ScriptLoader(ScriptExecutor scriptExecutor) {
    this.scriptExecutor = scriptExecutor;
  }

  public void load() {
    loadScript(SwshDefineScript.values());
  }

  private void loadScript(DefineScript... defineScript) {
    for (DefineScript script : defineScript) {
      script.getScript().setExecutor(scriptExecutor);
    }
  }
}

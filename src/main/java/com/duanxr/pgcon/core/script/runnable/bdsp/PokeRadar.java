package com.duanxr.pgcon.core.script.runnable.bdsp;

import com.duanxr.pgcon.core.script.BaseScript;
import com.duanxr.pgcon.core.script.ScriptLoader;
import com.duanxr.pgcon.core.util.Pokemon;
import com.duanxr.pgcon.output.action.ButtonAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author 段然 2021/12/9
 */
@Component
public class PokeRadar extends BaseScript {

  @Override
  protected void execute() throws Exception {
    pokemon.gridAnalyze(this);
  }

  @Override
  public String name() {
    return "Pokemon Radar";
  }

  public PokeRadar(@Autowired ScriptLoader scriptLoader) {
    super(scriptLoader);
  }
}

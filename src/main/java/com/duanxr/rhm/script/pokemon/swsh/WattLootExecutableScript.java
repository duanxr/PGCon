package com.duanxr.rhm.script.pokemon.swsh;

import com.duanxr.rhm.core.execute.ScriptExecutor;
import com.duanxr.rhm.script.ExecutableScript;
import com.duanxr.rhm.script.Subscript;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Duanran 2019/12/17
 */
@Slf4j
public class WattLootExecutableScript extends ExecutableScript {

  public WattLootExecutableScript() {
    subscriptList.add(new BoxArrowFindSubscript());
  }

  public class BoxArrowFindSubscript implements Subscript {

    @Override
    public Object call() {
      return null;
    }
  }

}

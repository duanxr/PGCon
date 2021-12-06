package com.duanxr.rhm.cache;

import com.duanxr.rhm.cache.loadable.LoadableScript;
import com.duanxr.rhm.script.Script;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Duanran 2019/12/18
 */
public class ScriptCache {

  private static Map<String, Script> cache = new ConcurrentHashMap<>();

  public static void put(String name, Script script) {
    cache.put(name, script);
  }

  public static Script get(String name) {
    return cache.get(name);
  }

  public static List<String> getScriptList() {
    return new ArrayList<>(cache.keySet());
  }

  public static void load(LoadableScript loadableScript) {
    put(loadableScript.getName(), loadableScript.getScript());
  }
}

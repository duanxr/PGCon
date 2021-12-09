package com.duanxr.pgcon.core.script;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Duanran 2019/12/18
 */
public class ScriptCache {

  private static final List<Script> CACHE = new LinkedList<>();

  public static void add(Script script) {
    CACHE.add(script);
  }

  public static List<Script> getScriptList() {
    return CACHE;
  }

}

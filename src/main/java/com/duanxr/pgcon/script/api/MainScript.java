package com.duanxr.pgcon.script.api;

/**
 * @author 段然 2021/12/9
 */
public interface MainScript extends Script {

  boolean isLoop();

  default void load() {
    // do nothing
  }

  default void clear() {
    // do nothing
  }

}

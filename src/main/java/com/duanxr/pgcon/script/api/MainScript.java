package com.duanxr.pgcon.script.api;

/**
 * @author 段然 2021/12/9
 */
public interface MainScript extends Runnable,Script {

  @Override
  void run();

}

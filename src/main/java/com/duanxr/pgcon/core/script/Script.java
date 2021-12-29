package com.duanxr.pgcon.core.script;

/**
 * @author 段然 2021/12/9
 */
public interface Script extends Runnable {

  @Override
  void run();

  void stop();

  String getName();

}

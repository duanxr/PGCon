package com.duanxr.pgcon.log;

/**
 * @author 段然 2022/8/19
 */
public interface Logger {

  void debug(String msg, Object... args);

  void info(String msg, Object... args);
  void warn(String msg, Object... args);

  void error(String msg, Object... args);

}

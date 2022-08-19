package com.duanxr.pgcon.test;

import com.duanxr.pgcon.log.GuiLogger;
import lombok.extern.log4j.Log4j2;

/**
 * @author 段然 2022/8/19
 */
public class LogUtilTest {

  public static void main(String[] args) {
    GuiLogger guiLogger = new GuiLogger(null);
    guiLogger.info("test111");
  }

}

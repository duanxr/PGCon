package com.duanxr.pgcon.util;

import lombok.experimental.UtilityClass;
import org.apache.logging.log4j.spi.ExtendedLogger;

/**
 * @author 段然 2022/7/29
 */

@UtilityClass
public final class LogUtil {
  public static Object formatToString(String format, Object... args) {
    return new Object() {
      @Override
      public String toString() {
        return String.format(format, args);
      }
    };
  }



}

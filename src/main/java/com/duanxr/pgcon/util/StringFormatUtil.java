package com.duanxr.pgcon.util;

import lombok.experimental.UtilityClass;

/**
 * @author 段然 2022/7/29
 */

@UtilityClass
public final class StringFormatUtil {

  public static Object formatObj(String format, Object... args) {
    return new Object() {
      @Override
      public String toString() {
        return String.format(format, args);
      }
    };
  }

  public static Object format(String format, Object... args) {
    return String.format(format, args);
  }


}

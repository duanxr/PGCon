package com.duanxr.pgcon.util;

import lombok.experimental.UtilityClass;
import org.opencv.core.Point;

/**
 * @author 段然 2021/12/29
 */
@UtilityClass
public class MatUtil {

  public static Point calculateInterceptionPoint(Point s1, Point s2, Point d1, Point d2) {
    double a1 = s2.y - s1.y;
    double b1 = s1.x - s2.x;
    double c1 = a1 * s1.x + b1 * s1.y;
    double a2 = d2.y - d1.y;
    double b2 = d1.x - d2.x;
    double c2 = a2 * d1.x + b2 * d1.y;
    double delta = a1 * b2 - a2 * b1;
    return new Point((float) ((b2 * c1 - b1 * c2) / delta),
        (float) ((a1 * c2 - a2 * c1) / delta));
  }
}

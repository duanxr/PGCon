package com.duanxr.pgcon.util;

import lombok.Data;
import org.opencv.core.Point;

/**
 * @author Duanran 2019/12/13
 */
@Data
public class CachedImageArea {

  private int number = 0;
  private Point leftTopPoint;
  private Point rightBottomPoint;

  public CachedImageArea(int left, int top, int right, int bottom) {
    leftTopPoint = new Point(left, top);
    rightBottomPoint = new Point(right, bottom);
  }

  public int getLeft() {
    return (int) this.leftTopPoint.x;
  }

  public int getTop() {
    return (int) this.leftTopPoint.y;
  }

  public int getRight() {
    return (int) this.rightBottomPoint.x;
  }

  public int getBottom() {
    return (int) this.rightBottomPoint.y;
  }
}

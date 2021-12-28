package com.duanxr.pgcon.core.detect;

import lombok.Data;

/**
 * @author 段然 2021/12/6
 */
@Data
public class Area {

  private int x;
  private int y;
  private int width;
  private int height;

  private Area(int x, int y, int width, int height) {
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
  }

  public static Area ofRect(int x, int y, int weight, int high) {
    return new Area(x, y, weight, high);
  }

  public static Area ofPoint(int x1, int y1, int x2, int y2) {
    int x = Math.min(x1, x2);
    int y = Math.min(y1, y2);
    int weight = Math.max(x1, x2) - x;
    int high = Math.max(y1, y2) - y;
    return new Area(x, y, weight, high);
  }

  public static Area ofLines(int left, int top, int right, int bottom) {
    return new Area(left, top, right - left, bottom - top);
  }

  public int getLeft() {
    return x;
  }

  public int getTop() {
    return y;
  }

  public int getRight() {
    return x + width;
  }

  public int getBottom() {
    return y + height;
  }

}

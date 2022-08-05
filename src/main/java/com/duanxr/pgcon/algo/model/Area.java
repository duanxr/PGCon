package com.duanxr.pgcon.algo.model;

import lombok.Getter;

/**
 * @author 段然 2021/12/6
 */
@Getter
public class Area {

  private final int x;
  private final int y;
  private final int width;
  private final int height;

  private Area(int x, int y, int width, int height) {
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
  }

  public static Area ofRect(int x, int y, int weight, int high) {
    return new Area(x, y, weight, high);
  }

  public static Area ofPoints(int x1, int y1, int x2, int y2) {
    int x = Math.min(x1, x2);
    int y = Math.min(y1, y2);
    int weight = Math.max(x1, x2) - x;
    int high = Math.max(y1, y2) - y;
    return new Area(x, y, weight, high);
  }

  public static Area ofLines(int left, int top, int right, int bottom) {
    return new Area(left, top, right - left, bottom - top);
  }

  public static Area ofRect(float x, float y, float weight, float high) {
    return Area.ofRect((int) x, (int) y, (int) weight, (int) high);
  }

  public static Area ofPoints(float x1, float y1, float x2, float y2) {
    return Area.ofPoints((int) x1, (int) y1, (int) x2, (int) y2);
  }

  public static Area ofLines(float left, float top, float right, float bottom) {
    return Area.ofLines((int) left, (int) top, (int) right, (int) bottom);
  }

  public static Area ofRect(double x, double y, double weight, double high) {
    return Area.ofRect((int) x, (int) y, (int) weight, (int) high);
  }

  public static Area ofPoints(double x1, double y1, double x2, double y2) {
    return Area.ofPoints((int) x1, (int) y1, (int) x2, (int) y2);
  }

  public static Area ofLines(double left, double top, double right, double bottom) {
    return Area.ofLines((int) left, (int) top, (int) right, (int) bottom);
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

package com.duanxr.pgcon.gui.display.impl;

import com.duanxr.pgcon.gui.display.api.BaseDrawable;
import com.duanxr.pgcon.gui.display.api.Drawable;
import java.awt.Color;
import java.awt.Graphics;
import lombok.Getter;
import lombok.Setter;

/**
 * @author 段然 2021/12/9
 */
@Getter
@Setter
public class Line extends BaseDrawable implements Drawable {

  private int x1;
  private int y1;
  private int x2;
  private int y2;
  private Color color;

  public Line(int x1, int y1, int x2, int y2, Color color, int duration) {
    super(duration);
    this.x1 = x1;
    this.y1 = y1;
    this.x2 = x2;
    this.y2 = y2;
    this.color = color;
  }

  public Line(int x1, int y1, int x2, int y2, Color color) {
    this.x1 = x1;
    this.y1 = y1;
    this.x2 = x2;
    this.y2 = y2;
    this.color = color;
  }

  @Override
  public void draw(Graphics graphics) {
    graphics.setColor(this.getColor());
    graphics.drawLine(x1, y1, x2, y2);
  }
}

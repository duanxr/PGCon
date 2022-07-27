package com.duanxr.pgcon.gui.display.canvas.impl;

import com.duanxr.pgcon.gui.display.canvas.api.BaseDrawable;
import com.duanxr.pgcon.gui.display.canvas.api.Drawable;
import java.awt.Color;
import java.awt.Graphics;
import lombok.Getter;
import lombok.Setter;

/**
 * @author 段然 2021/12/9
 */
@Getter
@Setter
public class Circle extends BaseDrawable implements Drawable {

  private int x;
  private int y;
  private int r;
  private Color color;

  public Circle(int x, int y, int r, Color color, int duration) {
    super(duration);
    this.x = x;
    this.y = y;
    this.r = r;
    this.color = color;
  }

  public Circle(int x, int y, int r, Color color) {
    this.x = x;
    this.y = y;
    this.r = r;
    this.color = color;
  }

  @Override
  public void draw(Graphics graphics) {
    graphics.setColor(this.getColor());
    graphics.fillOval(x - (r / 2), y - (r / 2), r, r);
  }
}

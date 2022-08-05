package com.duanxr.pgcon.gui.display.impl;

import com.duanxr.pgcon.core.model.Area;
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
public class Oval extends BaseDrawable implements Drawable {

  private Area area;
  private Color color;

  public Oval(Area area, Color color, int duration) {
    super(duration);
    this.area = area;
    this.color = color;
  }

  public Oval(Area area, Color color) {
    this.area = area;
    this.color = color;
  }

  @Override
  public void draw(Graphics graphics) {
    graphics.setColor(this.getColor());
    graphics.fillOval(area.getX(), area.getY(), area.getWidth(), area.getHeight());
  }
}

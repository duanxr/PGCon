package com.duanxr.pgcon.gui.draw;

import com.duanxr.pgcon.core.detect.model.Area;
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

package com.duanxr.pgcon.gui.display.impl;

import com.duanxr.pgcon.core.model.Area;
import com.duanxr.pgcon.gui.display.api.BaseDrawable;
import com.duanxr.pgcon.gui.display.api.Drawable;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import lombok.Getter;
import lombok.Setter;

/**
 * @author 段然 2021/12/9
 */
@Getter
@Setter
public class Picture extends BaseDrawable implements Drawable {

  private Image image;
  private Area area;
  private Color color;

  public Picture(Image image, Area area, Color color, int duration) {
    super(duration);
    this.image = image;
    this.area = area;
    this.color = color;
  }

  public Picture(Image image, Area area, Color color) {
    this.image = image;
    this.area = area;
    this.color = color;
  }

  @Override
  public void draw(Graphics graphics) {
    graphics.setColor(this.getColor());
    graphics.drawImage(image,this.getArea().getX(), this.getArea().getY(),
        this.getArea().getWidth(), this.getArea().getHeight(), null);
  }
}

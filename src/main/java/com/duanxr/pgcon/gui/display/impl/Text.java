package com.duanxr.pgcon.gui.display.impl;

import com.duanxr.pgcon.core.model.Area;
import com.duanxr.pgcon.gui.display.api.BaseDrawable;
import com.duanxr.pgcon.gui.display.api.Drawable;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import lombok.Getter;
import lombok.Setter;

/**
 * @author 段然 2021/12/9
 */
@Getter
@Setter
public class Text extends BaseDrawable implements Drawable {

  private static final Font MICROSOFT_YA_HEI = new Font("Microsoft YaHei", Font.PLAIN, 18);

  private Area area;
  private String text;
  private Color color;
  private double fontSize;

  public Text(Area area, String text, Color color, double fontSize, int duration) {
    super(duration);
    this.area = area;
    this.text = text;
    this.color = color;
    this.fontSize = fontSize;
  }

  public Text(Area area, String text, Color color, double fontSize) {
    this.area = area;
    this.text = text;
    this.color = color;
    this.fontSize = fontSize;
  }

  @Override
  public void draw(Graphics graphics) {
    graphics.setColor(this.getColor());
    if (MICROSOFT_YA_HEI.getSize() != (int) this.getFontSize()) {
      graphics.setFont(MICROSOFT_YA_HEI.deriveFont((float) this.getFontSize()));
    } else {
      graphics.setFont(MICROSOFT_YA_HEI);
    }
    FontMetrics metrics = graphics.getFontMetrics(graphics.getFont());
    int x = this.getArea().getX()
        + (this.getArea().getWidth() - metrics.stringWidth(this.getText())) / 2;
    int y = this.getArea().getY() + ((this.getArea().getHeight() - metrics.getHeight()) / 2)
        + metrics.getAscent();
    graphics.drawString(this.getText(), x, y);
  }
}

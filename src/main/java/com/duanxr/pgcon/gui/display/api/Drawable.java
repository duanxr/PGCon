package com.duanxr.pgcon.gui.display.api;

import java.awt.Graphics;

/**
 * @author 段然 2021/12/9
 */
public interface Drawable {

  void draw(Graphics graphics);

  boolean isExpired();

  long getExpireTime();

  long getDuration();

}
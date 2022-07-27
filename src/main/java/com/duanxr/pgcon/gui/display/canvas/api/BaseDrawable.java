package com.duanxr.pgcon.gui.display.canvas.api;

import com.duanxr.pgcon.gui.display.canvas.api.Drawable;

/**
 * @author 段然 2021/12/9
 */
public abstract class BaseDrawable implements Drawable {

  protected final long expireTime;

  protected BaseDrawable(int duration) {
    expireTime = System.currentTimeMillis() + duration;
  }

  protected BaseDrawable() {
    expireTime = Long.MAX_VALUE;
  }

  @Override
  public boolean isExpired() {
    return System.currentTimeMillis() > expireTime;
  }
}

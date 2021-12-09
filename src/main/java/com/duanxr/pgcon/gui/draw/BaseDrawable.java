package com.duanxr.pgcon.gui.draw;

/**
 * @author 段然 2021/12/9
 */
public abstract class BaseDrawable implements Drawable {

  protected final long expireTime;

  BaseDrawable(int duration) {
    expireTime = System.currentTimeMillis() +duration;
  }

  BaseDrawable() {
    expireTime = Long.MAX_VALUE;
  }

  @Override
  public boolean isExpired() {
    return System.currentTimeMillis()>expireTime;
  }
}

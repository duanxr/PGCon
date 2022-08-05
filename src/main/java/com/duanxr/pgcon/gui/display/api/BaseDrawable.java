package com.duanxr.pgcon.gui.display.api;

/**
 * @author 段然 2021/12/9
 */
public abstract class BaseDrawable implements Drawable {

  protected final long expireTime;

  protected final long duration;

  protected BaseDrawable(int duration) {
    this.expireTime = System.currentTimeMillis() + duration;
    this.duration = duration;
  }

  protected BaseDrawable() {
    expireTime = Long.MAX_VALUE;
    duration = -1;
  }

  @Override
  public boolean isExpired() {
    return System.currentTimeMillis() > expireTime;
  }

  @Override
  public long getExpireTime() {
    return expireTime;
  }

  @Override
  public long getDuration() {
    return duration;
  }
}

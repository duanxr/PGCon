package com.duanxr.pgcon.gui.draw;

import java.awt.Graphics;

/**
 * @author 段然 2021/12/9
 */
public interface Drawable {
    void draw(Graphics graphics);

    boolean isExpired();
}

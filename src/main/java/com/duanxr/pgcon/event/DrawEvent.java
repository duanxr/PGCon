package com.duanxr.pgcon.event;

import com.duanxr.pgcon.gui.display.canvas.api.Drawable;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author 段然 2021/12/15
 */
@Data
@AllArgsConstructor
public class DrawEvent {

  private String key;
  private Drawable drawable;
}

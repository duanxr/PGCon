package com.duanxr.pgcon.gui.display;

import com.duanxr.pgcon.gui.display.api.Drawable;
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

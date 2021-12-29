package com.duanxr.pgcon.event;

import java.awt.image.BufferedImage;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author 段然 2021/12/15
 */
@Data
@AllArgsConstructor
public class FrameEvent {

  private BufferedImage frame;
  private long timestamp;
}

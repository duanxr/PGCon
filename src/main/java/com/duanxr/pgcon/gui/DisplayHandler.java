/*
package com.duanxr.pgcon.gui;


import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.opencv.core.Mat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

*/
/**
 * @author Duanran 2019/12/21
 *//*

@Service
public class DisplayHandler {


  private static final Color BACK_COLOR = new Color(249, 38, 114, 180);
  private static final Color BACK_DDAD_COLOR = new Color(18, 249, 43, 128);
  private static final Color TEXT_COLOR = new Color(226, 226, 226);
  private static final Font MICROSOFT_YA_HEI = new Font("Microsoft YaHei", Font.PLAIN, 18);

  @Autowired
  private InputHandler inputHandler;

  private Map<String, DrawArea> detectRects;

  private List<String> needRemoveKey;

  public DisplayHandler() {
    this.detectRects = new HashMap<>();
    this.needRemoveKey = new ArrayList<>();
  }

  public BufferedImage getImage() {
    for (Entry<String, DrawArea> drawAreaEntry : detectRects.entrySet()) {
      DrawArea drawArea = drawAreaEntry.getValue();
      if (System.currentTimeMillis() - drawArea.getTime()
          > TEMPLATE_MATCH_RECT_SHOW_TIME) {
        needRemoveKey.add(drawAreaEntry.getKey());
      }
    }
    for (String key : needRemoveKey) {
      detectRects.remove(key);
    }
    needRemoveKey.clear();
    Mat image = new Mat();
    inputHandler.getStream().copyTo(image);
    BufferedImage bufferedImage = MatUtil.toBufferedImage(image);
    Graphics graphics = bufferedImage.getGraphics();
    for (Entry<String, DrawArea> drawArea : detectRects.entrySet()) {
      if (drawArea.getKey().equals("DDAD")) {
        graphics.setColor(BACK_DDAD_COLOR);
      } else {
        graphics.setColor(BACK_COLOR);
      }
      graphics
          .fillRect(drawArea.getValue().getCachedImageArea().getLeft(),
              drawArea.getValue().getCachedImageArea().getTop(),
              drawArea.getValue().getCachedImageArea().getRight() - drawArea.getValue()
                  .getCachedImageArea().getLeft(),
              drawArea.getValue().getCachedImageArea().getBottom() - drawArea.getValue()
                  .getCachedImageArea().getTop());
      graphics.setColor(TEXT_COLOR);
      graphics.setFont(MICROSOFT_YA_HEI);
      graphics.drawString(drawArea.getValue().getText(),
          drawArea.getValue().getCachedImageArea().getLeft() + 5,
          drawArea.getValue().getCachedImageArea().getBottom() - 5);
    }
    return bufferedImage;
  }

  public void setDetectRect(CachedImageArea cachedImageArea, String key, String text) {
    DrawArea drawArea = new DrawArea(cachedImageArea, System.currentTimeMillis(), text);
    this.detectRects.put(key, drawArea);
  }

  @Getter
  @AllArgsConstructor
  public static class DrawArea {

    private CachedImageArea cachedImageArea;
    private long time;
    private String text;
  }
}
*/

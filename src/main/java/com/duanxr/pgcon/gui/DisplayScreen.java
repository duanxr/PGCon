package com.duanxr.pgcon.gui;

import com.duanxr.pgcon.config.GuiConfig;
import com.duanxr.pgcon.config.InputConfig;
import com.duanxr.pgcon.core.detect.model.Area;
import com.duanxr.pgcon.event.DrawEvent;
import com.duanxr.pgcon.gui.draw.Rectangle;
import com.google.common.eventbus.EventBus;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author 段然 2021/12/9
 */
@Slf4j
@Component
public class DisplayScreen extends JLabel {

  private static final String MOUSE_DRAGGED_KEY = "mouseDragged";
  private final double xScale;
  private final double yScale;
  private final ImageIcon imageIcon;
  private BufferedImage frame;
  private Point pointStart = null;
  private Point pointEnd = null;

  public DisplayScreen(InputConfig inputConfig, GuiConfig guiConfig, EventBus eventBus) {
    this.xScale = 1D * inputConfig.getWidth() / guiConfig.getWidth();
    this.yScale = 1D * inputConfig.getHeight() / guiConfig.getHeight();
    Dimension dimension = new Dimension(guiConfig.getWidth(),
        guiConfig.getHeight());
    this.imageIcon = new ImageIcon();
    this.setIcon(imageIcon);
    this.setMinimumSize(dimension);
    this.setPreferredSize(dimension);
    this.setMaximumSize(dimension);
    this.addMouseListener(new MouseAdapter() {
      public void mousePressed(MouseEvent event) {
        pointStart = event.getPoint();
      }

      public void mouseReleased(MouseEvent event) {
        try {
          Area area = Area.ofPoints((pointStart.x * xScale),  (pointStart.y * yScale),
              (pointEnd.x * xScale),  (pointEnd.y * yScale));
          BufferedImage subImage = frame.getSubimage(area.getX(), area.getY(), area.getWidth(),
              area.getHeight());
          File file = File.createTempFile("PGCon", ".png");
          ImageIO.write(subImage, "png", file);
          log.info("拖动区域ofPoints: {},{},{},{}, 已保存到: {}",
              (int) (pointStart.x * xScale), (int) (pointStart.y * yScale),
              (int) (pointEnd.x * xScale), (int) (pointEnd.y * yScale), file.getAbsolutePath());
        } catch (Exception e) {
          log.error("", e);
        }
        pointStart = null;
      }
    });
    this.addMouseMotionListener(new MouseMotionAdapter() {
      public void mouseMoved(MouseEvent event) {
        pointEnd = event.getPoint();
      }

      public void mouseDragged(MouseEvent event) {
        pointEnd = event.getPoint();
        int startX = (int) (pointStart.x * xScale);
        int startY = (int) (pointStart.y * yScale);
        int endX = (int) (pointEnd.x * xScale);
        int endY = (int) (pointEnd.y * yScale);
        eventBus.post(
            new DrawEvent(MOUSE_DRAGGED_KEY,
                new Rectangle(
                    Area.ofPoints(startX, startY, endX, endY),
                    new Color(255, 0, 127, 128),
                    5000))
        );
      }
    });
  }

  public void repaint(BufferedImage draw, BufferedImage frame) {
    this.frame = frame;
    this.imageIcon.setImage(draw);
    this.repaint();
  }
}

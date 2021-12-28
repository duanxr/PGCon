package com.duanxr.pgcon.gui;

import static com.duanxr.pgcon.util.ConstantConfig.SIZE;

import com.duanxr.pgcon.core.detect.base.Area;
import com.duanxr.pgcon.event.DrawEvent;
import com.duanxr.pgcon.gui.draw.Rectangle;
import com.google.common.eventbus.EventBus;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author 段然 2021/12/9
 */
@Slf4j
@Component
public class DisplayScreen extends JLabel {

  private static final String MOUSE_DRAGGED_KEY = "mouseDragged";
  private static final double X_SCALE = 1920.0 / 1280.0;
  private static final double Y_SCALE = 1080.0 / 720.0;

  private final ImageIcon imageIcon;
  private Point pointStart = null;
  private Point pointEnd = null;

  @Autowired
  public DisplayScreen(EventBus eventBus) {
    Dimension dimension = new Dimension((int) SIZE.width, (int) SIZE.height);
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
        log.info("拖动区域: {},{},{},{}",
            (int) (pointStart.y * Y_SCALE), (int) (pointEnd.y * Y_SCALE),
            (int) (pointStart.x * X_SCALE), (int) (pointEnd.x * X_SCALE));
        pointStart = null;
      }
    });
    this.addMouseMotionListener(new MouseMotionAdapter() {
      public void mouseMoved(MouseEvent event) {
        pointEnd = event.getPoint();
      }

      public void mouseDragged(MouseEvent event) {
        pointEnd = event.getPoint();
        int startX = (int) (pointStart.x * X_SCALE);
        int startY = (int) (pointStart.y * Y_SCALE);
        int endX = (int) (pointEnd.x * X_SCALE);
        int endY = (int) (pointEnd.y * Y_SCALE);
        int rectStartPointX = Math.min(startX, endX);
        int rectStartPointY = Math.min(startY, endY);
        int rectEndPointX = Math.max(startX, endX);
        int rectEndPointY = Math.max(startY, endY);
        eventBus.post(
            new DrawEvent(MOUSE_DRAGGED_KEY,
                new Rectangle(
                    new Area(rectStartPointX, rectStartPointY,
                        rectEndPointX - rectStartPointX,
                        rectEndPointY - rectStartPointY),
                    new Color(255, 0, 127, 128),
                    5000))
        );
      }
    });
  }

  public void repaint(BufferedImage draw) {
    this.imageIcon.setImage(draw);
    this.repaint();
  }
}

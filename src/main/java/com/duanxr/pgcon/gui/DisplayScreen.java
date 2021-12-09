package com.duanxr.pgcon.gui;

import static com.duanxr.pgcon.util.ConstantConfig.SIZE;

import com.duanxr.pgcon.core.detect.Area;
import com.duanxr.pgcon.gui.draw.Rectangle;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.concurrent.TimeUnit;
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

  private final DisplayHandler displayHandler;
  private Point pointStart = null;
  private Point pointEnd = null;

  public DisplayScreen(DisplayHandler displayHandler) {
    GridBagConstraints bagConstraints = new GridBagConstraints();
    bagConstraints.fill = GridBagConstraints.NONE;
    bagConstraints.anchor = GridBagConstraints.EAST;
    bagConstraints.gridheight = 12;
    bagConstraints.gridwidth = 1;
    bagConstraints.gridx = 0;
    bagConstraints.gridy = 0;
    Dimension dimension = new Dimension((int) SIZE.width, (int) SIZE.height);
    this.setMinimumSize(dimension);
    this.setPreferredSize(dimension);
    this.setMaximumSize(dimension);
    this.displayHandler = displayHandler;
    displayHandler.setDisplayScreen(this);
    this.addMouseListener(new MouseAdapter() {
      public void mousePressed(MouseEvent event) {
        pointStart = event.getPoint();
      }
      public void mouseReleased(MouseEvent event) {
        log.info("拖动区域:  {},{},{},{}", pointStart.y, pointEnd.y, pointStart.x, pointEnd.x);
        pointStart = null;
      }
    });
    this.addMouseMotionListener(new MouseMotionAdapter() {
      public void mouseMoved(MouseEvent event) {
        //pointEnd = event.getPoint();
      }
      public void mouseDragged(MouseEvent event) {
        pointEnd = event.getPoint();
        displayHandler.addDrawRule("mouseDragged",new Rectangle(new Area(pointStart.x, pointStart.y, pointEnd.x - pointStart.x,
            pointEnd.y - pointStart.y), new Color(255, 0, 127, 128), 5000));
      }
    });
  }


}

package com.duanxr.pgcon.util;

import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import lombok.extern.slf4j.Slf4j;
import org.opencv.core.Mat;

/**
 * @author Duanran 2019/12/13
 */
@Slf4j
public class DebugUtil {

  public static ImageIcon icon;
  public static JLabel lbl;

  public static  JFrame frame;

  public static void displayImage(Mat m) {
    displayImage(ImageUtil.matToBufferedImage(m));
  }

  public static synchronized void displayImage(BufferedImage img2) {
    if (frame == null) {
      frame = new JFrame();
      icon = new ImageIcon();
      lbl = new JLabel();
      frame.add(lbl);
      lbl.setIcon(icon);
      frame.setLayout(new FlowLayout());
      frame.setSize(img2.getWidth(null) + 50, img2.getHeight(null) + 50);
      frame.setVisible(true);
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    File file = TempFileUtil.saveTempImage(img2);
    log.info("保存到: {}", file.getAbsolutePath());
    icon.setImage(img2);
    lbl.repaint();
  }

}

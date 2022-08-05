package com.duanxr.pgcon.util;

import java.awt.FlowLayout;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import org.opencv.core.Mat;

/**
 * @author Duanran 2019/12/13
 */
public class DebugUtil {
  public static void displayImage(Image img2) {
    ImageIcon icon = new ImageIcon(img2);
    JFrame frame = new JFrame();
    frame.setLayout(new FlowLayout());
    frame.setSize(img2.getWidth(null) + 50, img2.getHeight(null) + 50);
    JLabel lbl = new JLabel();
    lbl.setIcon(icon);
    frame.add(lbl);
    frame.setVisible(true);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  }
}

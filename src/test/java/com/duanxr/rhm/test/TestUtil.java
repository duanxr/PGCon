package com.duanxr.rhm.test;

import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import org.opencv.core.Mat;

/**
 * @author Duanran 2019/12/13
 */
public class TestUtil {

  private static final ImageIcon IMAGE_ICON = new ImageIcon();

  private static final JLabel J_LABEL = new JLabel();

  static {
    JFrame frame = new JFrame();
    frame.setLayout(new FlowLayout());
    frame.setSize(1920 + 50, 1080 + 50);
    J_LABEL.setIcon(IMAGE_ICON);
    frame.add(J_LABEL);
    frame.setVisible(true);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  }

  public static void displayImage(Mat m) {
    displayImage(Mat2BufferedImage(m));
  }

  public static BufferedImage Mat2BufferedImage(Mat m) {
    // Fastest code
    // output can be assigned either to a BufferedImage or to an CachedImageTemplate
    int type = BufferedImage.TYPE_BYTE_GRAY;
    if (m.channels() > 1) {
      type = BufferedImage.TYPE_3BYTE_BGR;
    }
    int bufferSize = m.channels() * m.cols() * m.rows();
    byte[] b = new byte[bufferSize];
    m.get(0, 0, b); // get all the pixels
    BufferedImage image = new BufferedImage(m.cols(), m.rows(), type);
    final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
    System.arraycopy(b, 0, targetPixels, 0, b.length);
    return image;
  }

  public static void displayImage(Image img2) {
    IMAGE_ICON.setImage(img2);
    J_LABEL.repaint();
  }
}

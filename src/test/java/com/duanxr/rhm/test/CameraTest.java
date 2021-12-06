package com.duanxr.rhm.test;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;


public class CameraTest {

  static JLabel lbl = new JLabel();
  static ImageIcon icon = new ImageIcon();

  public static void main(String[] args) throws Exception {
    nu.pattern.OpenCV.loadShared();

    Mat frame = new Mat();
    VideoCapture videoCapture = new VideoCapture(1);
    JFrame jframe = new JFrame("MyTitle");
    jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    JLabel vidpanel = new JLabel();
    jframe.setContentPane(vidpanel);
    jframe.setVisible(true);
    while (true) {
      if (videoCapture.read(frame)) {
        ImageIcon image = new ImageIcon(Mat2BufferedImage(frame));
        Thread.sleep(15);
        vidpanel.setIcon(image);
        vidpanel.repaint();

      }
    }

  }

  public static void showResult(Mat img) {
    Imgproc.resize(img, img, new Size(1280, 720));
    MatOfByte matOfByte = new MatOfByte();
    Imgcodecs.imencode(".jpg", img, matOfByte);
    byte[] byteArray = matOfByte.toArray();
    BufferedImage bufImage = null;
    try {
      InputStream in = new ByteArrayInputStream(byteArray);
      bufImage = ImageIO.read(in);
      JFrame frame = new JFrame();
      frame.getContentPane().add(new JLabel(new ImageIcon(bufImage)));
      frame.pack();
      frame.setVisible(true);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void displayImage(Mat m) {
    displayImage(Mat2BufferedImage(m));
  }

  public static BufferedImage Mat2BufferedImage(Mat m) {
    Imgproc.resize(m, m, new Size(1280, 720));
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

    icon.setImage(img2);
  }
}
/*
package boofCV;

import boofcv.abst.feature.detect.interest.ConfigFastHessian;
import boofcv.abst.feature.detect.interest.InterestPointDetector;
import boofcv.factory.feature.detect.interest.FactoryInterestPoint;
import boofcv.gui.feature.FancyInterestPointRender;
import boofcv.gui.image.ShowImages;
import boofcv.io.UtilIO;
import boofcv.io.image.ConvertBufferedImage;
import boofcv.io.image.UtilImageIO;
import boofcv.struct.image.GrayF32;
import boofcv.struct.image.ImageGray;
import georegression.struct.point.Point2D_F64;
import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class ExampleInterestPoint {

  public static <T extends ImageGray<T>>
  void detect(BufferedImage image, Class<T> imageType) {
    T input = ConvertBufferedImage.convertFromSingle(image, null, imageType);

    // Create a Fast Hessian detector from the SURF paper.
    // Other detectors can be used in this example too.
    InterestPointDetector<T> detector = FactoryInterestPoint.fastHessian(
        new ConfigFastHessian(10, 2, 100, 2, 9, 3, 4), imageType);

    // find interest points in the image
    detector.detect(input);

    // Show the features
    displayResults(image, detector);
  }

  private static <T extends ImageGray<T>>
  void displayResults(BufferedImage image, InterestPointDetector<T> detector) {
    Graphics2D g2 = image.createGraphics();
    FancyInterestPointRender render = new FancyInterestPointRender();

    for (int i = 0; i < detector.getNumberOfFeatures(); i++) {
      Point2D_F64 pt = detector.getLocation(i);

      // note how it checks the capabilities of the detector
      if (detector.hasScale()) {
        int radius = (int) detector.getRadius(i);
        render.addCircle((int) pt.x, (int) pt.y, radius);
      } else {
        render.addPoint((int) pt.x, (int) pt.y);
      }
    }
    // make the circle's thicker
    g2.setStroke(new BasicStroke(3));

    // just draw the features onto the input image
    render.draw(g2);
    ShowImages.showWindow(image, "Detected Features", true);
  }

  public static void main(String[] args) {
    BufferedImage image = UtilImageIO.loadImage(UtilIO.pathExample("sunflowers.jpg"));
    detect(image, GrayF32.class);
  }
}*/

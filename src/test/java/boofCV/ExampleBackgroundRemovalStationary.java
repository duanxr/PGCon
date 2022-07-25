package boofCV;

/**
 * @author 段然 2021/12/10
 */

import boofcv.alg.background.BackgroundModelStationary;
import boofcv.factory.background.ConfigBackgroundBasic;
import boofcv.factory.background.ConfigBackgroundGaussian;
import boofcv.factory.background.ConfigBackgroundGmm;
import boofcv.factory.background.FactoryBackgroundModel;
import boofcv.gui.binary.VisualizeBinaryData;
import boofcv.gui.image.ImageGridPanel;
import boofcv.gui.image.ShowImages;
import boofcv.io.MediaManager;
import boofcv.io.image.SimpleImageSequence;
import boofcv.io.wrapper.DefaultMediaManager;
import boofcv.misc.BoofMiscOps;
import boofcv.struct.image.GrayF32;
import boofcv.struct.image.GrayU8;
import boofcv.struct.image.ImageBase;
import boofcv.struct.image.ImageType;
import java.awt.image.BufferedImage;

/**
 * Example showing how to perform background modeling when the camera is assumed to be stationary. This scenario
 * can be computed much faster than the moving camera case and depending on the background model can some times produce
 * reasonable results when the camera has a little bit of jitter.
 *
 * @author Peter Abeles
 */
public class ExampleBackgroundRemovalStationary {
  public static void main( String[] args ) {
//		String fileName = UtilIO.pathExample("background/rubixfire.mp4"); // dynamic background
//		String fileName = UtilIO.pathExample("background/horse_jitter.mp4"); // degraded performance because of jitter
//		String fileName = UtilIO.pathExample("tracking/chipmunk.mjpeg"); // Camera moves. Stationary will fail here

    // Comment/Uncomment to switch input image type
    ImageType imageType = ImageType.single(GrayF32.class);
//		ImageType imageType = ImageType.il(3, InterleavedF32.class);
//		ImageType imageType = ImageType.il(3, InterleavedU8.class);

		//ConfigBackgroundGmm configGmm = new ConfigBackgroundGmm();

    // Comment/Uncomment to switch algorithms
    BackgroundModelStationary background =
        FactoryBackgroundModel.stationaryBasic(new ConfigBackgroundBasic(2, 0.9F), imageType);
				//FactoryBackgroundModel.stationaryGmm(configGmm, imageType);
//        FactoryBackgroundModel.stationaryGaussian(new ConfigBackgroundGaussian(5, 0.7F), imageType);
    MediaManager media = DefaultMediaManager.INSTANCE;
    SimpleImageSequence video =
        media.openCamera("0",640,480, background.getImageType());
//				media.openCamera(null,640,480,background.getImageType());

    // Declare storage for segmented image. 1 = moving foreground and 0 = background
    GrayU8 segmented = new GrayU8(video.getWidth(), video.getHeight());

    var visualized = new BufferedImage(segmented.width, segmented.height, BufferedImage.TYPE_INT_RGB);
    var gui = new ImageGridPanel(1, 2);
    gui.setImages(visualized, visualized);

    ShowImages.showWindow(gui, "Static Scene: Background Segmentation", true);

    double fps = 0;
    double alpha = 0.01; // smoothing factor for FPS

    while (video.hasNext()) {
      ImageBase input = video.next();

      long before = System.nanoTime();
      background.updateBackground(input, segmented);
      long after = System.nanoTime();

      fps = (1.0 - alpha)*fps + alpha*(1.0/((after - before)/1e9));

      VisualizeBinaryData.renderBinary(segmented, false, visualized);
      //gui.setImage(0, 0, (BufferedImage)video.getGuiImage());
      gui.setImage(0, 1, visualized);
      gui.repaint();
      System.out.println("FPS = " + fps);

      BoofMiscOps.sleep(5);
    }
    System.out.println("done!");
  }
}

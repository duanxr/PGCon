package com.duanxr.pgcon.gui;


import com.dooapp.fxform.FXForm;
import com.dooapp.fxform.view.factory.FactoryProvider;
import com.duanxr.pgcon.PGConApplication;
import com.duanxr.pgcon.core.preprocessing.PreProcessor;
import com.duanxr.pgcon.core.preprocessing.PreprocessorFactory;
import com.duanxr.pgcon.config.ConstantConfig;
import com.duanxr.pgcon.component.DaemonTask;
import com.duanxr.pgcon.gui.debug.DebugFilterConfig;
import com.duanxr.pgcon.gui.debug.DebugMainConfig;
import com.duanxr.pgcon.gui.debug.DebugThreshConfig;
import com.duanxr.pgcon.util.ImageUtil;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.opencv.core.Mat;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;


/**
 * @author 段然 2022/7/27
 */
@Slf4j
public class PGConGUI extends Application {

  private ConfigurableApplicationContext context;

  @Override
  public void init() {
    try {
      ApplicationContextInitializer<GenericApplicationContext> initializer =
          applicationContext -> {
            applicationContext.registerBean(Parameters.class, this::getParameters);
            applicationContext.registerBean(HostServices.class, this::getHostServices);
          };
      this.context = new SpringApplicationBuilder().sources(PGConApplication.class)
          .initializers(initializer).run(getParameters().getRaw().toArray(new String[0]));
    } catch (Exception e) {
      log.error("PGCon GUI init error", e);
    }
  }

  @Override
  public void stop() {
    context.close();
    System.exit(0);
  }

  @Override
  @SneakyThrows
  public void start(Stage primaryStage) {
    try {
      primaryStage.setTitle(ConstantConfig.MAIN_PANEL_TITLE);
      FXMLLoader loader = new FXMLLoader();
      loader.setControllerFactory(context::getBean);
      URL resource = PGConGUI.class.getResource("/javafx/MainPanel.fxml");
      loader.setLocation(resource);
      AnchorPane load = loader.load();
      Scene scene = new Scene(load);
      primaryStage.setScene(scene);
      primaryStage.setResizable(false);
      primaryStage.show();
    } catch (Exception e) {
      log.error("PGCon GUI start error", e);
      throw e;
    }
  }


  private FactoryProvider editorFactoryProvider;
  private FactoryProvider labelFactoryProvider;

  private FactoryProvider tooltipFactoryProvider;

  @SneakyThrows
  private void test() {
    editorFactoryProvider = context.getBean("editorFactoryProvider", FactoryProvider.class);
    labelFactoryProvider = context.getBean("labelFactoryProvider", FactoryProvider.class);
    tooltipFactoryProvider = context.getBean("tooltipFactoryProvider", FactoryProvider.class);
    Stage debugWindow = new Stage();
    debugWindow.setTitle("OCR Debug Window");
    debugWindow.setResizable(true);
    Pane pane = new Pane();
    Scene debugScene = new Scene(pane);
    debugWindow.setScene(debugScene);
    Node debugNode = generateDebugConfigNode(debugConfig);
    Node imageFilterDebugNode = generateDebugConfigNode(debugFilterConfig);
    Node imageBinaryDebugNode = generateDebugConfigNode(debugThreshConfig);
    HBox vBox = new HBox(debugNode, imageFilterDebugNode, imageBinaryDebugNode);
    pane.getChildren().add(vBox);

    BufferedImage bufferedImage = ImageIO.read(
        new File("C:\\Users\\段然\\Desktop\\QQ截图20220801090051.jpg"));
    PreprocessorFactory preprocessorFactory = context.getBean("preprocessorFactory",
        PreprocessorFactory.class);
    bufferedImage = bufferedImage.getSubimage(0, 0, bufferedImage.getWidth() - 1,
        bufferedImage.getHeight());
    WritableImage selectedImage = new WritableImage(bufferedImage.getWidth(),
        bufferedImage.getHeight());
    WritableImage liveImage = new WritableImage(bufferedImage.getWidth(),
        bufferedImage.getHeight());
    debugConfig.getSelectedImage().set(selectedImage);
    BufferedImage finalBufferedImage = bufferedImage;
    Platform.runLater(() -> SwingFXUtils.toFXImage(finalBufferedImage, selectedImage));
    debugConfig.getLiveImage().set(liveImage);
    ExecutorService executorService = context.getBean("executorService", ExecutorService.class);
    executorService.execute(DaemonTask.of("test", () -> {
      try {
        TimeUnit.MILLISECONDS.sleep(300);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
      List<PreProcessor> preProcessors = preprocessorFactory.getPreProcessors(
          Arrays.asList(debugFilterConfig.convertToPreProcessorConfig(),
              debugThreshConfig.convertToThreshPreProcessorConfig()));
      BufferedImage subImage = finalBufferedImage;
      Mat mat = ImageUtil.bufferedImageToMat(subImage);
      for (PreProcessor preProcessor : preProcessors) {
        preProcessor.preProcess(mat);
      }
      subImage = ImageUtil.matToBufferedImage(mat);
      BufferedImage finalSubImage = subImage;
      Platform.runLater(() -> SwingFXUtils.toFXImage(finalSubImage, liveImage));
    }));
    debugWindow.show();
  }
  private double[] rgbFilter(double blueWeight, double greenWeight, double redWeight) {
    double[] defaultWeight = {0.114, 0.587, 0.299};
    double sum = blueWeight + greenWeight + redWeight;
    if (sum != 3) {
      defaultWeight[0] = defaultWeight[0] * blueWeight / sum;
      defaultWeight[1] = defaultWeight[1] * greenWeight / sum;
      defaultWeight[2] = defaultWeight[2] * redWeight / sum;
    }
    return defaultWeight;
  }

  private final DebugMainConfig debugConfig = new DebugMainConfig();
  private final DebugFilterConfig debugFilterConfig = new DebugFilterConfig();
  private final DebugThreshConfig debugThreshConfig = new DebugThreshConfig();

  private Node generateDebugConfigNode(Object configBean) {
    return new FXForm<>(configBean, labelFactoryProvider,
        tooltipFactoryProvider, editorFactoryProvider);
  }


}
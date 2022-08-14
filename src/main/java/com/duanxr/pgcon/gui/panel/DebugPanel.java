package com.duanxr.pgcon.gui.panel;

import com.dooapp.fxform.FXForm;
import com.duanxr.pgcon.component.FrameManager;
import com.duanxr.pgcon.core.detect.api.ImageCompare;
import com.duanxr.pgcon.core.detect.api.OCR;
import com.duanxr.pgcon.core.detect.api.OCR.ApiConfig;
import com.duanxr.pgcon.core.detect.api.OCR.Param;
import com.duanxr.pgcon.core.detect.impl.TesseractOCR;
import com.duanxr.pgcon.core.model.Area;
import com.duanxr.pgcon.core.preprocessing.PreProcessor;
import com.duanxr.pgcon.core.preprocessing.PreProcessorConfig;
import com.duanxr.pgcon.core.preprocessing.PreprocessorFactory;
import com.duanxr.pgcon.gui.FXFormGenerator;
import com.duanxr.pgcon.gui.debug.DebugBlurConfig;
import com.duanxr.pgcon.gui.debug.DebugColorPickConfig;
import com.duanxr.pgcon.gui.debug.DebugDetectConfig;
import com.duanxr.pgcon.gui.debug.DebugDetectConfig.DetectType;
import com.duanxr.pgcon.gui.debug.DebugFilterConfig;
import com.duanxr.pgcon.gui.debug.DebugImageCompareConfig;
import com.duanxr.pgcon.gui.debug.DebugMainConfig;
import com.duanxr.pgcon.gui.debug.DebugNormalizeConfig;
import com.duanxr.pgcon.gui.debug.DebugOcrConfig;
import com.duanxr.pgcon.gui.debug.DebugResizeConfig;
import com.duanxr.pgcon.gui.debug.DebugResultConfig;
import com.duanxr.pgcon.gui.debug.DebugThreshConfig;
import com.duanxr.pgcon.util.ImageUtil;
import com.duanxr.pgcon.util.LogUtil;
import com.duanxr.pgcon.util.PropertyCacheUtil;
import com.google.common.base.Strings;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.StringEscapeUtils;
import org.opencv.core.Mat;
import org.springframework.stereotype.Component;

/**
 * @author 段然 2022/8/7
 */
@Slf4j
@Component
public class DebugPanel {

  private static final String DEBUG_WINDOW_TITLE = "Debug";
  private final DebugBlurConfig debugBlurConfig;
  private final DebugColorPickConfig debugColorPickConfig;
  private final DebugMainConfig debugConfig;
  private final DebugDetectConfig debugDetectConfig;
  private final DebugFilterConfig debugFilterConfig;
  private final DebugImageCompareConfig debugImageCompareConfig;
  private final DebugNormalizeConfig debugNormalizeConfig;
  private final DebugOcrConfig debugOcrConfig;
  private final DebugResizeConfig debugResizeConfig;
  private final DebugResultConfig debugResultConfig;
  private final DebugThreshConfig debugThreshConfig;
  private final FrameManager frameManager;
  private final FXFormGenerator fxFormGenerator;
  private final ImageCompare imageCompare;
  private final OCR ocr;
  private final PreprocessorFactory preprocessorFactory;
  private WritableImage convertedImage;
  private Exception debugException;
  private Stage debugStage;
  private WritableImage liveImage;
  private Area selectedArea;
  private BufferedImage selectedBufferedImage;
  private WritableImage selectedImage;
  private String template;

  public DebugPanel(DebugColorPickConfig debugColorPickConfig, DebugMainConfig debugConfig,
      DebugDetectConfig debugDetectConfig, DebugFilterConfig debugFilterConfig,
      DebugImageCompareConfig debugImageCompareConfig, DebugNormalizeConfig debugNormalizeConfig,
      DebugOcrConfig debugOcrConfig, DebugResultConfig debugResultConfig,
      DebugThreshConfig debugThreshConfig, FXFormGenerator fxFormGenerator,
      DebugBlurConfig debugBlurConfig, DebugResizeConfig debugResizeConfig, PreprocessorFactory preprocessorFactory, ImageCompare imageCompare, OCR ocr,
      FrameManager frameManager) {
    this.debugColorPickConfig = debugColorPickConfig;
    this.debugConfig = debugConfig;
    this.debugDetectConfig = debugDetectConfig;
    this.debugFilterConfig = debugFilterConfig;
    this.debugImageCompareConfig = debugImageCompareConfig;
    this.debugNormalizeConfig = debugNormalizeConfig;
    this.debugOcrConfig = debugOcrConfig;
    this.debugResultConfig = debugResultConfig;
    this.debugThreshConfig = debugThreshConfig;
    this.fxFormGenerator = fxFormGenerator;
    this.debugBlurConfig = debugBlurConfig;
    this.debugResizeConfig = debugResizeConfig;
    this.preprocessorFactory = preprocessorFactory;
    this.imageCompare = imageCompare;
    this.ocr = ocr;
    this.frameManager = frameManager;
  }

  public void openPanel(Area selectedArea, BufferedImage selectedBufferedImage) {
    if (debugStage != null && !debugStage.isShowing()) {
      this.selectedArea = selectedArea;
      this.selectedBufferedImage = selectedBufferedImage;
      this.selectedImage = new WritableImage(selectedArea.getWidth(), selectedArea.getHeight());
      this.convertedImage = new WritableImage(selectedArea.getWidth(), selectedArea.getHeight());
      this.liveImage = new WritableImage(selectedArea.getWidth(), selectedArea.getHeight());
      this.debugConfig.getSelectedImage().set(selectedImage);
      this.debugConfig.getConvertedImage().set(convertedImage);
      this.debugConfig.getLiveImage().set(liveImage);
      debugStage.show();
      Platform.runLater(() -> SwingFXUtils.toFXImage(selectedBufferedImage, selectedImage));
      Platform.runLater(() -> SwingFXUtils.toFXImage(selectedBufferedImage, convertedImage));
    }
  }

  public void processDebug() {
    if (debugStage != null && debugStage.isShowing()) {
      try {
        BufferedImage input = frameManager.getFrame().getImage();
        BufferedImage selectedImage = input.getSubimage(selectedArea.getX(), selectedArea.getY(),
            selectedArea.getWidth(), selectedArea.getHeight());
        Mat liveMat = ImageUtil.bufferedImageToMat(selectedImage);
        Mat convertedMat = ImageUtil.bufferedImageToMat(selectedBufferedImage);
        List<PreProcessorConfig> preProcessorConfigs = Arrays.asList(
            debugFilterConfig.convertToPreProcessorConfig(),
            debugResizeConfig.convertToResizePreProcessorConfig(),
            debugBlurConfig.convertToSmoothingPreProcessorConfig(),
            debugColorPickConfig.convertToColorPickerFilterPreProcessorConfig(),
            debugNormalizeConfig.convertToNormalizeConfig(),
            debugThreshConfig.convertToThreshPreProcessorConfig());

        List<PreProcessor> preProcessors = preprocessorFactory.getPreProcessors(
            preProcessorConfigs);

        long start = System.currentTimeMillis();
        for (PreProcessor preProcessor : preProcessors) {
          liveMat = preProcessor.preProcess(liveMat);
          convertedMat = preProcessor.preProcess(convertedMat);
        }
        long preProcessTime = (System.currentTimeMillis() - start) / 2;

        BufferedImage liveDebugImage = ImageUtil.matToBufferedImage(liveMat);
        BufferedImage convertedDebugImage = ImageUtil.matToBufferedImage(convertedMat);

        Platform.runLater(() -> {
          SwingFXUtils.toFXImage(liveDebugImage, liveImage);
          SwingFXUtils.toFXImage(convertedDebugImage, convertedImage);
        });

        start = System.currentTimeMillis();
        StringBuilder result = new StringBuilder();
        DetectType detectType = debugDetectConfig.getDetectType().get();
        if (detectType == DetectType.IMAGE_COMPARE) {
          template = ImageUtil.matToJson(convertedMat);
          ImageCompare.Param param = ImageCompare.Param.builder()
              .area(selectedArea)
              .method(debugImageCompareConfig.getImageCompareType().get())
              .template(template)
              .preProcessors(preProcessorConfigs)
              .build();
          ImageCompare.Result detect = imageCompare.detect(param);
          String similarity = LogUtil.format("%.02f", detect.getSimilarity()).toString();
          result.append("Similarity:").append(similarity).append("\n");
        } else if (detectType == DetectType.OCR) {
          Param param = Param.builder()
              .area(selectedArea)
              .preProcessors(preProcessorConfigs)
              .apiConfig(ApiConfig.builder()
                  .method(debugOcrConfig.getOcrType().get())
                  .whitelist(debugOcrConfig.getWhiteList().get())
                  .blacklist(debugOcrConfig.getBlackList().get())
                  .ocrEngineMode(debugOcrConfig.getEngineMode().get())
                  .pageSegMode(debugOcrConfig.getPageSegMode().get())
                  .build())
              .build();
          OCR.Result detect = ocr.detect(param);
          result.append("Result:").append(detect.getTextWithoutSpace()).append("\n");
          result.append("Confidence:").append(detect.getConfidence()).append("\n");
        }
        long detectTime = System.currentTimeMillis() - start;
        result.append("Preprocess time: ").append(preProcessTime).append(" ms\n");
        result.append("Detect time: ").append(detectTime).append(" ms");
        ((SimpleStringProperty) debugResultConfig.getDetectResult()).set(result.toString());
      } catch (Exception e) {
        if (!isSameException(e)) {
          log.error("debug error", e);
        }
      }
    }
  }


  private boolean isSameException(Exception e) {
    boolean isSame =
        debugException != null && e.getClass() == debugException.getClass() && e.getMessage()
            .equals(debugException.getMessage());
    debugException = e;
    return isSame;
  }

  private String generateCode() {
    StringBuilder code = new StringBuilder();
    DetectType detectType = debugDetectConfig.getDetectType().get();
    if (detectType == DetectType.IMAGE_COMPARE) {
      code.append(
          "private static final ImageCompare.Param IMAGE_COMPARE = ImageCompare.Param.builder()\n");

      code.append(".area(Area.ofRect(")
          .append(selectedArea.getX()).append(",")
          .append(selectedArea.getY()).append(",")
          .append(selectedArea.getWidth()).append(",")
          .append(selectedArea.getHeight()).append("))\n");

      code.append(".method(ImageCompare.Method.")
          .append(debugImageCompareConfig.getImageCompareType().get().name())
          .append(")\n");

      generatePreProcessorCode(code);

      code.append(".template(\"").append(StringEscapeUtils.escapeJava(template))
          .append("\")\n")
          .append(".build();");

    } else if (detectType == DetectType.OCR) {

      code.append("private static final OCR.Param param = OCR.Param.builder()\n");

      code.append(".area(Area.ofRect(")
          .append(selectedArea.getX()).append(",")
          .append(selectedArea.getY()).append(",")
          .append(selectedArea.getWidth()).append(",")
          .append(selectedArea.getHeight()).append("))\n");

      generatePreProcessorCode(code);

      code.append(".apiConfig(ApiConfig.builder()\n")
          .append(".method(OCR.Method.")
          .append(debugOcrConfig.getOcrType().get().name()).append(")\n");

      if (!Strings.isNullOrEmpty(debugOcrConfig.getWhiteList().get())) {
        code.append(".whitelist(\"")
            .append(StringEscapeUtils.escapeJava(debugOcrConfig.getWhiteList().get()))
            .append("\")\n");
      }
      if (!Strings.isNullOrEmpty(debugOcrConfig.getBlackList().get())) {
        code.append(".blacklist(\"")
            .append(StringEscapeUtils.escapeJava(debugOcrConfig.getBlackList().get()))
            .append("\")\n");
      }

      if (debugOcrConfig.getEngineMode().get() != TesseractOCR.DEFAULT_OCR_ENGINE_MODE) {
        code.append(".ocrEngineMode(")
            .append(debugOcrConfig.getEngineMode().get()).append(")\n");
      }

      if (debugOcrConfig.getPageSegMode().get() != TesseractOCR.DEFAULT_PAGE_SEG_MODE) {
        code.append(".pageSegMode(")
            .append(debugOcrConfig.getPageSegMode().get()).append(")\n");
      }

      code.append(".build())\n.build();");

    }
    return code.toString();
  }

  private void generatePreProcessorCode(StringBuilder code) {
    if (debugFilterConfig.getEnableRGBFilter().get()) {
      code.append(".preProcessor(")
          .append(
              "com.duanxr.pgcon.core.preprocessing.config.ChannelsFilterPreProcessorConfig.builder()\n")
          .append(".enable(true)\n")
          .append(".redWeight(").append(debugFilterConfig.getRedWeight().get()).append(")\n")
          .append(".greenWeight(").append(debugFilterConfig.getGreenWeight().get()).append(")\n")
          .append(".blueWeight(").append(debugFilterConfig.getBlueWeight().get()).append(")\n")
          .append(".build())\n");
    }
    if (debugColorPickConfig.getEnableColorPickFilter().get()) {
      code.append(".preProcessor(")
          .append(
              "com.duanxr.pgcon.core.preprocessing.config.ColorPickFilterPreProcessorConfig.builder()\n")
          .append(".enable(true)\n")
          .append(".targetColor(javafx.scene.paint.Color.color(")
          .append(debugColorPickConfig.getTargetColor().get().getRed()).append(",")
          .append(debugColorPickConfig.getTargetColor().get().getGreen()).append(",")
          .append(debugColorPickConfig.getTargetColor().get().getBlue()).append("))\n")
          .append(".range(").append(debugColorPickConfig.getRange().get()).append(")\n")
          .append(
              ".pickType(com.duanxr.pgcon.core.preprocessing.config.ColorPickFilterPreProcessorConfig.PickType.")
          .append(debugColorPickConfig.getPickType().get().name()).append(")\n")
          .append(
              ".maskType(com.duanxr.pgcon.core.preprocessing.config.ColorPickFilterPreProcessorConfig.MaskType.")
          .append(debugColorPickConfig.getMaskType().get().name()).append(")\n")
          .append(".inverse(").append(debugColorPickConfig.getInverse().get()).append(")\n")
          .append(".build())\n");
    }
    if (debugNormalizeConfig.getEnableNormalizeFilter().get()) {
      code.append(".preProcessor(")
          .append(
              "com.duanxr.pgcon.core.preprocessing.config.NormalizePreProcessorConfig.builder()\n")
          .append(".enable(true)\n")
          .append(".build())\n");
    }
    if (debugThreshConfig.getEnableThresh().get()) {
      code.append(".preProcessor(")
          .append(
              "com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder()\n")
          .append(".enable(true)\n")
          .append(".binaryThreshold(").append(debugThreshConfig.getBinaryThreshold().get())
          .append(")\n")
          .append(".inverse(").append(debugThreshConfig.getInverse().get()).append(")\n")
          .append(
              ".threshType(com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.")
          .append(debugThreshConfig.getThreshType().get().name()).append(")\n");
      if (debugThreshConfig.getThreshType().get().isAdaptive()) {
        code.append(".adaptiveThreshC(").append(debugThreshConfig.getAdaptiveThreshC().get())
            .append(")\n")
            .append(".adaptiveBlockSize(").append(debugThreshConfig.getAdaptiveBlockSize().get())
            .append(")\n");
      }
      code.append(".build())\n");
    }
  }

  public void initialize(Stage debugStage) {
    this.debugStage = debugStage;
    debugStage.setTitle(DEBUG_WINDOW_TITLE);
    debugStage.setResizable(false);
    Pane pane = new Pane();
    Scene debugScene = new Scene(pane);
    debugStage.setScene(debugScene);
    PropertyCacheUtil.bindPropertyBean("DEBUG_FILTER_CONFIG", debugFilterConfig);
    PropertyCacheUtil.bindPropertyBean("DEBUG_COLOR_PICK_CONFIG", debugColorPickConfig);
    PropertyCacheUtil.bindPropertyBean("DEBUG_NORMALIZE_CONFIG", debugNormalizeConfig);
    PropertyCacheUtil.bindPropertyBean("DEBUG_THRESH_CONFIG", debugThreshConfig);
    PropertyCacheUtil.bindPropertyBean("DEBUG_DETECT_CONFIG", debugDetectConfig);
    PropertyCacheUtil.bindPropertyBean("DEBUG_OCR_CONFIG", debugOcrConfig);
    PropertyCacheUtil.bindPropertyBean("DEBUG_IMAGE_COMPARE_CONFIG", debugImageCompareConfig);
    PropertyCacheUtil.bindPropertyBean("DEBUG_BLUR_CONFIG", debugBlurConfig);
    PropertyCacheUtil.bindPropertyBean("DEBUG_RESIZE_CONFIG", debugResizeConfig);
    FXForm<?> debugNode = fxFormGenerator.generateNode(debugConfig);
    FXForm<?> filterDebugNode = fxFormGenerator.generateNode(debugFilterConfig);
    FXForm<?> colorPickDebugNode = fxFormGenerator.generateNode(debugColorPickConfig);
    FXForm<?> normalizeDebugNode = fxFormGenerator.generateNode(debugNormalizeConfig);
    FXForm<?> binaryDebugNode = fxFormGenerator.generateNode(debugThreshConfig);
    FXForm<?> detectDebugNode = fxFormGenerator.generateNode(debugDetectConfig);
    FXForm<?> ocrDebugNode = fxFormGenerator.generateNode(debugOcrConfig);
    FXForm<?> imageCompareDebugNode = fxFormGenerator.generateNode(debugImageCompareConfig);
    FXForm<?> resultDebugNode = fxFormGenerator.generateNode(debugResultConfig);
    FXForm<?> resizeDebugNode = fxFormGenerator.generateNode(debugResizeConfig);
    FXForm<?> blurDebugNode = fxFormGenerator.generateNode(debugBlurConfig);
    setDebugWindowSize(debugNode);
    setDebugConfigSize(resizeDebugNode);
    setDebugConfigSize(blurDebugNode);
    setDebugConfigSize(filterDebugNode);
    setDebugConfigSize(colorPickDebugNode);
    setDebugConfigSize(normalizeDebugNode);
    setDebugConfigSize(binaryDebugNode);
    setDebugConfigSize(detectDebugNode);
    setDebugConfigSize(ocrDebugNode);
    setDebugConfigSize(imageCompareDebugNode);
    setDebugConfigSize(resultDebugNode);
    Button generateCodeButton = new Button("Generate Code");
    VBox detectBox = new VBox(imageCompareDebugNode);
    pane.getChildren().add(new HBox(debugNode,
        new VBox(filterDebugNode, resizeDebugNode),
        new VBox(blurDebugNode, colorPickDebugNode),
        new VBox(normalizeDebugNode, binaryDebugNode),
        new VBox(detectDebugNode, detectBox, resultDebugNode, generateCodeButton)));
    generateCodeButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> copyCodeToClipboard());
    debugDetectConfig.getDetectType().addListener((observable, oldValue, newValue) -> {
      detectBox.getChildren().clear();
      if (newValue == DetectType.IMAGE_COMPARE) {
        detectBox.getChildren().add(imageCompareDebugNode);
      } else if (newValue == DetectType.OCR) {
        detectBox.getChildren().add(ocrDebugNode);
      }
    });
  }
  private void copyCodeToClipboard() {
    Clipboard clipboard = Clipboard.getSystemClipboard();
    ClipboardContent content = new ClipboardContent();
    content.putString(generateCode());
    clipboard.setContent(content);
  }
  private void setDebugWindowSize(FXForm<?> debugNode) {
    double width = 280;
    ArrayList<Node> nodes = getAllNodes(debugNode, null);
    for (Node node : nodes) {
      if (node instanceof ImageView imageView) {
        imageView.setFitWidth(240);
        imageView.setFitHeight(180);
        imageView.setPreserveRatio(true);
      }
    }
    debugNode.setMinWidth(width);
    debugNode.setPrefWidth(width);
    debugNode.setMaxWidth(width);
  }

  private void setDebugConfigSize(FXForm<?> debugNode) {
    double width = 180;
    debugNode.setMinWidth(width);
    debugNode.setPrefWidth(width);
    debugNode.setMaxWidth(width);
  }

  private ArrayList<Node> getAllNodes(Parent parent, ArrayList<Node> nodes) {
    if (nodes == null) {
      nodes = new ArrayList<>();
    }
    for (Node node : parent.getChildrenUnmodifiable()) {
      nodes.add(node);
      if (node instanceof Parent) {
        getAllNodes((Parent) node, nodes);
      }
    }
    return nodes;
  }

  public void close() {
    if (debugStage != null && debugStage.isShowing()) {
      debugStage.close();
    }
  }
}

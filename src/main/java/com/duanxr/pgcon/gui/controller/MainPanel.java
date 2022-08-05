package com.duanxr.pgcon.gui.controller;

import com.dooapp.fxform.FXForm;
import com.dooapp.fxform.view.factory.FactoryProvider;
import com.duanxr.pgcon.algo.model.Area;
import com.duanxr.pgcon.algo.preprocessing.PreProcessor;
import com.duanxr.pgcon.algo.preprocessing.PreprocessorFactory;
import com.duanxr.pgcon.config.GuiConfig;
import com.duanxr.pgcon.config.InputConfig;
import com.duanxr.pgcon.config.OutputConfig;
import com.duanxr.pgcon.core.DisplayHandler;
import com.duanxr.pgcon.core.FrameManager;
import com.duanxr.pgcon.core.FrameManager.CachedFrame;
import com.duanxr.pgcon.core.ProtocolManager;
import com.duanxr.pgcon.core.ScriptManager;
import com.duanxr.pgcon.gui.debug.DebugFilterConfig;
import com.duanxr.pgcon.gui.debug.DebugMainConfig;
import com.duanxr.pgcon.gui.debug.DebugThreshConfig;
import com.duanxr.pgcon.gui.display.DrawEvent;
import com.duanxr.pgcon.gui.display.impl.Rectangle;
import com.duanxr.pgcon.gui.exception.GuiAlertException;
import com.duanxr.pgcon.gui.log.GuiLog;
import com.duanxr.pgcon.gui.log.GuiLogLevel;
import com.duanxr.pgcon.gui.log.GuiLogView;
import com.duanxr.pgcon.gui.log.GuiLogger;
import com.duanxr.pgcon.input.impl.CameraImageInput;
import com.duanxr.pgcon.output.Controller;
import com.duanxr.pgcon.output.api.Protocol;
import com.duanxr.pgcon.script.api.ConfigurableScript;
import com.duanxr.pgcon.script.api.MainScript;
import com.duanxr.pgcon.script.component.ScriptRunner;
import com.duanxr.pgcon.util.CacheUtil;
import com.duanxr.pgcon.util.ImageConvertUtil;
import com.duanxr.pgcon.util.SaveUtil;
import com.duanxr.pgcon.util.SystemUtil;
import com.google.common.base.Strings;
import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.opencv.core.Mat;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * @author 段然 2022/7/27
 */
@Slf4j
@Component
public class MainPanel {

  private static final String CACHE_KEY_ENABLE_DEBUG = "ENABLE_DEBUG";
  private static final String CACHE_KEY_LOG_FOLLOW = "CACHE_KEY_LOG_FOLLOW";
  private static final String CACHE_KEY_LOG_LEVEL = "CACHE_KEY_LOG_LEVEL";
  private static final String CACHE_KEY_LOG_PAUSE = "CACHE_KEY_LOG_PAUSE";
  private static final String CACHE_KEY_LOG_SHOWTIME = "CACHE_KEY_LOG_SHOWTIME";
  private static final String CACHE_KEY_SCRIPT_CONFIG_CACHE_PREFIX = "CACHE_KEY_SCRIPT_CONFIG_CACHE_";
  private static final String CACHE_KEY_SELECTED_PORT = "SELECTED_PORT";
  private static final String CACHE_KEY_SELECTED_PROTOCOL = "SELECTED_PROTOCOL";
  private static final String CACHE_KEY_SELECTED_SCRIPT = "SELECTED_SCRIPT";
  private static final String CACHE_KEY_SELECTED_VIDEO = "SELECTED_VIDEO";
  private static final String DEBUG_WINDOW_TITLE = "Debug";
  private static final String DRAW_KEY_MOUSE_DRAGGED = "MOUSE_DRAGGED";
  private static final String SCRIPT_CONFIGURATION_WINDOW_TITLE = "Script Configuration";
  private final ObjectProperty<Image> canvasProperty;
  private final Controller controller;
  private final DebugMainConfig debugConfig;
  private final DebugFilterConfig debugFilterConfig;
  private final DebugThreshConfig debugThreshConfig;
  private final DisplayHandler displayHandler;
  private final Map<String, Node> dynamicConfigurationMap;
  private final FactoryProvider editorFactoryProvider;
  private final AtomicBoolean enableDebug;
  private final FrameManager frameManager;
  private final GuiConfig guiConfig;
  private final InputConfig inputConfig;
  private final FactoryProvider labelFactoryProvider;
  private final OutputConfig outputConfig;
  private final PreprocessorFactory preprocessorFactory;
  private final ProtocolManager protocolManager;
  private final List<String> protocols;
  private final ObjectProperty<Image> screenProperty;
  private final ScriptManager scriptManager;
  private final ScriptRunner scriptRunner;
  private final FactoryProvider tooltipFactoryProvider;
  private final double xScale;
  private final double yScale;
  @FXML
  private ImageView canvas;
  @FXML
  private Button capture;
  @FXML
  private Button clearLog;
  @FXML
  private AnchorPane console;
  @FXML
  private ToggleButton debug;
  private Scene debugScene;
  private Stage debugWindow;
  private GuiLogger guiLogger;
  private WritableImage liveImage;
  @FXML
  private ToggleButton logFollow;
  @FXML
  private ComboBox<GuiLogLevel> logLevel;
  @FXML
  private ToggleButton logPause;
  @FXML
  private ToggleButton logShowTime;
  private Point pointEnd;
  private Point pointStart;
  @FXML
  private ComboBox<String> portSelection;
  @FXML
  private ComboBox<String> protocolSelection;
  @FXML
  private Button run;
  @FXML
  private ImageView screen;
  @FXML
  private Button scriptConfig;
  private ScrollPane scriptConfigurationPane;
  private Stage scriptConfigurationWindow;
  @FXML
  private ComboBox<String> scriptSelection;
  private Area selectedArea;
  private BufferedImage selectedDebugImage;
  private WritableImage selectedImage;

  private WritableImage convertedImage;
  @FXML
  private SplitPane splitPaneX;
  @FXML
  private SplitPane splitPaneY;
  @FXML
  private ComboBox<String> videoSelection;

  public MainPanel(
      @Qualifier("tooltipFactoryProvider") FactoryProvider tooltipFactoryProvider,
      @Qualifier("editorFactoryProvider") FactoryProvider editorFactoryProvider,
      @Qualifier("labelFactoryProvider") FactoryProvider labelFactoryProvider,
      @Qualifier("enableDebug") AtomicBoolean enableDebug,
      OutputConfig outputConfig, ScriptRunner scriptRunner,
      ProtocolManager protocolManager, GuiConfig guiConfig,
      DisplayHandler displayHandler, FrameManager frameManager,
      ScriptManager scriptManager, Controller controller,
      InputConfig inputConfig, PreprocessorFactory preprocessorFactory) {
    this.editorFactoryProvider = editorFactoryProvider;
    this.labelFactoryProvider = labelFactoryProvider;
    this.tooltipFactoryProvider = tooltipFactoryProvider;
    this.enableDebug = enableDebug;
    this.outputConfig = outputConfig;
    this.scriptRunner = scriptRunner;
    this.scriptManager = scriptManager;
    this.controller = controller;
    this.protocolManager = protocolManager;
    this.guiConfig = guiConfig;
    this.inputConfig = inputConfig;
    this.protocols = protocolManager.getProtocolList();
    this.displayHandler = displayHandler;
    this.frameManager = frameManager;
    this.preprocessorFactory = preprocessorFactory;
    this.screenProperty = new SimpleObjectProperty<>();
    this.canvasProperty = new SimpleObjectProperty<>();
    this.pointStart = null;
    this.pointEnd = null;
    this.xScale = 1D * inputConfig.getWidth() / guiConfig.getWidth();
    this.yScale = 1D * inputConfig.getHeight() / guiConfig.getHeight();
    this.dynamicConfigurationMap = new HashMap<>();
    this.debugConfig = new DebugMainConfig();
    this.debugFilterConfig = new DebugFilterConfig();
    this.debugThreshConfig = new DebugThreshConfig();
  }

  @FXML
  public void initialize() {
    initializeScriptConfigurations();
    initializeDebugWindow();
    initializeGuiLogViewer();
    initializeVideoComponent();
    initializeScreenAndCanvas();
    initializeProtocolSelection();
    initializePortSelection();
    initializeScriptSelection();
    initializeButtons();
  }

  private void initializeDebugWindow() {
    debugWindow = new Stage();
    debugWindow.setTitle(DEBUG_WINDOW_TITLE);
    debugWindow.setResizable(false);
    Pane pane = new Pane();
    debugScene = new Scene(pane);
    debugWindow.setScene(debugScene);
    debugWindow.setOnCloseRequest(
        event -> callbackWithExceptionAlert(this::onDebugWindowClose));
    FXForm<?> debugNode = generateConfigNode(debugConfig);
    FXForm<?> filterDebugNode = generateConfigNode(debugFilterConfig);
    FXForm<?> binaryDebugNode = generateConfigNode(debugThreshConfig);
    debugNode.setMaxWidth(320);
    filterDebugNode.setMaxWidth(320);
    binaryDebugNode.setMaxWidth(320);
    HBox hBox = new HBox(debugNode, filterDebugNode, binaryDebugNode);
    pane.getChildren().add(hBox);
  }

  private void onDebugWindowClose() {

  }

  private void initializeScreenAndCanvas() {
    WritableImage screenImage = new WritableImage(guiConfig.getWidth(), guiConfig.getHeight());
    WritableImage canvasImage = new WritableImage(guiConfig.getWidth(), guiConfig.getHeight());
    screen.imageProperty().bind(screenProperty);
    canvas.imageProperty().bind(canvasProperty);
    screenProperty.set(screenImage);
    canvasProperty.set(canvasImage);
    displayHandler.registerScreen(
        input -> Platform.runLater(() -> {
          SwingFXUtils.toFXImage(input, screenImage);
          refreshDebug();
        }));
    displayHandler.registerCanvas(
        input -> Platform.runLater(() -> SwingFXUtils.toFXImage(input, canvasImage)));
    canvas.addEventHandler(MouseEvent.MOUSE_PRESSED,
        event -> callbackWithExceptionCatch(() -> onCanvasMousePressed(event)));
    canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED,
        event -> callbackWithExceptionCatch(() -> onCanvasMouseDragged(event)));
    canvas.addEventHandler(MouseEvent.MOUSE_RELEASED,
        event -> callbackWithExceptionCatch(() -> onCanvasMouseReleased(event)));
  }

  private void refreshDebug() {
    if (debugWindow.isShowing()) {
      List<PreProcessor> preProcessors = preprocessorFactory.getPreProcessors(
          Arrays.asList(debugFilterConfig.convertToPreProcessorConfig(),
              debugThreshConfig.convertToThreshPreProcessorConfig()));
      Mat originalMat = frameManager.getFrame().getMat();
      Mat liveMat  = ImageConvertUtil.deepSplitMat(originalMat,selectedArea);
      Mat convertedMat = ImageConvertUtil.deepSplitMat(originalMat,selectedArea);
      for (PreProcessor preProcessor : preProcessors) {
        try {
          preProcessor.preProcess(liveMat);
          preProcessor.preProcess(convertedMat);
        } catch (Exception e) {
          log.info("Error in preprocessor", e);
        }
      }
      BufferedImage liveDebugImagePP = ImageConvertUtil.matToBufferedImage(liveMat);
      BufferedImage convertedDebugImagePP = ImageConvertUtil.matToBufferedImage(convertedMat);
      SwingFXUtils.toFXImage(liveDebugImagePP, liveImage);
      SwingFXUtils.toFXImage(convertedDebugImagePP, convertedImage);
    }
  }

  private void onCanvasMouseReleased(MouseEvent event) {
    if (enableDebug.get()) {
      if (pointStart != null && pointEnd != null) {
        Area area = Area.ofPoints((pointStart.x * xScale), (pointStart.y * yScale),
            (pointEnd.x * xScale), (pointEnd.y * yScale));
        captureCanvasSelection(area);
      }
      pointStart = null;
      pointEnd = null;
    }
  }

  private void captureCanvasSelection(Area area) {
    try {
      this.selectedArea = area;
      selectedDebugImage = frameManager.getFrame().getImage()
          .getSubimage(area.getX(), area.getY(), area.getWidth(), area.getHeight());
      File file = SaveUtil.saveTempImage(selectedDebugImage);
      guiLogger.info("area of points: {},{},{},{}, saved to: {}",
          (int) (pointStart.x * xScale), (int) (pointStart.y * yScale),
          (int) (pointEnd.x * xScale), (int) (pointEnd.y * yScale),
          file.getAbsolutePath());
      if (!debugWindow.isShowing()) {
        debugWindow.show();
        selectedImage = new WritableImage(area.getWidth(), area.getHeight());
        convertedImage = new WritableImage(area.getWidth(), area.getHeight());
        liveImage = new WritableImage(area.getWidth(), area.getHeight());
        debugConfig.getSelectedImage().set(selectedImage);
        debugConfig.getConvertedImage().set(convertedImage);
        debugConfig.getLiveImage().set(liveImage);
        Platform.runLater(() -> SwingFXUtils.toFXImage(selectedDebugImage, selectedImage));
        Platform.runLater(() -> SwingFXUtils.toFXImage(selectedDebugImage, convertedImage));
      }
    } catch (RasterFormatException ignored) {
    }
  }

  private void onCanvasMouseDragged(MouseEvent event) {
    if (enableDebug.get()) {
      pointEnd = new Point((int) event.getX(), (int) event.getY());
      if (pointStart != null) {
        drawCanvasSelection(pointStart, pointEnd);
      }
    }
  }

  private void drawCanvasSelection(Point pointStart, Point pointEnd) {
    displayHandler.draw(
        new DrawEvent(DRAW_KEY_MOUSE_DRAGGED, new Rectangle(
            Area.ofPoints(pointStart.x, pointStart.y, pointEnd.x, pointEnd.y),
            new Color(255, 0, 127, 128),
            3000)));
  }

  private void onCanvasMousePressed(MouseEvent event) {
    if (enableDebug.get()) {
      pointStart = new Point((int) event.getX(), (int) event.getY());
    }
    if (debugWindow.isShowing()) {
      debugWindow.close();
    }
  }

  private void initializeScriptConfigurations() {
    scriptManager.getMainScripts().values().stream()
        .filter(script -> script instanceof ConfigurableScript)
        .map(script -> (ConfigurableScript) script).forEach(this::bindScriptConfigurationToGUI);
    initializeConfigurationWindow();
  }

  private void initializeConfigurationWindow() {
    scriptConfigurationWindow = new Stage();
    scriptConfigurationWindow.setTitle(SCRIPT_CONFIGURATION_WINDOW_TITLE);
    scriptConfigurationWindow.setResizable(false);
    scriptConfigurationPane = new ScrollPane();
    scriptConfigurationPane.setPrefHeight(640);
    scriptConfigurationPane.setMinWidth(350);
    scriptConfigurationPane.setMaxWidth(640);
    Scene scriptConfigurationScene = new Scene(scriptConfigurationPane);
    scriptConfigurationWindow.setScene(scriptConfigurationScene);
    scriptConfigurationWindow.setOnCloseRequest(
        event -> callbackWithExceptionAlert(this::saveScriptConfigurationToCache));
    scriptConfig.addEventHandler(MouseEvent.MOUSE_CLICKED,
        event -> callbackWithExceptionAlert(this::onScriptConfigurationButtonClick));
  }

  private String getCurrentSelectedScriptName() {
    return Strings.nullToEmpty(scriptSelection.getSelectionModel().getSelectedItem());
  }

  private void saveScriptConfigurationToCache() {
    String cacheKey = getScriptConfigurationCacheKey(getCurrentSelectedScriptName());
    Node content = scriptConfigurationPane.getContent();
    if (content instanceof FXForm<?>) {
      Object configBean = ((FXForm<?>) content).getSource();
      CacheUtil.setCache(cacheKey, configBean);
    }
  }

  private void onScriptConfigurationButtonClick() {
    if (scriptConfigurationWindow.isShowing()) {
      scriptConfigurationWindow.close();
    } else {
      scriptConfigurationWindow.show();
    }
  }

  private String getScriptConfigurationCacheKey(String scriptName) {
    return CACHE_KEY_SCRIPT_CONFIG_CACHE_PREFIX + DigestUtils.sha1Hex(scriptName).toUpperCase();
  }

  private void bindScriptConfigurationToGUI(ConfigurableScript configurableScript) {
    String scriptName = configurableScript.getScriptName();
    String cacheKey = getScriptConfigurationCacheKey(scriptName);
    Object registerConfig = configurableScript.registerConfig();
    try {
      CacheUtil.loadCache(cacheKey, registerConfig);
    } catch (Exception e) {
      guiLogger.warn("load script {} config cache failed", scriptName, e);
    }
    Node node = generateConfigNode(registerConfig);
    dynamicConfigurationMap.put(scriptName, node);
  }

  private FXForm<?> generateConfigNode(Object configBean) {
    FXForm<?> configForm = new FXForm<>(configBean, labelFactoryProvider,
        tooltipFactoryProvider, editorFactoryProvider);
    setScriptConfigurationFormStyle(configForm);
    return configForm;
  }

  private void setScriptConfigurationFormStyle(FXForm<?> configForm) {
    configForm.setPrefWidth(320);
  }

  private void initializeGuiLogViewer() {
    GuiLog guiLog = new GuiLog();
    guiLogger = new GuiLogger(guiLog, "");
    scriptManager.register(guiLogger);
    GuiLogView logView = new GuiLogView(guiLogger);
    logView.setFixedCellSize(24);
    logView.setPrefWidth(960);
    logView.setPrefHeight(190);
    logLevel.setItems(FXCollections.observableArrayList(GuiLogLevel.values()));
    logView.filterLevelProperty().bind(logLevel.getSelectionModel().selectedItemProperty());
    logView.showTimeStampProperty().bind(logShowTime.selectedProperty());
    logView.tailProperty().bind(logFollow.selectedProperty());
    logView.pausedProperty().bind(logPause.selectedProperty());
    console.getChildren().add(logView);
    clearLog.addEventHandler(MouseEvent.MOUSE_CLICKED,
        event -> callbackWithExceptionAlert(logView::clearLogs));
    Map<String, GuiLogLevel> logLevelMap = Arrays.stream(GuiLogLevel.values())
        .collect(Collectors.toMap(Enum::name, Function.identity()));
    CacheUtil.bindCache(CACHE_KEY_LOG_SHOWTIME, logShowTime.selectedProperty());
    CacheUtil.bindCache(CACHE_KEY_LOG_FOLLOW, logFollow.selectedProperty());
    CacheUtil.bindCache(CACHE_KEY_LOG_PAUSE, logPause.selectedProperty());
    CacheUtil.bindCache(CACHE_KEY_LOG_LEVEL, logLevel, GuiLogLevel::name, logLevelMap::get);
  }

  private void initializeVideoComponent() {
    videoSelection.setPromptText("Please select a video input");
    List<String> cameraList = SystemUtil.getCameraList();
    videoSelection.getItems().addAll(cameraList);
    CacheUtil.bindCache(CACHE_KEY_SELECTED_VIDEO, videoSelection,
        Function.identity(), Function.identity());
    try {
      openCam(videoSelection.getSelectionModel().getSelectedItem());
    } catch (Exception e) {
      guiLogger.error("initialize video component failed", e);
      videoSelection.getSelectionModel().select(null);
    }
    videoSelection.getSelectionModel().selectedItemProperty().addListener(
        (observable, oldValue, newValue) -> callbackWithExceptionAlert(() -> openCam(newValue)));
  }

  private void initializeProtocolSelection() {
    protocolSelection.setPromptText("Please select a protocol");
    protocolSelection.getItems().addAll(protocols);
    CacheUtil.bindCache(CACHE_KEY_SELECTED_PROTOCOL, protocolSelection,
        Function.identity(), Function.identity());
  }

  private void initializePortSelection() {
    portSelection.setPromptText("Please select a port");
    List<String> serialList = SystemUtil.getSerialList();
    portSelection.getItems().addAll(serialList);
    CacheUtil.bindCache(CACHE_KEY_SELECTED_PORT, portSelection,
        Function.identity(), Function.identity());
  }

  private void initializeScriptSelection() {
    scriptSelection.setPromptText("Please select a script");
    Set<String> scripts = scriptManager.getMainScripts().keySet();
    scriptSelection.getItems().addAll(scripts);
    CacheUtil.bindCache(CACHE_KEY_SELECTED_SCRIPT, scriptSelection,
        Function.identity(), Function.identity());
    loadScript(getCurrentSelectedScriptName());
    scriptSelection.getSelectionModel().selectedItemProperty().addListener(
        (observable, oldValue, newValue) -> callbackWithExceptionAlert(() -> loadScript(newValue)));
  }

  private void initializeButtons() {
    run.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> callbackWithExceptionAlert(this::run));
    capture.addEventHandler(MouseEvent.MOUSE_CLICKED,
        event -> callbackWithExceptionAlert(this::captureScreen));
    CacheUtil.bindCache(CACHE_KEY_ENABLE_DEBUG, debug.selectedProperty());
    enableDebug.set(debug.selectedProperty().getValue());
    debug.selectedProperty()
        .addListener((observable, oldValue, newValue) -> callbackWithExceptionAlert(
            () -> enableDebug.set(newValue)));
  }

  private void openCam(String camera) {
    if (!Strings.isNullOrEmpty(camera)) {
      displayHandler.setImageInput(new CameraImageInput(camera, inputConfig));
    }
  }

  private void callbackWithExceptionCatch(Runnable runnable) {
    try {
      runnable.run();
    } catch (Exception e) {
      logException(e);
    }
  }

  private void logException(Exception e) {
    if (e instanceof GuiAlertException) {
      guiLogger.error(e.getMessage());
    } else {
      guiLogger.error("GUI callback error", e);
    }
  }

  private void loadScript(String scriptName) {
    if (!Strings.isNullOrEmpty(scriptName)) {
      scriptConfig.setDisable(true);
      MainScript script = findScript(scriptName);
      if (script instanceof ConfigurableScript) {
        Node node = dynamicConfigurationMap.get(scriptName);
        if (node != null) {
          scriptConfigurationPane.setContent(node);
          scriptConfig.setDisable(false);
        }
      }
    }
  }

  private MainScript findScript(String scriptName) {
    MainScript script = scriptManager.getMainScripts().get(scriptName);
    if (script == null) {
      throw new GuiAlertException("cannot find script: " + scriptName);
    }
    return script;

  }

  private void callbackWithExceptionAlert(Runnable runnable) {
    try {
      runnable.run();
    } catch (Exception e) {
      alertException(e);
    }
  }

  private void alertException(Exception e) {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle("Error!");
    if (e instanceof GuiAlertException) {
      guiLogger.error(e.getMessage());
      alert.setHeaderText(e.getMessage());
    } else {
      guiLogger.error("A unexpected error occurred!", e);
      alert.setHeaderText("A unexpected error occurred!");
      alert.setContentText("The exception stacktrace was:");
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);
      e.printStackTrace(pw);
      String exceptionText = sw.toString();
      TextArea textArea = new TextArea(exceptionText);
      textArea.setEditable(false);
      textArea.setWrapText(true);
      textArea.setMaxWidth(Double.MAX_VALUE);
      textArea.setMaxHeight(Double.MAX_VALUE);
      GridPane.setVgrow(textArea, Priority.ALWAYS);
      GridPane.setHgrow(textArea, Priority.ALWAYS);
      GridPane expContent = new GridPane();
      expContent.setMaxWidth(Double.MAX_VALUE);
      expContent.add(textArea, 0, 1);
      alert.getDialogPane().setExpandableContent(expContent);
    }
    alert.showAndWait();
  }

  private void run() {
    if (!scriptRunner.isRunning()) {
      runScript();
    } else {
      stopScript();
    }
  }

  private void runScript() {
    checkSelections();
    loadProtocol();
    MainScript script = findScript(getCurrentSelectedScriptName());
    scriptConfigurationWindow.hide();
    script.load();
    scriptRunner.run(script, () -> Platform.runLater(this::enableScripts));
    disableSelections();
  }

  private void disableSelections() {
    run.setText("STOP!");
    protocolSelection.setDisable(true);
    portSelection.setDisable(true);
    scriptSelection.setDisable(true);
    videoSelection.setDisable(true);
    scriptConfig.setDisable(true);
  }

  private void loadProtocol() {
    controller.clear();
    String protocolName = protocolSelection.getSelectionModel().getSelectedItem();
    String port = portSelection.getSelectionModel().getSelectedItem();
    Protocol protocol = protocolManager.loadProtocol(protocolName, port,
        outputConfig.getBaudRate());
    if (!protocol.isConnected()) {
      protocol.clear();
      throw new GuiAlertException("protocol not connected");
    }
    controller.setProtocol(protocol);
  }

  private void checkSelections() {
    if (Strings.isNullOrEmpty(videoSelection.getSelectionModel().getSelectedItem())) {
      throw new GuiAlertException("please select a video");
    }
    if (Strings.isNullOrEmpty(protocolSelection.getSelectionModel().getSelectedItem())) {
      throw new GuiAlertException("please select a protocol");
    }
    if (Strings.isNullOrEmpty(portSelection.getSelectionModel().getSelectedItem())) {
      throw new GuiAlertException("please select a port");
    }
    if (Strings.isNullOrEmpty(scriptSelection.getSelectionModel().getSelectedItem())) {
      throw new GuiAlertException("please select a script");
    }
  }

  private void captureScreen() {
    CachedFrame frame = frameManager.getFrame();
    if (frame != null) {
      BufferedImage image = frame.getImage();
      File file = SaveUtil.saveImage(image);
      guiLogger.info("capture image saved to: {}", file.getAbsolutePath());
    }
  }

  private void stopScript() {
    scriptRunner.stop();
    enableScripts();
  }

  private void enableScripts() {
    run.setText("RUN!");
    protocolSelection.setDisable(false);
    portSelection.setDisable(false);
    scriptSelection.setDisable(false);
    videoSelection.setDisable(false);
    loadScript(getCurrentSelectedScriptName());
  }


}
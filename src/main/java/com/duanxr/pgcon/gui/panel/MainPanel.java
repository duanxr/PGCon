package com.duanxr.pgcon.gui.panel;

import com.dooapp.fxform.FXForm;
import com.duanxr.pgcon.config.GuiConfig;
import com.duanxr.pgcon.config.InputConfig;
import com.duanxr.pgcon.config.OutputConfig;
import com.duanxr.pgcon.core.detect.api.ImageCompare;
import com.duanxr.pgcon.core.detect.api.OCR;
import com.duanxr.pgcon.core.model.Area;
import com.duanxr.pgcon.core.preprocessing.PreprocessorFactory;
import com.duanxr.pgcon.exception.AlertErrorException;
import com.duanxr.pgcon.gui.FXFormGenerator;
import com.duanxr.pgcon.gui.display.DisplayService;
import com.duanxr.pgcon.gui.display.DrawEvent;
import com.duanxr.pgcon.gui.display.impl.Rectangle;
import com.duanxr.pgcon.input.FrameCacheService;
import com.duanxr.pgcon.input.FrameCacheService.CachedFrame;
import com.duanxr.pgcon.input.impl.CameraImageInput;
import com.duanxr.pgcon.log.GuiLogLevel;
import com.duanxr.pgcon.log.GuiLogView;
import com.duanxr.pgcon.log.GuiLogger;
import com.duanxr.pgcon.output.ControllerService;
import com.duanxr.pgcon.output.ProtocolService;
import com.duanxr.pgcon.output.api.Protocol;
import com.duanxr.pgcon.script.api.Script;
import com.duanxr.pgcon.script.api.ScriptInfo;
import com.duanxr.pgcon.script.component.ScriptCache;
import com.duanxr.pgcon.script.component.ScriptManager;
import com.duanxr.pgcon.script.component.ScriptRunner;
import com.duanxr.pgcon.script.component.ScriptService;
import com.duanxr.pgcon.util.CallBackUtil;
import com.duanxr.pgcon.util.PropertyCacheUtil;
import com.duanxr.pgcon.util.SaveUtil;
import com.duanxr.pgcon.util.SystemUtil;
import com.google.common.base.Strings;
import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
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
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
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
  private static final String DRAW_KEY_MOUSE_DRAGGED = "MOUSE_DRAGGED";
  private static final String SCRIPT_CONFIGURATION_WINDOW_TITLE = "Script Configuration";
  private final ObjectProperty<Image> canvasProperty;
  private final ControllerService controllerService;
  private final DebugPanel debugPanel;
  private final DisplayService displayService;
  private final Map<String, Node> dynamicConfigurationMap;
  private final AtomicBoolean enableDebug;
  private final ExecutorService executorService;
  private final FrameCacheService frameCacheService;
  private final FXFormGenerator fxFormGenerator;
  private final GuiConfig guiConfig;
  private final GuiLogger guiLogger;
  private final InputConfig inputConfig;
  private final ProtocolService protocolService;
  private final List<String> protocols;
  private final ObjectProperty<Image> screenProperty;
  private final ScriptManager scriptManager;
  private final ScriptRunner scriptRunner;
  private final ScriptService scriptService;
  @FXML
  private ImageView canvas;
  @FXML
  private Button capture;
  @FXML
  private Button clearLog;
  private String code;
  @FXML
  private AnchorPane console;
  private WritableImage convertedImage;
  @FXML
  private ToggleButton debug;
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
  @FXML
  private SplitPane splitPaneX;
  @FXML
  private SplitPane splitPaneY;
  @FXML
  private ComboBox<String> videoSelection;

  public MainPanel(
      @Qualifier("enableDebug") AtomicBoolean enableDebug,
      OutputConfig outputConfig, ScriptRunner scriptRunner,
      ProtocolService protocolService, GuiConfig guiConfig,
      DisplayService displayService, FrameCacheService frameCacheService,
      ScriptService scriptService, ControllerService controllerService,
      InputConfig inputConfig, PreprocessorFactory preprocessorFactory,
      ImageCompare imageCompare, OCR ocr, ScriptManager scriptManager, GuiLogger guiLogger,
      DebugPanel debugPanel,
      ExecutorService executorService, FXFormGenerator fxFormGenerator) {
    this.enableDebug = enableDebug;
    this.scriptRunner = scriptRunner;
    this.scriptService = scriptService;
    this.controllerService = controllerService;
    this.protocolService = protocolService;
    this.guiConfig = guiConfig;
    this.inputConfig = inputConfig;
    this.protocols = protocolService.getProtocolList();
    this.displayService = displayService;
    this.frameCacheService = frameCacheService;
    this.scriptManager = scriptManager;
    this.guiLogger = guiLogger;
    this.debugPanel = debugPanel;
    this.executorService = executorService;
    this.fxFormGenerator = fxFormGenerator;
    this.screenProperty = new SimpleObjectProperty<>();
    this.canvasProperty = new SimpleObjectProperty<>();
    this.pointStart = null;
    this.pointEnd = null;
    this.dynamicConfigurationMap = new HashMap<>();
    CallBackUtil.setGuiLogger(this.guiLogger);
  }

  @FXML
  public void initialize() {
    initializeDebugWindow();
    initializeGuiLogViewer();
    initializeVideoComponent();
    initializeScreenAndCanvas();
    initializeProtocolSelection();
    initializePortSelection();
    initializeScriptSelection();
    initializeButtons();
    debugPanel.initialize(new Stage());
  }

  private void initializeDebugWindow() {

  }

  private void initializeScreenAndCanvas() {
    WritableImage screenImage = new WritableImage(guiConfig.getWidth(), guiConfig.getHeight());
    WritableImage canvasImage = new WritableImage(guiConfig.getWidth(), guiConfig.getHeight());
    screen.imageProperty().bind(screenProperty);
    canvas.imageProperty().bind(canvasProperty);
    screenProperty.set(screenImage);
    canvasProperty.set(canvasImage);
    displayService.registerScreen(
        input -> {
          Platform.runLater(() -> SwingFXUtils.toFXImage(input, screenImage));
          debugPanel.processDebug();
        });
    displayService.registerCanvas(
        input -> Platform.runLater(() -> SwingFXUtils.toFXImage(input, canvasImage)));
    canvas.addEventHandler(MouseEvent.MOUSE_PRESSED,
        event -> CallBackUtil.callbackWithExceptionCatch(() -> onCanvasMousePressed(event)));
    canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED,
        event -> CallBackUtil.callbackWithExceptionCatch(() -> onCanvasMouseDragged(event)));
    canvas.addEventHandler(MouseEvent.MOUSE_RELEASED,
        event -> CallBackUtil.callbackWithExceptionCatch(() -> onCanvasMouseReleased(event)));
  }

  private void onCanvasMouseReleased(MouseEvent event) {
    if (enableDebug.get()) {
      if (pointStart != null && pointEnd != null) {
        captureCanvasSelection();
      }
      pointStart = null;
      pointEnd = null;
    }
  }

  private void captureCanvasSelection() {
    try {
      Area selectedArea = Area.ofPoints(
          (pointStart.x), (pointStart.y), (pointEnd.x), (pointEnd.y));
      Area area = displayService.screenToInput(selectedArea);
      BufferedImage selectedImage = frameCacheService.getFrame().getImage()
          .getSubimage(area.getX(), area.getY(), area.getWidth(), area.getHeight());
      File file = SaveUtil.saveTempImage(selectedImage);
      guiLogger.info("Area.ofRect({},{},{},{}) , saved to: {}",
          area.getX(), area.getY(), area.getWidth(), area.getHeight(), file.getAbsolutePath());
      debugPanel.openPanel(area, selectedImage);
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
    displayService.draw(
        new DrawEvent(DRAW_KEY_MOUSE_DRAGGED, new Rectangle(
            Area.ofPoints(pointStart.x, pointStart.y, pointEnd.x, pointEnd.y),
            new Color(255, 0, 127, 128),
            3000)));
  }

  private void onCanvasMousePressed(MouseEvent event) {
    if (enableDebug.get()) {
      pointStart = new Point((int) event.getX(), (int) event.getY());
    }
    debugPanel.close();
  }

  private void initializeScripts() {
    scriptService.loadScripts();
    scriptManager.getScripts().stream()
        .map(ScriptCache::getScript).map(Script::getInfo)
        .filter(ScriptInfo::isConfigurable)
        .forEach(this::bindScriptConfigurationToGUI);
    Platform.runLater(this::initializeConfigurationWindow);
  }

  private void initializeConfigurationWindow() {
    scriptConfigurationWindow = new Stage();
    scriptConfigurationWindow.setTitle(SCRIPT_CONFIGURATION_WINDOW_TITLE);
    scriptConfigurationWindow.setResizable(false);
    scriptConfigurationPane = new ScrollPane();
    scriptConfigurationPane.setPrefHeight(640);
    scriptConfigurationPane.setMinWidth(650);
    scriptConfigurationPane.setMaxWidth(350);
    Scene scriptConfigurationScene = new Scene(scriptConfigurationPane);
    scriptConfigurationWindow.setScene(scriptConfigurationScene);
    //scriptConfigurationWindow.setOnCloseRequest(event -> JavaFxUtil.callbackWithExceptionAlert(this::saveScriptConfigurationToCache));
    scriptConfig.addEventHandler(MouseEvent.MOUSE_CLICKED,
        event -> CallBackUtil.callbackWithExceptionAlert(this::onScriptConfigurationButtonClick));
  }

  private void saveScriptConfigurationToCache() {
    String cacheKey = getScriptConfigurationCacheKey(getCurrentSelectedScriptName());
    Node content = scriptConfigurationPane.getContent();
    if (content instanceof FXForm<?>) {
      Object configBean = ((FXForm<?>) content).getSource();
      PropertyCacheUtil.bindPropertyBean(cacheKey, configBean);
    }
  }

  private String getScriptConfigurationCacheKey(String scriptName) {
    return CACHE_KEY_SCRIPT_CONFIG_CACHE_PREFIX + DigestUtils.sha1Hex(scriptName).toUpperCase();
  }

  private String getCurrentSelectedScriptName() {
    return Strings.nullToEmpty(scriptSelection.getSelectionModel().getSelectedItem());
  }

  private void onScriptConfigurationButtonClick() {
    if (scriptConfigurationWindow.isShowing()) {
      scriptConfigurationWindow.close();
    } else {
      scriptConfigurationWindow.show();
    }
  }

  private void bindScriptConfigurationToGUI(ScriptInfo<Object> scriptInfo) {
    String scriptName = scriptInfo.getDescription();
    String cacheKey = getScriptConfigurationCacheKey(scriptName);
    Object registerConfig = scriptInfo.getConfig();
    PropertyCacheUtil.bindPropertyBean(cacheKey, registerConfig);
    FXForm<?> node = fxFormGenerator.generateNode(registerConfig);
    dynamicConfigurationMap.put(scriptName, node);
  }


  private void initializeGuiLogViewer() {
    GuiLogView logView = new GuiLogView(guiLogger);
    logView.setPrefWidth(960);
    logView.setPrefHeight(190);
    logLevel.setItems(FXCollections.observableArrayList(GuiLogLevel.values()));
    logView.filterLevelProperty().bind(logLevel.getSelectionModel().selectedItemProperty());
    logView.showTimeStampProperty().bind(logShowTime.selectedProperty());
    logView.tailProperty().bind(logFollow.selectedProperty());
    logView.pausedProperty().bind(logPause.selectedProperty());
    console.getChildren().add(logView);
    clearLog.addEventHandler(MouseEvent.MOUSE_CLICKED,
        event -> CallBackUtil.callbackWithExceptionAlert(logView::clearLogs));
    Map<String, GuiLogLevel> logLevelMap = Arrays.stream(GuiLogLevel.values())
        .collect(Collectors.toMap(Enum::name, Function.identity()));
    PropertyCacheUtil.bindBooleanProperty(CACHE_KEY_LOG_SHOWTIME, logShowTime.selectedProperty());
    PropertyCacheUtil.bindBooleanProperty(CACHE_KEY_LOG_FOLLOW, logFollow.selectedProperty());
    PropertyCacheUtil.bindBooleanProperty(CACHE_KEY_LOG_PAUSE, logPause.selectedProperty());
    PropertyCacheUtil.bindEnumComboBoxProperty(CACHE_KEY_LOG_LEVEL, logLevel, GuiLogLevel.class);
  }

  private void initializeVideoComponent() {
    videoSelection.setPromptText("Please select a video input");
    List<String> cameraList = SystemUtil.getCameraList();
    videoSelection.getItems().addAll(cameraList);
    PropertyCacheUtil.bindStringsComboBoxProperty(CACHE_KEY_SELECTED_VIDEO, videoSelection,
        cameraList);
    videoSelection.getSelectionModel().selectedItemProperty().addListener(
        (observable, oldValue, newValue) -> CallBackUtil.callbackWithExceptionAlert(
            () -> openCam(newValue)));
    executorService.execute(() -> {
      try {
        openCam(videoSelection.getSelectionModel().getSelectedItem());
      } catch (Exception e) {
        guiLogger.error("initialize video component failed", e);
        videoSelection.getSelectionModel().select(null);
      }
    });
  }

  private void initializeProtocolSelection() {
    protocolSelection.setPromptText("Please select a protocol");
    protocolSelection.getItems().addAll(protocols);
    PropertyCacheUtil.bindStringsComboBoxProperty(CACHE_KEY_SELECTED_PROTOCOL, protocolSelection,
        protocols);
  }

  private void initializePortSelection() {
    portSelection.setPromptText("Please select a port");
    List<String> serialList = SystemUtil.getSerialList();
    portSelection.getItems().addAll(serialList);
    PropertyCacheUtil.bindStringsComboBoxProperty(CACHE_KEY_SELECTED_PORT, portSelection,
        serialList);
  }

  private void initializeScriptSelection() {
    scriptSelection.setDisable(true);
    run.setDisable(true);
    executorService.execute(() -> {
      initializeScripts();
      Platform.runLater(() -> {
        scriptSelection.setPromptText("Please select a script");
        List<String> scripts = scriptManager.getScriptDescriptions();
        scriptSelection.getItems().addAll(scripts);
        PropertyCacheUtil.bindStringsComboBoxProperty(CACHE_KEY_SELECTED_SCRIPT, scriptSelection,
            scripts);
        loadScript(getCurrentSelectedScriptName());
        scriptSelection.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> CallBackUtil.callbackWithExceptionAlert(
                () -> loadScript(newValue)));
        scriptSelection.setDisable(false);
        run.setDisable(false);
      });
    });
  }

  private void initializeButtons() {
    run.addEventHandler(MouseEvent.MOUSE_CLICKED,
        event -> CallBackUtil.callbackWithExceptionAlert(this::run));
    capture.addEventHandler(MouseEvent.MOUSE_CLICKED,
        event -> CallBackUtil.callbackWithExceptionAlert(this::captureScreen));
    PropertyCacheUtil.bindBooleanProperty(CACHE_KEY_ENABLE_DEBUG, debug.selectedProperty());
    enableDebug.set(debug.selectedProperty().getValue());
    debug.selectedProperty()
        .addListener((observable, oldValue, newValue) -> CallBackUtil.callbackWithExceptionAlert(
            () -> enableDebug.set(newValue)));
  }

  private void openCam(String camera) {
    if (!Strings.isNullOrEmpty(camera)) {
      displayService.setImageInput(new CameraImageInput(camera, inputConfig));
    }
  }


  private void loadScript(String scriptName) {
    if (!Strings.isNullOrEmpty(scriptName)) {
      scriptConfig.setDisable(true);
      ScriptCache<Object> script = findScript(scriptName);
      if (script.getScript().getInfo().isConfigurable()) {
        Node node = dynamicConfigurationMap.get(scriptName);
        if (node != null) {
          scriptConfigurationPane.setContent(node);
          scriptConfig.setDisable(false);
        }
      }
    }
  }

  private ScriptCache<Object> findScript(String scriptName) {
    ScriptCache<Object> script = scriptManager.getScriptByDescription(scriptName);
    if (script == null) {
      throw new AlertErrorException("cannot find script: " + scriptName);
    }
    return script;

  }


  private void run() {
    if (!scriptRunner.isRunning()) {
      runScript();
    } else {
      stopScript();
    }
  }

  @SneakyThrows
  private void runScript() {
    checkSelections();
    loadProtocol();
    ScriptCache<Object> script = findScript(getCurrentSelectedScriptName());
    if (enableDebug.get()) {
      guiLogger.info("reload script {}", script.getScript().getInfo().getDescription());
      TimeUnit.MILLISECONDS.sleep(1000);
      scriptService.reloadScripts(script);
      guiLogger.info("reload script {} success", script.getScript().getInfo().getDescription());
    }
    scriptConfigurationWindow.hide();
    scriptRunner.run(script.getScript(), () -> Platform.runLater(this::enableScripts));
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
    controllerService.clear();
    String protocolName = protocolSelection.getSelectionModel().getSelectedItem();
    String port = portSelection.getSelectionModel().getSelectedItem();
    Protocol protocol = protocolService.loadProtocol(protocolName, port);
    if (!protocol.isConnected()) {
      protocol.clear();
      throw new AlertErrorException("protocol not connected");
    }
    controllerService.setProtocol(protocol);
  }

  private void checkSelections() {
    if (Strings.isNullOrEmpty(videoSelection.getSelectionModel().getSelectedItem())) {
      throw new AlertErrorException("please select a video");
    }
    if (Strings.isNullOrEmpty(protocolSelection.getSelectionModel().getSelectedItem())) {
      throw new AlertErrorException("please select a protocol");
    }
    if (Strings.isNullOrEmpty(portSelection.getSelectionModel().getSelectedItem())) {
      throw new AlertErrorException("please select a port");
    }
    if (Strings.isNullOrEmpty(scriptSelection.getSelectionModel().getSelectedItem())) {
      throw new AlertErrorException("please select a script");
    }
  }

  private void captureScreen() {
    CachedFrame frame = frameCacheService.getFrame();
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
    scriptRunner.stop();
    run.setText("RUN!");
    protocolSelection.setDisable(false);
    portSelection.setDisable(false);
    scriptSelection.setDisable(false);
    videoSelection.setDisable(false);
    loadScript(getCurrentSelectedScriptName());
  }


}
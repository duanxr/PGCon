package com.duanxr.pgcon.gui.controller;

import com.dooapp.fxform.FXForm;
import com.duanxr.pgcon.config.GuiConfig;
import com.duanxr.pgcon.config.InputConfig;
import com.duanxr.pgcon.config.OutputConfig;
import com.duanxr.pgcon.algo.detect.model.Area;
import com.duanxr.pgcon.gui.display.canvas.DrawEvent;
import com.duanxr.pgcon.gui.component.GuiAlertException;
import com.duanxr.pgcon.gui.display.canvas.impl.Rectangle;
import com.duanxr.pgcon.gui.display.DisplayHandler;
import com.duanxr.pgcon.gui.log.GuiLog;
import com.duanxr.pgcon.gui.log.GuiLogLevel;
import com.duanxr.pgcon.gui.log.GuiLogView;
import com.duanxr.pgcon.gui.log.GuiLogger;
import com.duanxr.pgcon.input.component.FrameManager;
import com.duanxr.pgcon.input.component.FrameManager.CachedFrame;
import com.duanxr.pgcon.input.impl.CameraImageInput;
import com.duanxr.pgcon.input.impl.StaticImageInput;
import com.duanxr.pgcon.output.Controller;
import com.duanxr.pgcon.output.ProtocolManager;
import com.duanxr.pgcon.script.api.DynamicScript;
import com.duanxr.pgcon.script.api.MainScript;
import com.duanxr.pgcon.script.component.ScriptManager;
import com.duanxr.pgcon.script.component.ScriptRunner;
import com.duanxr.pgcon.util.CacheUtil;
import com.duanxr.pgcon.util.SystemUtil;
import com.duanxr.pgcon.util.SaveUtil;
import com.google.common.base.Strings;
import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.Collectors;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import lombok.extern.slf4j.Slf4j;
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
  private static final String CACHE_KEY_SELECTED_PORT = "SELECTED_PORT";
  private static final String CACHE_KEY_SELECTED_PROTOCOL = "SELECTED_PROTOCOL";
  private static final String CACHE_KEY_SELECTED_SCRIPT = "SELECTED_SCRIPT";
  private static final String CACHE_KEY_SELECTED_VIDEO = "SELECTED_VIDEO";

  private static final StaticImageInput DEFAULT_IMAGE_INPUT = new StaticImageInput(
      "/img/no_input.bmp");
  private static final String DRAW_KEY_MOUSE_DRAGGED = "MOUSE_DRAGGED";
  private final ObjectProperty<Image> canvasProperty;
  private final Controller controller;
  private final DisplayHandler displayHandler;
  private final Map<String, List<Node>> dynamicConfigurationMap;
  private final AtomicBoolean enableDebug;
  private final FrameManager frameManager;
  private final GuiConfig guiConfig;
  private final InputConfig inputConfig;
  private final OutputConfig outputConfig;
  private final ProtocolManager protocolManager;
  private final List<String> protocols;
  private final ObjectProperty<Image> screenProperty;
  private final ScriptManager scriptManager;
  private final ScriptRunner scriptRunner;
  private final double xScale;
  private final double yScale;
  @FXML
  private ImageView canvas;
  @FXML
  private AnchorPane console;
  private Point pointEnd;
  private Point pointStart;
  @FXML
  private ComboBox<String> portSelection;
  @FXML
  private ComboBox<String> protocolSelection;
  @FXML
  private Button run;
  @FXML
  private Button capture;

  @FXML
  private Button clearLog;
  @FXML
  private ImageView screen;
  @FXML
  private Label scriptConfigLabel;
  @FXML
  private FlowPane scriptConfig;
  @FXML
  private ComboBox<String> scriptSelection;
  @FXML
  private SplitPane splitPaneX;
  @FXML
  private SplitPane splitPaneY;

  @FXML
  private ToggleButton logFollow;

  @FXML
  private ToggleButton logPause;

  @FXML
  private ToggleButton logShowTime;
  @FXML
  private ToggleButton debug;
  @FXML
  private ComboBox<GuiLogLevel> logLevel;
  @FXML
  private ComboBox<String> videoSelection;

  private GuiLogger guiLogger;

  public MainPanel(@Qualifier("enableDebug") AtomicBoolean enableDebug,
      OutputConfig outputConfig, ScriptRunner scriptRunner, ScriptManager scriptManager,
      Controller controller, ProtocolManager protocolManager,
      GuiConfig guiConfig, DisplayHandler displayHandler, FrameManager frameManager,
      InputConfig inputConfig) {
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
    this.screenProperty = new SimpleObjectProperty<>();
    this.canvasProperty = new SimpleObjectProperty<>();
    this.pointStart = null;
    this.pointEnd = null;
    this.xScale = 1D * inputConfig.getWidth() / guiConfig.getWidth();
    this.yScale = 1D * inputConfig.getHeight() / guiConfig.getHeight();
    this.dynamicConfigurationMap = new HashMap<>();
  }

  @FXML
  public void initialize() {
    initializeScriptConfigurations();
    initializeGuiLogViewer();
    initializeVideoComponent();
    initializeProtocolSelection();
    initializePortSelection();
    initializeScriptSelection();
    initializeButtons();
  }

  private void initializeScriptConfigurations() {
    scriptManager.getMainScripts().values().stream()
        .filter(script -> script instanceof DynamicScript)
        .map(script -> (DynamicScript) script).forEach(script -> {
          Object configBean = script.registerConfigBean();
          FXForm configForm = new FXForm(configBean);
          configForm.setVisible(false);
          //TODO flow
          dynamicConfigurationMap.put(script.getScriptName(), Collections.singletonList(configForm));
        });
  }

  private void initializeGuiLogViewer() {
    GuiLog guiLog = new GuiLog();
    guiLogger = new GuiLogger(guiLog, "");
    scriptManager.register(guiLogger);
    GuiLogView logView = new GuiLogView(guiLogger);
    logView.setPrefWidth(960);
    logView.setPrefHeight(160);
    logLevel.setItems(FXCollections.observableArrayList(GuiLogLevel.values()));
    logView.filterLevelProperty().bind(logLevel.getSelectionModel().selectedItemProperty());
    logView.showTimeStampProperty().bind(logShowTime.selectedProperty());
    logView.tailProperty().bind(logFollow.selectedProperty());
    logView.pausedProperty().bind(logPause.selectedProperty());
    console.getChildren().add(logView);
    clearLog.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> logView.clearLogs());
    CacheUtil.bindCache(CACHE_KEY_LOG_SHOWTIME, logShowTime.selectedProperty());
    CacheUtil.bindCache(CACHE_KEY_LOG_FOLLOW, logFollow.selectedProperty());
    CacheUtil.bindCache(CACHE_KEY_LOG_PAUSE, logPause.selectedProperty());
    String selectedItemCache = CacheUtil.get(CACHE_KEY_LOG_LEVEL);
    GuiLogLevel guiLogLevelCache = null;
    if (!Strings.isNullOrEmpty(selectedItemCache)
        && (guiLogLevelCache = Arrays.stream(GuiLogLevel.values()).collect(
        Collectors.toMap(Enum::name, Function.identity())).get(selectedItemCache)) != null) {
      logLevel.getSelectionModel().select(guiLogLevelCache);
    }
    logLevel.getSelectionModel().selectedItemProperty().addListener(
        (observable, oldValue, newValue) -> {
          if (newValue != null) {
            CacheUtil.set(CACHE_KEY_LOG_LEVEL, newValue.name());
          }
        });

  }

  private void initializeVideoComponent() {
    videoSelection.setPromptText("请选择视频源");
    List<String> cameraList = SystemUtil.getCameraList();
    String selectedItemCache = CacheUtil.get(CACHE_KEY_SELECTED_VIDEO);
    cameraList.forEach(videoSelection.getItems()::add);
    if (!Strings.isNullOrEmpty(selectedItemCache) && cameraList.contains(selectedItemCache)) {
      videoSelection.getSelectionModel().select(selectedItemCache);
      openCam(selectedItemCache);
    }
    videoSelection.getSelectionModel().selectedItemProperty().addListener(
        (observable, oldValue, newValue) -> {
          if (newValue != null) {
            openCam(newValue);
            CacheUtil.set(CACHE_KEY_SELECTED_VIDEO, newValue);
          }
        });
    screen.imageProperty().bind(screenProperty);
    canvas.imageProperty().bind(canvasProperty);
    displayHandler.registerScreen(input -> screenProperty.set(SwingFXUtils.toFXImage(input, null)));
    displayHandler.registerCanvas(input -> canvasProperty.set(SwingFXUtils.toFXImage(input, null)));
    canvas.addEventHandler(MouseEvent.MOUSE_PRESSED,
        event -> callbackWithExceptionCatch(() -> {
          if (enableDebug.get()) {
            pointStart = new Point((int) event.getX(), (int) event.getY());
          }
        }));
    canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED,
        event -> callbackWithExceptionCatch(() -> {
          if (enableDebug.get()) {
            pointEnd = new Point((int) event.getX(), (int) event.getY());
            if (pointStart != null) {
              displayHandler.draw(
                  new DrawEvent(DRAW_KEY_MOUSE_DRAGGED, new Rectangle(
                      Area.ofPoints(pointStart.x, pointStart.y, pointEnd.x, pointEnd.y),
                      new Color(255, 0, 127, 128),
                      3000)));
            }
          }
        }));
    canvas.addEventHandler(MouseEvent.MOUSE_RELEASED,
        event -> callbackWithExceptionCatch(() -> {
          if (enableDebug.get()) {
            if (pointStart != null) {
              Area area = Area.ofPoints((pointStart.x * xScale), (pointStart.y * yScale),
                  (pointEnd.x * xScale), (pointEnd.y * yScale));
              try {
                BufferedImage subImage = frameManager.get().getImage()
                    .getSubimage(area.getX(), area.getY(), area.getWidth(), area.getHeight());
                File file = SaveUtil.saveTempImage(subImage);
                guiLogger.info("area of points: {},{},{},{}, saved to: {}",
                    (int) (pointStart.x * xScale), (int) (pointStart.y * yScale),
                    (int) (pointEnd.x * xScale), (int) (pointEnd.y * yScale),
                    file.getAbsolutePath());
              } catch (RasterFormatException ignored) {
              }
            }
            pointStart = null;
          }
        }));
  }

  private void initializeProtocolSelection() {
    protocolSelection.setPromptText("请选择固件协议");
    String selectedItemCache = CacheUtil.get(CACHE_KEY_SELECTED_PROTOCOL);
    protocols.forEach(protocolSelection.getItems()::add);
    if (!Strings.isNullOrEmpty(selectedItemCache) && protocols.contains(selectedItemCache)) {
      protocolSelection.getSelectionModel().select(selectedItemCache);
    }
    protocolSelection.getSelectionModel().selectedItemProperty().addListener(
        (observable, oldValue, newValue) -> {
          if (newValue != null) {
            CacheUtil.set(CACHE_KEY_SELECTED_PROTOCOL, newValue);
          }
        });
  }

  private void initializePortSelection() {
    portSelection.setPromptText("请选择串口端口");
    String selectedItemCache = CacheUtil.get(CACHE_KEY_SELECTED_PORT);
    List<String> serialList = SystemUtil.getSerialList();
    serialList.forEach(portSelection.getItems()::add);
    if (!Strings.isNullOrEmpty(selectedItemCache) && serialList.contains(selectedItemCache)) {
      portSelection.getSelectionModel().select(selectedItemCache);
    }
    portSelection.getSelectionModel().selectedItemProperty().addListener(
        (observable, oldValue, newValue) -> {
          if (newValue != null) {
            CacheUtil.set(CACHE_KEY_SELECTED_PORT, newValue);
          }
        });
  }

  private void initializeScriptSelection() {
    scriptSelection.setPromptText("请选择执行脚本");
    String selectedItemCache = CacheUtil.get(CACHE_KEY_SELECTED_SCRIPT);
    scriptManager.getMainScripts().keySet().forEach(scriptSelection.getItems()::add);
    if (!Strings.isNullOrEmpty(selectedItemCache) && scriptManager.getMainScripts()
        .containsKey(selectedItemCache)) {
      scriptSelection.getSelectionModel().select(selectedItemCache);
    }
    scriptSelection.getSelectionModel().selectedItemProperty().addListener(
        (observable, oldValue, newValue) -> {
          if (newValue != null) {
            loadScript(newValue);
            CacheUtil.set(CACHE_KEY_SELECTED_SCRIPT, newValue);
          }
        });
  }

  private void initializeButtons() {
    run.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> run());
    capture.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> capture());
    boolean enableDebugCache = Boolean.TRUE.toString()
        .equalsIgnoreCase(CacheUtil.get(CACHE_KEY_ENABLE_DEBUG));
    enableDebug.set(enableDebugCache);
    debug.selectedProperty().setValue(enableDebugCache);
    debug.selectedProperty().addListener(
        (observable, oldValue, newValue) -> {
          if (newValue != null) {
            CacheUtil.set(CACHE_KEY_ENABLE_DEBUG, newValue.toString());
            enableDebug.set(newValue);
          }
        });
  }

  private void capture() {
    CachedFrame frame = frameManager.get();
    if (frame != null) {
      BufferedImage image = frame.getImage();
      File file = SaveUtil.saveImage(image);
      guiLogger.info("capture image saved to: {}", file.getAbsolutePath());
    }
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
      guiLogger.error("GUI callback error", e);
    }
  }

  private void loadScript(String scriptName) {
    if (!Strings.isNullOrEmpty(scriptName)) {
      MainScript script = scriptManager.getMainScripts().get(scriptName);
      if (script == null) {
        throw new GuiAlertException("cannot find script: " + scriptName);
      }
      scriptConfig.getChildren().clear();
      if (script instanceof DynamicScript) {
        scriptConfigLabel.setVisible(false);
        scriptConfig.getChildren().addAll(dynamicConfigurationMap.get(scriptName));
      } else {
        scriptConfigLabel.setVisible(true);
        scriptConfigLabel.setText("The loaded script is not configurable");
      }
    }
  }

  private void run() {
  }

  private void stopScript() {
  }

  private void callbackWithExceptionAlert(Runnable runnable) {
    try {
      runnable.run();
    } catch (Exception e) {
      guiLogger.error("GUI callback error", e);
      Alert alert = new Alert(Alert.AlertType.ERROR);
      alert.setTitle("Error!");
      if (e instanceof GuiAlertException) {
        alert.setHeaderText(e.getMessage());
      } else {
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
  }
}

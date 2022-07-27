package com.duanxr.pgcon.gui.controller;

import com.duanxr.pgcon.config.GuiConfig;
import com.duanxr.pgcon.config.InputConfig;
import com.duanxr.pgcon.config.OutputConfig;
import com.duanxr.pgcon.core.detect.model.Area;
import com.duanxr.pgcon.event.DrawEvent;
import com.duanxr.pgcon.gui.display.canvas.impl.Rectangle;
import com.duanxr.pgcon.gui.old.DisplayHandler;
import com.duanxr.pgcon.gui.old.DisplayScreen;
import com.duanxr.pgcon.input.component.FrameManager;
import com.duanxr.pgcon.input.impl.CameraImageInput;
import com.duanxr.pgcon.input.impl.StaticImageInput;
import com.duanxr.pgcon.output.Controller;
import com.duanxr.pgcon.output.api.Protocol;
import com.duanxr.pgcon.script.component.ScriptManager;
import com.duanxr.pgcon.script.component.ScriptRunner;
import com.duanxr.pgcon.util.SystemUtil;
import com.duanxr.pgcon.util.TempFileUtil;
import com.google.common.base.Strings;
import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.Collectors;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * @author 段然 2022/7/27
 */
@Slf4j
@Component
public class MainPanel {

  private static final StaticImageInput DEFAULT_IMAGE_INPUT = new StaticImageInput(
      "/img/no_input.bmp");
  private static final String MOUSE_DRAGGED = "MOUSE_DRAGGED";
  private final Controller controller;
  private final DisplayHandler displayHandler;
  //private final DisplayHandler displayHandler;
  private final DisplayScreen displayScreen;
  private final AtomicBoolean enableDebug;
  private final GuiConfig guiConfig;
  private final ObjectProperty<Image> screenProperty;
  private final ObjectProperty<Image> canvasProperty;
  private final OutputConfig outputConfig;
  private final Map<String, Protocol> protocols;
  private final ScriptManager scriptManager;
  private final ScriptRunner scriptRunner;
  private final FrameManager frameManager;
  @FXML
  private ComboBox<String> portSelection;
  @FXML
  private ComboBox<String> protocolSelection;
  @FXML
  private ImageView screen;

  @FXML
  private ImageView canvas;
  @FXML
  private ComboBox<String> scriptSelection;
  @FXML
  private SplitPane splitPaneX;
  @FXML
  private SplitPane splitPaneY;
  @FXML
  private ComboBox<String> videoSelection;

  private Point pointStart;
  private Point pointEnd;

  private double xScale;
  private double yScale;

  public MainPanel(@Qualifier("enableDebug") AtomicBoolean enableDebug, List<Protocol> protocolList,
      OutputConfig outputConfig, ScriptRunner scriptRunner, ScriptManager scriptManager,
      DisplayScreen displayScreen, Controller controller, GuiConfig guiConfig,
      DisplayHandler displayHandler, FrameManager frameManager, InputConfig inputConfig) {
    this.enableDebug = enableDebug;
    this.outputConfig = outputConfig;
    this.scriptRunner = scriptRunner;
    this.scriptManager = scriptManager;
    this.displayScreen = displayScreen;
    this.controller = controller;
    this.guiConfig = guiConfig;
    this.protocols = protocolList.stream()
        .collect(Collectors.toMap(Protocol::getName, Function.identity()));
    this.displayHandler = displayHandler;
    this.frameManager = frameManager;
    this.screenProperty = new SimpleObjectProperty<>();
    this.canvasProperty = new SimpleObjectProperty<>();
    this.pointStart = null;
    this.pointEnd = null;
    this.xScale = 1D * inputConfig.getWidth() / guiConfig.getWidth();
    this.yScale = 1D * inputConfig.getHeight() / guiConfig.getHeight();
  }

  @FXML
  public void initialize() {
    initializeVideoComponent();
    initializeProtocolSelection();
    initializePortSelection();
    initializeScriptSelection();
  }

  private void initializeVideoComponent() {
    videoSelection.setPromptText("请选择视频源");
    List<String> cameraList = SystemUtil.getCameraList();
    String selectedItemCache = null; //TODO read from config
    cameraList.forEach(videoSelection.getItems()::add);
    if (!Strings.isNullOrEmpty(selectedItemCache) && cameraList.contains(selectedItemCache)) {
      videoSelection.getSelectionModel().select(selectedItemCache);
    }
    videoSelection.getSelectionModel().selectedItemProperty().addListener(
        (observable, oldValue, newValue) -> {
          if (newValue != null) {
            openCam(newValue);
          }
        });
    //TODO WritableImage?
    screen.imageProperty().bind(screenProperty);
    canvas.imageProperty().bind(canvasProperty);
    displayHandler.registerScreen(input -> screenProperty.set(SwingFXUtils.toFXImage(input, null)));
    displayHandler.registerCanvas(input -> canvasProperty.set(SwingFXUtils.toFXImage(input, null)));
    //todo enabledebug? exception cache?
    canvas.addEventHandler(MouseEvent.MOUSE_PRESSED,
        event -> {
          pointStart = new Point((int) event.getX(), (int) event.getY());
        });
    canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
      pointEnd = new Point((int) event.getX(), (int) event.getY());
      if (pointStart != null) {
        displayHandler.draw(
            new DrawEvent(MOUSE_DRAGGED, new Rectangle(
                Area.ofPoints(pointStart.x, pointStart.y, pointEnd.x, pointEnd.y),
                new Color(255, 0, 127, 128),
                3000)));
      }
    });
    canvas.addEventHandler(MouseEvent.MOUSE_RELEASED,
        event -> {
          if (pointStart != null) {
            try {
              Area area = Area.ofPoints((pointStart.x * xScale), (pointStart.y * yScale),
                  (pointEnd.x * xScale), (pointEnd.y * yScale));
              if (area.getWidth() > 0 && area.getHeight() > 0) {
                BufferedImage subImage = frameManager.get().getImage()
                    .getSubimage(area.getX(), area.getY(), area.getWidth(), area.getHeight());
                File file = TempFileUtil.saveTempImage(subImage);
                log.info("area of points: {},{},{},{}, saved to: {}",
                    (int) (pointStart.x * xScale), (int) (pointStart.y * yScale),
                    (int) (pointEnd.x * xScale), (int) (pointEnd.y * yScale),
                    file.getAbsolutePath());
              }
            } catch (Exception e) {
              log.error("", e);
            }
          }
          pointStart = null;
        });
  }

  private void initializeProtocolSelection() {
    protocolSelection.setPromptText("请选择固件协议");
    String selectedItemCache = null; //TODO read from config
    protocols.keySet().forEach(protocolSelection.getItems()::add);
    if (!Strings.isNullOrEmpty(selectedItemCache) && protocols.containsKey(selectedItemCache)) {
      protocolSelection.getSelectionModel().select(selectedItemCache);
    }
    protocolSelection.getSelectionModel().selectedItemProperty().addListener(
        (observable, oldValue, newValue) -> {
          log.info("seleceted camera: {}", newValue);
          if (newValue != null) {
            openProtocol(newValue);
          }
        });
  }

  private void initializePortSelection() {
    portSelection.setPromptText("请选择串口端口");
    String selectedItemCache = null; //TODO read from config
    List<String> serialList = SystemUtil.getSerialList();
    serialList.forEach(portSelection.getItems()::add);
    if (!Strings.isNullOrEmpty(selectedItemCache) && serialList.contains(selectedItemCache)) {
      portSelection.getSelectionModel().select(selectedItemCache);
    }
    portSelection.getSelectionModel().selectedItemProperty().addListener(
        (observable, oldValue, newValue) -> {
          if (newValue != null) {
            openPort(newValue);
          }
        });
  }

  private void initializeScriptSelection() {
    scriptSelection.setPromptText("请选择执行脚本");
    String selectedItemCache = null; //TODO read from config
    scriptManager.getMainScripts().keySet().forEach(scriptSelection.getItems()::add);
    if (!Strings.isNullOrEmpty(selectedItemCache) && scriptManager.getMainScripts()
        .containsKey(selectedItemCache)) {
      scriptSelection.getSelectionModel().select(selectedItemCache);
    }
    scriptSelection.getSelectionModel().selectedItemProperty().addListener(
        (observable, oldValue, newValue) -> {
          if (newValue != null) {
            openScript(newValue);
          }
        });
  }

  private void openCam(String camera) {
    if (!Strings.isNullOrEmpty(camera)) {
      displayHandler.setImageInput(new CameraImageInput(camera, guiConfig));
    }
  }

  private void openProtocol(String observable) {

  }

  private void openPort(String observable) {

  }

  private void openScript(String observable) {

  }

  private void closeCam(Observable observable) {
    closeProtocol(null);
  }

  private void closeProtocol(Observable observable) {
    closePort(null);
  }

  private void closePort(Observable observable) {
    closeScript(null);
  }

  private void closeScript(Observable observable) {
    stopScript();
  }

  private void stopScript() {
  }


  private void openScriptConfig(Observable observable) {

  }

  private void debugChanged(Observable observable) {

  }

  private void clearLogs(Observable observable) {

  }

}

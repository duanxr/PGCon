package com.duanxr.pgcon.gui;

import static com.duanxr.pgcon.config.ConstantConfig.MAIN_PANEL_TITLE;

import com.duanxr.pgcon.config.GuiConfig;
import com.duanxr.pgcon.config.OutputConfig;
import com.duanxr.pgcon.script.api.MainScript;
import com.duanxr.pgcon.script.component.ScriptManager;
import com.duanxr.pgcon.script.component.ScriptRunner;
import com.duanxr.pgcon.input.impl.CameraImageInput;
import com.duanxr.pgcon.output.Controller;
import com.duanxr.pgcon.output.impl.ec.EasyConProtocol;
import com.duanxr.pgcon.util.SystemUtil;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.swing.JFrame;
import javax.swing.JPanel;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author Duanran 2019/12/16
 */
@Slf4j
@Service
public class ControlPanel extends JFrame {

  private final DisplayHandler displayHandler;
  private final OutputConfig outputConfig;
  private final ScriptRunner scriptRunner;
  private final ScriptManager scriptManager;
  private final DisplayScreen screen;
  private final Controller controller;
  private final GuiConfig guiConfig;

  private final ControlBox<MainScript> controlBox;

  private JPanel extraPanel;

  public ControlPanel(DisplayHandler displayHandler,
      Controller controller, ScriptRunner scriptRunner,
      DisplayScreen screen, GuiConfig guiConfig, OutputConfig outputConfig,
      ScriptManager scriptManager) {
    this.displayHandler = displayHandler;
    this.scriptRunner = scriptRunner;
    this.outputConfig = outputConfig;
    this.controller = controller;
    this.guiConfig = guiConfig;
    this.screen = screen;
    this.scriptManager = scriptManager;

    this.controlBox = new ControlBox<>(this::selectScript, "选择执行脚本");
  }

  @SneakyThrows
  @PostConstruct
  private void initPanel() {
    Thread.sleep(1500);
    this.setSize(guiConfig.getWidth() + 200, guiConfig.getHeight() + 46);
    this.setResizable(false);
    this.setTitle(MAIN_PANEL_TITLE);
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setLayout(new GridBagLayout());
    addDisplayScreen();
    addVideoSelection();
    addOutputSelection();
    addScriptSelection();
    this.revalidate();
  }

  @SneakyThrows
  private void addScriptSelection() {
    scriptManager.getMainScripts().values().forEach(this::addScript);
    GridBagConstraints bagConstraints = new GridBagConstraints();
    bagConstraints.fill = GridBagConstraints.BOTH;
    bagConstraints.anchor = GridBagConstraints.WEST;
    bagConstraints.gridheight = 1;
    bagConstraints.gridwidth = 1;
    bagConstraints.gridx = 1;
    bagConstraints.gridy = 2;
    this.add(controlBox, bagConstraints);
  }

  private void selectScript(MainScript mainScript) {
    scriptRunner.stop();
    if (extraPanel != null) {
      this.remove(extraPanel);
    }
    if (mainScript == null) {
      return;
    }
    scriptRunner.run(mainScript);
  }

  @SneakyThrows
  private void addOutputSelection() {
    GridBagConstraints bagConstraints = new GridBagConstraints();
    bagConstraints.fill = GridBagConstraints.BOTH;
    bagConstraints.anchor = GridBagConstraints.WEST;
    bagConstraints.gridheight = 1;
    bagConstraints.gridwidth = 1;
    bagConstraints.gridx = 1;
    bagConstraints.gridy = 1;
    List<String> portList = SystemUtil.getSerialList();
    ControlBox<String> controlBox = new ControlBox<>(
        this::selectOutputPort, "选择输出端口");
    for (String name : portList) {
      controlBox.addItem(name, name);
    }
    this.add(controlBox, bagConstraints);
  }

  @SneakyThrows
  private void selectOutputPort(String portName) {
    if (portName != null) {
      this.controller.setProtocol(new EasyConProtocol(portName, outputConfig.getBaudRate()));
    }
  }

  @SneakyThrows
  private void addVideoSelection() {
    GridBagConstraints bagConstraints = new GridBagConstraints();
    bagConstraints.fill = GridBagConstraints.BOTH;
    bagConstraints.anchor = GridBagConstraints.WEST;
    bagConstraints.gridheight = 1;
    bagConstraints.gridwidth = 1;
    bagConstraints.gridx = 1;
    bagConstraints.gridy = 0;
    List<String> cameraList = SystemUtil.getCameraList();
    ControlBox<String> controlBox = new ControlBox<>(
        this::selectVideo, "选择视频源");
    for (String name : cameraList) {
      controlBox.addItem(name, name);
    }
    this.add(controlBox, bagConstraints);
  }

  private void selectVideo(String cameraName) {
    this.displayHandler.setImageInput(
        cameraName == null ? null : new CameraImageInput(cameraName, guiConfig));
  }

  @SneakyThrows
  private void addDisplayScreen() {
    GridBagConstraints bagConstraints = new GridBagConstraints();
    bagConstraints.fill = GridBagConstraints.NONE;
    bagConstraints.anchor = GridBagConstraints.EAST;
    bagConstraints.gridheight = 12;
    bagConstraints.gridwidth = 1;
    bagConstraints.gridx = 0;
    bagConstraints.gridy = 0;
    this.add(screen, bagConstraints);
    this.setVisible(true);
  }

  public void addScript(MainScript mainScript) {
    controlBox.addItem(mainScript, mainScript.getScriptName());
  }

}

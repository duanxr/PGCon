package com.duanxr.pgcon.gui;

import static com.duanxr.pgcon.util.ConstantConfig.MAIN_PANEL_TITLE;
import static com.duanxr.pgcon.util.ConstantConfig.SIZE;

import com.duanxr.pgcon.core.script.Script;
import com.duanxr.pgcon.core.script.ScriptLoader;
import com.duanxr.pgcon.core.script.ScriptRunner;
import com.duanxr.pgcon.input.CameraImageInput;
import com.duanxr.pgcon.input.StreamImageInput;
import com.duanxr.pgcon.output.Controller;
import com.duanxr.pgcon.output.SerialPort;
import com.duanxr.pgcon.output.sc.SerialConProtocol;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.swing.JFrame;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Duanran 2019/12/16
 */
@Slf4j
@Service
public class ControlPanel extends JFrame {

  @Autowired
  private DisplayHandler displayHandler;

  @Autowired
  private Controller controller;

  @Autowired
  private ScriptRunner scriptRunner;

  @Autowired
  private ScriptLoader scriptLoader;

  @Autowired
  private DisplayScreen screen;

  @PostConstruct
  private void initPanel() {
    this.setSize((int) (SIZE.width + 200), (int) (SIZE.height + 46));
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
    GridBagConstraints bagConstraints = new GridBagConstraints();
    bagConstraints.fill = GridBagConstraints.BOTH;
    bagConstraints.anchor = GridBagConstraints.WEST;
    bagConstraints.gridheight = 1;
    bagConstraints.gridwidth = 1;
    bagConstraints.gridx = 1;
    bagConstraints.gridy = 2;
    List<Script> list = scriptLoader.getScriptList();
    ControlBox<Script> controlBox = new ControlBox<>(this::selectScript, "选择执行脚本");
    for (Script script : list) {
      controlBox.addItem(script, script.name());
    }
    this.add(controlBox, bagConstraints);
  }

  private void selectScript(Script script) {
    scriptRunner.stopScript();
    if (script == null) {
      return;
    }
    scriptRunner.runScript(script);
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
    List<String> portList = SerialPort.getSerialList();
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
      this.controller.setProtocol(new SerialConProtocol(portName));
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
    List<String> cameraList = StreamImageInput.getCameraList();
    ControlBox<String> controlBox = new ControlBox<>(
        this::selectVideo, "选择视频源");
    for (String name : cameraList) {
      controlBox.addItem(name, name);
    }
    this.add(controlBox, bagConstraints);
  }

  private void selectVideo(String cameraName) {
    this.displayHandler.setImageInput(cameraName == null ? null : new CameraImageInput(cameraName));
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

}

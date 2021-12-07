/*
package com.duanxr.pgcon.gui;

import static com.duanxr.pgcon.util.ConstantConfig.MAIN_PANEL_TITLE;
import static com.duanxr.pgcon.util.ConstantConfig.MAIN_PANEL_VIDEO_RETRY_DELAY;
import static com.duanxr.pgcon.util.ConstantConfig.SIZE;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import javax.annotation.PostConstruct;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

*/
/**
 * @author Duanran 2019/12/16
 *//*

@Slf4j
@Service
public class ControlPanel extends JFrame {

  private static final Color COLOR = new Color(0, 255, 0, 127);
  @Autowired
  private DisplayHandler displayHandler;

  @Autowired
  private InputHandler inputHandler;

  @Autowired
  private ActionHandler actionHandler;

  @Autowired
  private ScriptRunner scriptRunner;

  @Autowired
  private ScriptLoader scriptLoader;

  private Executor executor;

  private JLabel videoLabel;

  @PostConstruct
  private void initPanel() {
    this.setSize((int) (SIZE.width + 200), (int) (SIZE.height + 46));
    this.setResizable(false);
    this.setTitle(MAIN_PANEL_TITLE);
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setLayout(new GridBagLayout());
    scriptLoader.load();
    addVideoLabel();
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
    List<String> scriptList = ScriptCache.getScriptList();
    com.duanxr.rhm.core.gui.ControlBox<String> controlBox = new com.duanxr.rhm.core.gui.ControlBox<>(
        this::selectScript, "选择执行脚本");
    for (String name : scriptList) {
      controlBox.addItem(name, name);
    }
    this.add(controlBox, bagConstraints);
  }

  private void selectScript(String scriptName) {
    scriptRunner.stopScript();
    if (scriptName == null) {
      return;
    }
    Script executableScript = ScriptCache.get(scriptName);
    if (executableScript != null) {
      scriptRunner.runScript(executableScript);
    }
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
    List<String> portList = SerialControllerOutput.getSerialList();
    com.duanxr.rhm.core.gui.ControlBox<String> controlBox = new com.duanxr.rhm.core.gui.ControlBox<>(
        this::selectOutputPort, "选择输出端口");
    for (int i = 0; i < portList.size(); i++) {
      String name = portList.get(i);
      controlBox.addItem(name, name);
    }
    this.add(controlBox, bagConstraints);
  }

  @SneakyThrows
  private void selectOutputPort(String portName) {
    this.actionHandler
        .setControllerOutput(portName == null ? null : new SerialControllerOutput(portName));
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
    List<String> cameraList = CameraImageInput.getCameraList();
    com.duanxr.rhm.core.gui.ControlBox<Integer> controlBox = new com.duanxr.rhm.core.gui.ControlBox<>(
        this::selectVideo, "选择视频源");
    for (int i = 0; i < cameraList.size(); i++) {
      String name = cameraList.get(i);
      controlBox.addItem(i, name);
    }
    this.add(controlBox, bagConstraints);
  }

  private void selectVideo(Integer index) {
    this.inputHandler.setNewInput(
        index == null ? InputHandler.getNoInputHandler() : new CameraImageInput(index));
  }

  @SneakyThrows
  private void addVideoLabel() {
    GridBagConstraints bagConstraints = new GridBagConstraints();
    bagConstraints.fill = GridBagConstraints.NONE;
    bagConstraints.anchor = GridBagConstraints.EAST;
    bagConstraints.gridheight = 12;
    bagConstraints.gridwidth = 1;
    bagConstraints.gridx = 0;
    bagConstraints.gridy = 0;
    this.videoLabel = new JLabel() {
      Point pointStart = null;
      Point pointEnd = null;

      {
        addMouseListener(new MouseAdapter() {
          public void mousePressed(MouseEvent e) {
            pointStart = e.getPoint();
          }

          public void mouseReleased(MouseEvent e) {
            log.info("拖动区域:  {},{},{},{}", pointStart.y, pointEnd.y, pointStart.x, pointEnd.x);
            pointStart = null;
          }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
          public void mouseMoved(MouseEvent e) {
            pointEnd = e.getPoint();
          }

          public void mouseDragged(MouseEvent e) {
            pointEnd = e.getPoint();
            displayHandler.setDetectRect(
                new CachedImageArea(pointStart.x, pointStart.y, pointEnd.x, pointEnd.y), "DDAD",
                "");
          }
        });
      }
    };
    Dimension dimension = new Dimension((int) SIZE.width, (int) SIZE.height);
    this.videoLabel.setMinimumSize(dimension);
    this.videoLabel.setPreferredSize(dimension);
    this.videoLabel.setMaximumSize(dimension);
    this.add(videoLabel, bagConstraints);
    this.setVisible(true);
    this.executor = Executors.newSingleThreadExecutor();
    this.executor.execute(this::showVideo);
  }

  @SneakyThrows
  private void showVideo() {
    while (inputHandler != null) {
      try {
        ImageIcon image = new ImageIcon(displayHandler.getImage());
        videoLabel.setIcon(image);
        videoLabel.repaint();
      } catch (Exception e) {
        log.warn("show video Exception.", e);
      }
      Thread.sleep(MAIN_PANEL_VIDEO_RETRY_DELAY);
    }
  }

}
*/

package com.duanxr.pgcon.core.script;

import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * @author 段然 2021/12/9
 */
public interface Script extends Runnable {

  String name();

  void stop();

  JPanel label();
}

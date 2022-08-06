package com.duanxr.pgcon.gui.fxform.factory;

import com.dooapp.fxform.view.FXFormNode;
import com.dooapp.fxform.view.FXFormNodeWrapper;
import javafx.scene.control.TextField;
import javafx.util.Callback;

/**
 * @author 段然 2022/8/6
 */
public class ReadOnlyTextFactory implements Callback<Void, FXFormNode> {

  public FXFormNode call(Void aVoid) {
    final TextField textField = new TextField();
    textField.setEditable(false);
    return new FXFormNodeWrapper(textField, textField.textProperty());
  }

}
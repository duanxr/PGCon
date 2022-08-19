package com.duanxr.pgcon.gui.fxform.factory;

import com.dooapp.fxform.view.FXFormNode;
import com.dooapp.fxform.view.FXFormNodeWrapper;
import javafx.scene.control.Label;
import javafx.util.Callback;

/**
 * @author 段然 2022/8/6
 */
public class ReadOnlyLabelFactory implements Callback<Void, FXFormNode> {

  public FXFormNode call(Void aVoid) {
    final Label textField = new Label();
    textField.setDisable(true);
    return new FXFormNodeWrapper(textField, textField.textProperty(),false);
  }

}
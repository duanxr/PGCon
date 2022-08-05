package com.duanxr.pgcon.gui.fxform.factory;

import com.dooapp.fxform.view.FXFormNode;
import com.dooapp.fxform.view.FXFormNodeWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Label;
import javafx.util.Callback;

/**
 * @author 段然 2022/7/31
 */
public class ConfigLabelFactory implements Callback<Void, FXFormNode> {

  private final String labelText;

  public ConfigLabelFactory(String labelText) {
    this.labelText = labelText;
  }

  public FXFormNode call(Void aVoid) {
    final Label label = new Label();
    label.setMinWidth(Label.USE_PREF_SIZE);
    label.setMaxWidth(Label.USE_PREF_SIZE);
    label.setText(labelText);
    return new FXFormNodeWrapper(label, new SimpleStringProperty(), false);
  }

}

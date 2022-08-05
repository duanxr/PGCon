package com.duanxr.pgcon.gui.fxform.provider;

import com.dooapp.fxform.model.Element;
import com.dooapp.fxform.view.FXFormNode;
import com.dooapp.fxform.view.factory.DefaultLabelFactoryProvider;
import com.dooapp.fxform.view.factory.impl.LabelFactory;
import com.duanxr.pgcon.gui.fxform.annotation.ConfigLabel;
import com.duanxr.pgcon.gui.fxform.factory.ConfigLabelFactory;
import javafx.util.Callback;

/**
 * @author 段然 2022/7/31
 */
public class ConfigLabelFactoryProvider extends DefaultLabelFactoryProvider {

  public Callback<Void, FXFormNode> getFactory(Element element) {
    try {
      ConfigLabel annotation = (ConfigLabel) element.getAnnotation(ConfigLabel.class);
      return new ConfigLabelFactory(annotation.value());
    } catch (Exception ignored) {
    }
    return new LabelFactory();
  }

}

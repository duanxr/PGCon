package com.duanxr.pgcon.gui.fxform.factory;

import com.dooapp.fxform.view.FXFormNode;
import com.dooapp.fxform.view.FXFormNodeWrapper;
import com.duanxr.pgcon.gui.fxform.annotation.ConfigPercentage;
import java.lang.annotation.Annotation;
import javafx.scene.control.Slider;
import javafx.util.Callback;

/**
 * @author 段然 2022/7/31
 */
public class PercentageFactory extends AnnotationFactory {
  public PercentageFactory() {
    super(ConfigPercentage.class);
  }

  public FXFormNode call(Void aVoid) {
    Slider slider = new Slider(0.0,1.0,1.0);
    return new FXFormNodeWrapper(slider, slider.valueProperty());
  }

}

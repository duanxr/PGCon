package com.duanxr.pgcon.gui.debug;

import com.dooapp.fxform.annotation.FormFactory;
import com.dooapp.fxform.view.factory.impl.EnumChoiceBoxFactory;
import com.duanxr.pgcon.core.preprocessing.config.SmoothingPreProcessorConfig;
import com.duanxr.pgcon.core.preprocessing.config.SmoothingPreProcessorConfig.SmoothingType;
import com.duanxr.pgcon.gui.fxform.annotation.ConfigLabel;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * @author 段然 2022/8/1
 */
@Data
@Component
public class DebugBlurConfig {
  @ConfigLabel("Enable Smoothing")
  private SimpleBooleanProperty enableSmoothing = new SimpleBooleanProperty(false);
  @ConfigLabel("Size")
  private SimpleIntegerProperty size = new SimpleIntegerProperty(5);
  @ConfigLabel("Sigma Color")
  private SimpleDoubleProperty sigmaColor = new SimpleDoubleProperty(75);
  @ConfigLabel("Sigma Space")
  private SimpleDoubleProperty sigmaSpace = new SimpleDoubleProperty(75);
  @ConfigLabel("Smoothing Type")
  @FormFactory(EnumChoiceBoxFactory.class)
  private SimpleObjectProperty<SmoothingType> smoothingType = new SimpleObjectProperty<>(
      SmoothingType.GAUSSIAN);

  public SmoothingPreProcessorConfig convertToSmoothingPreProcessorConfig() {
    return SmoothingPreProcessorConfig.builder()
        .enable(enableSmoothing.get())
        .type(smoothingType.get())
        .size(size.get())
        .sigmaColor(sigmaColor.get())
        .sigmaSpace(sigmaSpace.get())
        .build();
  }

}

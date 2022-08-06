package com.duanxr.pgcon.gui.debug;

import com.dooapp.fxform.annotation.FormFactory;
import com.dooapp.fxform.view.factory.impl.EnumChoiceBoxFactory;
import com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig;
import com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType;
import com.duanxr.pgcon.gui.fxform.annotation.ConfigLabel;
import com.duanxr.pgcon.gui.fxform.annotation.ConfigPercentage;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import lombok.Data;

/**
 * @author 段然 2022/8/1
 */
@Data

public class DebugDetectConfig {
  @ConfigLabel("Detect Type")
  @FormFactory(EnumChoiceBoxFactory.class)
  private SimpleObjectProperty<DetectType> detectType = new SimpleObjectProperty<>(
      DetectType.IMAGE_COMPARE);
  public enum DetectType {
    IMAGE_COMPARE,
    OCR,
    ;
  }

}

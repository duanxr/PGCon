package com.duanxr.pgcon.gui.debug;

import com.dooapp.fxform.annotation.FormFactory;
import com.dooapp.fxform.view.factory.impl.EnumChoiceBoxFactory;
import com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig;
import com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType;
import com.duanxr.pgcon.gui.fxform.annotation.ConfigLabel;
import com.duanxr.pgcon.gui.fxform.annotation.ConfigPercentage;
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
public class DebugThreshConfig {

  @ConfigLabel("Enable Thresh")
  private SimpleBooleanProperty enableThresh = new SimpleBooleanProperty(false);

  @ConfigLabel("Threshold")
  @ConfigPercentage
  private SimpleDoubleProperty binaryThreshold = new SimpleDoubleProperty(0.5);

  @ConfigLabel("Inverse")
  private SimpleBooleanProperty inverse = new SimpleBooleanProperty(false);

  @ConfigLabel("Thresh Type")
  @FormFactory(EnumChoiceBoxFactory.class)
  private SimpleObjectProperty<ThreshType> threshType = new SimpleObjectProperty<>(ThreshType.BINARY);

  @ConfigLabel("Adaptive Thresh C")
  private SimpleIntegerProperty adaptiveThreshC = new SimpleIntegerProperty(2);

  @ConfigLabel("Adaptive Thresh Block Size")
  private SimpleIntegerProperty adaptiveBlockSize = new SimpleIntegerProperty(11);

  public ThreshPreProcessorConfig convertToThreshPreProcessorConfig() {
    return ThreshPreProcessorConfig.builder()
        .enable(enableThresh.get())
        .binaryThreshold(binaryThreshold.get())
        .inverse(inverse.get())
        .threshType(threshType.get())
        .adaptiveThreshC(adaptiveThreshC.get())
        .adaptiveBlockSize(adaptiveBlockSize.get())
        .build();
  }

}

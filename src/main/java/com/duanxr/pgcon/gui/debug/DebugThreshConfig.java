package com.duanxr.pgcon.gui.debug;

import com.dooapp.fxform.annotation.FormFactory;
import com.dooapp.fxform.view.factory.impl.EnumChoiceBoxFactory;
import com.duanxr.pgcon.algo.preprocessing.config.ThreshPreProcessorConfig;
import com.duanxr.pgcon.algo.preprocessing.config.ThreshPreProcessorConfig.ThreshType;
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

public class DebugThreshConfig {

  @ConfigLabel("Enable Thresh")
  private BooleanProperty enableThresh = new SimpleBooleanProperty(false);

  @ConfigLabel("Threshold")
  @ConfigPercentage
  private DoubleProperty binaryThreshold = new SimpleDoubleProperty(0.5);

  @ConfigLabel("Inverse")
  private BooleanProperty inverse = new SimpleBooleanProperty(false);

  @ConfigLabel("Thresh Type")
  @FormFactory(EnumChoiceBoxFactory.class)
  private SimpleObjectProperty<ThreshType> threshType = new SimpleObjectProperty<>(
      ThreshType.BINARY);

  @ConfigLabel("Adaptive Thresh C")
  private IntegerProperty adaptiveThreshC = new SimpleIntegerProperty(2);

  @ConfigLabel("Adaptive Thresh Block Size")
  private IntegerProperty adaptiveBlockSize = new SimpleIntegerProperty(11);

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

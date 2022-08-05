package com.duanxr.pgcon.gui.debug;

import com.duanxr.pgcon.core.preprocessing.config.ChannelsFilterPreProcessorConfig;
import com.duanxr.pgcon.core.preprocessing.config.NormalizePreProcessorConfig;
import com.duanxr.pgcon.gui.fxform.annotation.ConfigLabel;
import com.duanxr.pgcon.gui.fxform.annotation.ConfigPercentage;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import lombok.Data;

/**
 * @author 段然 2022/8/1
 */
@Data
public class DebugNormalizeConfig {

  @ConfigLabel("Enable Normalize Filter")
  private BooleanProperty enableRGBFilter = new SimpleBooleanProperty(false);

  public NormalizePreProcessorConfig convertToNormalizeConfig() {
    return NormalizePreProcessorConfig.builder()
        .enable(enableRGBFilter.get())
        .build();
  }
}

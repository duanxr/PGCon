package com.duanxr.pgcon.gui.debug;

import com.duanxr.pgcon.core.preprocessing.config.NormalizePreProcessorConfig;
import com.duanxr.pgcon.gui.fxform.annotation.ConfigLabel;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import lombok.Data;

/**
 * @author 段然 2022/8/1
 */
@Data
public class DebugNormalizeConfig {

  @ConfigLabel("Enable Normalize Filter")
  private BooleanProperty enableNormalizeFilter = new SimpleBooleanProperty(false);

  public NormalizePreProcessorConfig convertToNormalizeConfig() {
    return NormalizePreProcessorConfig.builder()
        .enable(enableNormalizeFilter.get())
        .build();
  }
}

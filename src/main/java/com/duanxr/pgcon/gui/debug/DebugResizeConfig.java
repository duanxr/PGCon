package com.duanxr.pgcon.gui.debug;

import com.duanxr.pgcon.core.preprocessing.config.ResizePreProcessorConfig;
import com.duanxr.pgcon.gui.fxform.annotation.ConfigLabel;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * @author 段然 2022/8/1
 */
@Data
@Component
public class DebugResizeConfig {

  @ConfigLabel("Enable Resize")
  private SimpleBooleanProperty enableResize = new SimpleBooleanProperty(false);

  @ConfigLabel("Scale")
  private SimpleDoubleProperty scale = new SimpleDoubleProperty(1);


  public ResizePreProcessorConfig convertToResizePreProcessorConfig() {
    return ResizePreProcessorConfig.builder()
        .enable(enableResize.get())
        .scale(Math.min(scale.get(), 10.0))
        .build();
  }

}

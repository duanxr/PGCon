package com.duanxr.pgcon.gui.debug;

import com.duanxr.pgcon.core.preprocessing.config.ChannelsFilterPreProcessorConfig;
import com.duanxr.pgcon.gui.fxform.annotation.ConfigLabel;
import com.duanxr.pgcon.gui.fxform.annotation.ConfigPercentage;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * @author 段然 2022/8/1
 */
@Data
@Component
public class DebugFilterConfig {
  @ConfigLabel("Enable RGB Filter")
  private SimpleBooleanProperty enableRGBFilter = new SimpleBooleanProperty(false);
  @ConfigLabel("Red Weight")
  @ConfigPercentage
  private SimpleDoubleProperty redWeight = new SimpleDoubleProperty(1.0);
  @ConfigLabel("Green Weight")
  @ConfigPercentage
  private SimpleDoubleProperty greenWeight = new SimpleDoubleProperty(1.0);
  @ConfigLabel("Blue Weight")
  @ConfigPercentage
  private SimpleDoubleProperty blueWeight = new SimpleDoubleProperty(1.0);
  public ChannelsFilterPreProcessorConfig convertToPreProcessorConfig() {
    return ChannelsFilterPreProcessorConfig.builder()
        .enable(enableRGBFilter.get())
        .redWeight(redWeight.get())
        .greenWeight(greenWeight.get())
        .blueWeight(blueWeight.get())
        .build();
  }

}

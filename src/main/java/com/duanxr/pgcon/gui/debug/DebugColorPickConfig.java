package com.duanxr.pgcon.gui.debug;

import com.dooapp.fxform.annotation.FormFactory;
import com.dooapp.fxform.view.factory.impl.ColorPickerFactory;
import com.duanxr.pgcon.core.preprocessing.config.ColorPickFilterPreProcessorConfig;
import com.duanxr.pgcon.gui.fxform.annotation.ConfigLabel;
import com.duanxr.pgcon.gui.fxform.annotation.ConfigPercentage;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.paint.Color;
import lombok.Data;

/**
 * @author 段然 2022/8/1
 */
@Data
public class DebugColorPickConfig {

  @ConfigLabel("Enable Color Pick Filter")
  private BooleanProperty enableColorPickFilter = new SimpleBooleanProperty(false);

  @ConfigLabel("Target Color")
  @FormFactory(ColorPickerFactory.class)
  private ObjectProperty<Color> targetColor = new SimpleObjectProperty<>(Color.BLUE);

  @ConfigLabel("Hue Range")
  @ConfigPercentage
  private DoubleProperty hueRange = new SimpleDoubleProperty(0.2);

  @ConfigLabel("Saturation Range")
  @ConfigPercentage
  private DoubleProperty saturationRange = new SimpleDoubleProperty(0.2);

  @ConfigLabel("Value Range")
  @ConfigPercentage
  private DoubleProperty valueRange = new SimpleDoubleProperty(0.2);

  @ConfigLabel("Inverse")
  private BooleanProperty inverse = new SimpleBooleanProperty(false);

  public ColorPickFilterPreProcessorConfig convertToColorPickerFilterPreProcessorConfig() {
    return ColorPickFilterPreProcessorConfig.builder()
        .enable(enableColorPickFilter.get())
        .targetColor(Color.color(targetColor.get().getRed(), targetColor.get().getGreen(), targetColor.get().getBlue()))
        .hueRange(hueRange.get())
        .saturationRange(saturationRange.get())
        .valueRange(valueRange.get())
        .inverse(inverse.get())
        .build();
  }
}

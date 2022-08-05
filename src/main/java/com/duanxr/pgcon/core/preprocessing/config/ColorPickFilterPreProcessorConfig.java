package com.duanxr.pgcon.core.preprocessing.config;

import com.duanxr.pgcon.core.preprocessing.PreProcessorConfig;
import javafx.scene.paint.Color;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author 段然 2022/8/1
 */
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ColorPickFilterPreProcessorConfig extends PreProcessorConfig {

  private Color targetColor = Color.BLUE;

  private double hueRange = 0.2;

  private double saturationRange = 0.2;

  private double valueRange = 0.2;

  private boolean inverse = false;

}

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

  private boolean inverse = false;

  private double range = 0.1;

  private PickType pickType = PickType.CIE76;

  private MaskType maskType = MaskType.BLACK;

  public enum PickType {
    CIE76,
    CIE94,
    CIEDE2000,
    CMC,
    ;
  }

  public enum MaskType {
    BLACK,
    WHITE,
    GARY,
    ;
  }
}

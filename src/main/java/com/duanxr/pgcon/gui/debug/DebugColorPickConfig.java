package com.duanxr.pgcon.gui.debug;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.alibaba.fastjson2.annotation.JSONField;
import com.dooapp.fxform.annotation.FormFactory;
import com.dooapp.fxform.view.factory.impl.ColorPickerFactory;
import com.dooapp.fxform.view.factory.impl.EnumChoiceBoxFactory;
import com.duanxr.pgcon.core.preprocessing.config.ColorPickFilterPreProcessorConfig;
import com.duanxr.pgcon.core.preprocessing.config.ColorPickFilterPreProcessorConfig.MaskType;
import com.duanxr.pgcon.core.preprocessing.config.ColorPickFilterPreProcessorConfig.PickType;
import com.duanxr.pgcon.gui.fxform.annotation.ConfigLabel;
import com.duanxr.pgcon.gui.fxform.annotation.ConfigPercentage;
import java.lang.reflect.Type;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.paint.Color;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author 段然 2022/8/1
 */
@Data
@Component
public class DebugColorPickConfig {
  @ConfigLabel("Enable Color Pick Filter")
  private SimpleBooleanProperty enableColorPickFilter = new SimpleBooleanProperty(false);
  @ConfigLabel("Target Color")
  @FormFactory(ColorPickerFactory.class)
  @JSONField(deserializeUsing = ColorSerializer.class)
  private SimpleObjectProperty<Color> targetColor = new SimpleObjectProperty<>(Color.BLUE);
  @ConfigLabel("Range")
  @ConfigPercentage
  private SimpleDoubleProperty range = new SimpleDoubleProperty(0.1);
  @ConfigLabel("Inverse")
  private SimpleBooleanProperty inverse = new SimpleBooleanProperty(false);

  @ConfigLabel("Pick Type")
  @FormFactory(EnumChoiceBoxFactory.class)
  private SimpleObjectProperty<PickType> pickType = new SimpleObjectProperty<>(PickType.CIE76);

  @ConfigLabel("Mask Type")
  @FormFactory(EnumChoiceBoxFactory.class)
  private SimpleObjectProperty<MaskType> maskType = new SimpleObjectProperty<>(MaskType.BLACK);

  @Slf4j
  public static class ColorSerializer implements ObjectDeserializer {

    @SuppressWarnings("unchecked")
    public <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
      SimpleObjectProperty simpleObjectProperty = parser.parseObject(SimpleObjectProperty.class);
      JSONObject value = (JSONObject) simpleObjectProperty.get();
      simpleObjectProperty.set(Color.BLUE);
      if (value != null) {
        try {
          Color color = Color.color(
              value.getDoubleValue("red"),
              value.getDoubleValue("green"),
              value.getDoubleValue("blue"));
          simpleObjectProperty.set(color);
        } catch (Exception e) {
          log.error("cannot deserialze color", e);
        }
      }
      return (T) simpleObjectProperty;
    }
  }

  public ColorPickFilterPreProcessorConfig convertToColorPickerFilterPreProcessorConfig() {
    return ColorPickFilterPreProcessorConfig.builder()
        .enable(enableColorPickFilter.get())
        .targetColor(Color.color(
            targetColor.get().getRed(),
            targetColor.get().getGreen(),
            targetColor.get().getBlue()))
        .range(range.get())
        .inverse(inverse.get())
        .pickType(pickType.get())
        .maskType(maskType.get())
        .build();
  }
}

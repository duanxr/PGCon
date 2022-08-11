package com.duanxr.pgcon.gui.debug;

import com.dooapp.fxform.annotation.FormFactory;
import com.dooapp.fxform.view.factory.impl.EnumChoiceBoxFactory;
import com.duanxr.pgcon.core.detect.api.ImageCompare.Method;
import com.duanxr.pgcon.gui.fxform.annotation.ConfigLabel;
import javafx.beans.property.SimpleObjectProperty;
import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * @author 段然 2022/8/1
 */
@Data
@Component
public class DebugImageCompareConfig {

  @ConfigLabel("ImageCompare Type")
  @FormFactory(EnumChoiceBoxFactory.class)
  private SimpleObjectProperty<Method> imageCompareType = new SimpleObjectProperty<>(
      Method.TM_CCOEFF);

}

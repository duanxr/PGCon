package com.duanxr.pgcon.gui.debug;

import com.duanxr.pgcon.gui.fxform.annotation.ConfigLabel;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleStringProperty;
import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * @author 段然 2022/8/1
 */
@Data
@Component
public class DebugResultConfig {

  @ConfigLabel("Detect Result")
  private ReadOnlyStringProperty detectResult = new SimpleStringProperty();

}

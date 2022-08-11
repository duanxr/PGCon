package com.duanxr.pgcon.gui.debug;

import com.dooapp.fxform.annotation.FormFactory;
import com.dooapp.fxform.view.factory.impl.EnumChoiceBoxFactory;
import com.dooapp.fxform.view.factory.impl.TextAreaFactory;
import com.duanxr.pgcon.core.detect.api.OCR.Method;
import com.duanxr.pgcon.core.detect.impl.TesseractOCR;
import com.duanxr.pgcon.gui.fxform.annotation.ConfigLabel;
import com.duanxr.pgcon.gui.fxform.factory.ReadOnlyTextFactory;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
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

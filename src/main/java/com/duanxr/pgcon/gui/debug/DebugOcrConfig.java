package com.duanxr.pgcon.gui.debug;

import com.dooapp.fxform.annotation.FormFactory;
import com.dooapp.fxform.view.factory.impl.EnumChoiceBoxFactory;
import com.dooapp.fxform.view.factory.impl.TextAreaFactory;
import com.dooapp.fxform.view.factory.impl.TextFactory;
import com.dooapp.fxform.view.factory.impl.TextFieldFactory;
import com.duanxr.pgcon.core.detect.api.OCR.Method;
import com.duanxr.pgcon.core.detect.impl.TesseractOCR;
import com.duanxr.pgcon.gui.fxform.annotation.ConfigLabel;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * @author 段然 2022/8/1
 */
@Data
@Component
public class DebugOcrConfig {

  @ConfigLabel("OCR Type")
  @FormFactory(EnumChoiceBoxFactory.class)
  private SimpleObjectProperty<Method> ocrType = new SimpleObjectProperty<>(
      Method.CHS);

  @ConfigLabel("White List")
  private SimpleStringProperty whiteList = new SimpleStringProperty();

  @ConfigLabel("Black List")
  private SimpleStringProperty blackList = new SimpleStringProperty();

  @ConfigLabel("PageSeg Mode")
  private SimpleIntegerProperty pageSegMode = new SimpleIntegerProperty(
      TesseractOCR.DEFAULT_PAGE_SEG_MODE);

  @ConfigLabel("Engine Mode")
  private SimpleIntegerProperty engineMode = new SimpleIntegerProperty(
      TesseractOCR.DEFAULT_OCR_ENGINE_MODE);

}

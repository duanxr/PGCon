package com.duanxr.pgcon.gui.debug;

import com.dooapp.fxform.annotation.FormFactory;
import com.dooapp.fxform.view.factory.impl.EnumChoiceBoxFactory;
import com.dooapp.fxform.view.factory.impl.TextAreaFactory;
import com.duanxr.pgcon.core.detect.api.OCR.Method;
import com.duanxr.pgcon.core.detect.impl.TesseractOCR;
import com.duanxr.pgcon.gui.fxform.annotation.ConfigLabel;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import lombok.Data;

/**
 * @author 段然 2022/8/1
 */
@Data

public class DebugOcrConfig {

  @ConfigLabel("OCR Type")
  @FormFactory(EnumChoiceBoxFactory.class)
  private SimpleObjectProperty<Method> ocrType = new SimpleObjectProperty<>(
      Method.CHS);

  @ConfigLabel("White List")
  @FormFactory(TextAreaFactory.class)
  private SimpleObjectProperty<String> whiteList = new SimpleObjectProperty<>();

  @ConfigLabel("Black List")
  @FormFactory(TextAreaFactory.class)
  private SimpleObjectProperty<String> blackList = new SimpleObjectProperty<>();

  @ConfigLabel("PageSeg Mode")
  private IntegerProperty pageSegMode = new SimpleIntegerProperty(
      TesseractOCR.DEFAULT_PAGE_SEG_MODE);

  @ConfigLabel("Engine Mode")
  private IntegerProperty engineMode = new SimpleIntegerProperty(
      TesseractOCR.DEFAULT_OCR_ENGINE_MODE);

}

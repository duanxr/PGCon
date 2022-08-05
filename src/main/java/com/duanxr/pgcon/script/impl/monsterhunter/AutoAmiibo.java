package com.duanxr.pgcon.script.impl.monsterhunter;

import com.duanxr.pgcon.core.detect.api.OCR;
import com.duanxr.pgcon.core.detect.api.OCR.ApiConfig;
import com.duanxr.pgcon.core.detect.api.OCR.Param;
import com.duanxr.pgcon.core.model.Area;
import com.duanxr.pgcon.output.action.ButtonAction;
import com.duanxr.pgcon.script.api.MainScript;
import com.duanxr.pgcon.script.component.ScriptEngine;
import org.springframework.stereotype.Component;

/**
 * @author 2022/7/25
 */
@Component
public class AutoAmiibo extends ScriptEngine implements MainScript {

  private int times = 1;
  private static final OCR.Param AMIIBO_READ = OCR.Param.builder()
      .area(Area.ofPoints(763, 888, 915, 948))
      .apiConfig(ApiConfig.builder()
          .method(OCR.Method.ENG)
          .build())
      .build();
  private static final OCR.Param AMIIBO_SUCCESS = Param.builder()
      .area(Area.ofPoints(960, 529, 1038, 567))
      .apiConfig(ApiConfig.builder()
          .method(OCR.Method.ENG)
          .build())
      .build();
  private static final OCR.Param AMIIBO_USED = OCR.Param.builder()
      .area(Area.ofPoints(190, 991, 106, 1017))
      .apiConfig(ApiConfig.builder()
          .method(OCR.Method.ENG)
          .build())
      .build();

  @Override
  public String getScriptName() {
    return "MHR auto amiibo(CHS.Ver)";
  }

  @Override
  public void execute() {
    info("This is the " + (times++) + " times");
    script("PlusOneDay");
    checkIfInAmiiboReading();
    checkIfAmiiboSuccess();
    lottery();
    checkIfInAmiiboReading();
  }

  private void checkIfAmiiboUsed() {
    if (containAmiibo(ocr(AMIIBO_USED))) {
      press(ButtonAction.B);
      sleep(150);
      press(ButtonAction.D_LEFT);
      sleep(150);
      press(ButtonAction.A);
      sleep(150);
      press(ButtonAction.A);
      sleep(150);
    }
  }

  private void checkIfInAmiiboReading() {
    until(() -> ocr(AMIIBO_READ),
        this::containAmiibo,
        () -> {
          press(ButtonAction.A);
          sleep(150);
          checkIfAmiiboUsed();
        });
  }

  private void checkIfAmiiboSuccess() {
    until(() -> ocr(AMIIBO_SUCCESS),
        this::containAmiibo,
        () -> sleep(150));
  }

  private void lottery() {
    for (int i = 0; i < 20; i++) {
      press(ButtonAction.A);
      sleep(100);
    }
  }

  private boolean containAmiibo(OCR.Result result) {
    return result.getTextWithoutSpace().toLowerCase().contains("amiibo");
  }

  @Override
  public boolean isLoop() {
    return true;
  }
}

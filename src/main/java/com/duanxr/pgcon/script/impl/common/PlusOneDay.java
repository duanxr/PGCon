package com.duanxr.pgcon.script.impl.common;

import com.duanxr.pgcon.core.detect.api.ImageCompare;
import com.duanxr.pgcon.core.detect.api.OCR;
import com.duanxr.pgcon.core.detect.api.OCR.Method;
import com.duanxr.pgcon.core.detect.api.OCR.Result;
import com.duanxr.pgcon.core.detect.model.Area;
import com.duanxr.pgcon.output.action.ButtonAction;
import com.duanxr.pgcon.script.api.MainScript;
import com.duanxr.pgcon.script.ScriptEngine;
import com.google.common.base.Strings;
import java.time.LocalDate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author 段然 2022/7/25
 */
@Slf4j
@Component
public class PlusOneDay extends ScriptEngine implements MainScript {

  private static final ImageCompare.Param mainMenu = ImageCompare.Param.builder()
      .method(ImageCompare.Method.TM_CCOEFF)
      .template("/img/common/mainMenu.png")
      .area(Area.ofPoints(1404, 781, 1489, 852)).build();

  private static final OCR.Param enableTimeSync = OCR.Param.builder()
      .method(OCR.Method.CHS)
      .area(Area.ofPoints(1495, 225, 1581, 271)).build();

  private static final OCR.Param timeYear = OCR.Param.builder()
      .method(OCR.Method.NMU)
      .area(Area.ofPoints(277,663,454,742)).build();

  private static final OCR.Param timeMonth = OCR.Param.builder()
      .method(OCR.Method.NMU)
      .area(Area.ofPoints(571,661,663,736)).build();

  private static final OCR.Param timeDay = OCR.Param.builder()
      .method(OCR.Method.NMU)
      .area(Area.ofPoints(769,661,861,742)).build();


  private static final OCR.Param amiiboSuccess = OCR.Param.builder()
      .method(OCR.Method.ENG)
      .area(Area.ofPoints(960, 529, 1038, 567)).build();

  private static final OCR.Param amiiboUsed = OCR.Param.builder()
      .method(OCR.Method.ENG)
      .area(Area.ofPoints(190,991,106,1017)).build();

  private static final OCR.Param amiiboRead = OCR.Param.builder()
      .method(OCR.Method.ENG)
      .area(Area.ofPoints(763, 888, 915, 948)).build();

  @Override
  public String getScriptName() {
    return "Plus One Day";
  }

  @Override
  public void run() {
    while (true) {
      try {
        toMainMenu();
        toDateMenu();
        checkIfDateIsSyncByInternet();
        toDateSetting();
        plusOneDay();
        backToGame();
        checkIfAmiiboUsed();
        checkIfInAmiiboReading();
        checkIfAmiiboSuccess();
        lottery();
        checkIfInAmiiboReading();
      } catch (Exception e) {
        log.error("", e);
      }
    }

  }

  private void checkIfAmiiboSuccess() {
    until(() -> ocr.detect(amiiboSuccess),
        input -> input.getText().toLowerCase().contains("amiibo"),
        () -> sleep(150));
  }

  private void checkIfInAmiiboReading() {
    until(() -> ocr.detect(amiiboRead),
        input -> input.getText().toLowerCase().contains("amiibo"),
        () -> {
          controller.press(ButtonAction.A);
          sleep(150);
        });
  }

  private void backToGame() {
    controller.press(ButtonAction.HOME);
    sleep(1000);
    controller.press(ButtonAction.HOME);
    sleep(1000);
  }

  private void toDateSetting() {
    controller.press(ButtonAction.D_BOTTOM);
    sleep(150);
    controller.press(ButtonAction.D_BOTTOM);
    sleep(150);
    controller.press(ButtonAction.A);
    sleep(150);
  }

  private void toDateMenu() {
    controller.press(ButtonAction.D_BOTTOM);
    sleep(150);
    controller.press(ButtonAction.D_RIGHT);
    sleep(150);
    controller.press(ButtonAction.D_RIGHT);
    sleep(150);
    controller.press(ButtonAction.D_RIGHT);
    sleep(150);
    controller.press(ButtonAction.D_RIGHT);
    sleep(150);
    controller.press(ButtonAction.D_RIGHT);
    sleep(150);
    controller.press(ButtonAction.A);
    sleep(1000);
    controller.hold(ButtonAction.D_BOTTOM);
    sleep(1300);
    controller.release(ButtonAction.D_BOTTOM);
    sleep(30);
    controller.press(ButtonAction.D_RIGHT);
    sleep(200);
    controller.hold(ButtonAction.D_BOTTOM);
    sleep(700);
    controller.release(ButtonAction.D_BOTTOM);
    sleep(100);
    controller.press(ButtonAction.A);
    sleep(500);
  }

  private void lottery() {
    for (int i = 0; i < 12; i++) {
      controller.press(ButtonAction.A);
      sleep(100);
    }
  }

  private void checkIfAmiiboUsed() {
    Result detect = ocr.detect(amiiboUsed);
    String text = detect.getText();
    if (text.toLowerCase().contains("amiibo")) {
      controller.press(ButtonAction.B);
      sleep(150);
      controller.press(ButtonAction.D_LEFT);
      sleep(150);
      controller.press(ButtonAction.A);
      sleep(150);
      controller.press(ButtonAction.A);
      sleep(150);
    }
  }


  private void toMainMenu() {
    until(() -> imageCompare.detect(mainMenu),
        input -> input.getSimilarity() > 0.8,
        () -> {
          controller.press(ButtonAction.HOME);
          sleep(1000);
        });
  }

  private void plusOneDay() {
    Long year = ocrNumber(timeYear, 4);
    Long month = ocrNumber(timeMonth, 2);
    Long day = ocrNumber(timeDay, 2);
    LocalDate  currentDay = LocalDate.of(year.intValue(), month.intValue(), day.intValue());
    LocalDate nextDay = currentDay.plusDays(1);
    if (currentDay.getYear() != nextDay.getYear()) {
      controller.press(ButtonAction.D_TOP);
      sleep(300);
    }
    controller.press(ButtonAction.A);
    sleep(150);
    if (currentDay.getMonthValue() != nextDay.getMonthValue()) {
      controller.press(ButtonAction.D_TOP);
      sleep(300);
    }
    controller.press(ButtonAction.A);
    sleep(150);
    if (currentDay.getDayOfMonth() != nextDay.getDayOfMonth()) {
      until(() -> ocrNumber(timeDay, 2),
          input -> input == nextDay.getDayOfMonth(),
          () -> {
            controller.press(ButtonAction.D_TOP);
            sleep(500);
          });
    }
    controller.press(ButtonAction.A);
    sleep(150);
    controller.press(ButtonAction.A);
    sleep(150);
    controller.press(ButtonAction.A);
    sleep(150);
    controller.press(ButtonAction.A);
    sleep(150);
    log.info("new date is: {}", nextDay);
  }

  private int toInt(String str) {
    try {
      return Integer.parseInt(str);
    } catch (Exception e) {
      log.error("", e);
      return -1;
    }
  }

  private void checkIfDateIsSyncByInternet() {
    until(() -> ocr.detect(enableTimeSync),
        input -> !"开启".equals(input.getText()),
        () -> {
          controller.press(ButtonAction.A);
          sleep(500);
        });
  }

}

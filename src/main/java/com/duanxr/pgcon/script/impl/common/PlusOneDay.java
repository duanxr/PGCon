package com.duanxr.pgcon.script.impl.common;

import com.duanxr.pgcon.core.detect.api.ImageCompare;
import com.duanxr.pgcon.core.detect.api.OCR;
import com.duanxr.pgcon.core.detect.api.OCR.Param;
import com.duanxr.pgcon.core.detect.api.OCR.Result;
import com.duanxr.pgcon.core.detect.model.Area;
import com.duanxr.pgcon.output.action.ButtonAction;
import com.duanxr.pgcon.output.action.StickAction;
import com.duanxr.pgcon.script.api.MainScript;
import com.duanxr.pgcon.script.ScriptEngine;
import java.time.LocalDate;
import java.util.Date;
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
      .area(Area.ofPoints(268, 667, 457, 747)).build();

  private static final OCR.Param timeMonth = OCR.Param.builder()
      .method(OCR.Method.NMU)
      .area(Area.ofPoints(564, 663, 667, 742)).build();

  private static final OCR.Param timeDay = OCR.Param.builder()
      .method(OCR.Method.NMU)
      .area(Area.ofPoints(757, 666, 868, 744)).build();

  @Override
  public String getScriptName() {
    return "Plus One Day";
  }

  @Override
  public void run() {
    toMainMenu();
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
    sleep(300);
    controller.hold(ButtonAction.D_BOTTOM);
    sleep(700);
    controller.release(ButtonAction.D_BOTTOM);
    sleep(100);
    controller.press(ButtonAction.A);
    sleep(1000);
    checkIfDateIsSyncByInternet();
    controller.press(ButtonAction.D_BOTTOM);
    sleep(150);
    controller.press(ButtonAction.D_BOTTOM);
    sleep(150);
    controller.press(ButtonAction.A);
    sleep(1000);
    plusOneDay();
    controller.press(ButtonAction.HOME);
    sleep(1000);
    controller.press(ButtonAction.HOME);
    sleep(1000);
  }

  private void toMainMenu() {
    ImageCompare.Result detect = null;
    while ((detect = imageCompare.detect(mainMenu)).getSimilarity() < 0.8) {
      controller.press(ButtonAction.HOME);
      sleep(1000);
    }
  }

  private void plusOneDay() {
    OCR.Result year = ocr.detect(timeYear);
    OCR.Result month = ocr.detect(timeMonth);
    OCR.Result day = ocr.detect(timeDay);
    String dateStr = year.getText() + "-" + month.getText() + "-" + day.getText();
    log.info("current date: {}", dateStr);
    LocalDate parse = LocalDate.parse(dateStr);
    LocalDate plusOneDay = parse.plusDays(1);
    if (plusOneDay.getYear() != parse.getYear()) {
      controller.press(ButtonAction.D_TOP);
      sleep(150);
    }
    controller.press(ButtonAction.A);
    sleep(150);
    if (plusOneDay.getMonthValue() != parse.getMonthValue()) {
      while (plusOneDay.getMonthValue() != Integer.parseInt((month = ocr.detect(timeMonth)).getText())) {
        controller.press(ButtonAction.D_TOP);
        sleep(150);
      }
    }
    controller.press(ButtonAction.A);
    sleep(150);
    if (plusOneDay.getDayOfMonth() != parse.getDayOfMonth()) {
      while (plusOneDay.getDayOfMonth() != Integer.parseInt((day = ocr.detect(timeDay)).getText())) {
        controller.press(ButtonAction.D_TOP);
        sleep(150);
      }
    }
    controller.press(ButtonAction.A);
    sleep(150);
    controller.press(ButtonAction.A);
    sleep(150);
    controller.press(ButtonAction.A);
    sleep(150);
    controller.press(ButtonAction.A);
    sleep(150);
  }

  private void checkIfDateIsSyncByInternet() {
    OCR.Result detect = null;
    while ("开启".equals((detect = ocr.detect(enableTimeSync)).getText())) {
      controller.press(ButtonAction.A);
      sleep(300);
    }
  }

}

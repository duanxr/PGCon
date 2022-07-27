package com.duanxr.pgcon.script.impl.common;

import com.duanxr.pgcon.core.detect.api.ImageCompare;
import com.duanxr.pgcon.core.detect.api.OCR;
import com.duanxr.pgcon.core.detect.api.OCR.ApiConfig;
import com.duanxr.pgcon.core.detect.api.OCR.Method;
import com.duanxr.pgcon.core.detect.api.OCR.Param;
import com.duanxr.pgcon.core.detect.model.Area;
import com.duanxr.pgcon.output.action.ButtonAction;
import com.duanxr.pgcon.script.api.Script;
import com.duanxr.pgcon.script.component.ScriptEngine;
import java.time.LocalDate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author 段然 2022/7/25
 */
@Slf4j
@Component
public class PlusOneDay extends ScriptEngine implements Script {

  private static final OCR.Param ENABLE_TIME_SYNC = Param.builder()
      .area(Area.ofPoints(1495, 225, 1581, 271))
      .apiConfig(ApiConfig.builder()
          .method(Method.CHS)
          .build())
      .build();
  private static final ImageCompare.Param MAIN_MENU = ImageCompare.Param.builder()
      .method(ImageCompare.Method.TM_CCOEFF)
      .template("/img/common/mainMenu.png")
      .area(Area.ofPoints(1404, 781, 1489, 852)).build();
  private static final String NUM_WHITELIST = "0123456789";
  private static final OCR.Param TIME_DAY = OCR.Param.builder()
      .area(Area.ofPoints(769, 661, 861, 742))
      .apiConfig(ApiConfig.builder()
          .method(Method.NMU)
          .whitelist(NUM_WHITELIST)
          .build())
      .build();
  private static final OCR.Param TIME_MONTH = OCR.Param.builder()
      .area(Area.ofPoints(571, 661, 663, 736))
      .apiConfig(ApiConfig.builder()
          .method(Method.NMU)
          .whitelist(NUM_WHITELIST)
          .build())
      .build();
  private static final OCR.Param TIME_YEAR = OCR.Param.builder()
      .area(Area.ofPoints(277, 663, 454, 742))
      .apiConfig(ApiConfig.builder()
          .method(Method.NMU)
          .whitelist(NUM_WHITELIST)
          .build())
      .build();

  @Override
  public String getScriptName() {
    return "PlusOneDay";
  }

  @Override
  public void execute() {
    toMainMenu();
    toDateMenu();
    checkIfDateIsSyncByInternet();
    toDateSetting();
    plusOneDay();
    backToGame();
  }

  private void toMainMenu() {
    until(() -> imageCompare.detect(MAIN_MENU),
        input -> input.getSimilarity() > 0.8,
        () -> {
          controller.press(ButtonAction.HOME);
          sleep(1000);
        });
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

  private void checkIfDateIsSyncByInternet() {
    until(() -> ocr.detect(ENABLE_TIME_SYNC),
        input -> !"开启".equals(input.getTextWithoutSpace()),
        () -> {
          controller.press(ButtonAction.A);
          sleep(500);
        });
  }

  private void toDateSetting() {
    controller.press(ButtonAction.D_BOTTOM);
    sleep(150);
    controller.press(ButtonAction.D_BOTTOM);
    sleep(150);
    controller.press(ButtonAction.A);
    sleep(150);
  }

  private void plusOneDay() {
    Long year = ocrNumber(TIME_YEAR, 4);
    Long month = ocrNumber(TIME_MONTH, 2);
    Long day = ocrNumber(TIME_DAY, 2);
    LocalDate currentDay = LocalDate.of(year.intValue(), month.intValue(), day.intValue());
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
      until(() -> ocrNumber(TIME_DAY, 2),
          input -> input == nextDay.getDayOfMonth(),
          () -> {
            controller.press(ButtonAction.D_TOP);
            sleep(800);
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

  private void backToGame() {
    controller.press(ButtonAction.HOME);
    sleep(1000);
    controller.press(ButtonAction.HOME);
    sleep(1000);
  }

}

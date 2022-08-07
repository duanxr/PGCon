package com.duanxr.pgcon.script.impl.common;

import com.duanxr.pgcon.core.detect.api.ImageCompare;
import com.duanxr.pgcon.core.detect.api.OCR;
import com.duanxr.pgcon.core.detect.api.OCR.ApiConfig;
import com.duanxr.pgcon.core.detect.api.OCR.Param;
import com.duanxr.pgcon.core.model.Area;
import com.duanxr.pgcon.script.api.Script;
import com.duanxr.pgcon.script.component.ScriptEngine;
import java.time.LocalDate;
import org.springframework.stereotype.Component;

/**
 * @author 段然 2022/7/25
 */
@Component
public class PlusOneDay extends ScriptEngine implements Script {

  private static final OCR.Param ENABLE_TIME_SYNC = Param.builder()
      .area(Area.ofRect(1494, 226, 80, 50))
      .preProcessor(com.duanxr.pgcon.core.preprocessing.config.NormalizePreProcessorConfig.builder()
          .enable(true).build()).preProcessor(
          com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder().enable(true)
              .binaryThreshold(0.5).inverse(false).threshType(
                  com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.OTSU)
              .adaptiveThreshC(2).adaptiveBlockSize(11).build()).apiConfig(
          ApiConfig.builder().method(OCR.Method.CHS)
              .build()).
      build();

  private static final OCR.Param DATE_SETTING_MENU = OCR.Param.builder()
      .area(Area.ofRect(92, 42, 226, 66))
      .preProcessor(com.duanxr.pgcon.core.preprocessing.config.NormalizePreProcessorConfig.builder()
          .enable(true).build()).preProcessor(
          com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder().enable(true)
              .binaryThreshold(0.5).inverse(false).threshType(
                  com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.OTSU)
              .adaptiveThreshC(2).adaptiveBlockSize(11).build()).apiConfig(
          ApiConfig.builder().method(OCR.Method.CHS)
              .build()).
      build();

  private static final OCR.Param DATE_CHANGE_MENU = OCR.Param.builder()
      .area(Area.ofRect(106, 408, 286, 44))
      .preProcessor(com.duanxr.pgcon.core.preprocessing.config.NormalizePreProcessorConfig.builder()
          .enable(true).build()).preProcessor(
          com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder().enable(true)
              .binaryThreshold(0.5).inverse(false).threshType(
                  com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.OTSU)
              .adaptiveThreshC(2).adaptiveBlockSize(11).build()).apiConfig(
          ApiConfig.builder().method(OCR.Method.CHS).build()).build();

  private static final ImageCompare.Param SETTING_MENU = ImageCompare.Param.builder()
      .area(Area.ofRect(90, 26, 86, 94)).method(ImageCompare.Method.TM_CCOEFF).preProcessor(
          com.duanxr.pgcon.core.preprocessing.config.NormalizePreProcessorConfig.builder()
              .enable(true).build()).preProcessor(
          com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder().enable(true)
              .binaryThreshold(0.5).inverse(false).threshType(
                  com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.OTSU)
              .adaptiveThreshC(2).adaptiveBlockSize(11).build()).template(
          "{\"data\":\"H/8BAP//////exEAAQAPVQA9D1YA/0UOrgEPrQCHD1UAMC8AAFYAFg5qAA+YAAkPVgAEDBgADsEAD5gBGS4AAFcADxgBGg0/AAZSAAxaAAUaAA9WABcfAFQABQUcAB8AVgAhHwBUAAYNHAAPAwERDwEAHQ+uARIf/1YAQx//VQBCDlcAD3IACS///1cAIw4dAA9VACMPNgIMD1UAJQ7hAg8fAAQPVQA5DhgED1UAHA9XADgOMwAPcgUGD1UAOw5XAA9WAGoPJwAUD1YAag4uAA8jBwsPVgBoDooCDyoAKA6MBA/gBR8PIgAYD4oACA9WAO4NNAAfAFYAQg8jABAPVgAyDSwAD1wDkQ6uCw+0BIgOLwAPugYKDwwGaw4OBw+rADAPugdFDzQADx8AZwhBD1UAIg/0Cw0fAFYALh8AFAo3DjUEDGQNDxULLQc+AA/BCzofAMMMDQ8aDWYILwAPSwAJHwAcDiYPVwAGDnIOD5EIGx8AHg85D2oBBg+3BAAPCgYmDpYAD+QRHR8AIhFCDhgBD+wACC8A/1YAQR//VgD//y8OvBMPAQD///8MUP//////\",\"length\":8084,\"rows\":94,\"type\":0,\"cols\":86}")
      .build();
  private static final ImageCompare.Param MAIN_MENU = ImageCompare.Param.builder()
      .area(Area.ofRect(1244, 778, 84, 78)).method(ImageCompare.Method.TM_CCOEFF).preProcessor(
          com.duanxr.pgcon.core.preprocessing.config.NormalizePreProcessorConfig.builder()
              .enable(true).build()).preProcessor(
          com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder().enable(true)
              .binaryThreshold(0.5).inverse(false).threshType(
                  com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.OTSU)
              .adaptiveThreshC(2).adaptiveBlockSize(11).build()).template(
          "{\"data\":\"H/8BAP///wgQAAEAD1MAPQ9VAEAPVACbD1AB/yUvAAD8ABQvAADgAigPVAADDrsAD5ABFRoAAQAPEAEiCj0AC1IAB1YAHwBUACQOUgAFVgAKewAPVgAPDwEAGB//VAAUD1MAQC///zsABg9TACcv//86AAcPUwAmCDsADhsAD1MAJB//OAAKD1QAKw+nBDkOigAP/AQCDzAFOw5VAA+DBRUPVAA/DyMAEA+nADkOVQAPVABnDyUAEg+nADoOVQAP0AYQDycAMg5zBA8wABoPVAD/Uyn/ACgADwwGKB//oAIsD/QCaA9EBJwPQAURHwDoBWsf/1QARw4zAA6NBw/gB2sO4QcPiAg3DzkAAi8AAPsAKQ46AA8bAAcPdAQeD4AKMA8oCxkP0AtnBy8AD0oABy8AAMwMOx//IA0lDwkBBg75Ag9wDiEOPgEPeBEfDpUCD8QOLQ+8AAgPYBKUD6gAPA9UAO4PpQFDDwEA//8KUP//////\",\"length\":6552,\"rows\":78,\"type\":0,\"cols\":84}")
      .build();
  private static final String NUM_WHITELIST = "0123456789";
  private static final OCR.Param TIME_DAY = OCR.Param.builder()
      .area(Area.ofRect(750, 652, 120, 106))
      .preProcessor(com.duanxr.pgcon.core.preprocessing.config.NormalizePreProcessorConfig.builder()
          .enable(true).build()).preProcessor(
          com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder().enable(true)
              .binaryThreshold(0.5).inverse(false).threshType(
                  com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.OTSU)
              .adaptiveThreshC(2).adaptiveBlockSize(11).build()).apiConfig(
          ApiConfig.builder().method(OCR.Method.NMU).whitelist(NUM_WHITELIST)
              .build()).build();
  private static final OCR.Param TIME_MONTH = OCR.Param.builder()
      .area(Area.ofRect(566, 654, 110, 96))
      .preProcessor(com.duanxr.pgcon.core.preprocessing.config.NormalizePreProcessorConfig.builder()
          .enable(true).build()).preProcessor(
          com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder().enable(true)
              .binaryThreshold(0.5).inverse(false).threshType(
                  com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.OTSU)
              .adaptiveThreshC(2).adaptiveBlockSize(11).build())
      .apiConfig(ApiConfig.builder().method(OCR.Method.NMU).whitelist(NUM_WHITELIST)
          .build()).build();
  private static final OCR.Param TIME_YEAR = OCR.Param.builder()
      .area(Area.ofRect(274, 656, 176, 84))
      .preProcessor(com.duanxr.pgcon.core.preprocessing.config.NormalizePreProcessorConfig.builder()
          .enable(true).build()).preProcessor(
          com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder().enable(true)
              .binaryThreshold(0.5).inverse(false).threshType(
                  com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.OTSU)
              .adaptiveThreshC(2).adaptiveBlockSize(11).build()).apiConfig(
          ApiConfig.builder().method(OCR.Method.NMU).whitelist(NUM_WHITELIST)
              .build()).build();

  @Override
  public String getScriptName() {
    return "PlusOneDay";
  }

  @Override
  public void execute() {
    initLocation();
    toMainMenu();
    toDateMenu();
    checkIfDateIsSyncByInternet();
    toDateSetting();
    plusOneDay();
    backToGame();
  }

  private void initLocation() {
    press(HOME);
    sleep(1000);
  }

  private void toMainMenu() {
    until(() -> imageCompare(MAIN_MENU),
        input -> input.getSimilarity() > 0.8,
        () -> {
          press(HOME);
          sleep(1000);
        });
  }

  private void toDateMenu() {
    press(D_BOTTOM);
    sleep(150);
    press(D_RIGHT);
    sleep(150);
    press(D_RIGHT);
    sleep(150);
    press(D_RIGHT);
    sleep(150);
    press(D_RIGHT);
    sleep(150);
    press(D_RIGHT);
    sleep(150);
    press(A);
    sleep(700);
    hold(D_BOTTOM);
    sleep(1500);
    release(D_BOTTOM);
    sleep(150);
    press(D_RIGHT);
    sleep(150);
    hold(D_BOTTOM);
    sleep(700);
    release(D_BOTTOM);
    sleep(150);
    press(A);
    sleep(500);
  }

  private void checkIfDateIsSyncByInternet() {
    until(() -> ocr(ENABLE_TIME_SYNC),
        input -> !"开启".equals(input.getTextWithoutSpace()),
        () -> {
          press(A);
          sleep(500);
        });
  }

  private void toDateSetting() {
    press(D_BOTTOM);
    sleep(150);
    press(D_BOTTOM);
    sleep(250);
    press(A);
    sleep(150);
  }

  private void plusOneDay() {
    Long year = ocrNumber(TIME_YEAR, 4);
    Long month = ocrNumber(TIME_MONTH, 2);
    Long day = ocrNumber(TIME_DAY, 2);
    LocalDate currentDay = LocalDate.of(year.intValue(), month.intValue(), day.intValue());
    LocalDate nextDay = currentDay.plusDays(1);
    if (currentDay.getYear() != nextDay.getYear()) {
      press(D_TOP);
      sleep(300);
    }
    press(A);
    sleep(150);
    if (currentDay.getMonthValue() != nextDay.getMonthValue()) {
      press(D_TOP);
      sleep(300);
    }
    press(A);
    sleep(150);
    if (currentDay.getDayOfMonth() != nextDay.getDayOfMonth()) {
      until(() -> ocrNumber(TIME_DAY, 2),
          input -> input == nextDay.getDayOfMonth(),
          () -> {
            press(D_TOP);
            sleep(300);
          });
    }
    press(A);
    sleep(150);
    press(A);
    sleep(150);
    press(A);
    sleep(150);
    press(A);
    sleep(150);
    info("set date to {}", nextDay);
  }

  private void backToGame() {
    press(HOME);
    sleep(1000);
    press(HOME);
    sleep(1000);
  }

}

package com.duanxr.rhm.script.pokemon.swsh;

import static com.duanxr.rhm.core.handler.action.ButtonAction.A;
import static com.duanxr.rhm.core.handler.action.ButtonAction.B;
import static com.duanxr.rhm.core.handler.action.ButtonAction.HOME;
import static com.duanxr.rhm.core.handler.action.StickSimpleAction.L_BOTTOM;
import static com.duanxr.rhm.core.handler.action.StickSimpleAction.L_LEFT;
import static com.duanxr.rhm.core.handler.action.StickSimpleAction.L_RIGHT;
import static com.duanxr.rhm.core.handler.action.StickSimpleAction.L_TOP;
import static com.duanxr.rhm.script.pokemon.swsh.define.SwshDefineImageOcr.MENU_DATE_CHANGE_DAY;
import static com.duanxr.rhm.script.pokemon.swsh.define.SwshDefineImageOcr.MENU_DATE_CHANGE_MONTH;
import static com.duanxr.rhm.script.pokemon.swsh.define.SwshDefineImageOcr.MENU_DATE_CHANGE_YEAR;
import static com.duanxr.rhm.script.pokemon.swsh.define.SwshDefineImageOcrCondition.MENU_DATE_CHANGE_ENABLE;
import static com.duanxr.rhm.script.pokemon.swsh.define.SwshDefineImageOcrCondition.MENU_DATE_CHANGING;
import static com.duanxr.rhm.script.pokemon.swsh.define.SwshDefineImageOcrCondition.ROTOM_MENU;
import static com.duanxr.rhm.script.pokemon.swsh.define.SwshDefineImageOcrCondition.ROTOM_START;
import static com.duanxr.rhm.script.pokemon.swsh.define.SwshDefineImageTemplateCondition.MAIN_PAGE;
import static com.duanxr.rhm.script.pokemon.swsh.define.SwshDefineImageTemplateCondition.MENU_DATE;
import static com.duanxr.rhm.script.pokemon.swsh.define.SwshDefineImageTemplateCondition.MENU_SETTING;
import static com.duanxr.rhm.script.pokemon.swsh.define.SwshDefineImageTemplateCondition.ROTOM_DONE;
import static com.duanxr.rhm.script.pokemon.swsh.define.SwshDefineImageTemplateCondition.ROTOM_LOTTERY_SELECTED;

import com.duanxr.rhm.core.execute.ScriptExecutor;
import com.duanxr.rhm.script.ExecutableScript;
import com.duanxr.rhm.script.ExecutableSubscript;
import java.time.LocalDate;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Duanran 2019/12/19
 */
@Slf4j
public class RotomLootExecutableScript extends ExecutableScript {

  public RotomLootExecutableScript() {
    subscriptList.add(new RotomLootSubscript());
  }

  public class RotomLootSubscript extends ExecutableSubscript {

    @Override
    protected void execute() {
      changeDate();
      fuckRotom();
    }

    public void intoMainPage() {
      waitImage(MAIN_PAGE, () -> {
        press(HOME);
        sleep(800);
      });
    }

    public void fuckRotom() {
      sleep(800);
      press(B);
      sleep(800);
      press(A);
      waitText(ROTOM_START);
      press(A);
      waitText(ROTOM_MENU);
      press(L_BOTTOM);
      waitImage(ROTOM_LOTTERY_SELECTED);
      waitImage(ROTOM_DONE, () -> {
        press(A);
        sleep(300);
      });
      waitNotImage(ROTOM_DONE, () -> {
        press(B);
        sleep(300);
      });
    }

    public void changeDate() {
      intoMainPage();
      press(L_BOTTOM);
      press(L_BOTTOM);
      press(L_RIGHT, 700);
      press(L_LEFT);
      press(A);
      waitImage(MENU_SETTING);
      press(L_BOTTOM, 1800);
      press(L_RIGHT);
      press(L_BOTTOM);
      press(L_BOTTOM);
      press(L_BOTTOM);
      press(L_BOTTOM);
      press(L_BOTTOM);
      press(A);
      waitImage(MENU_DATE);
      if (isTextExits(MENU_DATE_CHANGE_ENABLE)) {
        press(A);
      }
      press(L_BOTTOM);
      press(L_BOTTOM);
      press(A);
      waitText(MENU_DATE_CHANGING);
      LocalDate localDateT = getDate().plusDays(1);
      sleep(200);
      while (getDate().getYear() != localDateT.getYear()) {
        press(L_TOP);
        sleep(500);
      }
      press(A);
      while (getDate().getMonthValue() != localDateT.getMonthValue()) {
        press(L_TOP);
        sleep(500);
      }
      press(A);
      do {
        press(L_TOP);
        sleep(500);
      } while (getDate().getDayOfMonth() != localDateT.getDayOfMonth());
      press(A);
      press(A);
      press(A);
      press(A);
      press(HOME);
      sleep(500);
      press(HOME);
      sleep(500);
    }


    private LocalDate getDate() {
      while (true) {
        try {
          Map<String, String> ocr = getOcr(MENU_DATE_CHANGE_DAY, MENU_DATE_CHANGE_MONTH,
              MENU_DATE_CHANGE_YEAR);
          String year = ocr.get(MENU_DATE_CHANGE_YEAR.getName());
          String month = ocr.get(MENU_DATE_CHANGE_MONTH.getName());
          String day = ocr.get(MENU_DATE_CHANGE_DAY.getName());
          if (year.length() == 4 &&
              month.length() == 2 &&
              day.length() == 2) {
            return LocalDate.parse(year + "-" + month + "-" + day);
          }
        } catch (Exception e) {
          log.info("", e);
        }
      }
    }
  }

}

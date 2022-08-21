import com.duanxr.pgcon.core.detect.api.ImageCompare;
import com.duanxr.pgcon.core.detect.api.OCR;
import com.duanxr.pgcon.core.detect.api.OCR.ApiConfig;
import com.duanxr.pgcon.core.model.Area;
import com.duanxr.pgcon.exception.ResetScriptException;
import com.duanxr.pgcon.script.api.ScriptInfo;
import com.duanxr.pgcon.script.engine.PGConScriptEngineV1;
import java.time.LocalDate;
import java.util.concurrent.Future;
import lombok.SneakyThrows;

/**
 * @author 段然 2022/7/25
 */
public class PlusOneDayENG extends PGConScriptEngineV1<Object> {

  private static final ImageCompare.Param DATE_CHANGE_MENU = ImageCompare.Param.builder()
      .area(Area.ofRect(94, 40, 330, 72))
      .method(ImageCompare.Method.TM_CCOEFF)
      .preProcessor(com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder()
          .enable(true)
          .inverse(false)
          .threshType(
              com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.OTSU)
          .build())
      .template(
          "{\"R\":72,\"C\":330,\"T\":0,\"D\":\"H/8BAP///////////////////////////////j8AAACRAH0e/5IAD0kBky8AABcACQYJAAZ1AQ5+AQ+SAD0LAQAv//9KAaUfAAEABg40AA9KAVEPbwAXLwAASgGVHwBKAWQPcQAUDsECD0oBjg+SAEsPiwACD0oBog3JAA6mBg+SAFgOSwEPSgGhD8sDBQ+UAlYOhgAPSgGEH/9KAZMOhwAPlAL/Jg8kARQDBgQfAEoB/w8fABQHAg4TAArsBw6zBg8zCAwJFgAPqgUKDtIID0oBGwm/AB7/mAYPeAAOD0oBIQmJAAo1AQ4VAA8WAAUPewkVCo0ADkEAD34BAg9KASsLagAP3gECD9MAAQ9KAToPYAEDDyMBAg9KASEvAADSABEvAABKASofALgLBQ4oAw7TAA9KATcOjgANEwAPzwkDL///UQAKCc4KDmUBD0oBOQ6qAA5ZAA/dAQQPSgEeCncADqEADtcLDxwAAC8AAMgRDw6zAQ4NAA4bAA+UAjQLVgEOxgAP0wAAD0oBKQ4YAA9KARAPlQohDz0BAA8CBwMPSgE0DpkBDuIADtMAD0oBIR8A9wMQDkwAD5UKJw4wAQ8aAAQf/0oBMh//MAEOD3cAAg9KAU8NMQAOuQsPGgoMD5oBAA/UDgUPSgEwDiwADcIBDtMAD0oBsR//SgG5DMEGDkUEDxsPGwyjBA9KAVYf/0oBEx8ASgFADgsID0oBAA9RCAwPlQEBD0oBdw4oAA/TAAYPSgEpHwBKATwvAABKAdwOJAEPSgEzHwBKAS8f/ygFKw9KAVgL4AAGDwAPcgYGD0kBIA5qAA9KAZEf/0oBJQ52AA9yBhUP5AAJHwC8B30PSgEGD1YTNw5nAA8GCQcPcwAYD0oB/w0PHg4SD0oBlx//SgEvHwBKAYAfAEoBoS8AAA8MCA/gCgsPSgEeDrwHD3gPEg9KAVcfAEoBKA0uAQ9KARgfAHIAHA8GCQkPDBIID0oBVA5LAQ9KARgN7QcOEAkPZgESARAAH/9hCRoPXBIJDngAD0oBEB//mgssDmkIDlYHDiULDzYJBi8AAJoLCg0KBA5GAA5zAA+aCx0OqAAP5AxSDtMAD0oBHw8jAAwf/0sBBA8sDiQPSgEQDtEBD94DUi///0oBLA8kAAgPsgEED+oQBA9KATcPhgABD0oBUR//SgEnDyYACQQNAA/8BQkOMwIPUQAWDw0DAA82AAIOCAwPSwcWH/9qAAUODgAP0wACDwEA////////////////////////////////ilD//////w==\",\"L\":23760}")
      .build();
  private static final ImageCompare.Param DATE_SETTING_MENU = ImageCompare.Param.builder()
      .area(Area.ofRect(90, 400, 296, 58))
      .method(ImageCompare.Method.TM_CCOEFF)
      .preProcessor(com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder()
          .enable(true)
          .inverse(false)
          .threshType(
              com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.OTSU)
          .build())
      .template(
          "{\"R\":58,\"C\":296,\"T\":0,\"D\":\"H/8BAP///////////////////7YvAP8BAP8TTwAAAAAuABkPiABJBwEAL///KAGPHwABAAIOLQAPJwE9DisBDygBih8AKAF0D2sACw6PAA8oAV0PUAJnDYEADygBex//5AERAxMAD68BVg4pAQ9QAn4OiAAP1wJnD7wADw+hAFgPKAGDDCkBDGICDsoED14BEQ/TAwEfADoABglzBB//5AUZDlsADqEADj4DDygBIggVAA4EBA8TAAsPSwQQCBUADxQFAwgtAA4oAQ81BAwPWwAADqgBDrcADygBKx8AKAESDiwHD6QJEQ9mBwYJFQAPeAMaD7gABA6oAQ63AA8oASEJmAEJjwEPNQACCo0AD1YHBy8A/ykBAw92BwIOAwIPUAIcDSIBL/8AaAAADnwDDygBIgcJDA8oAQYMawIP3QcLDjIED0QACA8oASwIMgEfAH4ADQ8oASsf/44DBQ8nAQEv//+aARoMEgEPXQAADygBKg5sAQihAAiZCQ8oAVUf/3IAIg7VBQ5BAA6lCA8oARYOJgAPIgAFL///CwklHwAvCAUPKAE8H/8oAWkf/ygBQh8AKAEHH/9oDBMLRgAPKAESH/8oASYf/ygBGR//KAEnC9sADAgIDq4BDnEAD0MJDA6JAg5YCg5oCg8oATMPjwEBDygBKi8AACgBDwxUAQ9CCQIPKAF/Dk4MDygBJgvgAA9ACQAPmBESHwBuAQ0JcgAPKAFWDrcAD/AGJA7YAA8oASIOZQEPKAHBDrMADigBDycBCw6aBw8oAYEf/+ANJgsdAA4oAQ7JAA9kAAYPKAGADrcADygBMh8AKAEoH/8oARkO2QgPGAhPDygBIQ7KAA+QCw0PKAFLD0AJRR8AKAErD5IGBB8AKAEzD/AGDQ3VAw8oAUQf/ygBLA4ACA+zBgQP9AsiLwAAKAESDnYADygBQi8AAHYeFh8AvAYCCx4ALwD/QQkDDZIPD2YKCQ1gAA/WAwkKMAAPKAFCDGgADygBEA8iBwYvAABpCgcPDwAAD44LFA+yDw0OsQcPKAFCDrcADygBGw8fAAQOywAP2gAEL///kAskD8EAAC///1ACHR//KAECD7oODy///ykBGh//TAsJDxINBQ8jAxQM7AEITwAJwAAJXwAvAAC9AAkPEQAFCpwAD2gABw4mAQ8BAP////////////////////8tUP//////\",\"L\":17168}")
      .build();
  private static final ImageCompare.Param ENABLE_TIME_SYNC = ImageCompare.Param.builder()
      .area(Area.ofRect(1514, 222, 68, 56))
      .method(ImageCompare.Method.TM_CCORR)
      .preProcessor(com.duanxr.pgcon.core.preprocessing.config.NormalizePreProcessorConfig.builder()
          .enable(true)
          .build())
      .preProcessor(com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder()
          .enable(true)
          .inverse(false)
          .threshType(
              com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.OTSU)
          .build())
      .template(
          "{\"R\":56,\"C\":68,\"T\":0,\"D\":\"H/8BAP////9BFAABAA0XAAsJAA9BAA4LRgAGEAAOXwAPRAALAiwAFwBEAA6jAA9DAA0GLgAPhwAXCiEACA4ADg4BD0MAEgkQAAbZAAQJAA9DABMGEgAN4wAPRAAKDxQAAA4mAQ9EACEPiAAbDxQABggQAQPvAQ9EABYEFQAOmAEPRABGD+gAAh8ARAB3H/9EAFwPRQABHwBEAGEOMQMGEQAOTQAPPgQKDlQADwoFIA4NAA9EABkG7AQMXQMPRAAZDpUFD3wDAA9FABkf/xoGAA5DAA9cARMOPAAPAQD////2UP//////\",\"L\":3808}")
      .build();
  private static final ImageCompare.Param MAIN_MENU = ImageCompare.Param.builder()
      .area(Area.ofRect(1248, 778, 72, 78))
      .method(ImageCompare.Method.TM_CCOEFF)
      .preProcessor(com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder()
          .enable(true)
          .binaryThreshold(0.0)
          .inverse(false)
          .threshType(
              com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.OTSU)
          .build())
      .template(
          "{\"R\":78,\"C\":72,\"T\":0,\"D\":\"H/8BAP//lxAAAQAPRwA0LwAASAD/hg4xAA9IACIOMgAOkAAOKAAPdAIQA0cALwAAnAIZCzMAHAABAA80ARUJMAANRgAOGgAPSAANHwBHAAUBGwAGGgAPkQAHDwEAGQ9pAQkPSAA1H/9GADMNSQAPYgAHH/9JABgPGgAJD0gAFw8cAAAPSAAhD2UACw9HACsPHwAADygADA9JBA4PbwQuDkgAD7cAJA9IABgPJABSDyUAEQ9EAVkPJgAMB3UEDx0CJQ/MAysfAEgATR//SADEDyABJgwhAA+IAnIOSQAPPQMLD/ADEQ8UBJkORAEPoAVJDkkADzAKBA95BiwO9AIPUAchL/8ASAAgD7sKAA9HACIOcwoPzAMoHwDMAyMPjwleDmMCD2MBBA8+AAcvAABACxkPhQAID0gAFQ/kAAcOcAAPYAwUDhABD4IEEh8A8AwYD7EBDA8wD/9mH//YAMUO0w8PAQD/ZlD//////w==\",\"L\":5616}")
      .build();
  private static final ImageCompare.Param SETTING_MENU = ImageCompare.Param.builder()
      .area(Area.ofRect(90, 32, 88, 88))
      .method(ImageCompare.Method.TM_CCOEFF)
      .preProcessor(com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder()
          .enable(true)
          .binaryThreshold(0.0)
          .inverse(false)
          .threshType(
              com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.OTSU)
          .build())
      .template(
          "{\"R\":88,\"C\":88,\"T\":0,\"D\":\"H/8BAP///4kRAAEAD1cAPw9YAP9UHwAIAdovAABYABYObAAPnAALH/9gARMOxQAPWgMbLgAAWQAOHgEP+gEbBlQADxoABg9YABkfAFYABAYcAB8AWAAjHwBWAAYNHAAPCQETDwEAGQ+5ARkPVwBED1kABx//VwArL///OwAJD1YAJww9AA4dAA9YACYPRAILD1cAJw/zAgQOEgMPVwA7DpAFD1cAHg9ZAA0PHgY+DlgAD3UGFg+aBhIPzQY+D1gAJg+cBj0fAFgASw8pABYPWABsHwBYADsO/wQPUAUfDyQAAQ9YAHgf/1gAnB4ANQAPYAFWD1kAAw9DAhsPwAJaDy8AAA8hBBQP0ARFD1gAlA/pAgAPMAZkDzgHGR8A6AdED5gIci//AEgJRA9ZAAwfAFgAMB8AUAo4DlAED1gLMg5YBQNbBQ8ZAAIPWAAsHwC3DEYfAGcNNQ9jAAkv//8XDiYPvAAJD8gOJw8WAQgf/3gPKA9xAQYPzgMBD4AQLw8sAAIPMBEYD4gRQS//AMUFGA9LEi4OXQMP+BQ5D1gA//YOMhQPAQD///8gUP//////\",\"L\":7744}")
      .build();
  private static final long TIMEOUT = 2000L;
  private static final OCR.Param TIME_DAY = OCR.Param.builder()
      .area(Area.ofRect(376, 660, 96, 88))
      .preProcessor(com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder()
          .enable(true)
          .inverse(false)
          .threshType(
              com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.OTSU)
          .build())
      .apiConfig(ApiConfig.builder()
          .method(OCR.Method.NMU)
          .whitelist("0123456789")
          .build())
      .build();


  private static final OCR.Param TIME_MONTH = OCR.Param.builder()
      .area(Area.ofRect(170, 658, 116, 92))
      .preProcessor(com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder()
          .enable(true)
          .inverse(false)
          .threshType(
              com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.OTSU)
          .build())
      .apiConfig(ApiConfig.builder()
          .method(OCR.Method.NMU)
          .whitelist("0123456789")
          .build())
      .build();
  private static final OCR.Param TIME_YEAR = OCR.Param.builder()
      .area(Area.ofRect(590, 668, 186, 72))
      .preProcessor(com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder()
          .enable(true)
          .inverse(false)
          .threshType(
              com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.OTSU)
          .build())
      .apiConfig(ApiConfig.builder()
          .method(OCR.Method.NMU)
          .whitelist("0123456789")
          .build())
      .build();

  public PlusOneDayENG() {
    super(ScriptInfo.builder()
        .isLoop(false)
        .isHidden(true)
        .description("PlusOneDayENG")
        .build());
  }

  @Override
  public void execute() {
    try {
      init();
      toMainMenu();
      toDateMenu();
      checkIfDateIsSyncByInternet();
      toDateSetting();
      plusOneDay();
      backToGame();
    } catch (ResetScriptException e) {
      reset();
      execute();
    }
  }

  public void reset() {
    press(HOME);
    sleep(1000);
    press(HOME);
    sleep(1000);
    press(HOME);
    sleep(1000);
  }

  private void init() {
    press(HOME);
    sleep(1000);
  }

  private void toMainMenu() {
    until(() -> detect(MAIN_MENU),
        result -> result.getSimilarity() > 0.9,
        () -> {
          press(HOME);
          sleep(2000);
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
    until(() -> detect(SETTING_MENU),
        input -> input.getSimilarity() > 0.9,
        () -> sleep(150), TIMEOUT, this::reset);
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
    until(() -> detect(DATE_CHANGE_MENU),
        input -> input.getSimilarity() > 0.9,
        () -> sleep(150), TIMEOUT, this::reset);
  }

  private void checkIfDateIsSyncByInternet() {
    until(() -> detect(ENABLE_TIME_SYNC),
        input -> input.getSimilarity() > 0.9,
        () -> {
          press(A);
          sleep(500);
        }, TIMEOUT, this::reset);
  }

  private void toDateSetting() {
    press(D_BOTTOM);
    sleep(150);
    press(D_BOTTOM);
    sleep(250);
    press(A);
    sleep(150);
    until(() -> detect(DATE_SETTING_MENU),
        input -> input.getSimilarity() > 0.9,
        () -> sleep(150), TIMEOUT, this::reset);
  }

  @SneakyThrows
  private void plusOneDay() {
    Future<Long> yearF = async(() -> detectAccurateLong(TIME_YEAR, 3, TIMEOUT, this::reset));
    Future<Long> monthF = async(() -> detectAccurateLong(TIME_MONTH, 3, TIMEOUT, this::reset));
    Future<Long> dayF = async(() -> detectAccurateLong(TIME_DAY, 3, TIMEOUT, this::reset));
    Long year = yearF.get();
    Long month = monthF.get();
    Long day = dayF.get();
    LocalDate currentDay = LocalDate.of(year.intValue(), month.intValue(), day.intValue());
    LocalDate nextDay = currentDay.plusDays(1);
    if (currentDay.getMonthValue() != nextDay.getMonthValue()) {
      press(D_TOP);
      sleep(300);
    }
    press(A);
    sleep(150);
    if (currentDay.getDayOfMonth() != nextDay.getDayOfMonth()) {
      until(() -> detectAccurateLong(TIME_DAY, 3, TIMEOUT, this::reset),
          input -> input == nextDay.getDayOfMonth(),
          () -> {
            press(D_TOP);
            sleep(300);
          }, 120000L, this::reset);
    }
    press(A);
    sleep(150);
    if (currentDay.getYear() != nextDay.getYear()) {
      press(D_TOP);
      sleep(300);
    }
    press(A);
    sleep(150);
    press(A);
    sleep(150);
    press(A);
    sleep(150);
    press(A);
    sleep(150);
    press(A);
    sleep(150);
    info("The date is set to {}", nextDay);
  }

  private void backToGame() {
    press(HOME);
    sleep(1000);
    script("LunchGameENG");
  }

}

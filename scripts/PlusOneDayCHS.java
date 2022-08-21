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
public class PlusOneDayCHS extends PGConScriptEngineV1<Object> {
  private static final ImageCompare.Param DATE_CHANGE_MENU = ImageCompare.Param.builder()
      .area(Area.ofRect(100,50,226,60))
      .method(ImageCompare.Method.TM_CCOEFF)
      .preProcessor(com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder()
          .enable(true)
          .inverse(false)
          .threshType(com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.OTSU)
          .build())
      .template("{\"R\":60,\"C\":226,\"T\":0,\"D\":\"H/8BAP///////1UuAAALAA8wAAsPbwAsD+EAXxcACwAWAAoABAEADjAAHwBvACsIPwAPTQAoHwAZABUP4gABLgAA4wAP4gAYDh4BD+IACR8AxgAhDKQADQEAD+IAJw/hABovAADiABEOdAAOqwAP4gAnDxwAAwIIAA/iAFEG4wAP4gBEDx0ABAqRAx7/kgAPUQIJHwDiABof/+IAJQ9sAQgP4gATDpEADzsBDwszAA/iAAIP2QMbD8QBDA8bABIPpwACCgQBDxwCFgwzAA/iAAUOQAYP4gBODBoAD+IAIx//4gAkL///4gBKD0wFAA6mAg+hABsPpgIIC1sDDg4HD+IARA/EAQEfAOIANA/xAQkfAOIAVwxrAg/iAAYfAOIAGR//4gAXDrYED+IAUA6wBQ5GAw/iADUfAOIAqQkZAAyJAw/iAFcf/7YJBg+mAi4f/44HDA+TAAUPTAVkHwAKChIO8gcP4gAcH//iAFkf/+IAIw4AAg/iAEIvAP+mAigP4gACH//iACEOygMP4gA3D44AEA5qBA/UCAAP2A4cDuIAHwDiAMEGRwIKTwIPagQSD+IAkAkVAA/iABMOogAPiAMlHwCIAwcP4gBEDtYADuIADxAHOg/iAPEJrwIP4gAnCQkALwD/8gdkDz0BAg61Aw/iAFsP8gc4D4cEBA99BAEPtQQGD6YCEA6tDA/iAGwPxAABHwDiACAf/+IAcB//wwEGDC0AD+IALB//4gBmCk8ADyEAAQ+mAg0OMwAPXAwFD+IAcQ5nAQ5IAA9vAAMPXAwDDz4NdA+DCAMP4gBfHwAQB08O4gAP8gcWHwDcAwYPDAELD+IAUg5NAw7iAA9bDCcPxAEUHv85Cw8gDjcNcgAfAOIALQ8CDyAPqAEGD1wMMA5gAg4BAQ/hABwIDAIP4gBvHwB3BAAN8wAf/8AcCg8VAQgP4QABD+IAXA7BAAuXAw5qAg9jAAUf/9kCCw9gAQIPXQASH/8iAB4vAAAcDQoO0wkO4gAPtQAIBtwBDy0AAg7lAw/iACYPhgQED/4NEA2WAw9jAAYPfwwaD+IAJR//AQCXDrMADwEA//////////////+hUP//////\",\"L\":13560}")
      .build();
  private static final ImageCompare.Param DATE_SETTING_MENU = ImageCompare.Param.builder()
      .area(Area.ofRect(84,390,320,80))
      .method(ImageCompare.Method.TM_CCOEFF)
      .preProcessor(com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder()
          .enable(true)
          .inverse(false)
          .threshType(com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.OTSU)
          .build())
      .template("{\"R\":80,\"C\":320,\"T\":0,\"D\":\"H/8BAP//////////////////////////////////////EB8AwgD/LD8AAAAeABQOKgAPYwAfD24ADg/CAC4vAAARAS8DAQAJDQADAQAPQAErHwBvAAkOawAPQAEFDpAAD4wAFA7EAAzCAAelAAyaAA8/AR0AMgAeAEEBD4ACOy8AAEEBDwYMAA/WAQAPQAEgHwBAAQwfAJkABQ9AAUAPVwALHwBAATAOFwAPQAEFHwBAATAf/0ABLQmFAQ9cAAAP9gAODoEADhAABowAD/QABB8AawANDz0AAQIZAA/3AAYGiAAPQAEJCkEBCEQED0ABGw9cAAUPPwERDroCD0sFAA80AgsOhwQKgAIPnwABD8kCBg9AARoPQgYFD0ABKi8AAEABZg5ABg9AATsOWQIP3QcvD3QABQUIAAixAA7lAB//9QUAAlwDBzgAD0ABRR//3gkFB/0BH/8SBAcPQAFSD7wBAC8AAMQCCAh0Aw1hAA9AARENgAcOQAEP4gACD30EGQ/hAAIG2QAPQAFACp0FD1sBCALxAQ5mCQ9AARcMCwQPQAEAHwBAASsPJAAID0ABUA6bAgo1DA5qCA9AASsL3AQOggAPQAEKD8UAAQ9AATgOkAEOQAEO3gkPgAIJHwBAAVMP/QQHD0AGFA28CC4A/0ABD8ANGw5AAQ4wAw9AAREOHQAPQAYpDkABD34HBg9AASMNfgAPQAFvDpIND4AHOQ9AAREI2QAPQAFtDj0DD4AHCAr3AA6dBw/5DQ0BQgUCdggPQAE1HwBAAUQOQAsOQAYOKA4Pvw0KLwAAdgIADwAPCw+AByAPywACD0ABDx//QAEMD4AHQg7LBQ9AAT8fAEABPwYHAA5CBA+kBwMfAEABTA+AAhkGlwAPQAEgCAkAD0ABMArBAw/ACDwMoQAMsAUuAABTBw9ACwwKCgAPQAYSHwAEAQIf/0ABNx8AQAFYHwBAASAOCwAErAUPQAEoDgAKD0ABIx//QAEOH/8ABTYeAI8HLwD/QAEsH/9AARoDpAcEigcPQAE0HwBABlMOuQULwgYPwAMWH/9AAR8OIQUPrAYNDBoAD0ALBQ9AAVEIwwAL6gEPgAIhL///QAE1H/9ECQ0MZwcPQAFZDjYHD0ABNi///8ADHQU8FAIGAA6cBA+AAiUf/4AHCw9AATof/0ABIQ4jBg9AASEGbQ0O3QAPgAcKD0ABDg/ACEMPJAMALwD/QAEkDuYFD0ABJA96CAYfAIkEFw50Bg9AAUELZQANOgYfAEABGB8AQAESD/kAEgcIAA4aBA9AAT4f/0ABLQkOAA42CQ76AB8AQAEZHwCAAhIf/woEAx8AQAFRD0sABA8BABwOfgEMfAQP+gYQD0ABDx8AwA0KCzAGHwBAARkPBgQAD0ABVw+FBwIKQAEP+wAID0AMBAxRAQ//Dg8MBAELHgMf/1IlBwu6ASoAAAgAB48AD0ABEA5sBQ9+AhgO/QIPgAILApgACDUBDkYAD0ABFAu4AArXBw5aBQj6AQ5AAQ8nAAEPQAEQDvgDDkABD3IBDQ9rAAEPUCceD7EAAA+yAAgPZwACD0ABHA7hAA9AAVYf/1kGAg5AAQ8UBCYDEgAPRgADLwAAkAAVD/EAJw+kAgEPtgAJDwEA/////////////////////////////////////9JQ//////8=\",\"L\":25600}")
      .build();
  private static final ImageCompare.Param ENABLE_TIME_SYNC = ImageCompare.Param.builder()
      .area(Area.ofRect(1488,210,92,82))
      .method(ImageCompare.Method.TM_CCOEFF)
      .preProcessor(com.duanxr.pgcon.core.preprocessing.config.NormalizePreProcessorConfig.builder()
          .enable(true)
          .build())
      .preProcessor(com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder()
          .enable(true)
          .inverse(false)
          .threshType(com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.OTSU)
          .build())
      .template("{\"R\":82,\"C\":92,\"T\":0,\"D\":\"H/8BAP///////////8QuAAANAA4OAA9bACQvAAANAAMADgAEFQAJAQAPXAAiCzoADVUAD10AKA03AARKAA5bAA9cAC8EFgEICgAGEwAPVwAPD/QABgGxAAgsAA9cADMfADMCAA9cADwfAFwALQdiAAI5AAYUAR4AlgEOXAAP0QEZCqoADtEAD1wANg/fAiwMUAEPuAA/D1wAAw1FAw9wAToIXAAOKAIPgwIYBxEADJQDDlwAD94CJA9cAAQfAFwAQATTAA+YAzQGXAAEEAEOXAAPzAEaDwQFAgdqAA+YAyMOugIJ4AIf/1wAKA29AQpcAApTAA4UAQ8PARELFwEPCAUsHf9zAwo9Bw4kAA5cAA9aABIMDgAPPAMqCzUADzkECAu3Aw9bACMIPwAOvgQOXAAPEAEUDRYADlwADo0AD1gFGQVCAC///yMKSA8BAP///////////3RQ//////8=\",\"L\":7544}")
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
      .area(Area.ofRect(766, 664, 98, 74))
      .preProcessor(com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder()
          .enable(true)
          .binaryThreshold(0.0)
          .inverse(false)
          .threshType(
              com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.OTSU)
          .build())
      .apiConfig(ApiConfig.builder()
          .method(OCR.Method.NMU)
          .whitelist("1234567890")
          .build())
      .build();
  private static final OCR.Param TIME_MONTH = OCR.Param.builder()
      .area(Area.ofRect(564, 660, 110, 84))
      .preProcessor(com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder()
          .enable(true)
          .binaryThreshold(0.0)
          .inverse(false)
          .threshType(
              com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.OTSU)
          .build())
      .apiConfig(ApiConfig.builder()
          .method(OCR.Method.NMU)
          .whitelist("1234567890")
          .build())
      .build();
  private static final OCR.Param TIME_YEAR = OCR.Param.builder()
      .area(Area.ofRect(270, 670, 190, 68))
      .preProcessor(com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder()
          .enable(true)
          .binaryThreshold(0.0)
          .inverse(false)
          .threshType(
              com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.OTSU)
          .build())
      .apiConfig(ApiConfig.builder()
          .method(OCR.Method.NMU)
          .whitelist("1234567890")
          .build())
      .build();
  public PlusOneDayCHS() {
    super(ScriptInfo.builder()
        .isLoop(false)
        .isHidden(true)
        .description("PlusOneDayCHS")
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
    sleep(150);
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
      until(() -> detectAccurateLong(TIME_DAY, 3, TIMEOUT, this::reset),
          input -> input == nextDay.getDayOfMonth(),
          () -> {
            press(D_TOP);
            sleep(300);
          }, 120000L, this::reset);
    }
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
    script("LunchGameCHS");
  }

}

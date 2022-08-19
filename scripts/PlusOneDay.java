import com.duanxr.pgcon.core.detect.api.ImageCompare;
import com.duanxr.pgcon.core.detect.api.OCR;
import com.duanxr.pgcon.core.detect.api.OCR.ApiConfig;
import com.duanxr.pgcon.core.model.Area;
import com.duanxr.pgcon.exception.ResetScriptException;
import com.duanxr.pgcon.script.engine.PGConScriptEngineV1;
import java.time.LocalDate;
import org.springframework.stereotype.Component;

/**
 * @author 段然 2022/7/25
 */
@Component
public class PlusOneDay extends PGConScriptEngineV1<Object> {
  public PlusOneDay() {
    super(ScriptInfo.builder()
        .isLoop(false)
        .name("PlusOneDay")
        .build());
  }
  private static final ImageCompare.Param ENABLE_TIME_SYNC = ImageCompare.Param.builder()
      .area(Area.ofRect(1502, 230, 72, 40))
      .method(ImageCompare.Method.TM_CCOEFF)
      .preProcessor(com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder()
          .enable(true)
          .binaryThreshold(0.0)
          .inverse(false)
          .threshType(
              com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.OTSU)
          .build())
      .template(
          "{\"R\":40,\"C\":72,\"T\":0,\"D\":\"H/8BAP/yLgAADQAODgAPRwAQLwAADQADAA4ABBUACQEAD0gADgsmAA1BAA9JABQNIwAENQAORwAPSAAbBNoACCwBBxMAD7oADgU5AAnlAA5IAA8BARIMuwEPSABQAhUABz4AAw8ADjEADkgAD0YCEA9kAAEHawAPSAAgDz8CGA7iAQ+QACkPSAADDpICDyABJQ1HAA5oAQT3AQYFARn/AwEGMQAuAABIAA8+AhEPSAAEHwBIACwEqwAP0AIgBkgAA+sDDkgAD2gBBw/sAwIPngABD64BDAsiAAZAAg8YAxUNWQEKSAAPYAMVB3MADpoDD6gDFgrTAS8AAAwFAx8ASAAMCnkDCDAAD4gCFwwiAA6eAA/5AgQPRgAOCRMAD7kCBw/UAAoNFgAPTQcADpAADngHDvwDD/QHMR//AQD/LlD//////w==\",\"L\":2880}")
      .build();

  private static final ImageCompare.Param DATE_SETTING_MENU = ImageCompare.Param.builder()
      .area(Area.ofRect(1432, 648, 250, 110))
      .method(ImageCompare.Method.TM_CCOEFF)
      .preProcessor(com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder()
          .enable(true)
          .binaryThreshold(0.0)
          .inverse(false)
          .threshType(
              com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.OTSU)
          .build())
      .template(
          "{\"R\":110,\"C\":250,\"T\":0,\"D\":\"H/8BAP////////////++EQABAA/eAMcA4QAP+AACDwEA1Q/5AOYvAAD5AOYL+wAO4gMPzQTJD/kA4Q3rAA75AA/7ANQfAPoA/////8kP3AXxD/oA////////////////////////9Q/UGwINBxQO8hkP+gCsD9EcBw7lGw/6ALkPzB0JDAgWHwDuArgPyB4LDIwAD/oAvwwJAQ76AA75AA/6AKsPqyAELQAA+gAOeAMP+gCwDxQACAUKAB8A+gC8DtAAC4MABAkADmQGD/oAsA8RAQoDCAAfAPoAvQ7RAAyEAAIIAA75AA/6ALEf//oADR4A+QAP+gDIBRoABe4HD7gLuw5dAA/6AAYf//oA///cD+gD4w/cBdoF7AAP0AfiHwDECfEO+wAPuAvWDvoAD6wNwwcWAA+gD+YKqg8OoQ8PlBHHDhoTD44Sti//AIgTCg4bAA98FdUKfwAfAHAX2A5+Fy8AAGQZ4h//+gC+DpoMD1gbBB//+gBcDwEAew/6AP/////////////////////////////gDvsAD/oA2h8A+QDhDuwcDwEAxw/6AOoOBgIP+QDjD/wA1R//AQD/////////yVD//////w==\",\"L\":27500}")
      .build();

  private static final ImageCompare.Param DATE_CHANGE_MENU = ImageCompare.Param.builder()
      .area(Area.ofRect(104, 52, 218, 54))
      .method(ImageCompare.Method.TM_CCOEFF)
      .preProcessor(com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder()
          .enable(true)
          .binaryThreshold(0.0)
          .inverse(false)
          .threshType(
              com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.OTSU)
          .build())
      .template(
          "{\"R\":54,\"C\":218,\"T\":0,\"D\":\"H/8BAP////9jLgAACwAPMAALD28ALA/ZAFcXAAsAFgAKAAQBAA4wAB8AbwArHgB+AA/KADMPawAHBQsALgAA2wAP2gAYDhYBD9oACR8ArQAYDZwADQEAD9oAJw/ZABovAADaABEOdAAOqwAP2gAfDxwAAwMIAA/aAFAG2wAP2gA8Dx0ABArIAR7/igAPOQIJHwDaABof/9oAHQ+bAwgP2gATDokADzMBDwszAA/aAAIPuQMaDrQBDy8AGA+fAAIK/AAPDAIWDDMAD9oABQ8IBhcP2gAuDBoAD9oAIx//2gAkL///2gBCD9AGAA6OAg+ZABsPjgIICzsDDs4GD9oAPA/2BQIP2gA0D+EBCR8A2gBPDFMCD9oABh8A2gAZH//aABcOjgQP2gBIDngFDiYDD9oALAneAQ/aAJ4JGQAMaQMP2gBPH/9eCQYPjgIuH/9OBwwPkwAFDxwFXB8AsgkSD6oHFw8qAAUP2gBRH//aACMO8AEP2gA6D9sAAw7sCw+qBxgP2gAhDqoDD9oALw+GABAOQgQPhAgAD1AOHA7aAB8A2gC5Bh0CCiUCD9oAER8A2gCICRUAD9oAEw6iAA9oA1oP2gAiDuoFD9AGRA/aAOkJlwIP2gAnCQkAHwAEARIf/6oHNw8tAQIOlQMP2gBbD6oHMQ9fBAMPVQQBD40EBg/aABEOPQwP2gBNHwABABIOKgAP2gCPH/+zAQYMLQAP2gAsH//aAF4CEAAO0w8OcwYP2gAsDl0AD9oAUgVHAA5XAQ+RDQMPsAAhDxwFXQ8zCAUP2gBfHwDQBkcO2gAPqgcaCVEADjIGD9oAXwpHAA+cCwIP6wsoD1QPFB7/2QoPOAogD1YMCAUaBQ/aACof/9oAIA9YABIv///sCxkeAAQGDk8ND9kAGwj8AQ/aAGcNfgAOHg8PXgkICOoGD14NFQ/aAFQOuQALdwMPUgIBD1EDDg6rAQ4sAA8NBBEvAADaAAcPNgERHv/mBA7aAA/ZABkPhB0JDsUDD9oAIg9sAQAPfg0QDXYDD+sLBg8PDBoP2gAcDn4ADwEAbw+rAA4PAQD/////////0lD//////w==\",\"L\":11772}")
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

  @Override
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
    until(() -> detect(SETTING_MENU),
        input -> input.getSimilarity() > 0.9,
        () -> sleep(150), 10);
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
        () -> sleep(150), 10);
  }

  private void checkIfDateIsSyncByInternet() {
    until(() -> detect(ENABLE_TIME_SYNC),
        input -> input.getSimilarity() > 0.9,
        () -> {
          press(A);
          sleep(500);
        }, 5);
  }

  private void toDateSetting() {
    press(D_BOTTOM);
    sleep(150);
    press(D_BOTTOM);
    sleep(250);
    press(A);
    sleep(150);
    until(() -> detect(DATE_CHANGE_MENU),
        input -> input.getSimilarity() > 0.9,
        () -> sleep(150), 10);
  }

  private void plusOneDay() {
    Long year = numberOcr(TIME_YEAR, 3);
    Long month = numberOcr(TIME_MONTH, 3);
    Long day = numberOcr(TIME_DAY, 3);
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
      until(() -> numberOcr(TIME_DAY, 3),
          input -> input == nextDay.getDayOfMonth(),
          () -> {
            press(D_TOP);
            sleep(300);
          }, 63);
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

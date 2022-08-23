import com.duanxr.pgcon.core.detect.api.ImageCompare;
import com.duanxr.pgcon.core.detect.api.ImageCompare.Result;
import com.duanxr.pgcon.core.detect.api.ImageCompare.ResultList;
import com.duanxr.pgcon.core.detect.api.OCR;
import com.duanxr.pgcon.core.detect.api.OCR.ApiConfig;
import com.duanxr.pgcon.core.model.Area;
import com.duanxr.pgcon.exception.ResetScriptException;
import com.duanxr.pgcon.script.api.ScriptInfo;
import com.duanxr.pgcon.script.component.ScriptTask;
import com.duanxr.pgcon.script.engine.PGConScriptEngineV2;
import java.time.LocalDate;
import java.util.concurrent.Future;
import lombok.SneakyThrows;

/**
 * @author 段然 2022/8/23
 */
public abstract class NintendoSwitchEngineV2<T> extends PGConScriptEngineV2<T> {

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
  private static final float NINTENDO_SWITCH_ENGINE_V_2_SCORE_THRESHOLD = 0.7f;
  private static final ImageCompare.Param START_GAME_DATA_BACKUP_CHS = ImageCompare.Param.builder()
      .area(Area.ofRect(1236, 796, 162, 54))
      .method(ImageCompare.Method.TM_CCOEFF)
      .preProcessor(com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder()
          .enable(true)
          .inverse(false)
          .threshType(
              com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.OTSU)
          .build())
      .template(
          "{\"R\":54,\"C\":162,\"T\":0,\"D\":\"H/8BAP///////3kvAACIAHUfABkABh4AsQAPYQAuDnAAD6IAOB4AIgAPAQAID6IAKAlHAA9aAAocADYAD6EAGC4AAJMADvgAD6EABA9bABMDCgAPIgAHHwBEAQwPogBqDl0BDoIAD6IAJx3/XwAfAB4AAgQaAA+hAAwLgwAPDAIDBVgABR8ADzEDFw7UAAtoAQpPAA7oAg+iAAouAABKAAurAA/sARsPogBODUQCD4wCHw0gAAuTAQ5zAQ+iAB0v//+jACsOAAIOnAEPRAEYBR4ADEMBDr4EDqIACC0AATEGDqIAD8wDCA+iAC4OcgMPIAMUDSQAHwCiADYPHgYBHwCiAFIPEAULCIECD6IAAA+IAjQMQwICRQgP+QUTD6IABg6KAg8qAykPOAggGgCpBA+hBQIPRAExLwAAogAoCOcAD40AAw+IAh0OogAPYQURHgBJBR3/5gYPjgcGD6IAKR8AowkGH/8OAgwfACkDBAZWAxYAyQAP5gElDwYDEw4AAg+iAAwORwAPogAuD24EAg81AREa//YBD6IASR7/tgoPogAZCj8ADvYGD1QGJx4AiAgO1AMPKQ0EHwBKAwEOfgYPogAsDi8ID4EABQ+iAA0f/6IABQ86CDYKLAAOQwEPogAOHv99CQ5+AQ+IAh4IbAAOSQIPogAjAjkCD1MGLQ+YBwQOfwMP9gEWCSwDDogCD1QGLg7TAA6QAA+zABAEcAQPKgNFD6MABw9VARkfACoDAQ8xDyQPRAELDycLBA+qEA4PsgUCD9IPKQ6yBQhSBw7ICw/dBwgf/6IAQQ4KAAo6AQ7YAw9ICAgPogAeD7sGAwqKAgnDBA5+Ag+iABkPHQADD5IJAA+eCR8vAABmCgUHHQwPGBUbDOoAH/8BAP///////05Q//////8=\",\"L\":8748}")
      .build();
  private static final ImageCompare.Param START_GAME_DATA_BACKUP_ENG = ImageCompare.Param.builder()
      .area(Area.ofRect(1176, 826, 276, 50))
      .method(ImageCompare.Method.TM_CCOEFF)
      .preProcessor(com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder()
          .enable(true)
          .inverse(false)
          .threshType(
              com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.OTSU)
          .build())
      .template(
          "{\"R\":50,\"C\":276,\"T\":0,\"D\":\"H/8BAP//////////IS8A/wEA+xEAAQAPigB3HwBgAG8OiQAPEQFqCY0ALwAA8QI3DmAADycCEi8AABQBew+sAR8OMQAPFAEhByQADlEADxIBYw8UAUEvAAAUARgOgwAPFAFnDNgADxQBPQUMAA8UAZIOewAPFAE7DkoADxQBpA7uAg/9AgEWAA0BDjMEDhQBD2UGEAZEAApyAwl1AwpSAAeMAQ+BAAEOrwUO7QcPFAESDY0AD54AAw68AAy/AA4UAQ9pBAMOeAAPVQAHDCAHLwAAygUIBrEBDWMADxQBLA6fAA7DAA8UAR4ONAAPUQAFH/8UAREPowANDjkBD2MFEg5gAgpwAC///2AAAg6SAg9gAA4PaAEGC5sCB6ADHwAUARIMkAMPEwEILwAAFAEbH/+XAwMf/60CAQimAg8UARcOAQMPZAUJDuYADjwDDlwADkEBCxkADxUBCx8AgwkXCpoBDqAID2AAEg8jBAAPFAEXDkQBD6MAAA97AQoP1wYNDkoCDxQBQQ9mBAsfACMJDQohAA8UARsOdQAPJQgQD2EDAB8AgwkQDTIAD2AAGw/GAAIfABQBDQU1BA9KCA0OIAAfABQBGQ6hBg6nAwoqAA8UARse/zsMDxQBOB//FAEZH/8UARYO4gIO8AwOyAkPFAEXDWAADqwDDxQBIw7nAAqjAA8UAQAPsgAWB1IADrUADjoHDxQBIAwHEA8UARwISQIOVwIP3QcHDsoFBwQADigCDywEGC//ABQBMS///xQBQQ6jAA8UAQoOJQAPtQAkDgsBDxQBIw7gAQ8UARYPDQEDD7cIBA8UARYfABQBJg11Bw8UAT8fABQBFwcdAA9WAgYPKAJPDsQGDxQBYx8AFAEBDkEBDxQBCS///zkQKA6PCQ8UAUkJmgMPFAERC7sJDxQBEh//FAEsDCgCDrQJDxQBKi//AGgBDw/7BgUPPAgPDmQFDykCKB8AFAEPD3gGCR8AYAASD/AMAQ8UAXwPNw4QC3wADxQBGw5dCw91CwQPFAEUDg4ADvIODxQBBA+UCBsOigUIEQAvAACgCBMPaQsDD2EADQ/wAQUOMQAPFAEIL/8AoAgGD+0ACA8UASgf/xQBEQ7qDQ+HAAwPdwACD4MABg12AQ8UAQ0MswUOjgAPFAEUH/+oCQgcAAkUD/EDBQ9gABMKVQAO9wcPhA0DDzUJBQtOAA8kDgcPEwERDnQBDhYAC6UBDukGCoUBD2AABh8AeQAQD5ADBA/BBBIJNAMOOA8OpwAPAQD////////////////vUP//////\",\"L\":13800}")
      .build();
  private static final ImageCompare.Param START_GAME_SELECT_USER_CHS = ImageCompare.Param.builder()
      .area(Area.ofRect(84, 500, 232, 68))
      .method(ImageCompare.Method.TM_CCOEFF)
      .preProcessor(com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder()
          .enable(true)
          .inverse(false)
          .threshType(
              com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.OTSU)
          .build())
      .template(
          "{\"R\":68,\"C\":232,\"T\":0,\"D\":\"H/8BAP////////////////////94HwAiAA8fABUAAg/VAIoeABIACxQADyIAAw8VAAEPnABOD0sAKCgAAM8AABAAAxMAHwDBAAEBDAAP6AAFBjAACgEACD4ACxsADiQADzoBRAVNAA9+AAYIhwADzQAF1AAP6QAAD8UACC8AAOkAcA99AAcLCwAO0AEOMQEP6AA0D+kAOQeoAgPDAATNAAtvAg8NAQIPAgMDBxkADwwAEQ0ZAA7cAg/zADELQgAvAACcBAMPlAILCH0BC/MAD+gABQ+DAAIvAADpAEYPcQIDD4kAAA/aAgwM3wIM5wAP6ABtL///WAMMAR4AB40FA9kCDjsADLsEC2EDD+gAFgJUBhcAgwEv///oAFQMpgEPPAAFDUwAChUCDqADDugADs4AD6AHQg5RAA+NABMO1AAMKwEO0AEP6AAYH//oAFYOIgAP6AAPDTgAD3wDAw/oAFEOPwcPugIHB7AAD4gACg5BAA/oAGoHZAAO6AAPpwAVH/80BQwP6AARD6ADTBoADQYW/0gFA/MECQ8JD6IAEx4APQEOWAcODAAPuAJLDp4CDwwBCA/QARIOFQAO6AAPWAYPCrgGBWIMD3cEMx8ABAgKCjAAD+MKAA0zBw0rCA8oCGsPUQ0NLgD/hwMPswwBLwAA/ggFD+gAkwuWAA/oAAQP2QsKDtwAD7gCDgqIAA/oAD0LuQAP6AAWCBsBD5gNAA8oCHIP6AAbGwDoAA7FAw+ADgAPQAc0H/+4AlgHnwQOZggP0AQFD+gAJw/cAQcP6ABLBp4ECb8CDlAMDowAD+gAOA6uAw/oAB0DVxAI0gAI6AAJhQYKpwMP6AAKDygIEA72CA/fCA4PoANGCpEADi0RDugAD3AFHQ/nABMOvAIPTxAgDoULDwQIAA4lAA0oBA/oAE0OmQQPEAkQDrUGDwQJAw5UBA84FBsLdwAObAEPtwIEDxQBJQroAA8cBAwPfQwCD+gALx//6AAAD9ABSw7HAAs4AQ7nAA4YAg/oABEf/+gAbg4IAg4HFA/oABIOEQAPEhIMCyQBD1EEEg+IBCAPcAEHD7wTAAoWAA6gAwvoAA73AA8uAgMP6AAeDlkGD5gNGwtLAAl8AA/GAwkOeAAP6AAVHwDoAB8PKAglH/8dDQYO/gAP6AAGD54MCwIQAA8WAwoPcwMED0URPw5SGQ+dBRMGGQAPeAsMDvcADtMQDwEA//////////////////+2UP//////\",\"L\":15776}")
      .build();
  private static final ImageCompare.Param START_GAME_SELECT_USER_ENG = ImageCompare.Param.builder()
      .area(Area.ofRect(96, 500, 246, 58))
      .method(ImageCompare.Method.TM_CCOEFF)
      .preProcessor(com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder()
          .enable(true)
          .inverse(false)
          .threshType(
              com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.OTSU)
          .build())
      .template(
          "{\"R\":58,\"C\":246,\"T\":0,\"D\":\"H/8BAP//////////////////////gR8A9QDiPwAAAM8AvA7SAA/2AMsD9AAO+AAP9gA0LwAA9ACKDtUAD/YAMQ4hAg/1AH0P9gAPDw4EswjsAA6LAg/2AMkOwAAP9gDMD5MACQ30Ag0tAAgpAg8SBgQOPwMPPwYaC4AADp0AD5AACy4AAOQADvYAD/EDAg4tAA8hAAUL5wAPLAUIDxMBEwoKBQesAA58AA+xAAUOmwEO9gAPkQALDQMBD3sACQ/3AAEPPwYeDvYAD50AAA8UAAwP9gAYD5EAAg5lAA8PAgIoAAC4Ai8AAFoBBg4kAA/2ABMELwAPkAAODmUCD9kDCgyZAw5jAg8hAAYHiQAPOwQOC7UAD/YAFA7SAw+QABMOLwAPDAEIDvYACVoADhcAD1gBEg5+AQ/2ABoOxgAPkAAWBxABHwCDAAgJWgIPIQAPD/YAAg/3ABMP9gAqD5AAFR7//QIP9gAiBJ0AD7ABAw/1ABgP9gBYDvgADKkABHwADk4AD/YAAA/sARsP2AMzDoUBDvYAD2wOCA/2AF4OvQYP9gAUD5wCCA8WAgEPigkQBFYABuUHLgAAOwYPnAcCD/8JBA/7DCUPsAcND0gAAw/sARYGzAAORQAP9gAEHwD2AC4OqgAPnAkkDrEAD+wBFQ5DAg/2AE0NugAP9gAXC+gCD/YARC////YADQ5kAA/2ABMf/7oGHw5CAA/iAikPlgQMDxwFCw/OBB8L1QIP7AcPHwCyBgMPsQADD/YAHx//RQAOD/YANQ48AA/2ABsHqAEP9gASD8wBKw/tAQgP9gBGDvEEDp4CD/YAIArCAw/2AFsfAPYAWg9WAAQMaQQPdA0JBl4NDHQNCN0HD/cABA9SEQwNnQYJbwAHQwAPkAAGDzMHGg/EBQIPGQgDDxYADgxcAA98AAUvAACgABMOZQAPegAIB0sADxIKEA9aAAgPOgABDzcAEC8AAPYAQg96AA0O9gAO4hEPGQALDtgBDi8BD9MQHQQaAA6bAwwjAA/2AAIOsQAP9gARDlUDD3sPAA8gCQUPxAELDBIAH/9iAAsvAABlBQ0N2gEPsQAEDuUODwEA//////////+bUP//////\",\"L\":14268}")
      .build();
  private volatile PlusOneDaySubScript plusOneDaySubScript;

  protected NintendoSwitchEngineV2(ScriptInfo<T> scriptInfo) {
    super(scriptInfo);
  }

  protected void startGame() {
    press(A);
    sleep(1000);
    checkSelectUser();
    sleep(1000);
    checkDataBackUp();
  }

  private void checkSelectUser() {
    ResultList detect = detect(START_GAME_SELECT_USER_ENG, START_GAME_SELECT_USER_CHS);
    if (detect.getMax().getSimilarity() > NINTENDO_SWITCH_ENGINE_V_2_SCORE_THRESHOLD) {
      press(A);
      sleep(1000);
    }
  }

  private void checkDataBackUp() {
    ResultList detect = detect(START_GAME_DATA_BACKUP_ENG, START_GAME_DATA_BACKUP_CHS);
    if (detect.getMax().getSimilarity() > NINTENDO_SWITCH_ENGINE_V_2_SCORE_THRESHOLD) {
      press(D_RIGHT);
      sleep(150);
      press(A);
      sleep(1000);
    }
  }

  protected void restartGame() {
    closeGame();
    restartGame();
  }

  protected void closeGame() {
    goToMainMenu();
    press(X);
    sleep(300);
    press(A);
    sleep(5000);
  }

  protected void plusOneDay() {
    if (plusOneDaySubScript == null) {
      synchronized (this) {
        if (plusOneDaySubScript == null) {
          plusOneDaySubScript = new PlusOneDaySubScript();
          plusOneDaySubScript.setComponents(components);
        }
      }
    }
    new ScriptTask(plusOneDaySubScript).run();
  }

  protected void goToMainMenu() {
    until(() -> detect(MAIN_MENU),
        result -> result.getSimilarity() > NINTENDO_SWITCH_ENGINE_V_2_SCORE_THRESHOLD,
        () -> {
          press(HOME);
          sleep(2000);
        });
  }

  private class PlusOneDaySubScript extends PGConScriptEngineV2<Object> {

    private static final ImageCompare.Param DATE_CHANGE_MENU_CHS = ImageCompare.Param.builder()
        .area(Area.ofRect(100, 50, 226, 60))
        .method(ImageCompare.Method.TM_CCOEFF)
        .preProcessor(com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder()
            .enable(true)
            .inverse(false)
            .threshType(
                com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.OTSU)
            .build())
        .template(
            "{\"R\":60,\"C\":226,\"T\":0,\"D\":\"H/8BAP///////1UuAAALAA8wAAsPbwAsD+EAXxcACwAWAAoABAEADjAAHwBvACsIPwAPTQAoHwAZABUP4gABLgAA4wAP4gAYDh4BD+IACR8AxgAhDKQADQEAD+IAJw/hABovAADiABEOdAAOqwAP4gAnDxwAAwIIAA/iAFEG4wAP4gBEDx0ABAqRAx7/kgAPUQIJHwDiABof/+IAJQ9sAQgP4gATDpEADzsBDwszAA/iAAIP2QMbD8QBDA8bABIPpwACCgQBDxwCFgwzAA/iAAUOQAYP4gBODBoAD+IAIx//4gAkL///4gBKD0wFAA6mAg+hABsPpgIIC1sDDg4HD+IARA/EAQEfAOIANA/xAQkfAOIAVwxrAg/iAAYfAOIAGR//4gAXDrYED+IAUA6wBQ5GAw/iADUfAOIAqQkZAAyJAw/iAFcf/7YJBg+mAi4f/44HDA+TAAUPTAVkHwAKChIO8gcP4gAcH//iAFkf/+IAIw4AAg/iAEIvAP+mAigP4gACH//iACEOygMP4gA3D44AEA5qBA/UCAAP2A4cDuIAHwDiAMEGRwIKTwIPagQSD+IAkAkVAA/iABMOogAPiAMlHwCIAwcP4gBEDtYADuIADxAHOg/iAPEJrwIP4gAnCQkALwD/8gdkDz0BAg61Aw/iAFsP8gc4D4cEBA99BAEPtQQGD6YCEA6tDA/iAGwPxAABHwDiACAf/+IAcB//wwEGDC0AD+IALB//4gBmCk8ADyEAAQ+mAg0OMwAPXAwFD+IAcQ5nAQ5IAA9vAAMPXAwDDz4NdA+DCAMP4gBfHwAQB08O4gAP8gcWHwDcAwYPDAELD+IAUg5NAw7iAA9bDCcPxAEUHv85Cw8gDjcNcgAfAOIALQ8CDyAPqAEGD1wMMA5gAg4BAQ/hABwIDAIP4gBvHwB3BAAN8wAf/8AcCg8VAQgP4QABD+IAXA7BAAuXAw5qAg9jAAUf/9kCCw9gAQIPXQASH/8iAB4vAAAcDQoO0wkO4gAPtQAIBtwBDy0AAg7lAw/iACYPhgQED/4NEA2WAw9jAAYPfwwaD+IAJR//AQCXDrMADwEA//////////////+hUP//////\",\"L\":13560}")
        .build();
    private static final ImageCompare.Param DATE_CHANGE_MENU_ENG = ImageCompare.Param.builder()
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
    private static final ImageCompare.Param DATE_SETTING_MENU_CHS = ImageCompare.Param.builder()
        .area(Area.ofRect(84, 390, 320, 80))
        .method(ImageCompare.Method.TM_CCOEFF)
        .preProcessor(com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder()
            .enable(true)
            .inverse(false)
            .threshType(
                com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.OTSU)
            .build())
        .template(
            "{\"R\":80,\"C\":320,\"T\":0,\"D\":\"H/8BAP//////////////////////////////////////EB8AwgD/LD8AAAAeABQOKgAPYwAfD24ADg/CAC4vAAARAS8DAQAJDQADAQAPQAErHwBvAAkOawAPQAEFDpAAD4wAFA7EAAzCAAelAAyaAA8/AR0AMgAeAEEBD4ACOy8AAEEBDwYMAA/WAQAPQAEgHwBAAQwfAJkABQ9AAUAPVwALHwBAATAOFwAPQAEFHwBAATAf/0ABLQmFAQ9cAAAP9gAODoEADhAABowAD/QABB8AawANDz0AAQIZAA/3AAYGiAAPQAEJCkEBCEQED0ABGw9cAAUPPwERDroCD0sFAA80AgsOhwQKgAIPnwABD8kCBg9AARoPQgYFD0ABKi8AAEABZg5ABg9AATsOWQIP3QcvD3QABQUIAAixAA7lAB//9QUAAlwDBzgAD0ABRR//3gkFB/0BH/8SBAcPQAFSD7wBAC8AAMQCCAh0Aw1hAA9AARENgAcOQAEP4gACD30EGQ/hAAIG2QAPQAFACp0FD1sBCALxAQ5mCQ9AARcMCwQPQAEAHwBAASsPJAAID0ABUA6bAgo1DA5qCA9AASsL3AQOggAPQAEKD8UAAQ9AATgOkAEOQAEO3gkPgAIJHwBAAVMP/QQHD0AGFA28CC4A/0ABD8ANGw5AAQ4wAw9AAREOHQAPQAYpDkABD34HBg9AASMNfgAPQAFvDpIND4AHOQ9AAREI2QAPQAFtDj0DD4AHCAr3AA6dBw/5DQ0BQgUCdggPQAE1HwBAAUQOQAsOQAYOKA4Pvw0KLwAAdgIADwAPCw+AByAPywACD0ABDx//QAEMD4AHQg7LBQ9AAT8fAEABPwYHAA5CBA+kBwMfAEABTA+AAhkGlwAPQAEgCAkAD0ABMArBAw/ACDwMoQAMsAUuAABTBw9ACwwKCgAPQAYSHwAEAQIf/0ABNx8AQAFYHwBAASAOCwAErAUPQAEoDgAKD0ABIx//QAEOH/8ABTYeAI8HLwD/QAEsH/9AARoDpAcEigcPQAE0HwBABlMOuQULwgYPwAMWH/9AAR8OIQUPrAYNDBoAD0ALBQ9AAVEIwwAL6gEPgAIhL///QAE1H/9ECQ0MZwcPQAFZDjYHD0ABNi///8ADHQU8FAIGAA6cBA+AAiUf/4AHCw9AATof/0ABIQ4jBg9AASEGbQ0O3QAPgAcKD0ABDg/ACEMPJAMALwD/QAEkDuYFD0ABJA96CAYfAIkEFw50Bg9AAUELZQANOgYfAEABGB8AQAESD/kAEgcIAA4aBA9AAT4f/0ABLQkOAA42CQ76AB8AQAEZHwCAAhIf/woEAx8AQAFRD0sABA8BABwOfgEMfAQP+gYQD0ABDx8AwA0KCzAGHwBAARkPBgQAD0ABVw+FBwIKQAEP+wAID0AMBAxRAQ//Dg8MBAELHgMf/1IlBwu6ASoAAAgAB48AD0ABEA5sBQ9+AhgO/QIPgAILApgACDUBDkYAD0ABFAu4AArXBw5aBQj6AQ5AAQ8nAAEPQAEQDvgDDkABD3IBDQ9rAAEPUCceD7EAAA+yAAgPZwACD0ABHA7hAA9AAVYf/1kGAg5AAQ8UBCYDEgAPRgADLwAAkAAVD/EAJw+kAgEPtgAJDwEA/////////////////////////////////////9JQ//////8=\",\"L\":25600}")
        .build();

    private static final ImageCompare.Param DATE_SETTING_MENU_ENG = ImageCompare.Param.builder()
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
    private static final ImageCompare.Param ENABLE_TIME_SYNC_CHS = ImageCompare.Param.builder()
        .area(Area.ofRect(1488, 210, 92, 82))
        .method(ImageCompare.Method.TM_CCOEFF)
        .preProcessor(
            com.duanxr.pgcon.core.preprocessing.config.NormalizePreProcessorConfig.builder()
                .enable(true)
                .build())
        .preProcessor(com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder()
            .enable(true)
            .inverse(false)
            .threshType(
                com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.OTSU)
            .build())
        .template(
            "{\"R\":82,\"C\":92,\"T\":0,\"D\":\"H/8BAP///////////8QuAAANAA4OAA9bACQvAAANAAMADgAEFQAJAQAPXAAiCzoADVUAD10AKA03AARKAA5bAA9cAC8EFgEICgAGEwAPVwAPD/QABgGxAAgsAA9cADMfADMCAA9cADwfAFwALQdiAAI5AAYUAR4AlgEOXAAP0QEZCqoADtEAD1wANg/fAiwMUAEPuAA/D1wAAw1FAw9wAToIXAAOKAIPgwIYBxEADJQDDlwAD94CJA9cAAQfAFwAQATTAA+YAzQGXAAEEAEOXAAPzAEaDwQFAgdqAA+YAyMOugIJ4AIf/1wAKA29AQpcAApTAA4UAQ8PARELFwEPCAUsHf9zAwo9Bw4kAA5cAA9aABIMDgAPPAMqCzUADzkECAu3Aw9bACMIPwAOvgQOXAAPEAEUDRYADlwADo0AD1gFGQVCAC///yMKSA8BAP///////////3RQ//////8=\",\"L\":7544}")
        .build();

    private static final ImageCompare.Param ENABLE_TIME_SYNC_ENG = ImageCompare.Param.builder()
        .area(Area.ofRect(1514, 222, 68, 56))
        .method(ImageCompare.Method.TM_CCORR)
        .preProcessor(
            com.duanxr.pgcon.core.preprocessing.config.NormalizePreProcessorConfig.builder()
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
    private static final OCR.Param TIME_DAY_CHS = OCR.Param.builder()
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
            .whitelist("0123456789")
            .build())
        .build();
    private static final OCR.Param TIME_DAY_ENG = OCR.Param.builder()
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
    private static final OCR.Param TIME_MONTH_CHS = OCR.Param.builder()
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
            .whitelist("0123456789")
            .build())
        .build();
    private static final OCR.Param TIME_MONTH_ENG = OCR.Param.builder()
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
    private static final OCR.Param TIME_YEAR_CHS = OCR.Param.builder()
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
            .whitelist("0123456789")
            .build())
        .build();
    private static final OCR.Param TIME_YEAR_ENG = OCR.Param.builder()
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
    private final int checkDateTimes = 3;
    private final long checkDelay = 150L;
    private final long checkTimeOut = 2000L;

    public PlusOneDaySubScript() {
      super(ScriptInfo.builder()
          .isLoop(false)
          .isHidden(true)
          .description("PlusOneDaySubScript")
          .build());
    }

    @Override
    public void execute() {
      try {
        init();
        goToMainMenu();
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

    private void reset() {
      press(HOME);
      sleep(1000);
      press(HOME);
      sleep(1000);
    }

    private void init() {
      press(HOME);
      sleep(1000);
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
          result -> result.getSimilarity() > NINTENDO_SWITCH_ENGINE_V_2_SCORE_THRESHOLD,
          () -> sleep(checkDelay), checkTimeOut, this::reset);
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
      until(() -> detect(DATE_CHANGE_MENU_CHS, DATE_CHANGE_MENU_ENG),
          resultList -> resultList.getMax().getSimilarity()
              > NINTENDO_SWITCH_ENGINE_V_2_SCORE_THRESHOLD,
          () -> sleep(checkDelay), checkTimeOut, this::reset);
    }

    private void checkIfDateIsSyncByInternet() {
      until(() -> detect(ENABLE_TIME_SYNC_CHS, ENABLE_TIME_SYNC_ENG),
          resultList -> resultList.getMax().getSimilarity()
              > NINTENDO_SWITCH_ENGINE_V_2_SCORE_THRESHOLD,
          () -> {
            press(A);
            sleep(500);
          }, checkTimeOut, this::reset);
    }

    private void toDateSetting() {
      press(D_BOTTOM);
      sleep(150);
      press(D_BOTTOM);
      sleep(250);
      press(A);
      sleep(150);
    }

    @SneakyThrows
    private void plusOneDay() {
      Result result = until(() -> detect(DATE_SETTING_MENU_CHS, DATE_SETTING_MENU_ENG),
          resultList -> resultList.getMax().getSimilarity()
              > NINTENDO_SWITCH_ENGINE_V_2_SCORE_THRESHOLD,
          () -> sleep(checkDelay), checkTimeOut, this::reset).getMax();
      if (result.getParam() == DATE_SETTING_MENU_CHS) {
        plusOneDayCHS();
      } else if (result.getParam() == DATE_SETTING_MENU_ENG) {
        plusOneDayENG();
      }

    }

    @SneakyThrows
    private void plusOneDayENG() {
      Future<Long> yearF = async(
          () -> detectAccurateNumber(TIME_YEAR_ENG, checkDateTimes, checkTimeOut, this::reset));
      Future<Long> monthF = async(
          () -> detectAccurateNumber(TIME_MONTH_ENG, checkDateTimes, checkTimeOut, this::reset));
      Future<Long> dayF = async(
          () -> detectAccurateNumber(TIME_DAY_ENG, checkDateTimes, checkTimeOut, this::reset));
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
        until(() -> detectAccurateNumber(TIME_DAY_ENG, 3, checkTimeOut, this::reset),
            result -> result == nextDay.getDayOfMonth(),
            () -> {
              press(D_TOP);
              sleep(checkDelay);
              sleep(checkDelay);
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

    @SneakyThrows
    private void plusOneDayCHS() {
      Future<Long> yearF = async(() ->
          detectAccurateNumber(TIME_YEAR_CHS, checkDateTimes, checkTimeOut, this::reset));
      Future<Long> monthF = async(() ->
          detectAccurateNumber(TIME_MONTH_CHS, checkDateTimes, checkTimeOut, this::reset));
      Future<Long> dayF = async(() ->
          detectAccurateNumber(TIME_DAY_CHS, checkDateTimes, checkTimeOut, this::reset));
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
        until(() -> detectAccurateNumber(TIME_DAY_CHS, checkDateTimes, checkTimeOut, this::reset),
            result -> result == nextDay.getDayOfMonth(),
            () -> {
              press(D_TOP);
              sleep(checkDelay);
              sleep(checkDelay);
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
      startGame();
    }
  }


}

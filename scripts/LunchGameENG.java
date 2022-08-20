import com.duanxr.pgcon.core.detect.api.ImageCompare;
import com.duanxr.pgcon.core.detect.api.ImageCompare.Result;
import com.duanxr.pgcon.core.model.Area;
import com.duanxr.pgcon.script.api.ScriptInfo;
import com.duanxr.pgcon.script.engine.PGConScriptEngineV1;

/**
 * @author 段然 2022/7/25
 */
public class LunchGameENG extends PGConScriptEngineV1<Object> {
  private static final ImageCompare.Param BACKUP = ImageCompare.Param.builder()
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
  private static final ImageCompare.Param SELECT_USER = ImageCompare.Param.builder()
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

  public LunchGameENG() {
    super(ScriptInfo.builder()
        .isLoop(false)
        .isHidden(true)
        .name("LunchGameENG")
        .build());
  }

  @Override
  public void execute() {
    press(A);
    sleep(1000);
    checkSelectUser();
    checkBackUp();
  }

  private void checkSelectUser() {
    Result detect = detect(SELECT_USER);
    if (detect.getSimilarity() > 0.9) {
      press(A);
      sleep(1000);
    }
  }

  private void checkBackUp() {
    Result detect = detect(BACKUP);
    if (detect.getSimilarity() > 0.9) {
      press(D_RIGHT);
      sleep(150);
      press(A);
      sleep(1000);
    }
  }
}

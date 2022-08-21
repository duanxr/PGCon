import com.duanxr.pgcon.core.detect.api.ImageCompare;
import com.duanxr.pgcon.core.detect.api.ImageCompare.Result;
import com.duanxr.pgcon.core.model.Area;
import com.duanxr.pgcon.script.api.ScriptInfo;
import com.duanxr.pgcon.script.engine.PGConScriptEngineV1;

/**
 * @author 段然 2022/7/25
 */
public class LunchGame extends PGConScriptEngineV1<Object> {

  private static final ImageCompare.Param BACKUP_ENG = ImageCompare.Param.builder()
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
  private static final ImageCompare.Param SELECT_USER_ENG = ImageCompare.Param.builder()
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

  private static final ImageCompare.Param BACKUP_CHS = ImageCompare.Param.builder()
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
  private static final ImageCompare.Param SELECT_USER_CHS = ImageCompare.Param.builder()
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

  public LunchGame() {
    super(ScriptInfo.builder()
        .isLoop(false)
        .isHidden(true)
        .description("LunchGame")
        .build());
  }

  @Override
  public void execute() {
    press(A);
    sleep(1000);
    checkSelectUser();
    sleep(1000);
    checkBackUp();
  }

  private void checkSelectUser() {
    Result detect = detectBest(SELECT_USER_ENG,SELECT_USER_CHS);
    if (detect.getSimilarity() > 0.9) {
      press(A);
      sleep(1000);
    }
  }

  private void checkBackUp() {
    Result detect = detectBest(BACKUP_ENG, BACKUP_CHS);
    if (detect.getSimilarity() > 0.9) {
      press(D_RIGHT);
      sleep(150);
      press(A);
      sleep(1000);
    }
  }
}

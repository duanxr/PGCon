import com.dooapp.fxform.annotation.FormFactory;
import com.duanxr.pgcon.core.detect.api.ImageCompare;
import com.duanxr.pgcon.core.detect.api.OCR;
import com.duanxr.pgcon.core.detect.api.OCR.ApiConfig;
import com.duanxr.pgcon.core.model.Area;
import com.duanxr.pgcon.exception.ResetScriptException;
import com.duanxr.pgcon.gui.fxform.annotation.ConfigLabel;
import com.duanxr.pgcon.gui.fxform.factory.ReadOnlyLabelFactory;
import com.duanxr.pgcon.output.action.StickAction;
import com.duanxr.pgcon.script.api.ScriptInfo;
import com.duanxr.pgcon.script.engine.PGConScriptEngineV1;
import java.util.concurrent.TimeUnit;
import javafx.beans.property.SimpleLongProperty;
import lombok.Data;

/**
 * @author DuanXR 2021/12/9
 */
public class MHRiseAutoPoints extends PGConScriptEngineV1<MHRiseAutoPoints.Config> {

  private static final ImageCompare.Param BACK_SELECTION = ImageCompare.Param.builder()
      .area(Area.ofRect(84, 984, 162, 40))
      .method(ImageCompare.Method.TM_CCOEFF)
      .preProcessor(
          com.duanxr.pgcon.core.preprocessing.config.ColorPickFilterPreProcessorConfig.builder()
              .enable(true)
              .targetColor(javafx.scene.paint.Color.color(1.0, 1.0, 1.0))
              .range(0.19072165641767042)
              .pickType(
                  com.duanxr.pgcon.core.preprocessing.config.ColorPickFilterPreProcessorConfig.PickType.CIEDE2000)
              .maskType(
                  com.duanxr.pgcon.core.preprocessing.config.ColorPickFilterPreProcessorConfig.MaskType.BLACK)
              .inverse(true)
              .build())
      .preProcessor(com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder()
          .enable(true)
          .binaryThreshold(0.0)
          .inverse(true)
          .threshType(
              com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.BINARY)
          .build())
      .template(
          "{\"R\":40,\"C\":162,\"T\":0,\"D\":\"H/8BAP////9eKgAADgABAQAPQwAcD1AABwVWAA4eAA4uAA8SAAYXAJsACKIAHwABAAMDRwASAJsADzYAASv//xQABXcAAm0AC1kAD6IAGBr/MAAPngABC9AAD6IAAAEjAA+iACoDJAAMowAO2wEHnAEPcgEGBEoACVQACGsBAyIADaIAD2UABw+9AAENtAAPogAPCNABDUIABmsBH/+iAAUO1gEOdgAPjwAAD6IANQWmAAyiAAsrAg5EAQ6IAg9dAQUfAKICACn/ABQAD6IALA4qAw+iACcKFgAMYQIOogAFJwAHogAdAIgCA2QDCKoEAx8EA7IDB6oABvwCBbMELwD/ogANH/+iABIfAIgCDAt2AS8AAF0BAwZ7AC8AAKIABAdQAAglAAVuABoAogAvAACiAA4eAIMCCP8BCXgBLv//ogAf/6IADwUwAA6IAg5AAQ/1AAEGJQAfAIACBA+iAAIGEQEPogAoH/+iAAsOKgMPogAWDZUHD6IACgjWAw6IBQ8qAwQNmQYCIgMPogAXL///ogAIHwCiAA8f/6IACwdvAw8qAwMPegUFCNwBD6IAJh8AogAgHgDMAw8qAwgKUwUPbgQPBgQEHf/aBA8qAxALVgAPbgQLBXwDDeAID6IACi8AAAsAAQW9AQ+iAAQFVAMPEwkNDUQBJwAAaQAeAIYBDh4CB9gBCQEGDu4EDIoELv8AVgEHMgMPGAsABvgBDWMCHwCTCwYKGwIFCgAIowMfAKcGBQnUBgcbAAViCS0AAMwDBVICCb8AD0QBCQgHBR8AWgABDikBD6IAHwhGAg6ZDA/CCg4DygAHagQL0wYK4AULlgMLWwcEUwIOBgAIEwkNBAIOXgMFYgMPEQICCq0ADqoECFoBCoADDZwBCbgHD40ABA+iAAwN9QEvAAA9AQEJBwUF9AYLXAAI8wAJgwIKZwYIWwgG2QAPGgwNCs4CDFACCxoACNoFCMUHD8AKBg59Ag8BAFgvAP8BAP//////J1D//////w==\",\"L\":6480}")
      .build();
  private static final ImageCompare.Param BUG_READY = ImageCompare.Param.builder()
      .area(Area.ofRect(964, 980, 70, 62))
      .method(ImageCompare.Method.TM_CCOEFF)
      .preProcessor(
          com.duanxr.pgcon.core.preprocessing.config.ColorPickFilterPreProcessorConfig.builder()
              .enable(true)
              .targetColor(javafx.scene.paint.Color.color(1.0, 1.0, 1.0))
              .range(0.2938144492699917)
              .pickType(
                  com.duanxr.pgcon.core.preprocessing.config.ColorPickFilterPreProcessorConfig.PickType.CIE94)
              .maskType(
                  com.duanxr.pgcon.core.preprocessing.config.ColorPickFilterPreProcessorConfig.MaskType.BLACK)
              .inverse(false)
              .build())
      .preProcessor(com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder()
          .enable(true)
          .binaryThreshold(0.0)
          .inverse(false)
          .threshType(
              com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.BINARY)
          .build())
      .template(
          "{\"R\":62,\"C\":70,\"T\":0,\"D\":\"H/8BAP83TwAAAABDADAPRgAtHgBFAA9CAB8PRQAuHwBFADAGQAAOWwEPUgEcDqEBD0QAXBn/CwEOiQAPRAAbD4sAKB8ARQA3BT8AD0UAKAcJAA7lAQ9FABgIkgAPRAAiD28CLQmJAB//igAULwAAVwECHwCLABoFIgAIyAAIRQAOhQMPvQQHCLgDCOEBDxUBEw9FAAEJgQAOPwMPRgAGC0UACEgAD+QBEg9FAAQFygAPKQITCUoBD44AAA9GAC4f/0UAFg5DAAyOAA8CBBsPjgAED3kDIgw3Ah//RgBaL///RgB8DuwBD0YANx8ARgAmL///jQAwCg8AD8MGJQ9GACwf//AIAQ9GAP9KD4sAvB//RQAzD4sAdQ+KADAf/wEA///WUP//////\",\"L\":4340}")
      .build();
  private static final ImageCompare.Param CANV = ImageCompare.Param.builder()
      .area(Area.ofRect(1216, 128, 160, 96))
      .method(ImageCompare.Method.TM_CCOEFF)
      .preProcessor(com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder()
          .enable(true)
          .inverse(false)
          .threshType(
              com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.OTSU)
          .build())
      .template(
          "{\"R\":96,\"C\":160,\"T\":0,\"D\":\"H/8BAP///////////////////xA/AAAAoQCND7EAiwyQAC8AAKIAex8AoQACD6AAjR8AHgIWD6AAjR8AoABdD+EBDQ/NAAAPoABbH/+gABkPoQBTLgAAdQAOeAAPQQFiAF4AAmIAAOwCBpsADqEAD0EBYwFdAAMFAArdAQ6IAg+gAAwvAACgAEsPGgEYHgDgAQ+gAEYPRgIDDuIBD1cEAgqgAB8APwFGCosAHwA2ABEGoAAO9wMPOAE5DqAADnYFD6AABAllAQ6gAA9nBigXAKAABmYAHwChABMeAAEADk8DD2cGLA5eAAfnAA6iBQ+eAAMPjwQHD/MFAA+gACwNtwEPoAAFD58AIA4VCQ82BhQBBAAL5QAvAACgABwORgYPoAA0ChkCAoQBDs0ED6AADA5/AA8HBw0OcgEPMAAGDikEDzMGFgiPBA6fAA9mBgkPFAIQB5gBCmMDDskCDxMIBA+9ARoMawEPkwQPBWsCD/wJJw4cAQ/HCREO2gAPoAALDy4JLA+9AxEPOAMhBW0BD3UHAg+aABcOoAAPoQA0BB0ADhAADyoEDA4CBQ/+BAcfAKAALwuHAw5WAQ+dBSkvAACWCQMPtQcSDNoCD4oIEg87BhsM6AMFywkPoAAZDpUAD/gDCA+dABgNSAAPoAAcARsADl4KD6EAFA5NAw+gACEPPwEMDpgFD2gEEApqBQ++DhgKQQYOPwEPOwAKCRYNDjMED1oOJA6gAArbBw80ChQLsgEOWgIPnwAkCiIELwAAGAoYDk8ID5kDAw+fACoM4gcfAKAAFgy5CATkCw5HAQ+fABAfAKAADh4AwAQO8w0PEQwKC2oEDqADDzkFAA9tCxMf/1gDAA4LBg+wDA0LSQAfAJsICQogBA+iBRAEKQQJqQEeAKAADk8ND9MOBQ/LAAkPqgIZBvQKDjoCDn8CD48OBQ8cDRYOnwAPIQgMBT8BBKAACYICD8oTHw75BA9ZCQ8PoAAKF//fAQUbCQiLAg+gADwHlwAI+A0PoAAJKwD/oAAeAKAAD6EANQ8jAAEPnwANBd8BDU0HDrcPDwoZEQ+fAA0OQgMOHgMPoAAIBzAAD1MSOwoaBQ8DCxEGNAEOkAQOkhMPqhY2CJIADxECEAoHAAv+CQ9JF0ENPgYfAB8IDgU6AAgTBw7fAQ+dACwOhAkPoAAcDh8ADzwBPQ6hAA+gABQf/6AATS///6AAhw5QAQ+kCxYEFAIPoABaH/9fFhQH6AMPoAAFDwEAaw9JDw4f/wEAbQ+gAKQf/58AjC///6AA/zIv//+hAIgPoACNDr8UDwEA//////////8NUP//////\",\"L\":15360}")
      .build();
  private static final ImageCompare.Param EXPLORE_MISSION = ImageCompare.Param.builder()
      .area(Area.ofRect(1372, 32, 128, 128))
      .method(ImageCompare.Method.TM_CCOEFF)
      .preProcessor(
          com.duanxr.pgcon.core.preprocessing.config.ColorPickFilterPreProcessorConfig.builder()
              .enable(true)
              .targetColor(javafx.scene.paint.Color.color(0.3019607961177826, 0.501960813999176,
                  0.3019607961177826))
              .range(0.30927824449771524)
              .pickType(
                  com.duanxr.pgcon.core.preprocessing.config.ColorPickFilterPreProcessorConfig.PickType.CIEDE2000)
              .maskType(
                  com.duanxr.pgcon.core.preprocessing.config.ColorPickFilterPreProcessorConfig.MaskType.BLACK)
              .inverse(true)
              .build())
      .preProcessor(com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder()
          .enable(true)
          .binaryThreshold(0.0)
          .inverse(false)
          .threshType(
              com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.BINARY)
          .build())
      .template(
          "{\"R\":128,\"C\":128,\"T\":0,\"D\":\"HwABAAQv/wABAC8f/wEAEA87AAcvAACDAFEPNwAEDoAAD4gARA8vAAQOgQAPhQBEDykABA6BAA+EAEQPJQAOLwAAhwBKDygBEw+FAAoPqAFSD30AagODAA6IAA9/AE0PCgEBD34AawMJAQ4oBA9/AFcW/54FDigED/4AUA8zAAAPgABtL///gABUDzQABw8xB1APMwACD4EAVA8yABIP+gRgDi0AD6AGPg+BAGYPMwESDyAFJQ7RAg+AADQf/zEADh8A1AISD3QADA8zAggP3gEHD34JMwkgAA+qAwIPKQEsD0MADA+AACIPWgMeBjoAD70ACh//gQARDiUBD1sDIA49AA+BABQv//+AAD4PXgAYL///gAA/DyUAEQ/TBQ8PgQAmD6UAEg9/ACgP3QEwAyYAD38ALC8AAN0CJSr//+gED4AAOA+qAhcPgABCD64CBQ9/AEIPkQcZD4AAbS8AAIAAwA85ABsPfwJPDzAADA+AAI4DJQAPfwEkD8ILQR8ADgs5LwAAfwBJD4AAEQ/+AGsP7AgGD4AAVw9/AGwfAH8AbA++EwEPgACxD7gAFg//ALMPRQAmD3oEZw46AA9/AGAOgAAPfwBsH//9ATEPgABaL///gABqD0IAAw98BlYPQQApH/+AAAIP/wBUDn0ADoAAD/8AJw96CVMPPgACD4AASi///4AAIS///64RJQ43AA9+ASEPBg8DD4EAIh//fgIMD6ETBg+EATQLGgAPIR4ZDwUDMS//AIAAJy///4MEFw+AAEMPwRYBD34AGQ7oIA58Bg+BACoP+AlHH/+UGhIPgABRD0MBBh8A+QpJDiQAD8EXAA9eIiMPgAAfDpQUDj8iDt8YD4AAOi///74jAg+cAQUPgQE8DvABDyocDg8BAUoPUwAND4AAWC///yoBAA+BADAPVgAED3oZEg+AAFYPfwAFD4EBMQ+AAEgOAQEPgAA4HwCAAGsvAACBADMPdhxND4AARB8AgAA1DikFD04MSA8nAAUP/wBGDjEFD0snDg/MDkkPKQARDw0REw8BAT8OcQgPShAHDwAURA6AAg/JET0PIwADDvMID4ABQQ5QCQ7HCQ8JCxEf/4USOg+ZAg8PBgs5ClAfDoAAD0UtPg68Ag+bEwMPiilXB9UADnYmD38AVQ9gGgUPXwIPL///fwBIDg4BDw0CAA+GEjIOgQAPiAADD1IuAg9EFDAv///oHx8OKwIPPxwdD2whKw6fAQ//ACYPHQIDD3ofNg8zABIPgAABDwcBMw8tABMPIwAAD4AiHg+AADwf/4AiMQ8sAAoPggIPD4AAVh8AfgBaD2wWAQ9/AGwPgQAsHwCAAIEfAK8AKQ96IzUPgQAYDx0GAg95JTkOLBMPShMDDw0bSQJIAA+DFx0OgQAPsAlCDk8ADx4vTwxdAA/KEi0OkgAPmgEwD4AABQ9nOQoPSQtPHwABAGMPfwBsHwB+AGsOKAMPgABhHwABAP////8FUAAAAAAA\",\"L\":16384}")
      .build();
  private static final ImageCompare.Param FOUND_RESOURCES = ImageCompare.Param.builder()
      .area(Area.ofRect(456, 254, 1078, 624))
      .method(ImageCompare.Method.TM_CCOEFF)
      .preProcessor(
          com.duanxr.pgcon.core.preprocessing.config.ColorPickFilterPreProcessorConfig.builder()
              .enable(true)
              .targetColor(javafx.scene.paint.Color.color(1.0, 1.0, 1.0))
              .range(0.2938144370827872)
              .pickType(
                  com.duanxr.pgcon.core.preprocessing.config.ColorPickFilterPreProcessorConfig.PickType.CIE94)
              .maskType(
                  com.duanxr.pgcon.core.preprocessing.config.ColorPickFilterPreProcessorConfig.MaskType.BLACK)
              .inverse(true)
              .build())
      .preProcessor(com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder()
          .enable(true)
          .binaryThreshold(0.0)
          .inverse(false)
          .threshType(
              com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.BINARY)
          .build())
      .template(
          "{\"R\":40,\"C\":38,\"T\":0,\"D\":\"HwABAN9P/////yMAEAIBAA8kABEOKAAOJQAOKAAOJAAPJwAKCBsACAwADyUABgYOAA8lAAoOTQAOJgAOJwAPJgAaCRUAH/8mABEu//9NAA8HAQIPJgAMB+8BDyYACz///wAmAAIPEwAdDx0BAB8AJgAeAhMACzAADyYABwwaAg7vAQ6FAQ8mAAQJggIOJQAOrAIP0wIWDiUADygAAC8AACcAAw8lABIO8QIPAQD8UAAAAAAA\",\"L\":1520}")
      .build();
  private static final OCR.Param GOT_POINTS = OCR.Param.builder()
      .area(Area.ofRect(348, 438, 234, 34))
      .preProcessor(
          com.duanxr.pgcon.core.preprocessing.config.ColorPickFilterPreProcessorConfig.builder()
              .enable(true)
              .targetColor(javafx.scene.paint.Color.color(1.0, 1.0, 1.0))
              .range(0.2010309344841821)
              .pickType(
                  com.duanxr.pgcon.core.preprocessing.config.ColorPickFilterPreProcessorConfig.PickType.CIEDE2000)
              .maskType(
                  com.duanxr.pgcon.core.preprocessing.config.ColorPickFilterPreProcessorConfig.MaskType.BLACK)
              .inverse(true)
              .build())
      .preProcessor(com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder()
          .enable(true)
          .binaryThreshold(0.0)
          .inverse(true)
          .threshType(
              com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.BINARY)
          .build())
      .apiConfig(ApiConfig.builder()
          .method(OCR.Method.NMU)
          .whitelist("0123456789")
          .build())
      .build();
  private static final ImageCompare.Param IN_THE_PRINCESS = ImageCompare.Param.builder()
      .area(Area.ofRect(30, 46, 78, 70))
      .method(ImageCompare.Method.TM_CCOEFF)
      .preProcessor(
          com.duanxr.pgcon.core.preprocessing.config.ColorPickFilterPreProcessorConfig.builder()
              .enable(true)
              .targetColor(javafx.scene.paint.Color.color(1.0, 1.0, 1.0))
              .range(0.12371126367491375)
              .pickType(
                  com.duanxr.pgcon.core.preprocessing.config.ColorPickFilterPreProcessorConfig.PickType.CIEDE2000)
              .maskType(
                  com.duanxr.pgcon.core.preprocessing.config.ColorPickFilterPreProcessorConfig.MaskType.BLACK)
              .inverse(true)
              .build())
      .preProcessor(com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder()
          .enable(true)
          .binaryThreshold(0.0)
          .inverse(false)
          .threshType(
              com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.BINARY)
          .build())
      .template(
          "{\"R\":70,\"C\":78,\"T\":0,\"D\":\"HwABAP///70v//9NADov//9NADov//9OADsf/0wAOQ9PADQe/08AD04Aawc+AAxOAA5YAA95ARsHTQAHCwAPTgAsC1kAD08AKgr2AA98ASQEDgAfAE4AOAsVAA9OACYf/8kCNA45AA9MACcPnwMBD8QCNw9LBCgv//9BAAAMWgAPHQUhCrgCDj8FDx0FHQZTAw5BAQ/MASAPEQAGH/8dBSIPKgInB4AEH//bAQUO8gQPTgAzD+oAMA6NBQ+GARwKYQIO5QQPIgIdDyMCAA++AiQJVgAPTgAeB3cIDxcDAg6ZCA+BCBwPrgYlHwCSBA0Pfgg2DuEEDxgGKQ4BBw9OADIPUAcqDD8FL///TwcrDJ8HDzoILg60AA9OACIJLQEPhwEmC3kBDwkDLA9OAAYvAABOADIPRgt+D+ILOw9+DD0vAABNADpPAAD/AAEA////uFAAAAAAAA==\",\"L\":5460}")
      .build();
  private static final ImageCompare.Param IN_THE_REST = ImageCompare.Param.builder()
      .area(Area.ofRect(32, 46, 68, 66))
      .method(ImageCompare.Method.TM_CCOEFF)
      .preProcessor(
          com.duanxr.pgcon.core.preprocessing.config.ColorPickFilterPreProcessorConfig.builder()
              .enable(true)
              .targetColor(javafx.scene.paint.Color.color(1.0, 1.0, 1.0))
              .range(0.1752576296330634)
              .pickType(
                  com.duanxr.pgcon.core.preprocessing.config.ColorPickFilterPreProcessorConfig.PickType.CIEDE2000)
              .maskType(
                  com.duanxr.pgcon.core.preprocessing.config.ColorPickFilterPreProcessorConfig.MaskType.BLACK)
              .inverse(true)
              .build())
      .preProcessor(com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder()
          .enable(true)
          .inverse(false)
          .threshType(
              com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.OTSU)
          .build())
      .template(
          "{\"R\":66,\"C\":68,\"T\":0,\"D\":\"HwABAP//txT/AQAPQgAvDkUAD0IAFw9FAC0v//9FAC8DOQAFBQAv//9DACUACQAFlAAPRAAmCNkAD0QAZh//RAApCo4BDpsBD9sBHw9EAGgfAEMAMC8A/6kCNQ92AzwDEAAPOgAdCjYDDpEAD3wAFQ/ZAAIPNQQiDhsBDzUEGA9MBB8EIQAEBQAJ7AIOFwAPRAAHAzQEHQBZASEAADsADkUAD/ADEQ4YAA9EAB4f/0QAHx//RAAECmABD5YBDQsNAA/DAwIP8AMdD0QAAQ/wAxoPRAA0DwgEAA+oAksPdAMzCS4ADxcABQ8BABEPbwQAD/YHMg6CAA9CABAPRgADD0EALg9HABYv//9FAA0PQgAvD0YADi///0UAEw9EAJ0OHAAPRAAaDyEAKA7jAg8HAyIPiQISL/8AAQD//+FQAAAAAAA=\",\"L\":4488}")
      .build();
  private static final ImageCompare.Param MISSION_MASTER = ImageCompare.Param.builder()
      .area(Area.ofRect(108, 948, 56, 42))
      .method(ImageCompare.Method.TM_CCOEFF)
      .preProcessor(
          com.duanxr.pgcon.core.preprocessing.config.ColorPickFilterPreProcessorConfig.builder()
              .enable(true)
              .targetColor(javafx.scene.paint.Color.color(1.0, 1.0, 1.0))
              .range(0.12371126367491375)
              .pickType(
                  com.duanxr.pgcon.core.preprocessing.config.ColorPickFilterPreProcessorConfig.PickType.CIEDE2000)
              .maskType(
                  com.duanxr.pgcon.core.preprocessing.config.ColorPickFilterPreProcessorConfig.MaskType.BLACK)
              .inverse(true)
              .build())
      .preProcessor(com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder()
          .enable(true)
          .binaryThreshold(0.0)
          .inverse(false)
          .threshType(
              com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.BINARY)
          .build())
      .template(
          "{\"R\":42,\"C\":56,\"T\":0,\"D\":\"HwABAP/xL///FgADH/84ACYa/wEADzgADij//3AADnoADzgACQYXAA+xAAYPOAAgAiEAH/84ABgf/wEAAwo4AC//ABcBBwEbAA9wAAQPTwEHD6gACwaxAA84ACUfADgANg/fAAEPOAATDRQABTACDzgADC8A/6gAHwrkAQ9QAQgOzAAPOAAGH/+IAQcv//9MAgEJFAMOqAAPvQIGDzgADS7//8UBDjgADKgACvEBH/9SAAAo/wDpAA4oAg04AgT3AA9IAwEPOgIIC2AALwAADgAFCxUADgUBDykFIh8AAQD/6lAAAAAAAA==\",\"L\":2352}")
      .build();
  private static final ImageCompare.Param POINT_UP = ImageCompare.Param.builder()
      .area(Area.ofRect(1228, 306, 52, 36))
      .method(ImageCompare.Method.TM_CCOEFF)
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
          "{\"R\":36,\"C\":52,\"T\":0,\"D\":\"H/8BAJAvAAAzACAGNAAPPgASGwA0AC8AAGUAEgcoAA4zAA80AAsCCgAPNAAiBTgALwAAJAEEDCcABg4ABQsADX4ACjQACkMBBBsADLQAAkgAD6oBBgs4AAI7AAVrAQWLAA/uAQIPNAALDIcABO8BClQADhgACbABD+oAAAuXAQMqAQ0YAh8AWgACBVAAH/+OAAAMfAAI0AAOUAAPbQEBHv8GAgzhAQ44AAgrAguPAA0bAQv7AQ25AB8AZgIBH/80AAQNmwEPpgMHBQsBDzQAGB//NAAXDugCD2cAGB//zwAXBvkBDE4BD+YDEQhPBA8zABUCLQAPJwUnDhwFDwEA/ylQ//////8=\",\"L\":1872}")
      .build();
  private static final ImageCompare.Param POINT_UP_FOOD = ImageCompare.Param.builder()
      .area(Area.ofRect(608, 218, 98, 90))
      .method(ImageCompare.Method.TM_CCOEFF)
      .preProcessor(
          com.duanxr.pgcon.core.preprocessing.config.ColorPickFilterPreProcessorConfig.builder()
              .enable(true)
              .targetColor(
                  javafx.scene.paint.Color.color(0.0, 0.20000000298023224, 0.20000000298023224))
              .range(0.5257732301409132)
              .pickType(
                  com.duanxr.pgcon.core.preprocessing.config.ColorPickFilterPreProcessorConfig.PickType.CIEDE2000)
              .maskType(
                  com.duanxr.pgcon.core.preprocessing.config.ColorPickFilterPreProcessorConfig.MaskType.BLACK)
              .inverse(true)
              .build())
      .preProcessor(com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder()
          .enable(true)
          .binaryThreshold(0.0)
          .inverse(true)
          .threshType(
              com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.BINARY)
          .build())
      .template(
          "{\"R\":90,\"C\":98,\"T\":0,\"D\":\"LwD/AQD//2gPewJAHwABAAwPXwBMD2QAER8AXwAMD2QALx8AYAAJD2QAMS8AAGAABw9kADMvAABgAAUPZAA1LwAAYAADD2QAOB8AYAABD2QAOh4AYQAPYwA8HgBhAA9jADsuAABhAA9jADsuAABhAA9jAD0OYQAPYwA7LwAAYQA/LwAAYgBbDiQBD2MAMg9iAGQvAABjADkPYQBOLwAAYgBgLwAAYwBNDygADA9iAEYO5QAPnwQoDucAD0kBLQ4+AA8NAj4PYQAMDzMDNQOYBA+VA0QPOwAJD0QJPg5hAA9jADwOYAAPYwAGD2IAlC///2MACQ9iAE8f/2IALx//YgBuH/9iAIIOYQAPYgCrL///YwA4DjIADrsOD8UANQ9iAE8e/30PD2IAPR7/3g8PiQE7AjQAH/9iAEoPAQAcD2IATy///2IATw9pABMf/2MAMA9hAE4OTAMPYgA/Dk0DD2IAPy///7oIBR//AQA5D2IAxR//YgD/Ei///2MARw9iAFQf/2IASQ9hAE4OxhUPAQD//////////1pQ//////8=\",\"L\":8820}")
      .build();
  private static final ImageCompare.Param RUN_END = ImageCompare.Param.builder()
      .area(Area.ofRect(24, 28, 38, 40))
      .method(ImageCompare.Method.TM_CCOEFF)
      .preProcessor(com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder()
          .enable(true)
          .inverse(true)
          .threshType(
              com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.OTSU)
          .build())
      .template(
          "{\"R\":40,\"C\":38,\"T\":0,\"D\":\"H/8BAP9wLwAAJgATHwAmAI4FDAANOwAPJgAfBQ0ADyYAEg6sAA8mACADDgAPJgASDh0BDDABDyYAFR4AAQIPJgABHwAmAA8f/yYABgUBAAQQAB//JgAkBi8ACwgADwEA/5FQ//////8=\",\"L\":1520}")
      .build();
  private static final ImageCompare.Param TRAVEL_READ_CHS = ImageCompare.Param.builder()
      .area(Area.ofRect(832, 524, 236, 52))
      .method(ImageCompare.Method.TM_CCOEFF)
      .preProcessor(com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder()
          .enable(true)
          .inverse(false)
          .threshType(
              com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.OTSU)
          .build())
      .template(
          "{\"R\":52,\"C\":236,\"T\":0,\"D\":\"HwABAP////////////////93Ev8GAB//LgAUD20AMQEJABT/DQAf/wEAAgcbAAMJAA9TAAUIGwAPSgAGBB4AD0wAAghvAAwuAAwLAA/pABUOhwAP6wAGBJUACwsACQQBDQcBD0sABA/sAAIPMwAPD+wAAA78AA/sABsP2AEGDusADw8AAAq7AA/sAB4f/+wADgRkAA7IAA/sACEbANwAAQsABvIBC+sAD+wAChcASAACGQAOxAIP7AAVBgcADtwCDu4AD+wAFQgfAB8AjAMAD68CBAh1AAiOAA+FAgEfAMUAAw4xAApHAAnYAQ5pAQ/sACYOFQAK7AAOXwALUgAPugAOCewADroAD0EDAwtVARkAXAIP7AAjD9sCAg8JAgQPNgAPDikCDEgAC9MAHv8gAA4FAQ/sADQf/7QECysAALsBC4wAHgCmBg74AA45AQ/sACUfAOwAKA/WAAQNlAAOLwAFfQADKgUPDAIGH//xBhoPBwEADOgDDuwAD3QGGAxDAw9vAAAP7AAFC4IAD9UACQwzBi///x8BFA+YBwEfAJ0EGQ5NBAkSAB//7QALAocJD+wACh8A7AAFDpYAD2YAAQfXAw/JBwEP1QEXCHwAC6MJBvAFD3QBCAeTACoAACcBCbkBCaUBDnAKD84EBR8AdAYBDusAD+wAGAI5CAKjAQOnAAWiAA4rBA74Ag50AQXAAA69Bw7sAA/YAQEPYAcJD8ICGwl6AS///z0KEi8A/+wABwiTAA//BAEP7AAmC9gBH//rABkMxgcPKQseGgDxBQnyCQkKBg/1AgYP6AwEH/8IBwgOrwgP7AAeDzwAAR//TwQDLwAARgcCCm0GD+wAEg0KAh//pgsODPAABhsAD+sAEA/zCQIPGQoBLwAAwQIBK///IAIMmAIOhwUfAOwACx//egcPCJIADrEAD+wAFR8AOwACD9gBAC8AAN0CEwwwAQicBC8AAOwAGAroAAv9Ag/TDRYOsAMP2AEEDYYGD+sABw+IBRcP3wEAD34AAhv/xg4JVQAPSQgbHwCJAAkO7AAEzwIfAOwADA/xCRUP2AERD28BAg6HBw+ZBBMOHAMP7AADH/83CQMNKgUPtgwhD04NAw6IAQ+cBBsPYQEFD+wAFg9VAAQOQAsP7AAKCW8AD+wAEQ5LBQ8BABAPaQoAL///nAQJBTUAD/YEBw1zAwcZCgdpAw4xEg4ZBQ8dEQcO6wAPAQD////////////////rUAAAAAAA\",\"L\":12272}")
      .build();
  private static final ImageCompare.Param TRAVEL_READ_ENG = ImageCompare.Param.builder()
      .area(Area.ofRect(818, 534, 282, 40))
      .method(ImageCompare.Method.TM_CCOEFF)
      .preProcessor(com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder()
          .enable(true)
          .inverse(false)
          .threshType(
              com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.OTSU)
          .build())
      .template(
          "{\"R\":40,\"C\":282,\"T\":0,\"D\":\"HwABAP/////////FGv8BAA9BACEPcgAfDg4AD9oARw8aAWYKNQAO6AAOEwEPGgFUL///GwEtDxoBxALOAA4bAQ/gACYPGgFeHwAzAkgO2QAPGgFRL///OgAAH/8aARcPbwATH/8OABYPbQAEHwAaATgEhQIGaAMIYAAGigED3QQIDQELFwAKngMFEQUEVwAFKQAPLwUFCUQACo0ABQ4ADnIAC5cADxoBNwx1AAXOAASCAQjcAAqvAi////4ABArbAAZYAAV0AQ8vAAALFQAf/10AAg8iAAMDHAEfABoBRRz/jQAFKAAPGgACBz0ACrwADzQCCAkaAR4ASgEPGgEKCqoAD3QAAw5oBA8aAS4MqQMJIQIJQQINiQADSwQNawMPHQAEDl8AB2YBCY0BCpoBBX8GKwAAowEM4AMfABoBOR0AwwQPGgEaCk4AD5wGBg0aAQ+SAAYOzgMOYwEPGgEBH/8aAT0JFwALmAEbAMYCDxoBJw9TAAAPLgcKDnMADNcCCmgADjMCDxoBNQ3oAw8aATwI0QAOsQoPjwUGC1sDCpcDChoBDhkBDxoBMgzrBQ3NBw8aATkONwIKIgQf/xoBJR8AGgFECtAACVkDBwIED04DKQwVBB//kgABC20FDxoBXR8APQoEDosJDxoBFw6cBg9oBAYMMAAKogEODwYPggUIDxoBRAzfBARkBwfTAQ5QBw+2Bx0HEAAPGgEFCMoHGv8NAA+cBgcLdQAOTgMPaAQ9BjUCDpwFHv8QBAzNCQ4dAA+2BwEf/+oJBw9oBBULRQAOtAcPGgEqDRMFCX4LCxoBHgAaAQ9sBgUGdgIf/5wGEg5mAAyRAA8aAQMPzgg7AuYED/QGAQvrAi4AAC4FC30AClkBD7UDAghbBQ9SDhAOVwENkgAEFQADMQwMMwUPAQDkDwcBCw8aAf////9DDt8ZDwEA/////////////lAAAAAAAA==\",\"L\":11280}")
      .build();
  private static final ImageCompare.Param USE_STICK = ImageCompare.Param.builder()
      .area(Area.ofRect(100, 220, 38, 44))
      .method(ImageCompare.Method.TM_CCOEFF)
      .preProcessor(com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder()
          .enable(true)
          .inverse(false)
          .threshType(
              com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.OTSU)
          .build())
      .template(
          "{\"R\":44,\"C\":38,\"T\":0,\"D\":\"FP8BAB8AAQAABR0ADyYAGy8AACcACgILAB8AJgALCCUABwkACScAAy0ABSUADQoAHQCUAA8mAAIFKAAfACYAMQewAA8mAAo/AP8AJgA3HwAmAP8tAR4AH/8mAAkO4wIPJgD/cx//JgARDZEDDyUABC///yYADQwSAA8lAAYOdQAPlAADChcAD5QAAQH2BA0nAA5vAA9+BRQf/8oFFB8AygUSIwD/lgUf/yYAAAEEAB//AQAOUP//////\",\"L\":1672}")
      .build();
  private int missingResourcesCount = 0;
  private boolean pointUp0 = false;
  private boolean pointUp1 = false;
  private long start;

  public MHRiseAutoPoints() {
    super(ScriptInfo.<MHRiseAutoPoints.Config>builder()
        .config(new Config())
        .isLoop(true)
        .description("MHR Auto Points(CHS.Ver & ENG.Ver)")
        .build());
  }

  @Override
  public void execute() {
    launchGame();
    walkToPrincess();
    getMission();
    walkToCookCat();
    getFood();
    letsGo();
    get();
    backToCity();
  }

  private void letsGo() {
    sleep(200);
    press(ZR);
    sleep(350);
    press(A);
    sleep(10000);
  }

  private void backToCity() {
    press(PLUS);
    sleep(350);

    press(D_RIGHT);
    sleep(150);

    until(() -> detect(BACK_SELECTION),
        result -> result.getSimilarity() > 0.7,
        () -> {
          press(D_BOTTOM);
          sleep(350);
        },
        10000L, this::reset);

    press(A);
    sleep(150);

    press(D_LEFT);
    sleep(150);

    press(A);
    sleep(10000);

    until(() -> detect(RUN_END),
        result -> result.getSimilarity() > 0.7,
        () -> {
          press(A);
          sleep(150);
        }, 60000L, this::reset);

    addPointsSum();
  }

  private void addPointsSum() {
    try {
      press(A);
      sleep(1000);
      Long accurateLong = detectAccurateLong(GOT_POINTS, 3, 3000L, this::reset);
      config.getPoints().set(config.getPoints().get() + accurateLong);
      long millis = System.currentTimeMillis() - start;
      String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
          TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
          TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
      info("Got {} points cost {}", accurateLong, hms);
    } catch (Exception e) {
      warn("cannot get points", e);
    }
  }

  private void getFood() {
    press(A);
    until(() -> detect(IN_THE_REST),
        result -> result.getSimilarity() > 0.7,
        () -> sleep(50), 3000L, this::reset);
    press(A);
    sleep(350);
    press(A);
    sleep(350);
    press(A);
    sleep(850);
    if (detect(USE_STICK).getSimilarity() < 0.7) {
      press(MINUS);
      sleep(350);
    }
    pointUp1 = detect(POINT_UP_FOOD).getSimilarity() > 0.7;
    if (pointUp1 && pointUp0) {
      press(X);
      sleep(350);
    }
    press(A);
    sleep(350);
    press(D_BOTTOM);
    sleep(350);
    press(A);
    sleep(350);
    press(D_BOTTOM);
    sleep(350);
    press(A);
    sleep(350);
    press(A);
    sleep(350);
    press(A);
    sleep(350);
    until(() -> detect(BUG_READY),
        result -> result.getSimilarity() > 0.7,
        () -> {
          press(B);
          sleep(150);
        }, 10000L, this::reset);
  }

  private void getMission() {
    sleep(350);
    press(A);
    until(() -> detect(IN_THE_PRINCESS),
        result -> result.getSimilarity() > 0.7,
        () -> sleep(50), 3000L, this::reset);

    until(() -> detect(MISSION_MASTER),
        result -> result.getSimilarity() > 0.7,
        () -> {
          press(D_BOTTOM);
          sleep(350);
        }, 3000L, this::reset);

    press(A);
    sleep(350);
    press(D_TOP);
    sleep(350);
    press(A);
    sleep(350);
    until(() -> detect(EXPLORE_MISSION),
        result -> result.getSimilarity() > 0.7,
        () -> sleep(50), 3000L, this::reset);
    press(R);
    sleep(350);
    until(() -> detect(CANV),
        result -> result.getSimilarity() > 0.7,
        () -> {
          press(D_TOP);
          sleep(350);
        }, 3000L, this::reset);
    pointUp0 = detect(POINT_UP).getSimilarity() > 0.7;
    press(A);
    sleep(350);
    press(D_BOTTOM);
    sleep(350);
    press(A);
    sleep(350);
    press(A);
    sleep(2000);
  }

  private void walkToCookCat() {
    turn(90);
    run(2600);
  }

  private void run(int millis) {
    hold(R);
    walk(millis);
    release(R);
  }

  private void walkToPrincess() {
    turn(8);
    run(4500);
  }

  private void launchGame() {
    script("LunchGame");
    until(() -> detect(BUG_READY),
        result -> result.getSimilarity() > 0.7,
        () -> {
          press(A);
          sleep(550);
        });
    sleep(1000);
  }

  private void get() {
    int round = initRun();
    for (int i = 0; i < round; i++) {
      missingResourcesCount = 0;
      tryGet(this::get00);//done!
      tryGet(this::get01);//done!
      tryGet(this::get04);//done!
      tryGet(this::get09);//done!
      tryGet(this::get12);//done!
      tryGet(this::get07);//done!
      tryGet(this::get03);//done!
    }
  }

  private int initRun() {
    checkBugReady();
    sleep(4000);
    start = System.currentTimeMillis();
    if (pointUp0 && pointUp1) {
      info("points up detected, run 5 rounds");
    } else {
      info("points up not detected, run 1 round");
    }
    return pointUp0 && pointUp1 ? 5 : 1;
  }

  private void get12() {
    fastTravel(2);

    hold(StickAction.L_RIGHT);
    sleep(500);
    release(StickAction.L_RIGHT);

    sleep(350);

    turn(-40);

    bugJump(1100);
    sleep(1500);
    turn(20);

    bugJump(500);

    sleep(8000);

    checkResources();

    turn(-64);

    bugJump(900);

    sleep(2500);
    walk(1000);

    getResources();

    get13();
  }

  private void get13() {
    turn(-60);
    walk(1200);
    turn(20);
    run(6800);
    sleep(350);
    turn(45);
    walk(1200);
    getResources();

    get14();
  }

  private void get14() {
    turn(-24);
    run(6000);
    bugJump(1200);
    run(2550);
    getResources();
  }

  private void tryGet(Runnable runnable) {
    try {
      runnable.run();
    } catch (ResetScriptException e) {
      error("not found!", e);
      if (e.getRunnable() != null) {
        e.getRunnable().run();
      }
      missingResourcesCount++;
      if (missingResourcesCount >= 3) {
        throw new ResetScriptException("Script is not working, restart the game!").setRunnable(
            this::reset);
      }
    }
  }

  private void get09() {
    fastTravel(2);

    hold(StickAction.L_RIGHT);
    sleep(500);
    release(StickAction.L_RIGHT);

    sleep(350);

    turn(-40);

    bugJump(1200);
    sleep(350);
    bugJump(1200);

    sleep(2000);

    turn(65);

    run(1700);
    sleep(350);
    getResources();

    get10();
  }

  private void get10() {
    checkBugReady();
    turn(-45);
    bugJump(500);
    bugJump(500);
    run(4500);
    sleep(350);
    turn(-85);
    run(4400);
    turn(-85);
    walk(600);
    getResources();
  }

  private void fastTravel(int camp) {
    press(MINUS);
    sleep(850);
    press(A);
    sleep(200);
    for (int i = 0; i < camp; i++) {
      press(D_BOTTOM);
      sleep(200);
    }
    press(A);
    sleep(200);
    until(() -> detectBest(TRAVEL_READ_CHS, TRAVEL_READ_ENG),
        result -> result.getSimilarity() > 0.7,
        () -> sleep(50), 2000L, this::quitFastTravel);
    press(A);
    sleep(200);
    checkBugReady();
    sleep(1000);
  }

  private void quitFastTravel() {
    until(() -> detect(BUG_READY),
        result -> result.getSimilarity() > 0.7,
        () -> {
          press(B);
          sleep(150);
        });
  }

  private void get00() {
    fastTravel(0);
    turn(150);
    bugJump(1200);
    bugJump(1200);
    run(8100);
    turn(-109);
    run(1950);

    getResources();
  }

  private void get07() {
    fastTravel(2);

    hold(StickAction.L_LEFT);
    sleep(500);
    release(StickAction.L_LEFT);

    sleep(350);

    turn(90);

    bugJump(1200);

    sleep(2500);
    turn(-64);

    bugJump(1200);
    sleep(2500);

    walk(1500);
    sleep(150);

    getResources();

    //get08();
  }

  private void get08() {
    checkBugReady();

    turn(5);

    bugJump(1000);

    sleep(500);

    bugJump(1000);

    sleep(2500);

    checkResources();

    turn(35);

    checkBugReady();

    bugJump(1200);

    sleep(500);

    bugJump(2500);

    turn(145);

    walk(4900);

    getResources();


  }

  private void checkBugReady() {
    until(() -> detect(BUG_READY),
        result -> result.getSimilarity() > 0.7,
        () -> sleep(200), 200000L, this::reset);
    press(L);
    sleep(350);
  }

  private void turn(double degrees) {
    sleep(150);
    components.getControllerService().getProtocol().set(true, degrees);
    sleep(100);
    press(L_CENTER);
    sleep(350);
    resetDirection();
  }

  private void bugJump(int jumpDistance) {
    hold(ZL);
    press(X);
    sleep(jumpDistance);
    press(B);
    release(ZL);
  }

  private void checkResources() {
    until(() -> detect(FOUND_RESOURCES),
        result -> result.getSimilarity() > 0.7,
        () -> sleep(50), 2000L);
  }

  private void walk(int millis) {
    hold(StickAction.L_TOP);
    sleep(millis);
    release(StickAction.L_TOP);
  }

  private void getResources() {
    checkResources();
    sleep(800);
    press(A);
    sleep(5000);
    missingResourcesCount = 0;
  }

  private void reset() {
    press(HOME);
    sleep(3000);
    press(X);
    sleep(300);
    press(A);
    sleep(5000);
  }

  private void resetDirection() {
    sleep(150);
    press(L);
    sleep(350);
  }

  private void get04() {
    fastTravel(2);

    turn(-198);
    sleep(350);
    run(5300);
    sleep(350);
    walk(3400);

    getResources();

    get05();
  }

  private void get05() {
    sleep(350);
    turn(85);
    sleep(350);
    bugJump(1200);
    sleep(550);
    bugJump(1200);
    sleep(1550);

    turn(-90);
    run(500);
    checkResources();
    sleep(150);

    turn(75);
    run(3500);
    sleep(150);

    turn(20);
    bugJump(600);
    run(4400);

    getResources();

    get06();
  }

  private void get06() {
    turn(68);
    run(10700);

    turn(82);
    run(650);

    getResources();
  }

  private void turn0(int millis) {
    StickAction stickAction = millis < 0 ? StickAction.R_LEFT : StickAction.R_RIGHT;
    hold(stickAction);
    sleep(Math.abs(millis));
    release(stickAction);
  }

  private void get01() {
    fastTravel(1);

    turn(194);
    run(1450);

    getResources();

    get02();

  }

  private void get02() {

    turn(118);
    run(500);
    bugJump(1200);
    sleep(1200);
    getResources();

    //get11(); REPEATED
  }


  private void get03() {
    fastTravel(1);

    turn(194);
    run(3000);

    bugJump(1200);

    sleep(1600);
    turn(-10);
    run(5000);
    bugJump(1200);
    sleep(2100);
    walk(3000);

    getResources();
  }

  @Data
  public static class Config {

    @ConfigLabel("Got Points")
    @FormFactory(ReadOnlyLabelFactory.class)
    private SimpleLongProperty points = new SimpleLongProperty(0L);

  }

}

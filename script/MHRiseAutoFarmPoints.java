import com.dooapp.fxform.annotation.FormFactory;
import com.duanxr.pgcon.core.detect.api.ImageCompare;
import com.duanxr.pgcon.core.detect.api.OCR;
import com.duanxr.pgcon.core.detect.api.OCR.ApiConfig;
import com.duanxr.pgcon.core.model.Area;
import com.duanxr.pgcon.exception.ResetScriptException;
import com.duanxr.pgcon.gui.fxform.annotation.ConfigLabel;
import com.duanxr.pgcon.gui.fxform.factory.ReadOnlyLabelFactory;
import com.duanxr.pgcon.output.action.NintendoSwitchStandardStick;
import com.duanxr.pgcon.script.api.ScriptInfo;
import java.util.concurrent.TimeUnit;
import javafx.beans.property.SimpleLongProperty;
import lombok.Data;

/**
 * @author DuanXR 2021/12/9
 */
public class MHRiseAutoFarmPoints extends MonsterHunterRiseEngine<MHRiseAutoFarmPoints.Config> {

  private static final ImageCompare.Param COMPLETE_MISSION_SELECTED = ImageCompare.Param.builder()
      .area(Area.ofRect(84, 984, 162, 40)).method(ImageCompare.Method.TM_CCOEFF).preProcessor(
          com.duanxr.pgcon.core.preprocessing.config.ColorPickFilterPreProcessorConfig.builder()
              .enable(true).targetColor(javafx.scene.paint.Color.color(1.0, 1.0, 1.0))
              .range(0.19072165641767042).pickType(
                  com.duanxr.pgcon.core.preprocessing.config.ColorPickFilterPreProcessorConfig.PickType.CIEDE2000)
              .maskType(
                  com.duanxr.pgcon.core.preprocessing.config.ColorPickFilterPreProcessorConfig.MaskType.BLACK)
              .inverse(true).build()).preProcessor(
          com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder().enable(true)
              .binaryThreshold(0.0).inverse(true).threshType(
                  com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.BINARY)
              .build()).template(
          "{\"R\":40,\"C\":162,\"T\":0,\"D\":\"H/8BAP////9eKgAADgABAQAPQwAcD1AABwVWAA4eAA4uAA8SAAYXAJsACKIAHwABAAMDRwASAJsADzYAASv//xQABXcAAm0AC1kAD6IAGBr/MAAPngABC9AAD6IAAAEjAA+iACoDJAAMowAO2wEHnAEPcgEGBEoACVQACGsBAyIADaIAD2UABw+9AAENtAAPogAPCNABDUIABmsBH/+iAAUO1gEOdgAPjwAAD6IANQWmAAyiAAsrAg5EAQ6IAg9dAQUfAKICACn/ABQAD6IALA4qAw+iACcKFgAMYQIOogAFJwAHogAdAIgCA2QDCKoEAx8EA7IDB6oABvwCBbMELwD/ogANH/+iABIfAIgCDAt2AS8AAF0BAwZ7AC8AAKIABAdQAAglAAVuABoAogAvAACiAA4eAIMCCP8BCXgBLv//ogAf/6IADwUwAA6IAg5AAQ/1AAEGJQAfAIACBA+iAAIGEQEPogAoH/+iAAsOKgMPogAWDZUHD6IACgjWAw6IBQ8qAwQNmQYCIgMPogAXL///ogAIHwCiAA8f/6IACwdvAw8qAwMPegUFCNwBD6IAJh8AogAgHgDMAw8qAwgKUwUPbgQPBgQEHf/aBA8qAxALVgAPbgQLBXwDDeAID6IACi8AAAsAAQW9AQ+iAAQFVAMPEwkNDUQBJwAAaQAeAIYBDh4CB9gBCQEGDu4EDIoELv8AVgEHMgMPGAsABvgBDWMCHwCTCwYKGwIFCgAIowMfAKcGBQnUBgcbAAViCS0AAMwDBVICCb8AD0QBCQgHBR8AWgABDikBD6IAHwhGAg6ZDA/CCg4DygAHagQL0wYK4AULlgMLWwcEUwIOBgAIEwkNBAIOXgMFYgMPEQICCq0ADqoECFoBCoADDZwBCbgHD40ABA+iAAwN9QEvAAA9AQEJBwUF9AYLXAAI8wAJgwIKZwYIWwgG2QAPGgwNCs4CDFACCxoACNoFCMUHD8AKBg59Ag8BAFgvAP8BAP//////J1D//////w==\",\"L\":6480}")
      .build();
  private static final ImageCompare.Param EXPLORE_MISSION_SELECTED = ImageCompare.Param.builder()
      .area(Area.ofRect(1372, 32, 128, 128)).method(ImageCompare.Method.TM_CCOEFF).preProcessor(
          com.duanxr.pgcon.core.preprocessing.config.ColorPickFilterPreProcessorConfig.builder()
              .enable(true).targetColor(
                  javafx.scene.paint.Color.color(0.3019607961177826, 0.501960813999176,
                      0.3019607961177826)).range(0.30927824449771524).pickType(
                  com.duanxr.pgcon.core.preprocessing.config.ColorPickFilterPreProcessorConfig.PickType.CIEDE2000)
              .maskType(
                  com.duanxr.pgcon.core.preprocessing.config.ColorPickFilterPreProcessorConfig.MaskType.BLACK)
              .inverse(true).build()).preProcessor(
          com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder().enable(true)
              .binaryThreshold(0.0).inverse(false).threshType(
                  com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.BINARY)
              .build()).template(
          "{\"R\":128,\"C\":128,\"T\":0,\"D\":\"HwABAAQv/wABAC8f/wEAEA87AAcvAACDAFEPNwAEDoAAD4gARA8vAAQOgQAPhQBEDykABA6BAA+EAEQPJQAOLwAAhwBKDygBEw+FAAoPqAFSD30AagODAA6IAA9/AE0PCgEBD34AawMJAQ4oBA9/AFcW/54FDigED/4AUA8zAAAPgABtL///gABUDzQABw8xB1APMwACD4EAVA8yABIP+gRgDi0AD6AGPg+BAGYPMwESDyAFJQ7RAg+AADQf/zEADh8A1AISD3QADA8zAggP3gEHD34JMwkgAA+qAwIPKQEsD0MADA+AACIPWgMeBjoAD70ACh//gQARDiUBD1sDIA49AA+BABQv//+AAD4PXgAYL///gAA/DyUAEQ/TBQ8PgQAmD6UAEg9/ACgP3QEwAyYAD38ALC8AAN0CJSr//+gED4AAOA+qAhcPgABCD64CBQ9/AEIPkQcZD4AAbS8AAIAAwA85ABsPfwJPDzAADA+AAI4DJQAPfwEkD8ILQR8ADgs5LwAAfwBJD4AAEQ/+AGsP7AgGD4AAVw9/AGwfAH8AbA++EwEPgACxD7gAFg//ALMPRQAmD3oEZw46AA9/AGAOgAAPfwBsH//9ATEPgABaL///gABqD0IAAw98BlYPQQApH/+AAAIP/wBUDn0ADoAAD/8AJw96CVMPPgACD4AASi///4AAIS///64RJQ43AA9+ASEPBg8DD4EAIh//fgIMD6ETBg+EATQLGgAPIR4ZDwUDMS//AIAAJy///4MEFw+AAEMPwRYBD34AGQ7oIA58Bg+BACoP+AlHH/+UGhIPgABRD0MBBh8A+QpJDiQAD8EXAA9eIiMPgAAfDpQUDj8iDt8YD4AAOi///74jAg+cAQUPgQE8DvABDyocDg8BAUoPUwAND4AAWC///yoBAA+BADAPVgAED3oZEg+AAFYPfwAFD4EBMQ+AAEgOAQEPgAA4HwCAAGsvAACBADMPdhxND4AARB8AgAA1DikFD04MSA8nAAUP/wBGDjEFD0snDg/MDkkPKQARDw0REw8BAT8OcQgPShAHDwAURA6AAg/JET0PIwADDvMID4ABQQ5QCQ7HCQ8JCxEf/4USOg+ZAg8PBgs5ClAfDoAAD0UtPg68Ag+bEwMPiilXB9UADnYmD38AVQ9gGgUPXwIPL///fwBIDg4BDw0CAA+GEjIOgQAPiAADD1IuAg9EFDAv///oHx8OKwIPPxwdD2whKw6fAQ//ACYPHQIDD3ofNg8zABIPgAABDwcBMw8tABMPIwAAD4AiHg+AADwf/4AiMQ8sAAoPggIPD4AAVh8AfgBaD2wWAQ9/AGwPgQAsHwCAAIEfAK8AKQ96IzUPgQAYDx0GAg95JTkOLBMPShMDDw0bSQJIAA+DFx0OgQAPsAlCDk8ADx4vTwxdAA/KEi0OkgAPmgEwD4AABQ9nOQoPSQtPHwABAGMPfwBsHwB+AGsOKAMPgABhHwABAP////8FUAAAAAAA\",\"L\":16384}")
      .build();
  private static final ImageCompare.Param FOUND_RESOURCES = ImageCompare.Param.builder()
      .area(Area.ofRect(456, 254, 1078, 624)).method(ImageCompare.Method.TM_CCOEFF).preProcessor(
          com.duanxr.pgcon.core.preprocessing.config.ColorPickFilterPreProcessorConfig.builder()
              .enable(true).targetColor(javafx.scene.paint.Color.color(1.0, 1.0, 1.0))
              .range(0.2938144370827872).pickType(
                  com.duanxr.pgcon.core.preprocessing.config.ColorPickFilterPreProcessorConfig.PickType.CIE94)
              .maskType(
                  com.duanxr.pgcon.core.preprocessing.config.ColorPickFilterPreProcessorConfig.MaskType.BLACK)
              .inverse(true).build()).preProcessor(
          com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder().enable(true)
              .binaryThreshold(0.0).inverse(false).threshType(
                  com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.BINARY)
              .build()).template(
          "{\"R\":40,\"C\":38,\"T\":0,\"D\":\"HwABAN9P/////yMAEAIBAA8kABEOKAAOJQAOKAAOJAAPJwAKCBsACAwADyUABgYOAA8lAAoOTQAOJgAOJwAPJgAaCRUAH/8mABEu//9NAA8HAQIPJgAMB+8BDyYACz///wAmAAIPEwAdDx0BAB8AJgAeAhMACzAADyYABwwaAg7vAQ6FAQ8mAAQJggIOJQAOrAIP0wIWDiUADygAAC8AACcAAw8lABIO8QIPAQD8UAAAAAAA\",\"L\":1520}")
      .build();
  private static final OCR.Param GOT_POINTS = OCR.Param.builder()
      .area(Area.ofRect(348, 438, 234, 34)).preProcessor(
          com.duanxr.pgcon.core.preprocessing.config.ColorPickFilterPreProcessorConfig.builder()
              .enable(true).targetColor(javafx.scene.paint.Color.color(1.0, 1.0, 1.0))
              .range(0.2010309344841821).pickType(
                  com.duanxr.pgcon.core.preprocessing.config.ColorPickFilterPreProcessorConfig.PickType.CIEDE2000)
              .maskType(
                  com.duanxr.pgcon.core.preprocessing.config.ColorPickFilterPreProcessorConfig.MaskType.BLACK)
              .inverse(true).build()).preProcessor(
          com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder().enable(true)
              .binaryThreshold(0.0).inverse(true).threshType(
                  com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.BINARY)
              .build())
      .apiConfig(ApiConfig.builder().method(OCR.Method.NMU).whitelist("0123456789").build())
      .build();
  private static final ImageCompare.Param HOPPING_SKEWERS = ImageCompare.Param.builder()
      .area(Area.ofRect(100, 220, 38, 44)).method(ImageCompare.Method.TM_CCOEFF).preProcessor(
          com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder().enable(true)
              .inverse(false).threshType(
                  com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.OTSU)
              .build()).template(
          "{\"R\":44,\"C\":38,\"T\":0,\"D\":\"FP8BAB8AAQAABR0ADyYAGy8AACcACgILAB8AJgALCCUABwkACScAAy0ABSUADQoAHQCUAA8mAAIFKAAfACYAMQewAA8mAAo/AP8AJgA3HwAmAP8tAR4AH/8mAAkO4wIPJgD/cx//JgARDZEDDyUABC///yYADQwSAA8lAAYOdQAPlAADChcAD5QAAQH2BA0nAA5vAA9+BRQf/8oFFB8AygUSIwD/lgUf/yYAAAEEAB//AQAOUP//////\",\"L\":1672}")
      .build();
  private static final ImageCompare.Param LAVA_CAVERNS_SELECTED = ImageCompare.Param.builder()
      .area(Area.ofRect(1216, 128, 160, 96)).method(ImageCompare.Method.TM_CCOEFF).preProcessor(
          com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder().enable(true)
              .inverse(false).threshType(
                  com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.OTSU)
              .build()).template(
          "{\"R\":96,\"C\":160,\"T\":0,\"D\":\"H/8BAP///////////////////xA/AAAAoQCND7EAiwyQAC8AAKIAex8AoQACD6AAjR8AHgIWD6AAjR8AoABdD+EBDQ/NAAAPoABbH/+gABkPoQBTLgAAdQAOeAAPQQFiAF4AAmIAAOwCBpsADqEAD0EBYwFdAAMFAArdAQ6IAg+gAAwvAACgAEsPGgEYHgDgAQ+gAEYPRgIDDuIBD1cEAgqgAB8APwFGCosAHwA2ABEGoAAO9wMPOAE5DqAADnYFD6AABAllAQ6gAA9nBigXAKAABmYAHwChABMeAAEADk8DD2cGLA5eAAfnAA6iBQ+eAAMPjwQHD/MFAA+gACwNtwEPoAAFD58AIA4VCQ82BhQBBAAL5QAvAACgABwORgYPoAA0ChkCAoQBDs0ED6AADA5/AA8HBw0OcgEPMAAGDikEDzMGFgiPBA6fAA9mBgkPFAIQB5gBCmMDDskCDxMIBA+9ARoMawEPkwQPBWsCD/wJJw4cAQ/HCREO2gAPoAALDy4JLA+9AxEPOAMhBW0BD3UHAg+aABcOoAAPoQA0BB0ADhAADyoEDA4CBQ/+BAcfAKAALwuHAw5WAQ+dBSkvAACWCQMPtQcSDNoCD4oIEg87BhsM6AMFywkPoAAZDpUAD/gDCA+dABgNSAAPoAAcARsADl4KD6EAFA5NAw+gACEPPwEMDpgFD2gEEApqBQ++DhgKQQYOPwEPOwAKCRYNDjMED1oOJA6gAArbBw80ChQLsgEOWgIPnwAkCiIELwAAGAoYDk8ID5kDAw+fACoM4gcfAKAAFgy5CATkCw5HAQ+fABAfAKAADh4AwAQO8w0PEQwKC2oEDqADDzkFAA9tCxMf/1gDAA4LBg+wDA0LSQAfAJsICQogBA+iBRAEKQQJqQEeAKAADk8ND9MOBQ/LAAkPqgIZBvQKDjoCDn8CD48OBQ8cDRYOnwAPIQgMBT8BBKAACYICD8oTHw75BA9ZCQ8PoAAKF//fAQUbCQiLAg+gADwHlwAI+A0PoAAJKwD/oAAeAKAAD6EANQ8jAAEPnwANBd8BDU0HDrcPDwoZEQ+fAA0OQgMOHgMPoAAIBzAAD1MSOwoaBQ8DCxEGNAEOkAQOkhMPqhY2CJIADxECEAoHAAv+CQ9JF0ENPgYfAB8IDgU6AAgTBw7fAQ+dACwOhAkPoAAcDh8ADzwBPQ6hAA+gABQf/6AATS///6AAhw5QAQ+kCxYEFAIPoABaH/9fFhQH6AMPoAAFDwEAaw9JDw4f/wEAbQ+gAKQf/58AjC///6AA/zIv//+hAIgPoACNDr8UDwEA//////////8NUP//////\",\"L\":15360}")
      .build();
  private static final ImageCompare.Param MASTER_MISSION_SELECTED = ImageCompare.Param.builder()
      .area(Area.ofRect(108, 948, 56, 42)).method(ImageCompare.Method.TM_CCOEFF).preProcessor(
          com.duanxr.pgcon.core.preprocessing.config.ColorPickFilterPreProcessorConfig.builder()
              .enable(true).targetColor(javafx.scene.paint.Color.color(1.0, 1.0, 1.0))
              .range(0.12371126367491375).pickType(
                  com.duanxr.pgcon.core.preprocessing.config.ColorPickFilterPreProcessorConfig.PickType.CIEDE2000)
              .maskType(
                  com.duanxr.pgcon.core.preprocessing.config.ColorPickFilterPreProcessorConfig.MaskType.BLACK)
              .inverse(true).build()).preProcessor(
          com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder().enable(true)
              .binaryThreshold(0.0).inverse(false).threshType(
                  com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.BINARY)
              .build()).template(
          "{\"R\":42,\"C\":56,\"T\":0,\"D\":\"HwABAP/xL///FgADH/84ACYa/wEADzgADij//3AADnoADzgACQYXAA+xAAYPOAAgAiEAH/84ABgf/wEAAwo4AC//ABcBBwEbAA9wAAQPTwEHD6gACwaxAA84ACUfADgANg/fAAEPOAATDRQABTACDzgADC8A/6gAHwrkAQ9QAQgOzAAPOAAGH/+IAQcv//9MAgEJFAMOqAAPvQIGDzgADS7//8UBDjgADKgACvEBH/9SAAAo/wDpAA4oAg04AgT3AA9IAwEPOgIIC2AALwAADgAFCxUADgUBDykFIh8AAQD/6lAAAAAAAA==\",\"L\":2352}")
      .build();
  private static final ImageCompare.Param MISSION_PANEL_OPEN = ImageCompare.Param.builder()
      .area(Area.ofRect(30, 46, 78, 70)).method(ImageCompare.Method.TM_CCOEFF).preProcessor(
          com.duanxr.pgcon.core.preprocessing.config.ColorPickFilterPreProcessorConfig.builder()
              .enable(true).targetColor(javafx.scene.paint.Color.color(1.0, 1.0, 1.0))
              .range(0.12371126367491375).pickType(
                  com.duanxr.pgcon.core.preprocessing.config.ColorPickFilterPreProcessorConfig.PickType.CIEDE2000)
              .maskType(
                  com.duanxr.pgcon.core.preprocessing.config.ColorPickFilterPreProcessorConfig.MaskType.BLACK)
              .inverse(true).build()).preProcessor(
          com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder().enable(true)
              .binaryThreshold(0.0).inverse(false).threshType(
                  com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.BINARY)
              .build()).template(
          "{\"R\":70,\"C\":78,\"T\":0,\"D\":\"HwABAP///70v//9NADov//9NADov//9OADsf/0wAOQ9PADQe/08AD04Aawc+AAxOAA5YAA95ARsHTQAHCwAPTgAsC1kAD08AKgr2AA98ASQEDgAfAE4AOAsVAA9OACYf/8kCNA45AA9MACcPnwMBD8QCNw9LBCgv//9BAAAMWgAPHQUhCrgCDj8FDx0FHQZTAw5BAQ/MASAPEQAGH/8dBSIPKgInB4AEH//bAQUO8gQPTgAzD+oAMA6NBQ+GARwKYQIO5QQPIgIdDyMCAA++AiQJVgAPTgAeB3cIDxcDAg6ZCA+BCBwPrgYlHwCSBA0Pfgg2DuEEDxgGKQ4BBw9OADIPUAcqDD8FL///TwcrDJ8HDzoILg60AA9OACIJLQEPhwEmC3kBDwkDLA9OAAYvAABOADIPRgt+D+ILOw9+DD0vAABNADpPAAD/AAEA////uFAAAAAAAA==\",\"L\":5460}")
      .build();
  private static final ImageCompare.Param POINTS_BENEFIT_0 = ImageCompare.Param.builder()
      .area(Area.ofRect(1228, 306, 52, 36)).method(ImageCompare.Method.TM_CCOEFF).preProcessor(
          com.duanxr.pgcon.core.preprocessing.config.NormalizePreProcessorConfig.builder()
              .enable(true).build()).preProcessor(
          com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder().enable(true)
              .inverse(false).threshType(
                  com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.OTSU)
              .build()).template(
          "{\"R\":36,\"C\":52,\"T\":0,\"D\":\"H/8BAJAvAAAzACAGNAAPPgASGwA0AC8AAGUAEgcoAA4zAA80AAsCCgAPNAAiBTgALwAAJAEEDCcABg4ABQsADX4ACjQACkMBBBsADLQAAkgAD6oBBgs4AAI7AAVrAQWLAA/uAQIPNAALDIcABO8BClQADhgACbABD+oAAAuXAQMqAQ0YAh8AWgACBVAAH/+OAAAMfAAI0AAOUAAPbQEBHv8GAgzhAQ44AAgrAguPAA0bAQv7AQ25AB8AZgIBH/80AAQNmwEPpgMHBQsBDzQAGB//NAAXDugCD2cAGB//zwAXBvkBDE4BD+YDEQhPBA8zABUCLQAPJwUnDhwFDwEA/ylQ//////8=\",\"L\":1872}")
      .build();
  private static final ImageCompare.Param POINTS_BENEFIT_1 = ImageCompare.Param.builder()
      .area(Area.ofRect(618, 740, 174, 42))
      .method(ImageCompare.Method.TM_CCOEFF)
      .preProcessor(com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder()
          .enable(true)
          .inverse(true)
          .threshType(
              com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.OTSU)
          .build())
      .template(
          "{\"R\":42,\"C\":174,\"T\":0,\"D\":\"H/8BAP//////Fx8AVQBCLwAAHQAJDj8ADyIAEx4AWAAPrQAzHwAdACAqAADsAA30AA07AB8AAQACDxsABg+QAAIOJQAPWAAFLwAAjAABBzMAD64AIg9xAAcPrQABD64AJAWNAA+uACIX/wcADl0BD60ABw+uACYEagABBgAIHgAHGwIfABEAAwYVAA9pAgIPrQALDyYABA6BAA/TAgEIBgAJiQAOrgAOXQAOrgAPrQAICzIBCbsACMoBD2sDBQRVAQt4AQuuAB8ArgAaDwYCEA9oAgcOWwEPiwIGD4oABQ6cAg5fAg+tAA8KVgAvAAAnAwYPTwQMBYYAD64AAwqjAQ+7AxAfANgFCg84AAMPWAASBRcAClQCCNICHf8XAAY8AAvwAg50Ag/PAQEPrQAVHwDzAgIJRwQPgAMKDGUBDyYCBQ5CBw9sAwEPrgASDJ0AD4wDAgquAAsiAg6uAA8jAgQfAHAFFC///6sCCh//VgUECJgABdAABtsICIkFD64AIA4jAA+uABoPoAQHDBsCDBEGHwCKAwAfAA0CDB8ArgApD6gEAQ9hAgwIfAQPcAUEHwAtBRkPrgAPCXwJCvAHD5gHAA+aAwIO3wQPrgASBE0EDP8ADq4AD00KBx8ARQgKChcACG4BD64AIA6XAA+uABkf/60ADB8AyQgADJ4FD64AMx8ArgARCw4CD94CCQ1dAA7uAg6uAA/mDA4OGggPrQAPDjAHCEAHDx4GAQ+uACkPFAQAHwC3AhQPLgEAD3kKAh8AhAQOA3QODgoCDzwMEx//rQcbBRAADmcFDK8CD68ABA4BAw9cAQAPrgApDhIAD8wJAg+uABsf/2YDIB//1g8gBOoAD+0JCA9HAAAO2woPAQD////////NUP//////\",\"L\":7308}")
      .build();
  private static final ImageCompare.Param RESTAURANT_PANEL_OPEN = ImageCompare.Param.builder()
      .area(Area.ofRect(32, 46, 68, 66)).method(ImageCompare.Method.TM_CCOEFF).preProcessor(
          com.duanxr.pgcon.core.preprocessing.config.ColorPickFilterPreProcessorConfig.builder()
              .enable(true).targetColor(javafx.scene.paint.Color.color(1.0, 1.0, 1.0))
              .range(0.1752576296330634).pickType(
                  com.duanxr.pgcon.core.preprocessing.config.ColorPickFilterPreProcessorConfig.PickType.CIEDE2000)
              .maskType(
                  com.duanxr.pgcon.core.preprocessing.config.ColorPickFilterPreProcessorConfig.MaskType.BLACK)
              .inverse(true).build()).preProcessor(
          com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder().enable(true)
              .inverse(false).threshType(
                  com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.OTSU)
              .build()).template(
          "{\"R\":66,\"C\":68,\"T\":0,\"D\":\"HwABAP//txT/AQAPQgAvDkUAD0IAFw9FAC0v//9FAC8DOQAFBQAv//9DACUACQAFlAAPRAAmCNkAD0QAZh//RAApCo4BDpsBD9sBHw9EAGgfAEMAMC8A/6kCNQ92AzwDEAAPOgAdCjYDDpEAD3wAFQ/ZAAIPNQQiDhsBDzUEGA9MBB8EIQAEBQAJ7AIOFwAPRAAHAzQEHQBZASEAADsADkUAD/ADEQ4YAA9EAB4f/0QAHx//RAAECmABD5YBDQsNAA/DAwIP8AMdD0QAAQ/wAxoPRAA0DwgEAA+oAksPdAMzCS4ADxcABQ8BABEPbwQAD/YHMg6CAA9CABAPRgADD0EALg9HABYv//9FAA0PQgAvD0YADi///0UAEw9EAJ0OHAAPRAAaDyEAKA7jAg8HAyIPiQISL/8AAQD//+FQAAAAAAA=\",\"L\":4488}")
      .build();
  private static final ImageCompare.Param RUN_END = ImageCompare.Param.builder()
      .area(Area.ofRect(24, 28, 38, 40)).method(ImageCompare.Method.TM_CCOEFF).preProcessor(
          com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder().enable(true)
              .inverse(true).threshType(
                  com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.OTSU)
              .build()).template(
          "{\"R\":40,\"C\":38,\"T\":0,\"D\":\"H/8BAP9wLwAAJgATHwAmAI4FDAANOwAPJgAfBQ0ADyYAEg6sAA8mACADDgAPJgASDh0BDDABDyYAFR4AAQIPJgABHwAmAA8f/yYABgUBAAQQAB//JgAkBi8ACwgADwEA/5FQ//////8=\",\"L\":1520}")
      .build();
  private static final ImageCompare.Param TRAVEL_READY_CHS = ImageCompare.Param.builder()
      .area(Area.ofRect(832, 524, 236, 52)).method(ImageCompare.Method.TM_CCOEFF).preProcessor(
          com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder().enable(true)
              .inverse(false).threshType(
                  com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.OTSU)
              .build()).template(
          "{\"R\":52,\"C\":236,\"T\":0,\"D\":\"HwABAP////////////////93Ev8GAB//LgAUD20AMQEJABT/DQAf/wEAAgcbAAMJAA9TAAUIGwAPSgAGBB4AD0wAAghvAAwuAAwLAA/pABUOhwAP6wAGBJUACwsACQQBDQcBD0sABA/sAAIPMwAPD+wAAA78AA/sABsP2AEGDusADw8AAAq7AA/sAB4f/+wADgRkAA7IAA/sACEbANwAAQsABvIBC+sAD+wAChcASAACGQAOxAIP7AAVBgcADtwCDu4AD+wAFQgfAB8AjAMAD68CBAh1AAiOAA+FAgEfAMUAAw4xAApHAAnYAQ5pAQ/sACYOFQAK7AAOXwALUgAPugAOCewADroAD0EDAwtVARkAXAIP7AAjD9sCAg8JAgQPNgAPDikCDEgAC9MAHv8gAA4FAQ/sADQf/7QECysAALsBC4wAHgCmBg74AA45AQ/sACUfAOwAKA/WAAQNlAAOLwAFfQADKgUPDAIGH//xBhoPBwEADOgDDuwAD3QGGAxDAw9vAAAP7AAFC4IAD9UACQwzBi///x8BFA+YBwEfAJ0EGQ5NBAkSAB//7QALAocJD+wACh8A7AAFDpYAD2YAAQfXAw/JBwEP1QEXCHwAC6MJBvAFD3QBCAeTACoAACcBCbkBCaUBDnAKD84EBR8AdAYBDusAD+wAGAI5CAKjAQOnAAWiAA4rBA74Ag50AQXAAA69Bw7sAA/YAQEPYAcJD8ICGwl6AS///z0KEi8A/+wABwiTAA//BAEP7AAmC9gBH//rABkMxgcPKQseGgDxBQnyCQkKBg/1AgYP6AwEH/8IBwgOrwgP7AAeDzwAAR//TwQDLwAARgcCCm0GD+wAEg0KAh//pgsODPAABhsAD+sAEA/zCQIPGQoBLwAAwQIBK///IAIMmAIOhwUfAOwACx//egcPCJIADrEAD+wAFR8AOwACD9gBAC8AAN0CEwwwAQicBC8AAOwAGAroAAv9Ag/TDRYOsAMP2AEEDYYGD+sABw+IBRcP3wEAD34AAhv/xg4JVQAPSQgbHwCJAAkO7AAEzwIfAOwADA/xCRUP2AERD28BAg6HBw+ZBBMOHAMP7AADH/83CQMNKgUPtgwhD04NAw6IAQ+cBBsPYQEFD+wAFg9VAAQOQAsP7AAKCW8AD+wAEQ5LBQ8BABAPaQoAL///nAQJBTUAD/YEBw1zAwcZCgdpAw4xEg4ZBQ8dEQcO6wAPAQD////////////////rUAAAAAAA\",\"L\":12272}")
      .build();
  private static final ImageCompare.Param TRAVEL_READY_ENG = ImageCompare.Param.builder()
      .area(Area.ofRect(818, 534, 282, 40)).method(ImageCompare.Method.TM_CCOEFF).preProcessor(
          com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder().enable(true)
              .inverse(false).threshType(
                  com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.OTSU)
              .build()).template(
          "{\"R\":40,\"C\":282,\"T\":0,\"D\":\"HwABAP/////////FGv8BAA9BACEPcgAfDg4AD9oARw8aAWYKNQAO6AAOEwEPGgFUL///GwEtDxoBxALOAA4bAQ/gACYPGgFeHwAzAkgO2QAPGgFRL///OgAAH/8aARcPbwATH/8OABYPbQAEHwAaATgEhQIGaAMIYAAGigED3QQIDQELFwAKngMFEQUEVwAFKQAPLwUFCUQACo0ABQ4ADnIAC5cADxoBNwx1AAXOAASCAQjcAAqvAi////4ABArbAAZYAAV0AQ8vAAALFQAf/10AAg8iAAMDHAEfABoBRRz/jQAFKAAPGgACBz0ACrwADzQCCAkaAR4ASgEPGgEKCqoAD3QAAw5oBA8aAS4MqQMJIQIJQQINiQADSwQNawMPHQAEDl8AB2YBCY0BCpoBBX8GKwAAowEM4AMfABoBOR0AwwQPGgEaCk4AD5wGBg0aAQ+SAAYOzgMOYwEPGgEBH/8aAT0JFwALmAEbAMYCDxoBJw9TAAAPLgcKDnMADNcCCmgADjMCDxoBNQ3oAw8aATwI0QAOsQoPjwUGC1sDCpcDChoBDhkBDxoBMgzrBQ3NBw8aATkONwIKIgQf/xoBJR8AGgFECtAACVkDBwIED04DKQwVBB//kgABC20FDxoBXR8APQoEDosJDxoBFw6cBg9oBAYMMAAKogEODwYPggUIDxoBRAzfBARkBwfTAQ5QBw+2Bx0HEAAPGgEFCMoHGv8NAA+cBgcLdQAOTgMPaAQ9BjUCDpwFHv8QBAzNCQ4dAA+2BwEf/+oJBw9oBBULRQAOtAcPGgEqDRMFCX4LCxoBHgAaAQ9sBgUGdgIf/5wGEg5mAAyRAA8aAQMPzgg7AuYED/QGAQvrAi4AAC4FC30AClkBD7UDAghbBQ9SDhAOVwENkgAEFQADMQwMMwUPAQDkDwcBCw8aAf////9DDt8ZDwEA/////////////lAAAAAAAA==\",\"L\":11280}")
      .build();

  private static final float FARM_POINTS_DETECT_THRESHOLD = 0.7f;
  private final long detectLongTimeoutMillis = 30000L;
  private final long detectShortTimeoutMillis = 3000L;
  private final long fastModeTimeLimit = 270000; //4m30s
  private final long[] nodeCosts = new long[]{31000, 19000, 10000, 36000, 27000, 25000, 19000,
      29000, 24000, 26000, 20000, 36000, 18000, 17000};
  private final Runnable[] nodes = new Runnable[]{this::node00, this::node01, this::node02,
      this::node03, this::node04, this::node05, this::node06, this::node07, this::node08,
      this::node09, this::node10, this::node11, this::node12, this::node13};
  private final long[] nodeSuccessCount = new long[nodes.length];
  private final long[] nodeTimeCost = new long[nodes.length];
  private boolean fastMode = false;
  private boolean isPointsBenefit0 = false;
  private boolean isPointsBenefit1 = false;
  private int missNodeCount = 0;
  private long runCount = 0;
  private long runStartTime = 0;

  public MHRiseAutoFarmPoints() {
    super(ScriptInfo.<MHRiseAutoFarmPoints.Config>builder().config(new Config()).isLoop(true)
        .description("MHR Auto Farm Points(CHS.Ver & ENG.Ver)").build());
  }

  @Override
  public void execute() {
    launchGame();
    goToThePrincess();
    getMission();
    goToTheRestaurant();
    getFood();
    depart();
    farmPoints();
    completeMission();
    calculateReward();
  }

  private void launchGame() {
    startGame();
    checkBugReady(300000L, super::restartGame);
    sleep(1000);
  }

  private void goToThePrincess() {
    turn(8);
    run(4500);
  }

  private void getMission() {
    sleep(350);
    press(A);
    until(() -> detect(MISSION_PANEL_OPEN),
        result -> result.getSimilarity() > FARM_POINTS_DETECT_THRESHOLD, () -> sleep(50),
        detectShortTimeoutMillis, super::restartGame);
    until(() -> detect(MASTER_MISSION_SELECTED),
        result -> result.getSimilarity() > FARM_POINTS_DETECT_THRESHOLD, () -> {
          press(D_BOTTOM);
          sleep(350);
        }, detectShortTimeoutMillis, super::restartGame);
    press(A);
    sleep(350);
    press(D_TOP);
    sleep(350);
    press(A);
    sleep(350);
    until(() -> detect(EXPLORE_MISSION_SELECTED),
        result -> result.getSimilarity() > FARM_POINTS_DETECT_THRESHOLD,
        () -> sleep(50), detectShortTimeoutMillis, super::restartGame);
    press(R);
    sleep(350);
    until(() -> detect(LAVA_CAVERNS_SELECTED),
        result -> result.getSimilarity() > FARM_POINTS_DETECT_THRESHOLD, () -> {
          press(D_TOP);
          sleep(350);
        }, detectShortTimeoutMillis, super::restartGame);
    isPointsBenefit0 = detect(POINTS_BENEFIT_0).getSimilarity() > FARM_POINTS_DETECT_THRESHOLD;
    press(A);
    sleep(350);
    press(D_BOTTOM);
    sleep(350);
    press(A);
    sleep(350);
    press(A);
    sleep(2000);
  }

  private void goToTheRestaurant() {
    turn(90);
    run(2600);
  }

  private void getFood() {
    press(A);
    until(() -> detect(RESTAURANT_PANEL_OPEN),
        result -> result.getSimilarity() > FARM_POINTS_DETECT_THRESHOLD,
        () -> sleep(50), detectShortTimeoutMillis, super::restartGame);
    press(A);
    sleep(350);
    press(A);
    sleep(350);
    press(A);
    sleep(850);
    isPointsBenefit1 = detect(POINTS_BENEFIT_1).getSimilarity() > FARM_POINTS_DETECT_THRESHOLD;
    if (isPointsBenefit1 && isPointsBenefit0) {
      if (detect(HOPPING_SKEWERS).getSimilarity() < FARM_POINTS_DETECT_THRESHOLD) {
        press(MINUS);
        sleep(350);
      }
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
    closeAllPanel();
  }

  private void depart() {
    sleep(200);
    press(ZR);
    sleep(350);
    press(A);
    sleep(3000);
  }

  private void farmPoints() {
    initRun();
    farmRoute(1, 2);
    farmRoute(11, 12, 13);
    farmRoute(9, 10);
    farmRoute(0);
    farmRoute(3);
    farmRoute(4, 5, 6);
    farmRoute(7, 8);
  }

  private void completeMission() {
    press(PLUS);
    sleep(350);
    press(D_RIGHT);
    sleep(150);
    until(() -> detect(COMPLETE_MISSION_SELECTED),
        result -> result.getSimilarity() > FARM_POINTS_DETECT_THRESHOLD, () -> {
          press(D_BOTTOM);
          sleep(350);
        }, 10000L, super::restartGame);
    press(A);
    sleep(150);
    press(D_LEFT);
    sleep(150);
    press(A);
    sleep(5000);
    until(() -> detect(RUN_END), result -> result.getSimilarity() > FARM_POINTS_DETECT_THRESHOLD,
        () -> {
          press(A);
          sleep(350);
        }, detectLongTimeoutMillis, super::restartGame);
  }

  private void calculateReward() {
    try {
      press(A);
      sleep(3000);
      Long accurateLong = detectAccurateNumber(GOT_POINTS, 5, detectLongTimeoutMillis,
          super::restartGame);
      config.getPoints().set(config.getPoints().get() + accurateLong);
      long millis = System.currentTimeMillis() - runStartTime;
      String hms = formatTime(millis);
      info("Got {} points cost {}", accurateLong, hms);
      runCount++;
      for (int i = 0; i < nodes.length; i++) {
        info("Node {}: {} runs, {} success, {} avg cost", i, runCount,
            String.format("%.0f%%", 100.0 * nodeSuccessCount[i] / runCount),
            formatTime(nodeTimeCost[i] / (nodeSuccessCount[i] == 0 ? 1 : nodeSuccessCount[i])));
      }
    } catch (Exception e) {
      warn("cannot get points", e);
    }
  }

  private void initRun() {
    checkBugReady();
    runStartTime = System.currentTimeMillis();
    sleep(4000);
    missNodeCount = 0;
    fastMode = isPointsBenefit0 && isPointsBenefit1;
    if (fastMode) {
      warn("points benefit detected, use fast mode");
    } else {
      info("points benefit not detected, use slow mode");
    }
  }

  private void farmRoute(int... nodes) {
    int currentNode = nodes[0];
    try {
      for (int node : nodes) {
        currentNode = node;
        farmNode(node);
      }
    } catch (ResetScriptException e) {
      error("not found node {}", currentNode);
      if (e.getRunnable() != null) {
        e.getRunnable().run();
      }
      missNodeCount++;
      if (missNodeCount >= 3) {
        throw new ResetScriptException("Script is not working well, restart the game!").setRunnable(
            super::restartGame);
      }
    }
  }

  private String formatTime(long millis) {
    return String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
        TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
        TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
  }

  private void farmNode(int node) {
    Runnable runnable = nodes[node];
    long nodeCost = nodeCosts[node];
    if (fastMode && System.currentTimeMillis() + nodeCost > runStartTime + fastModeTimeLimit) {
      warn("fast mode time limit reached, finish farming");
      return;
    }
    long nodeStartTime = System.currentTimeMillis();
    runnable.run();
    nodeSuccessCount[node]++;
    nodeTimeCost[node] += System.currentTimeMillis() - nodeStartTime;
    missNodeCount = 0;
  }

  private void node11() {
    fastTravel(2);
    hold(NintendoSwitchStandardStick.L_RIGHT);
    sleep(500);
    release(NintendoSwitchStandardStick.L_RIGHT);
    sleep(350);
    turn(-40);
    wireBugJump(1100);
    sleep(1500);
    turn(20);
    wireBugJump(500);
    sleep(8000);
    checkResources();
    turn(-64);
    wireBugJump(900);
    sleep(2500);
    walk(1000);
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
    until(() -> detect(TRAVEL_READY_CHS, TRAVEL_READY_ENG),
        result -> result.getMax().getSimilarity() > FARM_POINTS_DETECT_THRESHOLD, () -> sleep(50),
        2000L, super::closeAllPanel);
    press(A);
    sleep(200);
    checkBugReady();
    sleep(1000);
  }

  private void checkResources() {
    until(() -> detect(FOUND_RESOURCES), result -> result.getSimilarity() > FARM_POINTS_DETECT_THRESHOLD, () -> sleep(50),
        2000L);
  }

  private void getResources() {
    checkResources();
    sleep(800);
    press(A);
    sleep(5000);
  }

  private void node12() {
    turn(-60);
    walk(1200);
    turn(20);
    run(6800);
    sleep(350);
    turn(45);
    walk(1200);
    getResources();
  }

  private void node13() {
    turn(-24);
    run(6000);
    wireBugJump(1200);
    run(2550);
    getResources();
  }

  private void node09() {
    fastTravel(2);
    hold(NintendoSwitchStandardStick.L_RIGHT);
    sleep(500);
    release(NintendoSwitchStandardStick.L_RIGHT);
    sleep(350);
    turn(-40);
    wireBugJump(1200);
    sleep(350);
    wireBugJump(1200);
    sleep(2000);
    turn(65);
    run(1700);
    sleep(350);
    getResources();
  }

  private void node10() {
    checkBugReady();
    turn(-45);
    wireBugJump(500);
    wireBugJump(500);
    run(4500);
    sleep(350);
    turn(-85);
    run(4400);
    turn(-85);
    walk(600);
    getResources();
  }

  private void node00() {
    fastTravel(0);
    turn(150);
    wireBugJump(1200);
    wireBugJump(1200);
    run(8100);
    turn(-109);
    run(1950);
    getResources();
  }

  private void node07() {
    fastTravel(2);
    hold(NintendoSwitchStandardStick.L_LEFT);
    sleep(500);
    release(NintendoSwitchStandardStick.L_LEFT);
    sleep(350);
    turn(90);
    wireBugJump(1200);
    sleep(2500);
    turn(-64);
    wireBugJump(1200);
    sleep(2500);
    walk(1500);
    sleep(150);
    getResources();
  }

  private void node08() {
    checkBugReady();
    turn(5);
    wireBugJump(1000);
    sleep(500);
    wireBugJump(1000);
    sleep(2500);
    checkResources();
    turn(35);
    checkBugReady();
    wireBugJump(1200);
    sleep(500);
    wireBugJump(2500);
    turn(145);
    walk(4900);
    getResources();
  }

  private void node04() {
    fastTravel(2);
    turn(-198);
    sleep(350);
    run(5300);
    sleep(350);
    walk(3400);
    getResources();
  }

  private void node05() {
    sleep(350);
    turn(85);
    sleep(350);
    wireBugJump(1200);
    sleep(550);
    wireBugJump(1200);
    sleep(1550);
    turn(-90);
    run(500);
    checkResources();
    sleep(150);
    turn(75);
    run(3500);
    sleep(150);
    turn(20);
    wireBugJump(600);
    run(4400);
    getResources();
  }

  private void node06() {
    turn(68);
    run(10700);
    turn(82);
    run(650);
    getResources();
  }

  private void node01() {
    fastTravel(1);
    turn(194);
    run(1450);
    getResources();
  }

  private void node02() {
    turn(118);
    run(500);
    wireBugJump(1200);
    sleep(1200);
    getResources();
  }


  private void node03() {
    fastTravel(1);
    turn(194);
    run(3000);
    wireBugJump(1200);
    sleep(1600);
    turn(-10);
    run(5000);
    wireBugJump(1200);
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

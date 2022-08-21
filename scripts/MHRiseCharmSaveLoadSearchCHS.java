import com.dooapp.fxform.annotation.FormFactory;
import com.dooapp.fxform.view.factory.impl.TextAreaFactory;
import com.duanxr.pgcon.core.detect.api.ImageCompare;
import com.duanxr.pgcon.core.detect.api.OCR;
import com.duanxr.pgcon.core.detect.api.OCR.ApiConfig;
import com.duanxr.pgcon.core.detect.api.OCR.Param;
import com.duanxr.pgcon.core.model.Area;
import com.duanxr.pgcon.exception.AlertErrorException;
import com.duanxr.pgcon.exception.InterruptScriptException;
import com.duanxr.pgcon.exception.ResetScriptException;
import com.duanxr.pgcon.gui.fxform.annotation.ConfigLabel;
import com.duanxr.pgcon.gui.fxform.factory.ReadOnlyLabelFactory;
import com.duanxr.pgcon.script.api.ScriptInfo;
import com.duanxr.pgcon.script.engine.PGConScriptEngineV1;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.SneakyThrows;
import org.apache.commons.math3.util.Pair;
import org.apache.commons.text.similarity.LevenshteinDistance;

/**
 * @author 段然 2022/7/25
 */
public class MHRiseCharmSaveLoadSearchCHS extends PGConScriptEngineV1<MHRiseCharmSaveLoadSearchCHS.Config> {

  private static final Map<String, String> AVAILABLE_SKILLS = getAvailableSkills();
  private static final String CHARM_DECORATION_LEVEL_0 = "{\"D\":\"HwABAP//Zx//AQAPDyoAbA8BAP//t1AAAAAAAA==\",\"L\":1512,\"R\":36,\"T\":0,\"C\":42}";
  private static final String CHARM_DECORATION_LEVEL_1 = "{\"D\":\"HwABALhf////AP8BAAEPLwAEDwEABw8vABwv//8vABwv//8vAAwOEgAOXgAPMAALH/8vAAwPMQAKDxoAAw8wAE0fAGAANh8AMAArDiABDoEBDjAAD+EBGQwRAA9wAgAPMAAdHwAwAAcfAAEDDww+AA8xAw4PMAAUD5ADdgyBAR//wAMaDg4CD+ABCw8PAgAPQAIqH/+gAhwPHQEBDzAAOQ4xAA/hBA8PsQELDwEA31AAAAAAAA==\",\"L\":1920,\"R\":40,\"T\":0,\"C\":48}";
  private static final String CHARM_DECORATION_LEVEL_2 = "{\"D\":\"HwABAP9pH/8BAAEPKwAYDzEABS///y8AGAcaAA8KAAAMLwAPFwAMH/8VAAcJBgAOLwAOLQAPLwAAD4cADg8dAAMOWwAPLgAVH/8uABYe/1wADucADkABD1sBDA5dAA+IAQkItgEu//+eAQ+2ARoPoQASD7UCBA8rAQQPLgARAh8AL/8AEAMODz0DMQ8tABoPagMeD5gDLw8zAAIvAADLAQMPYgACBxwADFsALwAAgwACD1YCAQ+/AAIfAC8AAQOhAA+KAAMPPwIEDxcBBA8uACAPcAECDywCBg8BANVQAAAAAAA=\",\"L\":2024,\"R\":44,\"T\":0,\"C\":46}";
  private static final String CHARM_DECORATION_LEVEL_3 = "{\"D\":\"HwABAK4f/wEABQ8tABov//8tABov//8tAAYCCwASACMAAwoADC8ADp8ADx0ACAoQAA95AAIPLQAWCyEADogAD14BAA8tABcIIwAU/wEBC4kABxAAD4oAAB//twACH/+4AB4PFQEBD3EBGgXHAA+IAAUOXgAPWgAGDi8ADy0AFg4vAA4XAA9yABIf/z0DGg9qA10PmAMpHwCYAw8GMwAYAAsADy4AAwZiAAYKAAQLAA/5AQMHeQAGawENiQEFSgMHjwEGrgEP9AMGBbcABxUADNEBDqkED64ECg8uAA8PAQD/A1AAAAAAAA==\",\"L\":1840,\"R\":40,\"T\":0,\"C\":46}";
  private static final String CHARM_DECORATION_LEVEL_4 = "{\"D\":\"HwABAIov//8rABgBAQAPKwAYBgEADywAEAk4AA8sABAOEQAPhAAHDpAADy4AGgsvAA91AA0PEwAAD84AEwQsAAOVAR3/qgEOLgAPwwEFHf8vAA8tAAgDFwAPLgAGH/8uAAQPigAUBiEAD+YAAQ4uAA4VAQ8vAAQAGAANXwEELwAMuQAELQAPhQECHwDMARMOzgEORgAPMwMADy4AEw5XAg4XAA6wAg+zAgUPLgAbH/87AgEHGQAHQQAuAACEAgpJAA0OAAtWAg6PAA8uAA4KVQAGmQAMhAIGowANkAAKhQIv//94BAUOFgEPLgAJHwBDAQMOdAEOfAQO0QEPLQAWCqUADi4ADwEApFAAAAAAAA==\",\"L\":1840,\"R\":40,\"T\":0,\"C\":46}";
  private static final OCR.Param CHARM_LEVEL_1ST = OCR.Param.builder()
      .area(Area.ofRect(1452, 442, 32, 46))
      .preProcessor(com.duanxr.pgcon.core.preprocessing.config.ResizePreProcessorConfig.builder()
          .enable(true)
          .scale(2.0)
          .build())
      .preProcessor(com.duanxr.pgcon.core.preprocessing.config.SmoothingPreProcessorConfig.builder()
          .enable(true)
          .type(
              com.duanxr.pgcon.core.preprocessing.config.SmoothingPreProcessorConfig.SmoothingType.MEDIAN)
          .size(5)
          .sigmaColor(75.0)
          .sigmaSpace(75.0)
          .build())
      .preProcessor(
          com.duanxr.pgcon.core.preprocessing.config.ColorPickFilterPreProcessorConfig.builder()
              .enable(true)
              .targetColor(javafx.scene.paint.Color.color(0.0, 0.0, 0.0))
              .range(0.36804116879328197)
              .pickType(
                  com.duanxr.pgcon.core.preprocessing.config.ColorPickFilterPreProcessorConfig.PickType.CIEDE2000)
              .maskType(
                  com.duanxr.pgcon.core.preprocessing.config.ColorPickFilterPreProcessorConfig.MaskType.WHITE)
              .inverse(true)
              .build())
      .preProcessor(com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder()
          .enable(true)
          .inverse(true)
          .threshType(
              com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.OTSU)
          .build())
      .apiConfig(ApiConfig.builder()
          .method(OCR.Method.NMU)
          .whitelist("01234567890")
          .pageSegMode(10)
          .build())
      .build();
  private static final OCR.Param CHARM_LEVEL_2ND = OCR.Param.builder()
      .area(Area.ofRect(1448, 520, 36, 44))
      .preProcessor(com.duanxr.pgcon.core.preprocessing.config.ResizePreProcessorConfig.builder()
          .enable(true)
          .scale(2.0)
          .build())
      .preProcessor(com.duanxr.pgcon.core.preprocessing.config.SmoothingPreProcessorConfig.builder()
          .enable(true)
          .type(
              com.duanxr.pgcon.core.preprocessing.config.SmoothingPreProcessorConfig.SmoothingType.MEDIAN)
          .size(5)
          .sigmaColor(75.0)
          .sigmaSpace(75.0)
          .build())
      .preProcessor(
          com.duanxr.pgcon.core.preprocessing.config.ColorPickFilterPreProcessorConfig.builder()
              .enable(true)
              .targetColor(javafx.scene.paint.Color.color(0.0, 0.0, 0.0))
              .range(0.36804116879328197)
              .pickType(
                  com.duanxr.pgcon.core.preprocessing.config.ColorPickFilterPreProcessorConfig.PickType.CIEDE2000)
              .maskType(
                  com.duanxr.pgcon.core.preprocessing.config.ColorPickFilterPreProcessorConfig.MaskType.WHITE)
              .inverse(true)
              .build())
      .preProcessor(com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder()
          .enable(true)
          .inverse(true)
          .threshType(
              com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.OTSU)
          .build())
      .apiConfig(ApiConfig.builder()
          .method(OCR.Method.NMU)
          .whitelist("01234567890")
          .pageSegMode(10)
          .build())
      .build();
  private static final OCR.Param CHARM_RARE_LEVEL = OCR.Param.builder()
      .area(Area.ofRect(1364, 288, 120, 34))
      .preProcessor(com.duanxr.pgcon.core.preprocessing.config.ResizePreProcessorConfig.builder()
          .enable(true)
          .scale(2.0)
          .build())
      .preProcessor(com.duanxr.pgcon.core.preprocessing.config.SmoothingPreProcessorConfig.builder()
          .enable(true)
          .type(
              com.duanxr.pgcon.core.preprocessing.config.SmoothingPreProcessorConfig.SmoothingType.BILATERAL)
          .size(5)
          .sigmaColor(75.0)
          .sigmaSpace(75.0)
          .build())
      .preProcessor(com.duanxr.pgcon.core.preprocessing.config.NormalizePreProcessorConfig.builder()
          .enable(true)
          .build())
      .preProcessor(com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder()
          .enable(true)
          .inverse(true)
          .threshType(
              com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.OTSU)
          .build())
      .apiConfig(ApiConfig.builder()
          .method(OCR.Method.ENG)
          .whitelist("0123456789RAE")
          .build())
      .build();
  private static final Area CHARM_SLOT_1ST = Area.ofRect(1346, 314, 62, 48);
  private static final ImageCompare.Param CHARM_S0O1 = ImageCompare.Param.builder()
      .method(ImageCompare.Method.TM_CCOEFF)
      .template(CHARM_DECORATION_LEVEL_0)
      .preProcessor(
          com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder().enable(true)
              .binaryThreshold(0.5).inverse(false).threshType(
                  com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.OTSU)
              .build())
      .area(CHARM_SLOT_1ST).build();
  private static final ImageCompare.Param CHARM_S1O1 = ImageCompare.Param.builder()
      .method(ImageCompare.Method.TM_CCOEFF)
      .template(CHARM_DECORATION_LEVEL_1)
      .preProcessor(
          com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder().enable(true)
              .binaryThreshold(0.5).inverse(false).threshType(
                  com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.OTSU)
              .build())
      .area(CHARM_SLOT_1ST).build();
  private static final ImageCompare.Param CHARM_S2O1 = ImageCompare.Param.builder()
      .method(ImageCompare.Method.TM_CCOEFF)
      .template(CHARM_DECORATION_LEVEL_2)
      .preProcessor(
          com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder().enable(true)
              .binaryThreshold(0.5).inverse(false).threshType(
                  com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.OTSU)
              .build())
      .area(CHARM_SLOT_1ST).build();
  private static final ImageCompare.Param CHARM_S3O1 = ImageCompare.Param.builder()
      .method(ImageCompare.Method.TM_CCOEFF)
      .template(CHARM_DECORATION_LEVEL_3)
      .preProcessor(
          com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder().enable(true)
              .binaryThreshold(0.5).inverse(false).threshType(
                  com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.OTSU)
              .build())
      .area(CHARM_SLOT_1ST).build();
  private static final ImageCompare.Param CHARM_S4O1 = ImageCompare.Param.builder()
      .method(ImageCompare.Method.TM_CCOEFF)
      .template(CHARM_DECORATION_LEVEL_4)
      .preProcessor(
          com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder().enable(true)
              .binaryThreshold(0.5).inverse(false).threshType(
                  com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.OTSU)
              .build())
      .area(CHARM_SLOT_1ST).build();
  private static final Area CHARM_SLOT_2ND = Area.ofRect(1394, 314, 52, 48);
  private static final ImageCompare.Param CHARM_S0O2 = ImageCompare.Param.builder()
      .method(ImageCompare.Method.TM_CCOEFF)
      .template(CHARM_DECORATION_LEVEL_0)
      .preProcessor(
          com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder().enable(true)
              .binaryThreshold(0.5).inverse(false).threshType(
                  com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.OTSU)
              .build())
      .area(CHARM_SLOT_2ND).build();
  private static final ImageCompare.Param CHARM_S1O2 = ImageCompare.Param.builder()
      .method(ImageCompare.Method.TM_CCOEFF)
      .template(CHARM_DECORATION_LEVEL_1)
      .preProcessor(
          com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder().enable(true)
              .binaryThreshold(0.5).inverse(false).threshType(
                  com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.OTSU)
              .build())
      .area(CHARM_SLOT_2ND).build();
  private static final ImageCompare.Param CHARM_S2O2 = ImageCompare.Param.builder()
      .method(ImageCompare.Method.TM_CCOEFF)
      .template(CHARM_DECORATION_LEVEL_2)
      .preProcessor(
          com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder().enable(true)
              .binaryThreshold(0.5).inverse(false).threshType(
                  com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.OTSU)
              .build())
      .area(CHARM_SLOT_2ND).build();
  private static final ImageCompare.Param CHARM_S3O2 = ImageCompare.Param.builder()
      .method(ImageCompare.Method.TM_CCOEFF)
      .template(CHARM_DECORATION_LEVEL_3)
      .preProcessor(
          com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder().enable(true)
              .binaryThreshold(0.5).inverse(false).threshType(
                  com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.OTSU)
              .build())
      .area(CHARM_SLOT_2ND).build();
  private static final ImageCompare.Param CHARM_S4O2 = ImageCompare.Param.builder()
      .method(ImageCompare.Method.TM_CCOEFF)
      .template(CHARM_DECORATION_LEVEL_4)
      .preProcessor(
          com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder().enable(true)
              .binaryThreshold(0.5).inverse(false).threshType(
                  com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.OTSU)
              .build())
      .area(CHARM_SLOT_2ND).build();
  private static final Area CHARM_SLOT_3TH = Area.ofRect(1432, 314, 56, 48);
  private static final ImageCompare.Param CHARM_S0O3 = ImageCompare.Param.builder()
      .method(ImageCompare.Method.TM_CCOEFF)
      .template(CHARM_DECORATION_LEVEL_0)
      .preProcessor(
          com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder().enable(true)
              .binaryThreshold(0.5).inverse(false).threshType(
                  com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.OTSU)
              .build())
      .area(CHARM_SLOT_3TH).build();
  private static final ImageCompare.Param CHARM_S1O3 = ImageCompare.Param.builder()
      .method(ImageCompare.Method.TM_CCOEFF)
      .template(CHARM_DECORATION_LEVEL_1)
      .preProcessor(
          com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder().enable(true)
              .binaryThreshold(0.5).inverse(false).threshType(
                  com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.OTSU)
              .build())
      .area(CHARM_SLOT_3TH).build();
  private static final ImageCompare.Param CHARM_S2O3 = ImageCompare.Param.builder()
      .method(ImageCompare.Method.TM_CCOEFF)
      .template(CHARM_DECORATION_LEVEL_2)
      .preProcessor(
          com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder().enable(true)
              .binaryThreshold(0.5).inverse(false).threshType(
                  com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.OTSU)
              .build())
      .area(CHARM_SLOT_3TH).build();
  private static final ImageCompare.Param CHARM_S3O3 = ImageCompare.Param.builder()
      .method(ImageCompare.Method.TM_CCOEFF)
      .template(CHARM_DECORATION_LEVEL_3)
      .preProcessor(
          com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder().enable(true)
              .binaryThreshold(0.5).inverse(false).threshType(
                  com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.OTSU)
              .build())
      .area(CHARM_SLOT_3TH).build();
  private static final ImageCompare.Param CHARM_S4O3 = ImageCompare.Param.builder()
      .method(ImageCompare.Method.TM_CCOEFF)
      .template(CHARM_DECORATION_LEVEL_4)
      .preProcessor(
          com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder().enable(true)
              .binaryThreshold(0.5).inverse(false).threshType(
                  com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.OTSU)
              .build())
      .area(CHARM_SLOT_3TH).build();
  private static final OCR.Param EMPTY_POTS = OCR.Param.builder()
      .area(Area.ofRect(1602,418,44,44))
      .preProcessor(com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder()
          .enable(true)
          .inverse(true)
          .threshType(com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.OTSU)
          .build())
      .apiConfig(ApiConfig.builder()
          .method(OCR.Method.NMU)
          .whitelist("0123456789")
          .build())
      .build();
  private static final ImageCompare.Param IS_2ND_SKILL_EXITS = ImageCompare.Param.builder()
      .area(Area.ofRect(1408, 524, 52, 44))
      .method(ImageCompare.Method.TM_CCOEFF)
      .preProcessor(
          com.duanxr.pgcon.core.preprocessing.config.ColorPickFilterPreProcessorConfig.builder()
              .enable(true)
              .targetColor(javafx.scene.paint.Color.color(1.0, 1.0, 1.0))
              .range(0.18762888793975818)
              .pickType(
                  com.duanxr.pgcon.core.preprocessing.config.ColorPickFilterPreProcessorConfig.PickType.CIEDE2000)
              .maskType(
                  com.duanxr.pgcon.core.preprocessing.config.ColorPickFilterPreProcessorConfig.MaskType.BLACK)
              .inverse(true)
              .build())
      .preProcessor(com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder()
          .enable(true)
          .inverse(true)
          .threshType(
              com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.OTSU)
          .build())
      .template(
          "{\"R\":44,\"C\":52,\"T\":0,\"D\":\"H/8BAP///wgvAAAzACAPNADILgAAGAEPNABIHwA0ABkDNQAOswEPNAA9Ai8ADzQAIA5+AQ80AEEO5QEPCAIODzQAFAUBAA40AA81AA8HbwEPAQD//2tQ//////8=\",\"L\":2288}")
      .build();
  private static final ImageCompare.Param MELDING_POT_FINISHED = ImageCompare.Param.builder()
      .area(Area.ofRect(1244,160,136,24))
      .method(ImageCompare.Method.TM_CCOEFF)
      .preProcessor(com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder()
          .enable(true)
          .inverse(true)
          .threshType(com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.OTSU)
          .build())
      .template("{\"R\":24,\"C\":136,\"T\":0,\"D\":\"G/8BAD4AAAALAA8BAAkvAAAIAAMLSwAf/wEAHA+IAAQfAAEAAgMZAAguAAxAAA0xAA+IACofAIgABgWJAA+IAAMfAIgALA1uACT//1YAL/8AugADBP8ACEQBDw8BJA/2AAcGIAANTAAPfwEGA7YAD4gAQw86ABIHEgAPiAAuBhQADg8BD4gACAQwAB8AiAApBfMADnMDDw8BBApfAAu0AA6nAh8AiAAmD1AEDC8AAOkDBg8wAwEPiAA1DlQAHwBhAwUvAACIAC0PPAADDhgAD+kDCx8ASQMADz8EGA+IAD0PMAMqDsYAD8MDAQ9BAg0PiAAtDyACAB8AiAASH/+IADUX/00BDaEAH/+IAAgPsgEBDyACKg+IAAcf/4gAGB8AiABFDv8BCYgAHwAQAUQOMwAPiAAKDEwEH/+IAGYOIQAPiAAxDzMABA9YBAgf/zADBggsAQ9OBRwO3wcPiAANH/+IAA4fAIgATg4ZCwzzAg6IAA/mBhYPHQAID3QAAQ/IBAsNNwEPAQCEUP//////\",\"L\":3264}")
      .build();
  private static final ImageCompare.Param MELDING_POT_SELECTION = ImageCompare.Param.builder()
      .area(Area.ofRect(52,948,288,76))
      .method(ImageCompare.Method.TM_CCOEFF)
      .preProcessor(com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder()
          .enable(true)
          .inverse(true)
          .threshType(com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.OTSU)
          .build())
      .template("{\"R\":76,\"C\":288,\"T\":0,\"D\":\"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\",\"L\":21888}")
      .build();
  private static final ImageCompare.Param MHR_IN_GAME = ImageCompare.Param.builder()
      .area(Area.ofRect(896, 972, 68, 70))
      .method(ImageCompare.Method.TM_CCOEFF)
      .preProcessor(
          com.duanxr.pgcon.core.preprocessing.config.ColorPickFilterPreProcessorConfig.builder()
              .enable(true)
              .targetColor(javafx.scene.paint.Color.color(1.0, 1.0, 1.0))
              .range(0.2649484612513914)
              .pickType(
                  com.duanxr.pgcon.core.preprocessing.config.ColorPickFilterPreProcessorConfig.PickType.CIE94)
              .maskType(
                  com.duanxr.pgcon.core.preprocessing.config.ColorPickFilterPreProcessorConfig.MaskType.BLACK)
              .inverse(true)
              .build())
      .preProcessor(com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder()
          .enable(true)
          .inverse(true)
          .threshType(
              com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.OTSU)
          .build())
      .template(
          "{\"R\":70,\"C\":68,\"T\":0,\"D\":\"H/8BAP///1o/AAAAQQAuD0QALB4AQwAPQAAdDkMAD0EAHA9EACwHPQAOUQEPBQEdDoYAD4kBGw5EAA9DABsPGgIqCUQBDoYAD1ACFQ+hAioIjAEHBwAPQwAmHgBDAA9CABcPGQIoCRICD4YAKAlfAg+gAg4fAEMAAwEGAA9DABUGRAIq/wCGAC///8oAEwQgAAycAwSHAARJAA9EAB4JwQAOXQIPywAELAAARQAHnAEf/0QAEQxCAAdFAB8ARAAUHwABAAIIfQQPRAAOD0MAAx4AxwUPLAUSDxQBAg9EACwOgQIPYAMRDsMFDx4DFw9EACsv//9EADEGFgMPiAAqDlUBD0QANR8ATwcmD4kALh//RAAlBkgADygIMwhFAB//RAD/OA/LAHsf/8sKKQ+GALYf/0EALggHCA8BAP//slD//////w==\",\"L\":4760}")
      .build();
  private static final double MHR_IN_GAME_MENU_THRESHOLD = 0.9;
  private static final OCR.Param MPA_COUNT = OCR.Param.builder()
      .area(Area.ofRect(1444, 28, 174, 38))
      .preProcessor(
          com.duanxr.pgcon.core.preprocessing.config.ColorPickFilterPreProcessorConfig.builder()
              .enable(true)
              .targetColor(javafx.scene.paint.Color.color(1.0, 1.0, 1.0))
              .range(0.2649484612513914)
              .pickType(
                  com.duanxr.pgcon.core.preprocessing.config.ColorPickFilterPreProcessorConfig.PickType.CIEDE2000)
              .maskType(
                  com.duanxr.pgcon.core.preprocessing.config.ColorPickFilterPreProcessorConfig.MaskType.BLACK)
              .inverse(true)
              .build())
      .preProcessor(com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder()
          .enable(true)
          .inverse(true)
          .threshType(
              com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.OTSU)
          .build())
      .apiConfig(ApiConfig.builder()
          .method(OCR.Method.NMU)
          .whitelist("0123456789")
          .build())
      .build();
  private static final OCR.Param MP_COUNT = OCR.Param.builder()
      .area(Area.ofRect(904, 298, 114, 44))
      .preProcessor(
          com.duanxr.pgcon.core.preprocessing.config.ColorPickFilterPreProcessorConfig.builder()
              .enable(true)
              .targetColor(javafx.scene.paint.Color.color(1.0, 1.0, 1.0))
              .range(0.2649484612513914)
              .pickType(
                  com.duanxr.pgcon.core.preprocessing.config.ColorPickFilterPreProcessorConfig.PickType.CIEDE2000)
              .maskType(
                  com.duanxr.pgcon.core.preprocessing.config.ColorPickFilterPreProcessorConfig.MaskType.BLACK)
              .inverse(true)
              .build())
      .preProcessor(com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder()
          .enable(true)
          .inverse(true)
          .threshType(
              com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.OTSU)
          .build())
      .apiConfig(ApiConfig.builder()
          .method(OCR.Method.NMU)
          .whitelist("0123456789")
          .build())
      .build();
  private static final ImageCompare.Param NS_MAIN_MENU = ImageCompare.Param.builder()
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
  private static final OCR.Param POINTS_COUNT = OCR.Param.builder()
      .area(Area.ofRect(1694, 28, 124, 38))
      .preProcessor(
          com.duanxr.pgcon.core.preprocessing.config.ColorPickFilterPreProcessorConfig.builder()
              .enable(true)
              .targetColor(javafx.scene.paint.Color.color(1.0, 1.0, 1.0))
              .range(0.2649484612513914)
              .pickType(
                  com.duanxr.pgcon.core.preprocessing.config.ColorPickFilterPreProcessorConfig.PickType.CIEDE2000)
              .maskType(
                  com.duanxr.pgcon.core.preprocessing.config.ColorPickFilterPreProcessorConfig.MaskType.BLACK)
              .inverse(true)
              .build())
      .preProcessor(com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder()
          .enable(true)
          .inverse(true)
          .threshType(
              com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.OTSU)
          .build())
      .apiConfig(ApiConfig.builder()
          .method(OCR.Method.NMU)
          .whitelist("0123456789")
          .build())
      .build();
  private static final String SKILLS_OCR_WHITELIST = getSkillsOcrWhitelist();
  private static final OCR.Param CHARM_SKILL_1ST = OCR.Param.builder()
      .area(Area.ofRect(1158, 414, 310, 38))
      .preProcessor(
          com.duanxr.pgcon.core.preprocessing.config.ChannelsFilterPreProcessorConfig.builder()
              .enable(true)
              .redWeight(1.0)
              .greenWeight(1.0)
              .blueWeight(1.0)
              .build())
      .preProcessor(
          com.duanxr.pgcon.core.preprocessing.config.ColorPickFilterPreProcessorConfig.builder()
              .enable(true)
              .targetColor(javafx.scene.paint.Color.color(1.0, 1.0, 1.0))
              .range(0.6239261551118693)
              .pickType(
                  com.duanxr.pgcon.core.preprocessing.config.ColorPickFilterPreProcessorConfig.PickType.CIEDE2000)
              .maskType(
                  com.duanxr.pgcon.core.preprocessing.config.ColorPickFilterPreProcessorConfig.MaskType.BLACK)
              .inverse(true)
              .build())
      .preProcessor(com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder()
          .enable(true)
          .inverse(true)
          .threshType(
              com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.OTSU)
          .build())
      .apiConfig(ApiConfig.builder()
          .method(OCR.Method.CHS)
          .whitelist(SKILLS_OCR_WHITELIST)
          .build())
      .build();
  private static final OCR.Param CHARM_SKILL_2ND = Param.builder()
      .area(Area.ofRect(1160, 488, 312, 42))
      .preProcessor(
          com.duanxr.pgcon.core.preprocessing.config.ChannelsFilterPreProcessorConfig.builder()
              .enable(true)
              .redWeight(1.0)
              .greenWeight(1.0)
              .blueWeight(1.0)
              .build())
      .preProcessor(
          com.duanxr.pgcon.core.preprocessing.config.ColorPickFilterPreProcessorConfig.builder()
              .enable(true)
              .targetColor(javafx.scene.paint.Color.color(1.0, 1.0, 1.0))
              .range(0.6239261551118693)
              .pickType(
                  com.duanxr.pgcon.core.preprocessing.config.ColorPickFilterPreProcessorConfig.PickType.CIEDE2000)
              .maskType(
                  com.duanxr.pgcon.core.preprocessing.config.ColorPickFilterPreProcessorConfig.MaskType.BLACK)
              .inverse(true)
              .build())
      .preProcessor(com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder()
          .enable(true)
          .inverse(true)
          .threshType(
              com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.OTSU)
          .build())
      .apiConfig(ApiConfig.builder()
          .method(OCR.Method.CHS)
          .whitelist(SKILLS_OCR_WHITELIST)
          .build())
      .build();
  private final AtomicBoolean findTarget;
  private final Set<String> hashSet = new HashSet<>();
  private final Map<String, Integer> skillTargets;
  private final List<TargetSlot> slotTargets;

  public MHRiseCharmSaveLoadSearchCHS() {
    super(ScriptInfo.<MHRiseCharmSaveLoadSearchCHS.Config>builder()
        .config(new MHRiseCharmSaveLoadSearchCHS.Config())
        .isLoop(true)
        .description("MHR Charm S&L Search(CHS.Ver)")
        .build());
    this.slotTargets = new ArrayList<>();
    this.skillTargets = new ConcurrentHashMap<>();
    this.findTarget = new AtomicBoolean(false);
  }

  private static String getSkillsOcrWhitelist() {
    Set<Character> characterSet = new HashSet<>();
    AVAILABLE_SKILLS.keySet().forEach(
        skill -> skill.trim().chars().boxed().map(input -> (char) input.intValue())
            .forEach(characterSet::add));
    StringBuilder sb = new StringBuilder();
    characterSet.stream().sorted().forEach(sb::append);
    return sb.toString();
  }

  private static Map<String, String> getAvailableSkills() {
    Map<String, String> skills = Maps.newConcurrentMap();
    skills.put("爆破风性强化", "爆破属性强化");
    skills.put("麻狂局性强化", "麻痹属性强化");
    skills.put("逆袭", "逆袭");
    skills.put("逆效", "逆袭");
    skills.put("逆约", "逆袭");
    skills.put("逆装", "逆袭");
    skills.put("挑战者", "挑战者");
    skills.put("无伤", "无伤");
    skills.put("怨恨", "怨恨");
    skills.put("息恨", "怨恨");
    skills.put("铠恨", "怨恨");
    skills.put("死里逃生", "死里逃生");
    skills.put("看破", "看破");
    skills.put("超会心", "超会心");
    skills.put("超会", "超会心");
    skills.put("超会人", "超会心");
    skills.put("超会人心", "超会心");
    skills.put("超会心心", "超会心");
    skills.put("弱点特效", "弱点特效");
    skills.put("力量解放", "力量解放");
    skills.put("精神抖匠", "精神抖擞");
    skills.put("精神抖擞", "精神抖擞");
    skills.put("精神抖", "精神抖擞");
    skills.put("会心击【属性】", "会心击属性");
    skills.put("达人艺", "达人艺");
    skills.put("火属性攻击强化", "火属性攻击强化");
    skills.put("水属性攻击强化", "水属性攻击强化");
    skills.put("冰属性攻击强化", "冰属性攻击强化");
    skills.put("雷属性攻击强化", "雷属性攻击强化");
    skills.put("龙属性攻击强化", "龙属性攻击强化");
    skills.put("攻击", "攻击");
    skills.put("毒属性强化", "毒属性强化");
    skills.put("属性强化", "毒属性强化");
    skills.put("麻痹属性强化", "麻痹属性强化");
    skills.put("麻精属性强化", "麻痹属性强化");
    skills.put("麻属性强化", "麻痹属性强化");
    skills.put("麻王属性强化", "麻痹属性强化");
    skills.put("睡眠属性强化", "睡眠属性强化");
    skills.put("睡眠性强化", "睡眠属性强化");
    skills.put("爆破属性强化", "爆破属性强化");
    skills.put("破属性强化", "爆破属性强化");
    skills.put("爆破罗性强化", "爆破属性强化");
    skills.put("匠", "匠");
    skills.put("利刃", "利刃");
    skills.put("弹丸节约", "弹丸节约");
    skills.put("刚刃打磨", "刚刃打磨");
    skills.put("刃打磨", "刚刃打磨");
    skills.put("心眼", "心眼");
    skills.put("弹道强化", "弹道强化");
    skills.put("钝器能手", "钝器能手");
    skills.put("解放弓的蓄力阶段", "解放弓的蓄力阶段");
    skills.put("集中", "集中");
    skills.put("强化持续", "强化持续");
    skills.put("跑者", "跑者");
    skills.put("体术", "体术");
    skills.put("耐力急速回复", "耐力急速回复");
    skills.put("防御性能", "防御性能");
    skills.put("防御强化", "防御强化");
    skills.put("攻击守势", "攻击守势");
    skills.put("拔刀术【技", "拔刀术技");
    skills.put("拔刀术技】", "拔刀术技");
    skills.put("拔刀术技", "拔刀术技");
    skills.put("拔刀术【力】", "拔刀术力");
    skills.put("拔刀术力】", "拔刀术力");
    skills.put("拔刀术【力", "拔刀术力");
    skills.put("拔刀术力", "拔刀术力");
    skills.put("纳刀术", "纳刀术");
    skills.put("击晕术", "击晕术");
    skills.put("击术", "击晕术");
    skills.put("击加术", "击晕术");
    skills.put("夺取耐力", "夺取耐力");
    skills.put("取耐力", "夺取耐力");
    skills.put("滑走强化", "滑走强化");
    skills.put("吹笛名人", "吹笛名人");
    skills.put("炮术", "炮术");
    skills.put("炮弹装填", "炮弹装填");
    skills.put("特殊射击强化", "特殊射击强化");
    skills.put("通常弹.连射箭强人", "通常弹连射箭强化");
    skills.put("通常弹.连射强化", "通常弹连射箭强化");
    skills.put("通常弹.连射强人", "通常弹连射箭强化");
    skills.put("贯穿弹.贯穿箭强人", "贯穿弹贯穿箭强化");
    skills.put("散弹.扩散箭强人", "散弹扩散箭强化");
    skills.put("穿弹.贯穿箭强化", "贯穿弹贯穿箭强化");
    skills.put("贯穿.贯穿箭强化", "贯穿弹贯穿箭强化");
    skills.put("贯穿弹.穿箭强化", "贯穿弹贯穿箭强化");
    skills.put("散弹.扩散强化", "散弹扩散箭强化");
    skills.put("装填扩充", "装填扩充");
    skills.put("装填速度", "装填速度");
    skills.put("减轻后坐力", "减轻后坐力");
    skills.put("减轻后具力", "减轻后坐力");
    skills.put("减轻后人具力", "减轻后坐力");
    skills.put("减轻后人力", "减轻后坐力");
    skills.put("抑制偏移", "抑制偏移");
    skills.put("速射强化", "速射强化");
    skills.put("防御", "防御");
    skills.put("精灵加护", "精灵加护");
    skills.put("体力回复量提升", "体力回复量提升");
    skills.put("回复速度", "回复速度");
    skills.put("器复速度", "回复速度");
    skills.put("快吃", "快吃");
    skills.put("耳塞", "耳塞");
    skills.put("风压耐性", "风压耐性");
    skills.put("耐震", "耐震");
    skills.put("泡沫之舞", "泡沫之舞");
    skills.put("放沫之舞", "泡沫之舞");
    skills.put("沫之舞", "泡沫之舞");
    skills.put("回避性能", "回避性能");
    skills.put("避性能", "回避性能");
    skills.put("器避性能", "回避性能");
    skills.put("回避距离提升", "回避距离提升");
    skills.put("避距离提升", "回避距离提升");
    skills.put("回避离提升", "回避距离提升");
    skills.put("回避皮离提升", "回避距离提升");
    skills.put("器避距离提升", "回避距离提升");
    skills.put("火耐性", "火耐性");
    skills.put("水耐性", "水耐性");
    skills.put("冰耐性", "冰耐性");
    skills.put("雷耐性", "雷耐性");
    skills.put("龙耐性", "龙耐性");
    skills.put("属性异常状态的耐性", "属性异常状态的耐性");
    skills.put("毒耐性", "毒耐性");
    skills.put("麻痹耐性", "麻痹耐性");
    skills.put("睡眠耐性", "睡眠耐性");
    skills.put("雪厥耐性", "昏厥耐性");
    skills.put("昏厥耐性", "昏厥耐性");
    skills.put("厥耐性", "昏厥耐性");
    skills.put("泥雪耐性", "泥雪耐性");
    skills.put("爆破异常状态的耐性", "爆破异常状态的耐性");
    skills.put("植生学", "植生学");
    skills.put("地质学", "地质学");
    skills.put("破坏王", "破坏王");
    skills.put("捕获名人", "捕获名人");
    skills.put("剥取名人", "剥取名人");
    skills.put("幸运", "幸运");
    skills.put("砥石使用高速化", "砥石使用高速化");
    skills.put("石使用高速化", "砥石使用高速化");
    skills.put("石石使用高速化", "砥石使用高速化");
    skills.put("炸弹客", "炸弹客");
    skills.put("最爱蘑菇", "最爱蘑菇");
    skills.put("最爱菇", "最爱蘑菇");
    skills.put("道具使用强化", "道具使用强化");
    skills.put("广域化", "广域化");
    skills.put("满足感", "满足感");
    skills.put("满足不", "满足感");
    skills.put("火场怪力", "火场怪力");
    skills.put("不屈", "不屈");
    skills.put("减轻胆怯", "减轻胆怯");
    skills.put("跳跃铁人", "跳跃铁人");
    skills.put("剥取铁人", "剥取铁人");
    skills.put("取铁人", "剥取铁人");
    skills.put("饥饿耐性", "饥饿耐性");
    skills.put("饥属耐性", "饥饿耐性");
    skills.put("饥耐性", "饥饿耐性");
    skills.put("飞身跃入", "飞身跃入");
    skills.put("佯动", "佯动");
    skills.put("骑乘名人", "骑乘名人");
    skills.put("霞皮的恩惠", "霞皮的恩惠");
    skills.put("钢壳的恩惠", "钢壳的恩惠");
    skills.put("炎鳞的恩惠", "炎鳞的恩惠");
    skills.put("龙气活性", "龙气活性");
    skills.put("翔虫使", "翔虫使");
    skills.put("墙面移动", "墙面移动");
    skills.put("高速变形", "高速变形");
    skills.put("火缠", "鬼火缠");
    skills.put("鬼火缠", "鬼火缠");
    skills.put("鬼火雪", "鬼火缠");
    skills.put("风纹一致", "风纹一致");
    skills.put("雷纹一致", "雷纹一致");
    skills.put("风雷合一", "风雷合一");
    skills.put("气血", "气血");
    skills.put("伏魔耗命", "伏魔耗命");
    skills.put("激昂", "激昂");
    skills.put("业铠【修罗】", "业铠修罗");
    skills.put("因祸得福", "因祸得福");
    skills.put("祸得福", "因祸得福");
    skills.put("因福得福", "因祸得福");
    skills.put("狂龙症【蚀】", "狂龙症蚀");
    skills.put("合气", "合气");
    skills.put("提供", "提供");
    skills.put("蓄力大师", "蓄力大师");
    skills.put("攻力大师", "蓄力大师");
    skills.put("力大师", "蓄力大师");
    skills.put("攻势", "攻势");
    skills.put("零件改造", "零件改造");
    skills.put("件改造", "零件改造");
    skills.put("打魔术【锐】", "打磨术锐");
    skills.put("打麻术【锐", "打磨术锐");
    skills.put("打麻术【锐】", "打磨术锐");
    skills.put("刃鳞打磨", "刃鳞打磨");
    skills.put("迅之气息", "迅之气息");
    skills.put("连击", "连击");
    skills.put("会心击属性", "会心击属性");
    skills.put("走壁移动翔", "走壁移动翔");
    skills.put("通常弹连射箭强化", "通常弹连射箭强化");
    skills.put("穿弹贯穿箭强化", "贯穿弹贯穿箭强化");
    skills.put("贯穿弹贯穿箭强化", "贯穿弹贯穿箭强化");
    skills.put("睡眠风性强化", "睡眠属性强化");
    skills.put("打磨术锐", "打磨术锐");
    skills.put("丸旺打磨", "刚刃打磨"); //2
    skills.put("饥局耐性", "饥饿耐性"); //1
    skills.put("精神抖扩", "精神抖擞"); //1
    skills.put("麻业属性强化", "麻痹属性强化"); //1
    skills.put("麻狂性强化", "麻痹属性强化"); //1
    skills.put("丸打磨", "刚刃打磨"); //1
    skills.put("打麻术锐", "打磨术锐"); //1
    skills.put("精神抖激", "精神抖擞"); //1
    skills.put("通具使用强化", "道具使用强化"); //1
    skills.put("放弹扩散强化", "散弹扩散箭强化"); //2
    skills.put("散弹扩散强化", "散弹扩散箭强化"); //1
    skills.put("耳雪", "耳塞"); //1
    skills.put("夺人人", "达人艺"); //2
    skills.put("耳夺", "耳塞"); //1
    skills.put("扩具使用强化", "道具使用强化"); //1
    skills.put("麻狂属性强化", "麻痹属性强化"); //1
    skills.put("会心击性", "会心击属性"); //1
    skills.put("性异常状态的耐性", "属性异常状态的耐性"); //1
    skills.put("耳守", "耳塞"); //1
    skills.put("鬼火", "鬼火缠"); //1
    skills.put("用者", "跑者"); //1
    skills.put("爆破性强化", "爆破属性强化"); //1
    skills.put("风丸打磨", "刚刃打磨"); //2
    skills.put("会心击罗性", "会心击属性"); //1
    skills.put("逆节", "逆袭"); //1
    skills.put("雷风性攻击强化", "雷属性攻击强化"); //1
    skills.put("昏打耐性", "昏厥耐性"); //1
    skills.put("饥蚀性", "饥饿耐性"); //2
    skills.put("业铠修罗", "业铠修罗");
    skills.put("狂龙症蚀", "狂龙症蚀");
    skills.put("逆芍", "逆袭"); //distance:1 confidence:48
    skills.put("逆获", "逆袭"); //distance:1 confidence:44
    skills.put("蛋丸打麻", "刚刃打磨"); //distance:3 confidence:19
    skills.put("减轻胆性", "减轻胆怯"); //distance:1 confidence:86
    skills.put("刃鳞打麻", "刃鳞打磨"); //distance:1 confidence:46
    skills.put("囊丸打磨", "刚刃打磨"); //distance:2 confidence:44
    skills.put("刚丸打磨", "刚刃打磨"); //distance:1 confidence:74
    skills.put("刚丸打麻", "刚刃打磨"); //distance:2 confidence:44
    skills.put("囊丸打麻", "刚刃打磨"); //distance:3 confidence:21
    skills.put("力最解放", "力量解放"); //distance:1 confidence:89
    skills.put("鬼火总", "鬼火缠"); //distance:1 confidence:43
    skills.put("丰石使用高速化", "砥石使用高速化"); //distance:1 confidence:67
    skills.put("鬼火起", "鬼火缠"); //distance:1 confidence:37
    skills.put("鬼火纺", "鬼火缠"); //distance:1 confidence:34
    skills.put("局性异常状态的耐性", "属性异常状态的耐性"); //distance:1 confidence:89
    skills.put("打磨术【锐】", "打磨术锐"); //distance:2 confidence:78
    skills.put("而力急速回复", "耐力急速回复"); //distance:1 confidence:81
    skills.put("睡眠局性强化", "睡眠属性强化"); //distance:1 confidence:87
    skills.put("耳计", "耳塞"); //distance:1 confidence:43
    skills.put("耳讲", "耳塞"); //distance:1 confidence:43
    skills.put("逆敬", "逆袭"); //distance:1 confidence:45
    skills.put("逆菩", "逆袭"); //distance:1 confidence:45
    skills.put("而震", "耐震"); //distance:1 confidence:61
    skills.put("回避虐离提升", "回避距离提升"); //distance:1 confidence:70
    skills.put("贯穿弹.贯穿箭强化", "贯穿弹贯穿箭强化"); //distance:1 confidence:67
    skills.put("贯穿弹.贯穿薄强化", "贯穿弹贯穿箭强化"); //distance:2 confidence:64
    skills.put("区", "匠"); //distance:1 confidence:80
    skills.put("走壁移动【翔】", "走壁移动翔"); //distance:2 confidence:53
    skills.put("急恨", "怨恨"); //distance:1 confidence:41
    skills.put("遂具使用强化", "道具使用强化"); //distance:1 confidence:61
    skills.put("击暴术", "击晕术"); //distance:1 confidence:50
    skills.put("击景术", "击晕术"); //distance:1 confidence:58
    skills.put("击早术", "击晕术"); //distance:1 confidence:57
    skills.put("击术术", "击晕术"); //distance:1 confidence:28
    skills.put("会心击【局性】", "会心击属性"); //distance:3 confidence:83
    skills.put("会心击【忆性】", "会心击属性"); //distance:3 confidence:69
    skills.put("逆疗", "逆袭"); //distance:1 confidence:46
    skills.put("拔刀术【力]", "拔刀术力"); //distance:2 confidence:79
    skills.put("散弹.扩散箭强化", "散弹扩散箭强化"); //distance:1 confidence:67
    skills.put("散弹.扩散笨强化", "散弹扩散箭强化"); //distance:2 confidence:63
    skills.put("散弹.扩散簿强化", "散弹扩散箭强化"); //distance:2 confidence:68
    skills.put("通常弹.连射箭强化", "通常弹连射箭强化"); //distance:1 confidence:85
    skills.put("埋厥耐性", "昏厥耐性"); //distance:1 confidence:49
    skills.put("霸厥耐性", "昏厥耐性"); //distance:1 confidence:32
    skills.put("击桶术", "击晕术"); //distance:1 confidence:30
    skills.put("精神拌撤", "精神抖擞"); //distance:2 confidence:60
    skills.put("著力大师", "蓄力大师"); //distance:1 confidence:62
    skills.put("着力大师", "蓄力大师"); //distance:1 confidence:66
    skills.put("闭力大师", "蓄力大师"); //distance:1 confidence:58
    skills.put("蔷力大师", "蓄力大师"); //distance:1 confidence:51
    skills.put("善力大师", "蓄力大师"); //distance:1 confidence:68
    skills.put("雷赂性攻击强化", "雷属性攻击强化"); //distance:1 confidence:79
    skills.put("体力回复最提升", "体力回复量提升"); //distance:1 confidence:95
    skills.put("拔刀术[力]", "拔刀术力"); //distance:2 confidence:63
    skills.put("打麻术【锐]", "打磨术锐"); //distance:3 confidence:80
    skills.put("打磨术【锐]", "打磨术锐"); //distance:2 confidence:78
    skills.put("走壁移动【翔", "走壁移动翔"); //distance:1 confidence:68
    skills.put("率运", "幸运"); //distance:1 confidence:41
    skills.put("它运", "幸运"); //distance:1 confidence:30
    skills.put("散弹.扩散薪强化", "散弹扩散箭强化"); //distance:2 confidence:48
    skills.put("族弹.扩散薪强化", "散弹扩散箭强化"); //distance:3 confidence:46
    skills.put("族弹.扩散和薪强化", "散弹扩散箭强化"); //distance:4 confidence:47
    skills.put("族弹.扩散称强化", "散弹扩散箭强化"); //distance:3 confidence:48
    skills.put("散弹.扩散蓟强化", "散弹扩散箭强化"); //distance:2 confidence:51
    skills.put("淫弹.扩散薪强化", "散弹扩散箭强化"); //distance:3 confidence:48
    skills.put("淫弹.扩散称强化", "散弹扩散箭强化"); //distance:3 confidence:57
    skills.put("散弹.扩散称强化", "散弹扩散箭强化"); //distance:2 confidence:56
    skills.put("拔刀术【技】", "拔刀术技"); //distance:2 confidence:87
    skills.put("录取铁人", "剥取铁人"); //distance:1 confidence:50
    skills.put("鬼火坊", "鬼火缠"); //distance:1 confidence:34
    skills.put("精神拌数", "精神抖擞"); //distance:2 confidence:59
    skills.put("精神拌抠", "精神抖擞"); //distance:2 confidence:59
    skills.put("精神拌拆", "精神抖擞"); //distance:2 confidence:59
    skills.put("精神拌巩", "精神抖擞"); //distance:2 confidence:59
    skills.put("精神拌激", "精神抖擞"); //distance:2 confidence:58
    skills.put("精神拌找", "精神抖擞"); //distance:2 confidence:58
    skills.put("精神拌娄", "精神抖擞"); //distance:2 confidence:59
    skills.put("三石使用高速化", "砥石使用高速化"); //distance:1 confidence:77
    skills.put("则丸打磨", "刚刃打磨"); //distance:2 confidence:46
    skills.put("出丸打磨", "刚刃打磨"); //distance:2 confidence:44
    skills.put("会心击【思性】", "会心击属性"); //distance:3 confidence:67
    skills.put("会心击【辕性】", "会心击属性"); //distance:3 confidence:76
    skills.put("回避具离提升", "回避距离提升"); //distance:1 confidence:47
    skills.put("散弹.扩散稍强化", "散弹扩散箭强化"); //distance:2 confidence:73
    skills.put("最爱蓄菇", "最爱蘑菇"); //distance:1 confidence:58
    skills.put("拔刀术【技]", "拔刀术技"); //distance:2 confidence:86
    skills.put("拔刀术【[力]", "拔刀术力"); //distance:3 confidence:65
    skills.put("员穿弹.贯穿箭强化", "贯穿弹贯穿箭强化"); //distance:2 confidence:60
    skills.put("钳厥耐性", "昏厥耐性"); //distance:1 confidence:39
    skills.put("鬼火绰", "鬼火缠"); //distance:1 confidence:47
    skills.put("击棱术", "击晕术"); //distance:1 confidence:58
    skills.put("打魔术【锐]", "打磨术锐"); //distance:3 confidence:76
    skills.put("麻闻属性强化", "麻痹属性强化"); //distance:1 confidence:56
    skills.put("麻靖属性强化", "麻痹属性强化"); //distance:1 confidence:64
    skills.put("麻兽属性强化", "麻痹属性强化"); //distance:1 confidence:58
    skills.put("最爱葛菇", "最爱蘑菇"); //distance:1 confidence:58
    skills.put("哆者", "跑者"); //distance:1 confidence:78
    skills.put("利丸", "利刃"); //distance:1 confidence:45
    skills.put("麻狼属性强化", "麻痹属性强化"); //distance:1 confidence:56
    skills.put("麻狼局性强化", "麻痹属性强化"); //distance:2 confidence:64
    skills.put("麻病属性强化", "麻痹属性强化"); //distance:1 confidence:63
    skills.put("爆破局性强化", "爆破属性强化"); //distance:1 confidence:73
    skills.put("爆破周性强化", "爆破属性强化"); //distance:1 confidence:77
    skills.put("不邮", "不屈"); //distance:1 confidence:46
    skills.put("芯力大师", "蓄力大师"); //distance:1 confidence:51
    skills.put("苹力大师", "蓄力大师"); //distance:1 confidence:54
    skills.put("茧力大师", "蓄力大师"); //distance:1 confidence:52
    skills.put("逆车", "逆袭"); //distance:1 confidence:57
    skills.put("击梭术", "击晕术"); //distance:1 confidence:40
    skills.put("族弹.扩散箔强化", "散弹扩散箭强化"); //distance:3 confidence:48
    skills.put("淫弹.扩散簿强化", "散弹扩散箭强化"); //distance:3 confidence:51
    skills.put("体力回复最近升", "体力回复量提升"); //distance:2 confidence:62
    skills.put("不悍", "不屈"); //distance:1 confidence:46
    skills.put("鬼火地", "鬼火缠"); //distance:1 confidence:34
    skills.put("会心击【局性]", "会心击属性"); //distance:3 confidence:76
    skills.put("会心击【局性", "会心击属性"); //distance:2 confidence:72
    skills.put("会心击【属性]", "会心击属性"); //distance:2 confidence:74
    skills.put("衬运", "幸运"); //distance:1 confidence:38
    skills.put("散弹.扩散和薪强化", "散弹扩散箭强化"); //distance:3 confidence:48
    skills.put("族弹.扩散簿强化", "散弹扩散箭强化"); //distance:3 confidence:50
    skills.put("击最术", "击晕术"); //distance:1 confidence:57
    skills.put("鬼火强", "鬼火缠"); //distance:1 confidence:34
    skills.put("精神拌抽", "精神抖擞"); //distance:2 confidence:58
    skills.put("精神拌灿", "精神抖擞"); //distance:2 confidence:58
    skills.put("饥馈耐性", "饥饿耐性"); //distance:1 confidence:56
    skills.put("员穿弹-贯穿箭强化", "贯穿弹贯穿箭强化"); //distance:2 confidence:66
    skills.put("员穿弹`贯穿箭强化", "贯穿弹贯穿箭强化"); //distance:2 confidence:59
    skills.put("精神拌烧", "精神抖擞"); //distance:2 confidence:59
    skills.put("精神拌粕", "精神抖擞"); //distance:2 confidence:58
    skills.put("精神拌涩", "精神抖擞"); //distance:2 confidence:58
    skills.put("利怕", "利刃"); //distance:1 confidence:45
    skills.put("鬼火纯", "鬼火缠"); //distance:1 confidence:30
    skills.put("忽恨", "怨恨"); //distance:1 confidence:32
    skills.put("员穿弹.贯穿简强化", "贯穿弹贯穿箭强化"); //distance:3 confidence:61
    skills.put("贯穿弹-贯穿箭强化", "贯穿弹贯穿箭强化"); //distance:1 confidence:65
    skills.put("会心击【属性", "会心击属性"); //distance:1 confidence:69
    skills.put("胞者", "跑者"); //distance:1 confidence:48
    skills.put("风和压耐性", "风压耐性"); //distance:1 confidence:61
    skills.put("吉属性强化", "毒属性强化"); //distance:1 confidence:48
    skills.put("中者", "跑者"); //distance:1 confidence:31
    skills.put("史者", "跑者"); //distance:1 confidence:34
    skills.put("散弹.扩散箔强化", "散弹扩散箭强化"); //distance:2 confidence:52
    skills.put("兄者", "跑者"); //distance:1 confidence:48
    skills.put("逆区", "逆袭"); //distance:1 confidence:46
    skills.put("通常弹.连射稍强化", "通常弹连射箭强化"); //distance:2 confidence:81
    skills.put("饥饭耐性", "饥饿耐性"); //distance:1 confidence:62
    skills.put("精神拌执", "精神抖擞"); //distance:2 confidence:58
    skills.put("饥蚀耐性", "饥饿耐性"); //distance:1 confidence:51
    skills.put("起会心", "超会心"); //distance:1 confidence:49
    skills.put("会心击【轩性】", "会心击属性"); //distance:3 confidence:69
    skills.put("了哆者", "挑战者"); //distance:2 confidence:59
    skills.put("基避虐离提升", "回避距离提升"); //distance:2 confidence:32
    skills.put("麻阁性强化", "麻痹属性强化"); //distance:2 confidence:67
    skills.put("击肾术", "击晕术"); //distance:1 confidence:30
    skills.put("风于耐性", "风压耐性"); //distance:1 confidence:54
    skills.put("麻州局性强化", "麻痹属性强化"); //distance:2 confidence:65
    skills.put("淫弹.扩散和薪强化", "散弹扩散箭强化"); //distance:4 confidence:45
    skills.put("族弹.扩散笨强化", "散弹扩散箭强化"); //distance:3 confidence:50
    skills.put("淫弹.扩散箔强化", "散弹扩散箭强化"); //distance:3 confidence:48
    skills.put("出丸打麻", "刚刃打磨"); //distance:3 confidence:39
    skills.put("羡力大师", "蓄力大师"); //distance:1 confidence:46
    skills.put("麻闻赂性强化", "麻痹属性强化"); //distance:2 confidence:56
    skills.put("散弹.扩散科强化", "散弹扩散箭强化"); //distance:2 confidence:58
    skills.put("回避臣离提升", "回避距离提升"); //distance:1 confidence:45
    skills.put("饥锯耐性", "饥饿耐性"); //distance:1 confidence:55
    skills.put("麻州属性强化", "麻痹属性强化"); //distance:1 confidence:58
    skills.put("弹拥强化", "弹道强化"); //distance:1 confidence:46
    skills.put("昔厥耐性", "昏厥耐性"); //distance:1 confidence:32
    skills.put("员穿弹.-贯穿箭强化", "贯穿弹贯穿箭强化"); //distance:3 confidence:58
    skills.put("丸鳞打磨", "刃鳞打磨"); //distance:1 confidence:36
    skills.put("地件改造", "零件改造"); //distance:1 confidence:66
    skills.put("硅取耐力", "夺取耐力"); //distance:1 confidence:59
    skills.put("如取铁人", "剥取铁人"); //distance:1 confidence:56
    skills.put("错厥耐性", "昏厥耐性"); //distance:1 confidence:35
    skills.put("者厥耐性", "昏厥耐性"); //distance:1 confidence:48
    skills.put("丸皇打磨", "刃鳞打磨"); //distance:2 confidence:35
    skills.put("利胃", "利刃"); //distance:1 confidence:46
    skills.put("埋顾耐性", "昏厥耐性"); //distance:2 confidence:35
    skills.put("昏厄耐性", "昏厥耐性"); //distance:1 confidence:24
    skills.put("回避丰离提升", "回避距离提升"); //distance:1 confidence:60
    skills.put("钳顾耐性", "昏厥耐性"); //distance:2 confidence:16
    skills.put("吹条名人", "吹笛名人"); //distance:1 confidence:45
    skills.put("霸顾耐性", "昏厥耐性"); //distance:2 confidence:29
    skills.put("基避性能", "回避性能"); //distance:1 confidence:46
    skills.put("夫件改造", "零件改造"); //distance:1 confidence:64
    skills.put("卉厥耐性", "昏厥耐性"); //distance:1 confidence:35
    skills.put("副取铁人", "剥取铁人"); //distance:1 confidence:54
    skills.put("丸旦打磨", "刃鳞打磨"); //distance:2 confidence:34
    skills.put("族弹.扩散科强化", "散弹扩散箭强化"); //distance:3 confidence:55
    skills.put("扳火缠", "鬼火缠"); //distance:1 confidence:34
    skills.put("放弹.扩散强化", "散弹扩散箭强化"); //1
    skills.put("放弹.扩散箭强化", "散弹扩散箭强化"); //1
    skills.put("运", "幸运"); //distance:1 confidence:0
    skills.put("贯穿弹.贯穿简强化", "贯穿弹贯穿箭强化"); //distance:2 confidence:62
    skills.put("攻厥耐性", "昏厥耐性"); //distance:1 confidence:31
    return skills;
  }

  @Override
  @SneakyThrows
  public void execute() {
    if (!config.charmAnalysisDebugMode.get()) {
      info("{} charms have been checked.", config.getCheckedCharms().get());
      launchGame();
      walkToShop();
      toMeldingPot();
      try {
        gazeStars();
      } catch (ResetScriptException e) {
        info("Reset script, change to new random seed.", e);
      }
      changeRandomSeed();
    } else {
      charmAnalysisDebug();
    }
  }

  @Override
  public void load() {
    String targetSlots = config.getTargetSlots().getValue();
    slotTargets.clear();
    slotTargets.addAll(parseSlotTargets(targetSlots));
    if (slotTargets.isEmpty()) {
      throw new AlertErrorException("Slot targets is empty!");
    }
    info("Target Slots: {}", slotTargets);
    Set<String> collect = new HashSet<>(AVAILABLE_SKILLS.values());
    String targetSkills = config.getTargetSkills().getValue();
    skillTargets.clear();
    skillTargets.putAll(parseSkillTargets(targetSkills, collect));
    info("Target Skills: {}", skillTargets);
  }

  private static Set<TargetSlot> parseSlotTargets(String targetSlots) {
    Set<TargetSlot> targetSlotSet = new HashSet<>();
    String[] split = targetSlots.split("\n");
    for (String targetSlot : split) {
      String trim = targetSlot.trim();
      if (trim.length() == 0) {
        continue;
      }
      if (trim.length() != 3) {
        throw new AlertErrorException("Invalid slot target: " + trim);
      }
      List<Integer> list = new ArrayList<>();
      for (int i = 0; i < trim.length(); i++) {
        char c = trim.charAt(i);
        if (c < 48 || c > 52) {
          throw new AlertErrorException("Invalid slot target: " + trim);
        }
        list.add(c - 48);
      }
      list.sort(Comparator.<Integer>comparingInt(i -> i).reversed());
      targetSlotSet.add(new TargetSlot(list.get(0), list.get(1), list.get(2)));
    }
    return targetSlotSet;
  }

  private static Map<String, Integer> parseSkillTargets(String targetSkills,
      Set<String> validator) {
    Map<String, Integer> target = Maps.newHashMap();
    String[] split = targetSkills.split("\n");
    for (String targetSkill : split) {
      String trim = targetSkill.trim();
      if (trim.trim().length() == 0) {
        continue;
      }
      if (trim.length() < 2) {
        throw new AlertErrorException("Invalid target skill : " + trim);
      }
      char level = trim.charAt(trim.length() - 1);
      if (level < 48 || level > 57) {
        throw new AlertErrorException("Invalid skill level: " + trim);
      }
      String skill = trim.substring(0, trim.length() - 1);
      if (!validator.contains(skill)) {
        throw new AlertErrorException("Invalid skill: " + trim);
      }
      target.put(skill, level - 48);
    }
    return target;
  }

  private void charmAnalysisDebug() {
    charmAnalyze();
    sleep(2000);
  }

  private void gazeStars() {
    while (!Thread.interrupted()) {
      sleep(1000);
      long potSlot = detectAccurateLong(EMPTY_POTS, 7, 10000L);
      if (potSlot == 0) {
        info("All pots are full, reset script.");
        throw new ResetScriptException();
      }
      fillPot(potSlot);
      checkCharms();
      if (findTarget.get()) {
        backToGameMenu();
        saveAndExit();
        launchGame();
        walkToShop();
        toMeldingPot();
      } else {
        sleep(150);
        press(D_BOTTOM);
        sleep(150);
      }
    }
  }

  @SneakyThrows
  private void checkMaterial(long need) {
    Future<Long> mpF = async(() -> detectAccurateLong(MP_COUNT, 7, 10000L));
    Future<Long> mpaF = async(() -> detectAccurateLong(MPA_COUNT, 7, 10000L));
    Future<Long> pointF = async(() -> detectAccurateLong(POINTS_COUNT, 7, 10000L));

    long mp = mpF.get();
    long mpa = mpaF.get();
    long point = pointF.get();

    info("You have {} Melding Puddings and {} MP Accelerants and {} Points", mp, mpa, point);
    if (mpa < need) {
      throw new ResetScriptException(
          "There are not enough MP Accelerants! need " + need + " but only have " + mpa);
    }
    long mpn = need * 5;
    if (mp < mpn) {
      throw new ResetScriptException(
          "There are not enough Melding Puddings! need " + mpn + " but only have " + mp);
    }
    long pn = need * 1000;
    if (point < pn) {
      throw new ResetScriptException(
          "There are not enough Points! need " + pn + " but only have " + point);
    }
  }

  private void changeRandomSeed() {
    abortGame();
    launchGame();
    walkToShop();
    toMeldingPot();
    try {
      fillPot(1);
    } catch (ResetScriptException e) {
      error("There are not enough materials, abort script!");
      throw new InterruptScriptException(e);
    }
    getAllCharms();
    backToGameMenu();
    saveAndExit();
  }

  private void fillPot(long times) {
    press(A);
    sleep(150);
    checkMaterial(times);
    for (int i = 0; i < times; i++) {
      fillPotOnce();
      if (i != times - 1) {
        press(A);
        sleep(200);
      }
    }
  }

  private void launchGame() {
    script("LunchGame");
    until(() -> detect(MHR_IN_GAME),
        result -> result.getSimilarity() > MHR_IN_GAME_MENU_THRESHOLD,
        () -> {
          press(A);
          sleep(150);
        });
  }

  private void walkToShop() {
    sleep(1000);
    hold(L_TOP);
    sleep(5350);
    hold(L_LEFT);
    sleep(1300);
    release(L_LEFT);
  }

  private void toMeldingPot() {
    press(A);
    sleep(150);
    until(() -> detect(MELDING_POT_SELECTION),
        input -> input.getSimilarity() > 0.9,
        () -> {
          press(D_TOP);
          sleep(250);
        }, 10000L, this::reset);
    press(A);
    sleep(300);
  }

  private void reset() {
    press(HOME);
    sleep(3000);
    press(X);
    sleep(300);
    press(A);
    sleep(5000);
  }

  private void fillPotOnce() {
    press(A);
    sleep(200);
    press(D_BOTTOM);
    sleep(200);
    press(A);
    sleep(200);
    press(D_TOP);
    sleep(200);
    press(A);
    sleep(200);
    press(D_LEFT);
    sleep(200);
    press(A);
    sleep(200);
    press(A);
    sleep(200);
  }

  private void checkCharms() {
    press(D_TOP);
    sleep(200);
    press(A);
    sleep(1000);
    findTarget.set(false);
    until(() -> detect(MELDING_POT_FINISHED),
        input -> input.getSimilarity() < 0.9,
        () -> {
          if (charmAnalyze()) {
            findTarget.set(true);
          }
          press(A);
          sleep(350);
        });
    sleep(200);
    press(A);
  }

  private void abortGame() {
    toMainMenu();
    press(X);
    sleep(200);
    press(A);
    sleep(200);
  }

  private void getAllCharms() {
    sleep(1000);
    press(D_TOP);
    sleep(200);
    press(A);
    sleep(200);
    press(D_TOP);
    sleep(200);
    press(A);
    sleep(200);
  }

  private void backToGameMenu() {
    until(() -> detect(MHR_IN_GAME),
        input -> input.getSimilarity() > MHR_IN_GAME_MENU_THRESHOLD,
        () -> {
          press(B);
          sleep(150);
        });
  }

  private void saveAndExit() {
    press(PLUS);
    sleep(500);
    press(D_LEFT);
    sleep(150);
    press(D_TOP);
    sleep(150);
    press(A);
    sleep(200);
    press(A);
    sleep(200);
    press(A);
    sleep(200);
    press(A);
    sleep(200);
    press(A);
    sleep(200);
    press(A);
    sleep(200);
  }

  @SneakyThrows
  private boolean charmAnalyze() {
    boolean hasSkill2 = detect(IS_2ND_SKILL_EXITS).getSimilarity() > 0.8;

    Future<OCR.Result> level2F = hasSkill2 ? async(() -> detect(CHARM_LEVEL_2ND)) : null;
    Future<String> skill2F = hasSkill2 ? async(() -> detectSkill(CHARM_SKILL_2ND)) : null;

    Future<OCR.Result> level1F = async(() -> detect(CHARM_LEVEL_1ST));
    Future<String> skill1F = async(() -> detectSkill(CHARM_SKILL_1ST));

    Future<OCR.Result> rareF = async(() -> detect(CHARM_RARE_LEVEL));
    Future<ImageCompare.Result> s0o1F = async(() -> detect(CHARM_S0O1));
    Future<ImageCompare.Result> s1o1F = async(() -> detect(CHARM_S1O1));
    Future<ImageCompare.Result> s2o1F = async(() -> detect(CHARM_S2O1));
    Future<ImageCompare.Result> s3o1F = async(() -> detect(CHARM_S3O1));
    Future<ImageCompare.Result> s4o1F = async(() -> detect(CHARM_S4O1));
    Future<ImageCompare.Result> s0o2F = async(() -> detect(CHARM_S0O2));
    Future<ImageCompare.Result> s1o2F = async(() -> detect(CHARM_S1O2));
    Future<ImageCompare.Result> s2o2F = async(() -> detect(CHARM_S2O2));
    Future<ImageCompare.Result> s3o2F = async(() -> detect(CHARM_S3O2));
    Future<ImageCompare.Result> s4o2F = async(() -> detect(CHARM_S4O2));
    Future<ImageCompare.Result> s0o3F = async(() -> detect(CHARM_S0O3));
    Future<ImageCompare.Result> s1o3F = async(() -> detect(CHARM_S1O3));
    Future<ImageCompare.Result> s2o3F = async(() -> detect(CHARM_S2O3));
    Future<ImageCompare.Result> s3o3F = async(() -> detect(CHARM_S3O3));
    Future<ImageCompare.Result> s4o3F = async(() -> detect(CHARM_S4O3));

    String skill1 = skill1F.get();
    String skill2 = hasSkill2 ? skill2F.get() : null;
    Long level1 = level1F.get().getTextAsNumber();
    Long level2 = hasSkill2 ? level2F.get().getTextAsNumber() : null;

    String rare = rareF.get().getTextWithoutSpace().toUpperCase();
    rare = rare.startsWith("RARE") ? rare.substring(4) : "?";

    List<ImageCompare.Result> o1L = Arrays.asList(s0o1F.get(), s1o1F.get(), s2o1F.get(),
        s3o1F.get(), s4o1F.get());
    List<ImageCompare.Result> o2L = Arrays.asList(s0o2F.get(), s1o2F.get(), s2o2F.get(),
        s3o2F.get(), s4o2F.get());
    List<ImageCompare.Result> o3L = Arrays.asList(s0o3F.get(), s1o3F.get(), s2o3F.get(),
        s3o3F.get(), s4o3F.get());

    int o1 = getMax(o1L);
    int o2 = getMax(o2L);
    int o3 = getMax(o3L);

    boolean isTarget = checkSkill(skill1, level1, skill2, level2) && checkSlot(o1, o2, o3);
    String result = String.format("R%s %s%s %s%s S%s%s%s", rare, skill1, level1,
        hasSkill2 ? skill2 : "", hasSkill2 ? level2 : "", o1, o2, o3);
    info("DingZhen the One-Eye identified the charm as : {}", result);
    if (isTarget) {
      warn("find a target charm:{}", result);
      press(PLUS);
      sleep(200);
      if (config.getRecordVideoWhenFind().get()) {
        hold(CAPTURE);
        sleep(2000);
        release(CAPTURE);
        sleep(200);
      }
      if (config.getNotifyWhenFind().get()) {
        push("find a target charm:" + result);
      }
    }
    config.getCheckedCharms().set(config.getCheckedCharms().getValue() + 1);
    return isTarget;
  }

  private void toMainMenu() {
    until(() -> detect(NS_MAIN_MENU),
        input -> input.getSimilarity() > 0.8,
        () -> {
          press(HOME);
          sleep(1000);
        });
  }

  private String detectSkill(OCR.Param param) {
    try {
      OCR.Result until = until(() -> detect(param),
          input -> {
            boolean containsKey = AVAILABLE_SKILLS.containsKey(input.getTextWithoutSpace());
            String text = input.getTextWithoutSpace();
            if (!Strings.isNullOrEmpty(input.getTextWithoutSpace()) && !containsKey) {
              info("Unknown skill detected: {} , confidence: {} ", text, input.getConfidence());
              calculateSkill(text, input.getConfidence());
            }
            return containsKey;
          },
          () -> sleep(50), 10000L);
      return until == null ? "" : AVAILABLE_SKILLS.get(until.getTextWithoutSpace());
    } catch (Exception e) {
      return "";
    }
  }

  private void calculateSkill(String text, int confidence) {
    LevenshteinDistance defaultInstance = LevenshteinDistance.getDefaultInstance();
    List<Pair<Integer, String>> list = new ArrayList<>();
    AVAILABLE_SKILLS.forEach((k, v) -> {
      Integer score = defaultInstance.apply(v, text);
      list.add(Pair.create(score, v));
    });
    Comparator<Pair<Integer, String>> comparing = Comparator.comparingInt(Pair::getKey);
    Comparator<Pair<Integer, String>> comparator = comparing.thenComparingInt(
        (Pair<Integer, String> o) -> o.getValue().length());
    list.sort(comparing);
    Pair<Integer, String> pair = list.get(0);
    String formatted = "skills.put(\"%s\", \"%s\"); //distance:%s confidence:%s\n".formatted(text,
        pair.getSecond(),
        pair.getFirst(), confidence);
    if (hashSet.add(text + "|" + pair.getSecond())) {
      System.out.println(formatted);
      if (config.captureScreenWhenUnknownSkillDetected.get()) {
        press(CAPTURE);
      }
      if (config.enableAutomaticSkillMatching.get()) {
        AVAILABLE_SKILLS.put(text, pair.getSecond());
      }
    }
  }

  private int getMax(List<ImageCompare.Result> list) {
    int max = 0;
    double point = 0.0;
    for (int i = 0; i < list.size(); i++) {
      Double similarity = list.get(i).getSimilarity();
      if (similarity > point) {
        max = i;
        point = similarity;
      }
    }
    return max;
  }

  private boolean checkSkill(String skill1, Long level1, String skill2, Long level2) {
    Integer l1 = skillTargets.get(skill1);
    Integer l2 = skill2 == null ? null : skillTargets.get(skill2);
    return l1 != null && l2 != null && l1 <= (level1 == null ? 0 : level1) && l2 <= (level2 == null
        ? 0 : level2);
  }

  private boolean checkSlot(int o1, int o2, int o3) {
    for (TargetSlot slotTarget : slotTargets) {
      if (slotTarget.isMatch(o1, o2, o3)) {
        return true;
      }
    }
    return false;
  }

  @Data
  public static class Config {

    @ConfigLabel("Target Skills")
    @FormFactory(TextAreaFactory.class)
    private SimpleStringProperty targetSkills = new SimpleStringProperty("""
        攻击2
        看破2
        超会心2
        弱点特效2""");
    @ConfigLabel("Target Slots")
    @FormFactory(TextAreaFactory.class)
    private SimpleStringProperty targetSlots = new SimpleStringProperty("""
        220
        310
        400""");


    @ConfigLabel("Record Video When Find")
    private SimpleBooleanProperty recordVideoWhenFind = new SimpleBooleanProperty(true);

    @ConfigLabel("Notify Me When Find")
    private SimpleBooleanProperty notifyWhenFind = new SimpleBooleanProperty(true);

    @ConfigLabel("Enable Automatic Skill Matching")
    private SimpleBooleanProperty enableAutomaticSkillMatching = new SimpleBooleanProperty(false);
    @ConfigLabel("Capture Screen When Unknown Skill Detected")
    private SimpleBooleanProperty captureScreenWhenUnknownSkillDetected = new SimpleBooleanProperty(
        false);
    @ConfigLabel("Charm Analysis Debug Mode")
    private SimpleBooleanProperty charmAnalysisDebugMode = new SimpleBooleanProperty(false);
    @ConfigLabel("Checked Charms Count")
    @FormFactory(ReadOnlyLabelFactory.class)
    private SimpleIntegerProperty checkedCharms = new SimpleIntegerProperty(0);

  }

  @EqualsAndHashCode
  public static class TargetSlot {

    private final int[] slots;

    public TargetSlot(int slot1, int slot2, int slot3) {
      this.slots = new int[]{slot1, slot2, slot3};
    }

    public boolean isMatch(int slot1, int slot2, int slot3) {
      return slots[0] <= slot1 && slots[1] <= slot2 && slots[2] <= slot3;
    }

    public String toString() {
      return Arrays.toString(this.slots);
    }
  }

}

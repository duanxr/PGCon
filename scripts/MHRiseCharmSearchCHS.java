import com.dooapp.fxform.annotation.FormFactory;
import com.dooapp.fxform.view.factory.impl.TextAreaFactory;
import com.duanxr.pgcon.core.detect.api.ImageCompare;
import com.duanxr.pgcon.core.detect.api.OCR;
import com.duanxr.pgcon.core.detect.api.OCR.ApiConfig;
import com.duanxr.pgcon.core.detect.api.OCR.Param;
import com.duanxr.pgcon.core.model.Area;
import com.duanxr.pgcon.exception.AbortScriptException;
import com.duanxr.pgcon.exception.AlertException;
import com.duanxr.pgcon.exception.ResetScriptException;
import com.duanxr.pgcon.gui.fxform.annotation.ConfigLabel;
import com.duanxr.pgcon.output.action.StickAction;
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
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.SneakyThrows;
import org.apache.commons.math3.util.Pair;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.stereotype.Component;

/**
 * @author 段然 2022/7/25
 */
@Component
public class MHRiseCharmSearchCHS extends PGConScriptEngineV1<MHRiseCharmSearchCHS.Config> {
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
      .area(Area.ofRect(1586,418,74,40))
      .preProcessor(com.duanxr.pgcon.core.preprocessing.config.ColorPickFilterPreProcessorConfig.builder()
          .enable(true)
          .targetColor(javafx.scene.paint.Color.color(1.0,1.0,1.0))
          .range(0.5620704867127992)
          .pickType(com.duanxr.pgcon.core.preprocessing.config.ColorPickFilterPreProcessorConfig.PickType.CIEDE2000)
          .maskType(com.duanxr.pgcon.core.preprocessing.config.ColorPickFilterPreProcessorConfig.MaskType.BLACK)
          .inverse(true)
          .build())
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
      .area(Area.ofRect(1224,156,164,30))
      .method(ImageCompare.Method.TM_CCOEFF)
      .preProcessor(com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder()
          .enable(true)
          .inverse(true)
          .threshType(com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.OTSU)
          .build())
      .template("{\"R\":30,\"C\":164,\"T\":0,\"D\":\"H/8BAP///84ZAAEADh4AAiMAD2wANg+DAAUPowAXHwA+AAAPpAA7HwCDAAEPpQAND6QAdA89ACUFGAAvAP/sAUkOAwEPpAAhL///SAFIHwCkACEH9wEI7AEbABgABA4AB6sBDyMAAg5nAAlbAgtuAA6aAg+kAAEMgAADpAAPWQABLwAAFgAECiIAD6QABwYpAAgyAA+kAA4fAKQACR//ggAAKAAAfAAv//+kABULDgAI1gAPpAAQBggACqQADBgABucBDiIABVMAD6QABAcbAAowAB//pAASH/+kAAUJbQAPCAAACDoADSMACqQADioACdUCD8ADEQ7sAQU0Aw+kAAgOOgAO6wIPpAAPDo4GDzQDFgmpAw9IAQQNOgAPpAARHwCkAAAPNAMkBxwAD6QABAdCAwrsAQ9IAT0fADQDBQkqAA+kAAYNDQAPpABJHwDYAwgfAKQABg80AysO1QAPpAAnH/+kAAofAKQAJwmsAA+QAhMeAGgGD5ACEAmhBQ/sARcOxAUPpAAfH/+kABovAABIAR0v//+jAAsv///6BwME0gUsAADpCS8A/wgAAQnCAB8AWwABCrcACV4AA2gBLwD/AQD///8qUP//////\",\"L\":4920}")
      .build();
  private static final ImageCompare.Param MELDING_POT_SELECTION = ImageCompare.Param.builder()
      .area(Area.ofRect(40,948,474,80))
      .method(ImageCompare.Method.TM_CCOEFF)
      .preProcessor(com.duanxr.pgcon.core.preprocessing.config.NormalizePreProcessorConfig.builder()
          .enable(true)
          .build())
      .template("{\"R\":80,\"C\":474,\"T\":0,\"D\":\"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\",\"L\":37920}")
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
          .method(OCR.Method.ENG)
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
          .method(OCR.Method.ENG)
          .whitelist(SKILLS_OCR_WHITELIST)
          .build())
      .build();
  private final AtomicBoolean findTarget;
  private final Set<String> hashSet = new HashSet<>();
  private final Map<String, Integer> skillTargets;
  private final List<TargetSlot> slotTargets;

  public MHRiseCharmSearchCHS() {
    super(ScriptInfo.<MHRiseCharmSearchCHS.Config>builder()
        .config(new MHRiseCharmSearchCHS.Config())
        .isLoop(true)
        .name("MHR Charm Search(CHS.Ver)")
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
    skills.put("放弹.扩散强化", "散弹扩散箭强化"); //1
    skills.put("放弹.扩散箭强化", "散弹扩散箭强化"); //1
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
      throw new AlertException("Slot targets is empty!");
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
        throw new AlertException("Invalid slot target: " + trim);
      }
      List<Integer> list = new ArrayList<>();
      for (int i = 0; i < trim.length(); i++) {
        char c = trim.charAt(i);
        if (c < 48 || c > 52) {
          throw new AlertException("Invalid slot target: " + trim);
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
        throw new AlertException("Invalid target skill : " + trim);
      }
      char level = trim.charAt(trim.length() - 1);
      if (level < 48 || level > 57) {
        throw new AlertException("Invalid skill level: " + trim);
      }
      String skill = trim.substring(0, trim.length() - 1);
      if (!validator.contains(skill)) {
        throw new AlertException("Invalid skill: " + trim);
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
      long potSlot = numberOcr(EMPTY_POTS, 10000);
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

  private void checkMaterial(long need) {
    long mp = numberOcr(MP_COUNT, 10000);
    long mpa = numberOcr(MPA_COUNT, 10000);
    long point = numberOcr(POINTS_COUNT, 10000);
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
      throw new AbortScriptException(e);
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
    until(() -> detect(MHR_IN_GAME),
        result -> result.getSimilarity() > MHR_IN_GAME_MENU_THRESHOLD,
        () -> {
          press(A);
          sleep(150);
        });
  }

  private void walkToShop() {
    sleep(1000);
    hold(StickAction.L_TOP);
    sleep(5350);
    hold(StickAction.L_LEFT);
    sleep(1300);
    release(StickAction.L_LEFT);
  }

  private void toMeldingPot() {
    press(A);
    sleep(150);
    until(() -> detect(MELDING_POT_SELECTION),
        input -> input.getSimilarity() > 0.9,
        () -> {
          press(D_TOP);
          sleep(250);
        });
    press(A);
    sleep(300);
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
    ((SimpleIntegerProperty) config.getCheckedCharms()).set(
        config.getCheckedCharms().getValue() + 1);
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
            if (input.getConfidence() < 10) {
              return false;
            }
            String text = input.getTextWithoutSpace();
            boolean containsKey = AVAILABLE_SKILLS.containsKey(text);
            if (!Strings.isNullOrEmpty(input.getTextWithoutSpace()) && !containsKey) {
              info("Unknown skill detected: {} , confidence: {} ", text, input.getConfidence());
              calculateSkill(text, input.getConfidence());
            }
            return containsKey;
          },
          () -> sleep(50), 30);
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
    Integer l2 = skillTargets.get(skill2);
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

    @ConfigLabel("Checked Charms Count")
    private ReadOnlyIntegerProperty checkedCharms = new SimpleIntegerProperty(0);
    @ConfigLabel("Target Skills")
    @FormFactory(TextAreaFactory.class)
    private SimpleStringProperty targetSkills = new SimpleStringProperty();
    @ConfigLabel("Target Slots")
    @FormFactory(TextAreaFactory.class)
    private SimpleStringProperty targetSlots = new SimpleStringProperty();
    @ConfigLabel("Enable automatic Skill Matching")
    private SimpleBooleanProperty enableAutomaticSkillMatching = new SimpleBooleanProperty(false);
    @ConfigLabel("Capture Screen When Unknown Skill Detected")
    private SimpleBooleanProperty captureScreenWhenUnknownSkillDetected = new SimpleBooleanProperty(
        false);
    @ConfigLabel("Record Video When Find")
    private SimpleBooleanProperty recordVideoWhenFind = new SimpleBooleanProperty(true);
    @ConfigLabel("Notify Me When Find")
    private SimpleBooleanProperty notifyWhenFind = new SimpleBooleanProperty(true);
    @ConfigLabel("Charm Analysis Debug Mode")
    private SimpleBooleanProperty charmAnalysisDebugMode = new SimpleBooleanProperty(false);
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

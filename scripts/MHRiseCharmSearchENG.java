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
public class MHRiseCharmSearchENG extends PGConScriptEngineV1<MHRiseCharmSearchENG.Config> {

  private static final Set<String> RAW_SKILLS = getRawSkills();

  private static final Map<String, String> AVAILABLE_SKILLS = getAvailableSkills();
  private static final String CHARM_DECORATION_LEVEL_0 = "{\"D\":\"HwABAP//Zx//AQAPDyoAbA8BAP//t1AAAAAAAA==\",\"L\":1512,\"R\":36,\"T\":0,\"C\":42}";
  private static final String CHARM_DECORATION_LEVEL_1 = "{\"D\":\"HwABALhf////AP8BAAEPLwAEDwEABw8vABwv//8vABwv//8vAAwOEgAOXgAPMAALH/8vAAwPMQAKDxoAAw8wAE0fAGAANh8AMAArDiABDoEBDjAAD+EBGQwRAA9wAgAPMAAdHwAwAAcfAAEDDww+AA8xAw4PMAAUD5ADdgyBAR//wAMaDg4CD+ABCw8PAgAPQAIqH/+gAhwPHQEBDzAAOQ4xAA/hBA8PsQELDwEA31AAAAAAAA==\",\"L\":1920,\"R\":40,\"T\":0,\"C\":48}";
  private static final String CHARM_DECORATION_LEVEL_2 = "{\"D\":\"HwABAP9pH/8BAAEPKwAYDzEABS///y8AGAcaAA8KAAAMLwAPFwAMH/8VAAcJBgAOLwAOLQAPLwAAD4cADg8dAAMOWwAPLgAVH/8uABYe/1wADucADkABD1sBDA5dAA+IAQkItgEu//+eAQ+2ARoPoQASD7UCBA8rAQQPLgARAh8AL/8AEAMODz0DMQ8tABoPagMeD5gDLw8zAAIvAADLAQMPYgACBxwADFsALwAAgwACD1YCAQ+/AAIfAC8AAQOhAA+KAAMPPwIEDxcBBA8uACAPcAECDywCBg8BANVQAAAAAAA=\",\"L\":2024,\"R\":44,\"T\":0,\"C\":46}";
  private static final String CHARM_DECORATION_LEVEL_3 = "{\"D\":\"HwABAK4f/wEABQ8tABov//8tABov//8tAAYCCwASACMAAwoADC8ADp8ADx0ACAoQAA95AAIPLQAWCyEADogAD14BAA8tABcIIwAU/wEBC4kABxAAD4oAAB//twACH/+4AB4PFQEBD3EBGgXHAA+IAAUOXgAPWgAGDi8ADy0AFg4vAA4XAA9yABIf/z0DGg9qA10PmAMpHwCYAw8GMwAYAAsADy4AAwZiAAYKAAQLAA/5AQMHeQAGawENiQEFSgMHjwEGrgEP9AMGBbcABxUADNEBDqkED64ECg8uAA8PAQD/A1AAAAAAAA==\",\"L\":1840,\"R\":40,\"T\":0,\"C\":46}";
  private static final String CHARM_DECORATION_LEVEL_4 = "{\"D\":\"HwABAIov//8rABgBAQAPKwAYBgEADywAEAk4AA8sABAOEQAPhAAHDpAADy4AGgsvAA91AA0PEwAAD84AEwQsAAOVAR3/qgEOLgAPwwEFHf8vAA8tAAgDFwAPLgAGH/8uAAQPigAUBiEAD+YAAQ4uAA4VAQ8vAAQAGAANXwEELwAMuQAELQAPhQECHwDMARMOzgEORgAPMwMADy4AEw5XAg4XAA6wAg+zAgUPLgAbH/87AgEHGQAHQQAuAACEAgpJAA0OAAtWAg6PAA8uAA4KVQAGmQAMhAIGowANkAAKhQIv//94BAUOFgEPLgAJHwBDAQMOdAEOfAQO0QEPLQAWCqUADi4ADwEApFAAAAAAAA==\",\"L\":1840,\"R\":40,\"T\":0,\"C\":46}";
  private static final Param CHARM_LEVEL_1ST = Param.builder()
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
  private static final Param CHARM_LEVEL_2ND = Param.builder()
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
  private static final Param CHARM_RARE_LEVEL = Param.builder()
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
  private static final Param EMPTY_POTS = Param.builder()
      .area(Area.ofRect(1558, 416, 88, 50))
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
      .area(Area.ofRect(1134, 150, 354, 80))
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
          "{\"R\":80,\"C\":354,\"T\":0,\"D\":\"H/8BAP//////////////////TDYAAAALAA8xABMOCAAPSwABD2IB/wQZABgADQEACSYAAwgADlcBBxwAL///YgH7CHoCLwAAYwEFGgC9Agw/AA6UAQ9iAfQOFgENgAEMFAAtAAChAQxjAQ9gAfEOFwEJeQIPUAUCDzIABQILAAAOAAUJAA/BAvEEFwEfAI0FBA86AAwHPgQFYgEPxAL6B9sDDwcIAS8AAMQFFgW4Ag9iAf8bDyQBAC8AABsHBAJiAQDKAg78Ag9iAf8HDucGD2IBCAeCBQ5NCA9iAf8JBbsGAZsCD30IBAS2CQ+uCf8IA+oGBYICDxMLBQUYAA9iAQkPiAX/Ay8AAHwJBx7/ZAgK5QIN2wkPYgH7BQgBBucOBxUPDxsLBA/qBgQHYgEEBAAPYgH/ARz/igIOGwsPYgEKAksADloLD+oG+wQXAC///2IBCQa1BQytDA82D/wNeQIFFwAPBAQED+ERAwepEA9iAf8XHP8VAAnEAiUAAMYCBz0ABlUICzIAD2IB/wkP6gYED5MFAQ/EAv8NHwDEAhAPYgH/Lg8bAAUtAABrEA4tGguuCQ9MCPoFuwoPohQPD64JHA5WAA8RC+cOpRQPwxsACMQCH/9iARAPJgT5DxsACB8APQAbCf8cDm4eDwEA/////////////////////////////9QO5SgP3AD/PA4ZKQ8BAEIP3QDnLwAAhgA8DkMAD3caIARKAA/dAH0PgwAhD4UAPQ5DAA+9GyMOjQAP3wBmD2IBzB8AYgGrDoIADycENA4nAA8EBB0Gyx8P4AApD2MBYQ9yPAMf/4MAOQ9iARUPTAA3H/9CAh0PmD5HD2AABw7kAQ9iAbIP4QAIDiQDDxQENQ9fAAMIYDUPxAJRHwDEAlQe/24oDjhAD3QFPA4SAA9iAWMPTQAVD/MAMw/xKQEP8wA3CnMFD4EAAw9iAVwONAsPxAJACUICD/1CBQ8XCj8PgQACHwBiAYQfAGIBNQrnAA9fRAQPmw89CoAAD2IBXg9LADYf/3kGEAgrBAzuBw65EA+yCCsPgAACD2IBPg/DAnIH/gwIWAsPYgEJHwDEAkAf/2IBiA6MAA8jESQI1gAOTggPKAQ9Hv9lOA9iAWEPYQFQBUEABuoAD8MCAA/sBj8c/+IBHwBiAYgf/2IBMR0AYgEOsgoO9gAPEAswC80BD9cANQ5DAA+UDEsPQgAVDuoGDxALOwdPAA9iAQsPxAJSD2EBEg9iAVgv//+YEEIZAFdDA401DyYEUg4xCw9hAUIFz0YH6T8L6z8PmBA8A1QADSIDDQEAD2IBdR8AGgU2DuIAD4YzSQ9iAXEPnQAaD2EBQA4BAA9iAf89D2MBGw/iAaMP4wDbBCoKDv0dDwEAuQ//OP//////////gVD//////w==\",\"L\":28320}")
      .build();
  private static final ImageCompare.Param MELDING_POT_SELECTION = ImageCompare.Param.builder()
      .area(Area.ofRect(44, 950, 296, 76))
      .method(ImageCompare.Method.TM_CCOEFF)
      .preProcessor(com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder()
          .enable(true)
          .inverse(true)
          .threshType(
              com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.OTSU)
          .build())
      .template(
          "{\"R\":76,\"C\":296,\"T\":0,\"D\":\"H/8BAP///////+8ZAA0APgAA/xAADz8ACi8AABQADQgPAA4LAA9UAAILRAAJNgAfACcBYwJ5AAN/AAUHAA6WABMAAQAPKAEqB0EAHwAoAQk/AP8ANgADDEABDwEAAg8oAUUbACgBDc0ADYUAD6EABA9QAhUP1gAMBCgBHwBBAAILDQAPKQFbCFcCHAAeAQ8oATcFZgAP1gAJBboBHwCGAgAPKAFmCZcACcoBDSgBH//yAgsOGQEPDwICDTwAD/QACAkoAQ8bAAIAYAUPJQFQBcgACigBCjUCDv0ADikBD2oCDARjAAl6AQHmAA8nAQkJNgAPDwEHDx4BAQ8uAU4IngQI5wIPKAE5D9sAAQ9eARAOKAEPBAdXD98ABAkUAAoBAQ4nAw5AAw8oAQ0PFQIFBiYABrEIBWkHBsEBChoBDk8CDxcIRg8oAQwOGwAP5QEJCrgADmsCDxUCDgRlAA68CA4oAQ92A1QErgAfAEQDCA4YCB8AKAEXD5ICAB//KAETDfAGDygBaQdACQ6wBgxACQ+QBxQJLQAPdwEBA2QDDncCCc0JDygBFA6lAg/BBU0f/4cHBQvfBSv///cAD/0JAw8oAQ0f/2oIBAvGCQZsAQ/wBgIfACgBVh//oAQEDmwDD/AGCg8oAQMIYgEGMwEM9wsJDAEIugEOCwEOvAIPKAF3CPIADG0EHwCUAwcHYQMf/wgNBwp4CA7GCQ9ACQofACQIXQlfAwoBAgVaAy8AACgBEAirAwe9Ch//WgsNCGoCCcYJDHQJDloND0wJVA4sBA40Cg8ZBBAHPAAL0AAPKAEhDVoDD1APAR8AKAFiHwA0Ag4P4g0EH/+RBAMKXQ8a/00JDjcCDSgBL///QAkHBxgADygBbwgPAAXSAC8AALYOCwqlEQsCBgrKBQ8oAQ4OHAAP0RMNC5UBDy8QQw8oAQMHGgEHrQAPZgoICD4RC0IPB5UDDygBAQgeAAhpAwaOES///xUUFg0BAw9PAjEG2wIJpgAPKAEUL///1gYAHwAoAQcfANcGCQ8oAQMfACgBCR8AKAFQHwDMCAML+wgE/QAOJgsOUQQNeAwJuwAFuwIKJwkP+QcCDSgBH/8oARcGjwEPTwI5D/AGAg6rBg28BA+hBgEPCwMKDzsAAgfzBw9QBgIPTwIJDm0GCjQADygBRQgMAQ8oAQAPdwMFCSkBDLkFDCAPBiYHDgUBClIABbsJDNcGDygBXR8AKAEIH/8oAQYFugQKBQEPKAEGDhcABqsDD/QOAx//KAEtDqsLD6gTNQhfAA5CAwpgFQt9AwuIBw8oAQQJKxwIWAEIPAAFJQAMhwIO5gcPoAQKDWIADwEAbB8AOQBeDUkAHgBYAA5ACQ8BAP///////////4MPmgkRD8cJRi8AAFsAEAMqFQ83AAIuAAChAA/WCj4CWQAMSA0fAH8aCw4SDwjOGh8AjA0QDtcdD7wVBQ8uEQQPNgAHD4ANCw6JAA8oAR8ObB0PKAEUD18iBw+gAA8DPwAIRxUfAKMAEw4XAQ0cAQ6JHQ8qASkN6BEP1wAMLwAAKAEWHwDAGQ4PqBcCDygBLAsTDx8AKAEnHwC9AAQPKAEUCQICG/8HEQ8oATcvAP+hAg0K9hgPBBsLDycBIA5cAQ8oARQNKAAPSRYCD4YADgfyAB//hwAaDAQBDEMqD0YeCg+BAgMPKAEiHwAoARALqxkHlRIHKAEKShgvAAAnFAYJGQIKMBgPKAFGDicBD1ACPB8AhQQRCAcBDngCDygBBR8AKAEUB1AADzYVAQhQAA72Bg5PAg8rARgOxwAPaAIYDygBAA56GQ8oAQkv/wD6AREGuxcNFAEMqxYOShoOAgcPJwEhDPMdDygBCx//KAE1CVEAD2snHA8oAQ0f/ygBRg7VFw/NGAsH1h0Iihsv//8oAQIPhQIOC9cZDaIADygBCB//UAIaLgAA6wMPKAEWDUEJDygBBwqIAA8oATUP1BwEDygBEA4SGQ+GJz0IbAAK9RofAGgGEA9QAgMJAQEOoAIPWQcFH/8oARQLNwAJ5AMPKAESHwAoATAOhQAKJwEvAAAoAUAOqAYLRQEPygENDSgBLwAAKAE/Ca4BDckmD1ECAQ8oARkfAOEOEQ8/CwQfAMoBCh//KAABD0AJDx8ATAkiDygCAAUaAA9pCgkIvg8PKAENH/8oASUO8gIPeAEBD7MDAA1nAg6zAg8oAS4PlQMECzYACJgAD8gFBi///ygBDgkcAhn/aycf/2snDQufAAmHAA7OJw5LDA+cCxsvAP8oARAOMwQfACgBNQspAA9rJwoFJwAvAP+7BQIPGAgKB08ADygBIgrMAwvlAA8oAQILoQYvAAAQAAMPKAEQDskEDigBDFAAC3cICu8CC0AJDzYlGwuwAA4lAQ2nBQkNAg8oAQkf/ycBBw4oAw8oAQoG8AQPKAEHHwAoAUwNUgMOJwEPYwcCD20ABg8oARENXQsPKAEFDuwEDxkGAA8RDQMNlAUIKAEf/ygBJB8AdAMNBgwADHsGH/8xAwoMewIHEQoOhgIfAPwHBh8ATwoBDLIJBKgCDygBAwuVAQuIBA9HAwsv//+DAgcMmAMLGwIPUQAFBDEABsoxCWUNBokACRsAD5AJCgKOAAiDAgvqAAiSAQ8oAQAOagYOdwMPKAEnDUgDDrwAD0ILCQstAg0oAQi0AA/kCwQf/2snCA0QNAbzBQrKAQ4oAQ+GJ0QP8gUADy4DAQ88PgAHEAANiQAODgQPoAAGBuACD0kDBQmRAA8yCAQOfQIPUAIKDhoEDycBDC//APImIAXICA+gAgUPTAAYDjsADzUpGQ44AARgAR8Aph0SDtMBDwEA//////////////+HUP//////\",\"L\":22496}")
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
  private static final Param MPA_COUNT = Param.builder()
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
  private static final Param MP_COUNT = Param.builder()
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
  private static final Param POINTS_COUNT = Param.builder()
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
  private static final Param CHARM_SKILL_1ST = Param.builder()
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
  private static final Param CHARM_SKILL_2ND = Param.builder()
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

  public MHRiseCharmSearchENG() {
    super(ScriptInfo.<MHRiseCharmSearchENG.Config>builder()
        .config(new MHRiseCharmSearchENG.Config())
        .isLoop(true)
        .name("MHR Charm Search(ENG.Ver)")
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

  private static Set<String> getRawSkills() {
    return new HashSet<>(
        Arrays.asList("Attack Boost", "Agitator", "Peak Performance", "Resentment", "Resuscitate",
            "Critical Eye", "Critical Boost", "Weakness Exploit", "Latent Power", "Maximum Might",
            "Critical Element", "Master's Touch", "Fire Attack", "Water Attack", "Ice Attack",
            "Thunder Attack", "Dragon Attack", "Poison Attack", "Paralysis Attack", "Sleep Attack",
            "Blast Attack", "Handicraft", "Razor Sharp", "Spare Shot", "Protective Polish",
            "Mind's Eye", "Ballistics", "Bludgeoner", "Bow Charge Plus", "Focus", "Power Prolonger",
            "Marathon Runner", "Constitution", "Stamina Surge", "Guard", "Guard Up",
            "Offensive Guard", "Critical Draw", "Punishing Draw", "Quick Sheathe", "Slugger",
            "Stamina Thief", "Affinity Sliding", "Horn Maestro", "Artillery", "Load Shells",
            "Special Ammo Boost", "Normal/Rapid Up", "Pierce Up", "Spread Up", "Ammo Up",
            "Reload Speed", "Recoil Down", "Steadiness", "Rapid Fire Up", "Defense Boost",
            "Divine Blessing", "Recovery Up", "Recovery Speed", "Speed Eating", "Earplugs",
            "Windproof", "Tremor Resistance", "Bubbly Dance", "Evade Window", "Evade Extender",
            "Fire Resistance", "Water Resistance", "Ice Resistance", "Thunder Resistance",
            "Dragon Resistance", "Blight Resistance", "Poison Resistance", "Paralysis Resistance",
            "Sleep Resistance", "Stun Resistance", "Muck Resistance", "Blast Resistance",
            "Botanist", "Geologist", "Partbreaker", "Capture Master", "Carving Master", "Good Luck",
            "Speed Sharpening", "Bombardier", "Mushroomancer", "Item Prolonger", "Wide-Range",
            "Free Meal", "Heroics", "Fortify", "Flinch Free", "Jump Master", "Carving Pro",
            "Hunger Resistance", "Leap of Faith", "Diversion", "Master Mounter",
            "Chameleos Blessing", "Kushala Blessing", "Teostra Blessing", "Dragonheart",
            "Wirebug Whisperer", "Wall Runner", "Counterstrike", "Rapid Morph", "Hellfire Cloak",
            "Wind Alignment", "Thunder Alignment", "Stormsoul", "Blood Rite", "Dereliction",
            "Furious", "Mail of Hellfire", "Coalescence", "Bloodlust", "Defiance", "Sneak Attack",
            "Adrenaline Rush", "Redirection", "Spiribird's Call", "Charge Master", "Foray",
            "Tune-Up", "Grinder (S)", "Bladescale Hone", "Wall Runner (Boost)", "Quick Breath",
            "Element Exploit", "Chain Crit", "Guts", "Status Trigger"));
  }

  private static Map<String, String> getAvailableSkills() {
    Map<String, String> skills = Maps.newConcurrentMap();
    RAW_SKILLS.forEach(skill -> {
      String lowerCase = convertEnglishSkill(skill);
      skills.put(lowerCase, skill);
    });
    return skills;
  }

  private static String convertEnglishSkill(String skill) {
    return skill == null ? null : skill.replaceAll("[^a-zA-Z]", "").toLowerCase();
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
      char level = trim.charAt(trim.length() - 1);
      if (level < 48 || level > 57) {
        throw new AlertException("Invalid skill level: " + trim);
      }
      String skill = trim.substring(0, trim.length() - 1);
      skill = convertEnglishSkill(skill);
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

  private String detectSkill(Param param) {
    try {
      OCR.Result until = until(() -> detect(param),
          input -> {
            if (input.getConfidence() < 10) {
              return false;
            }
            String text = convertEnglishSkill(input.getText());
            boolean containsKey = AVAILABLE_SKILLS.containsKey(text);
            if (!Strings.isNullOrEmpty(text) && !containsKey) {
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

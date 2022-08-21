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
public class MHRiseCharmSaveLoadSearchENG extends PGConScriptEngineV1<MHRiseCharmSaveLoadSearchENG.Config> {

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
          .whitelist("0123456789Rarity")
          .build())
      .build();
  private static final Param CHARM_SKILL_1ST = Param.builder()
      .area(Area.ofRect(1154, 412, 272, 42))
      .preProcessor(com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder()
          .enable(true)
          .inverse(true)
          .threshType(
              com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.OTSU)
          .build())
      .apiConfig(ApiConfig.builder()
          .method(OCR.Method.ENG)
          .build())
      .build();
  private static final Param CHARM_SKILL_2ND = Param.builder()
      .area(Area.ofRect(1158, 490, 318, 38))
      .preProcessor(
          com.duanxr.pgcon.core.preprocessing.config.ColorPickFilterPreProcessorConfig.builder()
              .enable(true)
              .targetColor(javafx.scene.paint.Color.color(1.0, 1.0, 1.0))
              .range(0.5620704867127992)
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
      .area(Area.ofRect(1584, 420, 76, 40))
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
      .area(Area.ofRect(1398, 528, 54, 36))
      .method(ImageCompare.Method.TM_CCOEFF)
      .preProcessor(com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder()
          .enable(true)
          .inverse(true)
          .threshType(
              com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.OTSU)
          .build())
      .template(
          "{\"R\":36,\"C\":54,\"T\":0,\"D\":\"H/8BAP//Gi8AADUAIg82ACQfADYAmC4AACMBDzYADgUMAA4jAQ9sAAsPNgAkHgDsAA8OAQ4FNgAf/zYAUj8A/wA2AB0BZwAOZQIPNgAQHwA2AFUf/zUAEij/AAEADzYAKg41AA/MAwEPAQD/hlD//////w==\",\"L\":1944}")
      .build();
  private static final ImageCompare.Param MELDING_POT_FINISHED = ImageCompare.Param.builder()
      .area(Area.ofRect(1224, 158, 170, 28))
      .method(ImageCompare.Method.TM_CCOEFF)
      .preProcessor(com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder()
          .enable(true)
          .inverse(true)
          .threshType(
              com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.OTSU)
          .build())
      .template(
          "{\"R\":28,\"C\":170,\"T\":0,\"D\":\"H/8BAP//nRkAAQAOHgACIwAPbAA2D4MABQ+pAB0fAEQAAA+qADsfAIMABQ9UAS0PqgBWD0MAKwWkAC8A//4BSQ8JARofAEICDQ9UAUwP7QAkBwkCCKgCGwAYAAQOAAe9AQ8jAAIOqgAJbQILbgAOsgIP7QAHDIYABq0CDV8ACAEAH/8jAAcPqgAHBlgAC2EAD6oAER8AqgAJGf9WAAVmAAeCAAc0AA+qAAwLDgAHqgAfAKoAFgYIAAqqAAwYAAb5AQ4iAAVTAA+qAAQHGwAKMAAf/6oAGB//qgAFCXMADwgAAAg6AA0jAAqqAA4qAAntAg+VAxcO/gEFUgMPqgAIDjoAD6oAIQ5VAQ9SAxwJzQMPVAEEDToAD6oAER8AqgAAD1IDKgccAA+qAAQHYAMO/gEPVAE/HwBSAwUJKgAPqgAGDQ0AD6oATx8A/AMND6oAAg9SAysfAFIDEx//qgA1HwCqACsOUAUP+gUQHgCkBg+oAhAJ1wUKIwEPqgAJDvoFD6oAJR//qgAaHwCoAh4v//+pABEv//9ICAMECAYsAABJCi8A/wgAAQnIAB8AWwABCr0ACV4AA3QBLwD/AQD///9OUP//////\",\"L\":4760}")
      .build();
  private static final ImageCompare.Param MELDING_POT_SELECTION = ImageCompare.Param.builder()
      .area(Area.ofRect(50, 952, 456, 72))
      .method(ImageCompare.Method.TM_CCOEFF)
      .preProcessor(com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder()
          .enable(true)
          .inverse(true)
          .threshType(
              com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.OTSU)
          .build())
      .template(
          "{\"R\":72,\"C\":456,\"T\":0,\"D\":\"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\",\"L\":32832}")
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
  private static final Set<String> RAW_SKILLS = getRawSkills();
  private static final Map<String, String> AVAILABLE_SKILLS = getAvailableSkills();
  private static final String SKILLS_OCR_WHITELIST = getSkillsOcrWhitelist();
  private final AtomicBoolean findTarget;
  private final Set<String> hashSet = new HashSet<>();
  private final Map<String, Integer> skillTargets;
  private final List<TargetSlot> slotTargets;

  public MHRiseCharmSaveLoadSearchENG() {
    super(ScriptInfo.<MHRiseCharmSaveLoadSearchENG.Config>builder()
        .config(new MHRiseCharmSaveLoadSearchENG.Config())
        .isLoop(true)
        .description("MHR Charm S&L Search(ENG.Ver)")
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
    skills.put("temprolonger", "Item Prolonger"); //distance:3 confidence:87
    skills.put("hommaestro", "Horn Maestro"); //distance:5 confidence:95
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
      throw new AlertErrorException("Slot targets is empty!");
    }
    info("Target Slots: {}", slotTargets);
    Set<String> collect = new HashSet<>(AVAILABLE_SKILLS.keySet());
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
      char level = trim.charAt(trim.length() - 1);
      if (level < 48 || level > 57) {
        throw new AlertErrorException("Invalid skill level: " + trim);
      }
      String skill = trim.substring(0, trim.length() - 1);
      skill = convertEnglishSkill(skill);
      if (!validator.contains(skill)) {
        throw new AlertErrorException("Invalid skill: " + trim);
      }
      target.put(AVAILABLE_SKILLS.get(skill), level - 48);
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
    rare = rare.startsWith("RARITY") ? rare.substring(6) : "?";

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

  private String detectSkill(Param param) {
    try {
      OCR.Result until = until(() -> detect(param),
          input -> {
            String text = convertEnglishSkill(input.getText());
            boolean containsKey = AVAILABLE_SKILLS.containsKey(text);
            if (!Strings.isNullOrEmpty(text) && !containsKey) {
              info("Unknown skill detected: {} , confidence: {} ", text, input.getConfidence());
              calculateSkill(text, input.getConfidence());
            }
            return containsKey;
          },
          () -> sleep(50), 10000L);
      return until == null ? "" : AVAILABLE_SKILLS.get(convertEnglishSkill(until.getText()));
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
    Integer l1 = skill1 == null ? null : skillTargets.get(skill1);
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
        Attack Boost2
        Critical Eye2
        Critical Boost2
        Weakness Exploit2""");
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

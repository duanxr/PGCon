package com.duanxr.pgcon.script.impl.monsterhunter;

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
import com.duanxr.pgcon.script.api.ConfigurableScript;
import com.duanxr.pgcon.script.component.ScriptEngine;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import lombok.Data;
import lombok.SneakyThrows;
import org.apache.commons.math3.util.Pair;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.stereotype.Component;

/**
 * @author 段然 2022/7/25
 */
@Component
public class AutoCharm extends ScriptEngine implements ConfigurableScript {

  private static final OCR.Param CHARM_LEVEL_1 = OCR.Param.builder()
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
  private static final OCR.Param CHARM_LEVEL_2 = OCR.Param.builder()
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
  private static final String CHARM_LEVEL_WHITELIST = "1234567890";
  private static final Area CHARM_O1 = Area.ofRect(1346, 314, 62, 48);
  private static final Area CHARM_O2 = Area.ofRect(1394, 314, 52, 48);
  private static final Area CHARM_O3 = Area.ofRect(1432, 314, 56, 48);
  private static final OCR.Param CHARM_RARE = OCR.Param.builder()
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
  private static final String CHARM_RARE_WHITELIST = "RAE0123456789";
  private static final String CHARM_S0 = "{\"D\":\"HwABAP//Zx//AQAPDyoAbA8BAP//t1AAAAAAAA==\",\"L\":1512,\"R\":36,\"T\":0,\"C\":42}";
  private static final ImageCompare.Param CHARM_S0O1 = ImageCompare.Param.builder()
      .method(ImageCompare.Method.TM_CCOEFF)
      .template(CHARM_S0)
      .preProcessor(
          com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder().enable(true)
              .binaryThreshold(0.5).inverse(false).threshType(
                  com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.OTSU)
              .build())
      .area(CHARM_O1).build();
  private static final ImageCompare.Param CHARM_S0O2 = ImageCompare.Param.builder()
      .method(ImageCompare.Method.TM_CCOEFF)
      .template(CHARM_S0)
      .preProcessor(
          com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder().enable(true)
              .binaryThreshold(0.5).inverse(false).threshType(
                  com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.OTSU)
              .build())
      .area(CHARM_O2).build();
  private static final ImageCompare.Param CHARM_S0O3 = ImageCompare.Param.builder()
      .method(ImageCompare.Method.TM_CCOEFF)
      .template(CHARM_S0)
      .preProcessor(
          com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder().enable(true)
              .binaryThreshold(0.5).inverse(false).threshType(
                  com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.OTSU)
              .build())
      .area(CHARM_O3).build();
  private static final String CHARM_S1 = "{\"D\":\"HwABALhf////AP8BAAEPLwAEDwEABw8vABwv//8vABwv//8vAAwOEgAOXgAPMAALH/8vAAwPMQAKDxoAAw8wAE0fAGAANh8AMAArDiABDoEBDjAAD+EBGQwRAA9wAgAPMAAdHwAwAAcfAAEDDww+AA8xAw4PMAAUD5ADdgyBAR//wAMaDg4CD+ABCw8PAgAPQAIqH/+gAhwPHQEBDzAAOQ4xAA/hBA8PsQELDwEA31AAAAAAAA==\",\"L\":1920,\"R\":40,\"T\":0,\"C\":48}";
  private static final ImageCompare.Param CHARM_S1O1 = ImageCompare.Param.builder()
      .method(ImageCompare.Method.TM_CCOEFF)
      .template(CHARM_S1)
      .preProcessor(
          com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder().enable(true)
              .binaryThreshold(0.5).inverse(false).threshType(
                  com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.OTSU)
              .build())
      .area(CHARM_O1).build();
  private static final ImageCompare.Param CHARM_S1O2 = ImageCompare.Param.builder()
      .method(ImageCompare.Method.TM_CCOEFF)
      .template(CHARM_S1)
      .preProcessor(
          com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder().enable(true)
              .binaryThreshold(0.5).inverse(false).threshType(
                  com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.OTSU)
              .build())
      .area(CHARM_O2).build();
  private static final ImageCompare.Param CHARM_S1O3 = ImageCompare.Param.builder()
      .method(ImageCompare.Method.TM_CCOEFF)
      .template(CHARM_S1)
      .preProcessor(
          com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder().enable(true)
              .binaryThreshold(0.5).inverse(false).threshType(
                  com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.OTSU)
              .build())
      .area(CHARM_O3).build();
  private static final String CHARM_S2 = "{\"D\":\"HwABAP9pH/8BAAEPKwAYDzEABS///y8AGAcaAA8KAAAMLwAPFwAMH/8VAAcJBgAOLwAOLQAPLwAAD4cADg8dAAMOWwAPLgAVH/8uABYe/1wADucADkABD1sBDA5dAA+IAQkItgEu//+eAQ+2ARoPoQASD7UCBA8rAQQPLgARAh8AL/8AEAMODz0DMQ8tABoPagMeD5gDLw8zAAIvAADLAQMPYgACBxwADFsALwAAgwACD1YCAQ+/AAIfAC8AAQOhAA+KAAMPPwIEDxcBBA8uACAPcAECDywCBg8BANVQAAAAAAA=\",\"L\":2024,\"R\":44,\"T\":0,\"C\":46}";
  private static final ImageCompare.Param CHARM_S2O1 = ImageCompare.Param.builder()
      .method(ImageCompare.Method.TM_CCOEFF)
      .template(CHARM_S2)
      .preProcessor(
          com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder().enable(true)
              .binaryThreshold(0.5).inverse(false).threshType(
                  com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.OTSU)
              .build())
      .area(CHARM_O1).build();
  private static final ImageCompare.Param CHARM_S2O2 = ImageCompare.Param.builder()
      .method(ImageCompare.Method.TM_CCOEFF)
      .template(CHARM_S2)
      .preProcessor(
          com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder().enable(true)
              .binaryThreshold(0.5).inverse(false).threshType(
                  com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.OTSU)
              .build())
      .area(CHARM_O2).build();
  private static final ImageCompare.Param CHARM_S2O3 = ImageCompare.Param.builder()
      .method(ImageCompare.Method.TM_CCOEFF)
      .template(CHARM_S2)
      .preProcessor(
          com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder().enable(true)
              .binaryThreshold(0.5).inverse(false).threshType(
                  com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.OTSU)
              .build())
      .area(CHARM_O3).build();
  private static final String CHARM_S3 = "{\"D\":\"HwABAK4f/wEABQ8tABov//8tABov//8tAAYCCwASACMAAwoADC8ADp8ADx0ACAoQAA95AAIPLQAWCyEADogAD14BAA8tABcIIwAU/wEBC4kABxAAD4oAAB//twACH/+4AB4PFQEBD3EBGgXHAA+IAAUOXgAPWgAGDi8ADy0AFg4vAA4XAA9yABIf/z0DGg9qA10PmAMpHwCYAw8GMwAYAAsADy4AAwZiAAYKAAQLAA/5AQMHeQAGawENiQEFSgMHjwEGrgEP9AMGBbcABxUADNEBDqkED64ECg8uAA8PAQD/A1AAAAAAAA==\",\"L\":1840,\"R\":40,\"T\":0,\"C\":46}";
  private static final ImageCompare.Param CHARM_S3O1 = ImageCompare.Param.builder()
      .method(ImageCompare.Method.TM_CCOEFF)
      .template(CHARM_S3)
      .preProcessor(
          com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder().enable(true)
              .binaryThreshold(0.5).inverse(false).threshType(
                  com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.OTSU)
              .build())
      .area(CHARM_O1).build();
  private static final ImageCompare.Param CHARM_S3O2 = ImageCompare.Param.builder()
      .method(ImageCompare.Method.TM_CCOEFF)
      .template(CHARM_S3)
      .preProcessor(
          com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder().enable(true)
              .binaryThreshold(0.5).inverse(false).threshType(
                  com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.OTSU)
              .build())
      .area(CHARM_O2).build();
  private static final ImageCompare.Param CHARM_S3O3 = ImageCompare.Param.builder()
      .method(ImageCompare.Method.TM_CCOEFF)
      .template(CHARM_S3)
      .preProcessor(
          com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder().enable(true)
              .binaryThreshold(0.5).inverse(false).threshType(
                  com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.OTSU)
              .build())
      .area(CHARM_O3).build();
  private static final String CHARM_S4 = "{\"D\":\"HwABAIov//8rABgBAQAPKwAYBgEADywAEAk4AA8sABAOEQAPhAAHDpAADy4AGgsvAA91AA0PEwAAD84AEwQsAAOVAR3/qgEOLgAPwwEFHf8vAA8tAAgDFwAPLgAGH/8uAAQPigAUBiEAD+YAAQ4uAA4VAQ8vAAQAGAANXwEELwAMuQAELQAPhQECHwDMARMOzgEORgAPMwMADy4AEw5XAg4XAA6wAg+zAgUPLgAbH/87AgEHGQAHQQAuAACEAgpJAA0OAAtWAg6PAA8uAA4KVQAGmQAMhAIGowANkAAKhQIv//94BAUOFgEPLgAJHwBDAQMOdAEOfAQO0QEPLQAWCqUADi4ADwEApFAAAAAAAA==\",\"L\":1840,\"R\":40,\"T\":0,\"C\":46}";
  private static final ImageCompare.Param CHARM_S4O1 = ImageCompare.Param.builder()
      .method(ImageCompare.Method.TM_CCOEFF)
      .template(CHARM_S4)
      .preProcessor(
          com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder().enable(true)
              .binaryThreshold(0.5).inverse(false).threshType(
                  com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.OTSU)
              .build())
      .area(CHARM_O1).build();
  private static final ImageCompare.Param CHARM_S4O2 = ImageCompare.Param.builder()
      .method(ImageCompare.Method.TM_CCOEFF)
      .template(CHARM_S4)
      .preProcessor(
          com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder().enable(true)
              .binaryThreshold(0.5).inverse(false).threshType(
                  com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.OTSU)
              .build())
      .area(CHARM_O2).build();
  private static final ImageCompare.Param CHARM_S4O3 = ImageCompare.Param.builder()
      .method(ImageCompare.Method.TM_CCOEFF)
      .template(CHARM_S4)
      .preProcessor(
          com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder().enable(true)
              .binaryThreshold(0.5).inverse(false).threshType(
                  com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.OTSU)
              .build())
      .area(CHARM_O3).build();
  private static final ImageCompare.Param HAS_SKILL_2 = ImageCompare.Param.builder()
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
  private static final ImageCompare.Param MATERIAL_CHECK_READY = ImageCompare.Param.builder()
      .area(Area.ofRect(52, 950, 142, 42))
      .method(ImageCompare.Method.TM_CCOEFF)
      .preProcessor(com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder()
          .enable(true)
          .inverse(true)
          .threshType(
              com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.OTSU)
          .build())
      .template(
          "{\"R\":42,\"C\":142,\"T\":0,\"D\":\"H/8BAP///0gvAAANAAU//wD/GwAGT/8AAABaAA8fABQADQ0jAAggABkAHgAFGgAAiQAfAI4AAhkAAQAPjgAzAm0ADVkACQgAD44AEQUuAA8BAAAPjgAuBI8ADgABD44ACR//jgAqD48AAg+PAQ8LsgAZ/xsACDMACv8BD44ADh//jgAMD44BCQlAAQc3AQ8aAQMfAMYBCwQ/AA+QAgoH5gAs/wATAAoMAA+OAAsfAI4AEx//jgANDFQBBYwBBykBL///HAEvB9wBD68ABAZ0AA9wBAQN3QEvAACQAAoLtwAOhwEe/z4AD6kADQ/hAgEPjwEPHgBgAg+OADcPHAABD44ADx8AjgAGA1gAHwBYAAYf/wQCGgocAAhDBQngAg+OAAEPVQQUGABGAA4gAg7oAg+mAg0GKAEf/44AHweXAQxSBB//NwESD20DAA5wBg+OAC8ONgAPjgAKB8MABwIGD44AHgg6AActAA7EBw+PAAsJ7wEPjgAqDAYAHv8bAw4bBg+HAwAEKgADvQYPjgAgCDoACYQCDwsHAw9tAwYIYQEPVAUELv//kAIPVQQADp8DD3AEAg+OAAof/44ACg46BA+OCAENZgAPjgAaBJcBDhcJDWAGD1oBDg+HAgEf/44ADQr3AgvjBAuDBw8JAAIIwAAGHwkOLAgHtwsPOAIHB9cCBY4AHwBuCQ0LWAcOHwgPAAcIDvEAD8UCBA7jBwz7Bw8lAQEPjgABDgUHD44AEQkiAA5ICA+JCAUf/zQBAwgDAw6zCQxPDA8cAQIPbAAJD8IHBws/CR//FA4aDzwAAQ9QABEvAP8BAP//////kVD//////w==\",\"L\":5964}")
      .build();
  private static final ImageCompare.Param MELDING_POT_DONE = ImageCompare.Param.builder()
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
          "{\"R\":76,\"C\":296,\"T\":0,\"D\":\"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\",\"L\":22496}")
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
  private static final OCR.Param MPA_NUM = OCR.Param.builder()
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
  private static final OCR.Param MP_NUM = OCR.Param.builder()
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
  private static final OCR.Param POINTS_NUM = OCR.Param.builder()
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
  private static final OCR.Param POT_SLOT = OCR.Param.builder()
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
  private static final Map<String, String> SKILLS = getSkills();
  private static final String SKILLS_WHITELIST = getSkillsWhitelist();
  private static final OCR.Param CHARM_SKILL_1 = OCR.Param.builder()
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
          .whitelist(SKILLS_WHITELIST)
          .build())
      .build();
  private static final OCR.Param CHARM_SKILL_2 = Param.builder()
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
          .whitelist(SKILLS_WHITELIST)
          .build())
      .build();
  private static List<List<Integer>> slotTargets;
  private final Config config = new Config();
  int number = 0;
  private Map<String, Integer> skillTargets;

  private static String getSkillsWhitelist() {
    Set<Character> characterSet = new HashSet<>();
    for (String skill : SKILLS.values()) {
      skill.trim().chars().boxed().map(input -> (char) input.intValue()).forEach(characterSet::add);
    }
    StringBuilder sb = new StringBuilder();
    characterSet.stream().sorted().forEach(sb::append);
    return sb.toString();
  }

  private static Map<String, String> getSkills() {
    Map<String, String> skills = Maps.newConcurrentMap();
    skills.put("放弹.扩散强化", "散弹扩散箭强化"); //1
    skills.put("利丸", "利刃"); //1
    skills.put("放弹.扩散箭强化", "散弹扩散箭强化"); //1
    skills.put("打麻术【锐]", "打磨术锐");
    skills.put("拔刀术【力]", "拔刀术力");
    skills.put("爆破风性强化", "爆破属性强化");
    skills.put("麻狂局性强化", "麻痹属性强化");
    skills.put("鬼火地", "鬼火缠");
    skills.put("爆破局性强化", "爆破属性强化");
    skills.put("逆袭", "逆袭");
    skills.put("逆效", "逆袭");
    skills.put("逆获", "逆袭");
    skills.put("逆约", "逆袭");
    skills.put("逆装", "逆袭");
    skills.put("挑战者", "挑战者");
    skills.put("无伤", "无伤");
    skills.put("怨恨", "怨恨");
    skills.put("息恨", "怨恨");
    skills.put("铠恨", "怨恨");
    skills.put("急恨", "怨恨");
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
    skills.put("会心击【属性", "会心击属性");
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
    skills.put("刚丸打磨", "刚刃打磨");
    skills.put("刚丸打麻", "刚刃打磨");
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
    skills.put("拔刀术【技】", "拔刀术技");
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
    skills.put("击术术", "击晕术");
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
    skills.put("通常弹.连射箭强化", "通常弹连射箭强化");
    skills.put("贯穿弹.贯穿箭强化", "贯穿弹贯穿箭强化");
    skills.put("穿弹.贯穿箭强化", "贯穿弹贯穿箭强化");
    skills.put("贯穿.贯穿箭强化", "贯穿弹贯穿箭强化");
    skills.put("贯穿弹.穿箭强化", "贯穿弹贯穿箭强化");
    skills.put("散弹.扩散箭强化", "散弹扩散箭强化");
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
    skills.put("体力回复最提升", "体力回复量提升");
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
    skills.put("减轻胆性", "减轻胆怯");
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
    skills.put("鬼火强", "鬼火缠");
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
    skills.put("打磨术【锐】", "打磨术锐");
    skills.put("打麻术【锐", "打磨术锐");
    skills.put("打麻术【锐】", "打磨术锐");
    skills.put("刃鳞打磨", "刃鳞打磨");
    skills.put("刃鳞打麻", "刃鳞打磨");
    skills.put("走壁移动【翔】", "走壁移动翔");
    skills.put("迅之气息", "迅之气息");
    skills.put("连击", "连击");
    skills.put("击暴术", "击晕术");
    skills.put("拔刀术[力]", "拔刀术力"); //2
    skills.put("睡眠局性强化", "睡眠属性强化"); //1
    skills.put("丸旺打磨", "刚刃打磨"); //2
    skills.put("丰石使用高速化", "砥石使用高速化"); //1
    skills.put("遂具使用强化", "道具使用强化"); //1
    skills.put("逆敬", "逆袭"); //1
    skills.put("耳计", "耳塞"); //1
    skills.put("耳讲", "耳塞"); //1
    skills.put("回避虐离提升", "回避距离提升"); //1
    skills.put("而震", "耐震"); //1
    skills.put("著力大师", "蓄力大师"); //1
    skills.put("鬼火总", "鬼火缠"); //1
    skills.put("击景术", "击晕术"); //1
    skills.put("局性异常状态的耐性", "属性异常状态的耐性");
    skills.put("走壁移动【翔", "走壁移动翔"); //1
    skills.put("饥局耐性", "饥饿耐性"); //1
    skills.put("会心击【局性]", "会心击属性"); //2
    skills.put("最爱蓄菇", "最爱蘑菇"); //1
    skills.put("中者", "跑者"); //1
    skills.put("精神抖扩", "精神抖擞"); //1
    skills.put("而力急速回复", "耐力急速回复"); //1
    skills.put("力最解放", "力量解放"); //1
    skills.put("会心击属性", "会心击属性");
    skills.put("走壁移动翔", "走壁移动翔");
    skills.put("通常弹连射箭强化", "通常弹连射箭强化");
    skills.put("穿弹贯穿箭强化", "贯穿弹贯穿箭强化");
    skills.put("贯穿弹贯穿箭强化", "贯穿弹贯穿箭强化");
    skills.put("睡眠风性强化", "睡眠属性强化");
    skills.put("打磨术锐", "打磨术锐");
    skills.put("麻业属性强化", "麻痹属性强化"); //1
    skills.put("麻狂性强化", "麻痹属性强化"); //1
    skills.put("丸打磨", "刚刃打磨"); //1
    skills.put("打麻术锐", "打磨术锐"); //1
    skills.put("精神抖激", "精神抖擞"); //1
    skills.put("通具使用强化", "道具使用强化"); //1
    skills.put("放弹扩散强化", "散弹扩散箭强化"); //2
    skills.put("散弹扩散强化", "散弹扩散箭强化"); //1
    skills.put("散弹扩散箭强化", "散弹扩散箭强化"); //0
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
    skills.put("饥蚀耐性", "饥饿耐性"); //1
    skills.put("会心击罗性", "会心击属性"); //1
    skills.put("逆节", "逆袭"); //1
    skills.put("雷风性攻击强化", "雷属性攻击强化"); //1
    skills.put("昏打耐性", "昏厥耐性"); //1
    skills.put("丸鳞打磨", "刃鳞打磨"); //1
    skills.put("饥蚀性", "饥饿耐性"); //2
    skills.put("地件改造", "零件改造"); //1
    return skills;
  }

  @Override
  @SneakyThrows
  public void execute() {
    info("{} charms has been checked.", number);
    launchGame();
    walkToShop();
    toMeldingPot();
    try {
      watchStars();
    } catch (ResetScriptException e) {
      info("Reset script, change charm seed.", e);
    }
    toNewCharmRandomSeed();
  }

  @Override
  public String getScriptName() {
    return "MHR auto charm(CHS.Ver)";
  }

  private boolean watchStars() {
    while (true) {
      sleep(100);
      long potSlot = numberOcr(POT_SLOT, 100);
      if (potSlot == 0) {
        info("No pots left, reset script.");
        throw new ResetScriptException();
      }
      fillPot(potSlot);
      if (checkCharms()) {
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
    //until(() -> imageCompare(MATERIAL_CHECK_READY), input -> input.getSimilarity() > 0.8, () -> sleep(50));
    long mp = numberOcr(MP_NUM, 100);
    long mpa = numberOcr(MPA_NUM, 100);
    long point = numberOcr(POINTS_NUM, 100);
    info("You have " + mp + " Melding Puddings and " + mpa + " MP Accelerants and " + point
        + " Points");
    long mpn = need * 5;
    if (mp < mpn) {
      throw new ResetScriptException(
          "Lack of Melding Puddings! need " + mpn + " but only have " + mp);
    }
    if (mpa < need) {
      throw new ResetScriptException(
          "Lack of MP Accelerants! need " + need + " but only have " + mpa);
    }
    long pn = need * 1000;
    if (point < pn) {
      throw new ResetScriptException(
          "Lack of Points! need " + pn + " but only have " + point);
    }
  }

  private void toNewCharmRandomSeed() {
    abortGame();
    launchGame();
    walkToShop();
    toMeldingPot();
    try {
      fillPot(1);
    } catch (ResetScriptException e) {
      error("you have no enough material to reset, abort script!", e);
      throw new AbortScriptException();
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
    until(() -> imageCompare(MHR_IN_GAME),
        input -> input.getSimilarity() > MHR_IN_GAME_MENU_THRESHOLD,
        () -> {
          press(A);
          sleep(150);
        });
  }

  private void walkToShop() {
    clear();
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
    until(() -> imageCompare(MELDING_POT_SELECTION),
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

  private boolean checkCharms() {
    press(D_TOP);
    sleep(200);
    press(A);
    sleep(1000);
    AtomicBoolean find = new AtomicBoolean(false);
    until(() -> imageCompare(MELDING_POT_DONE),
        input -> input.getSimilarity() < 0.9,
        () -> {
          if (charmAnalyze()) {
            find.set(true);
          }
          press(A);
          sleep(500);
        });
    sleep(200);
    press(A);
    return find.get();
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
    until(() -> imageCompare(MHR_IN_GAME),
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
    Future<ImageCompare.Result> hasSkills2 = async(() -> imageCompare(HAS_SKILL_2));

    Future<OCR.Result> rareF = async(() -> ocr(CHARM_RARE));
    Future<OCR.Result> level1F = async(() -> ocr(CHARM_LEVEL_1));
    Future<OCR.Result> level2F = async(() -> ocr(CHARM_LEVEL_2));

    Future<String> skill1F = async(() -> detectSkill(CHARM_SKILL_1));
    Future<String> skill2F = async(() -> detectSkill(CHARM_SKILL_2));

    Future<ImageCompare.Result> s0o1F = async(() -> imageCompare(CHARM_S0O1));
    Future<ImageCompare.Result> s1o1F = async(() -> imageCompare(CHARM_S1O1));
    Future<ImageCompare.Result> s2o1F = async(() -> imageCompare(CHARM_S2O1));
    Future<ImageCompare.Result> s3o1F = async(() -> imageCompare(CHARM_S3O1));
    Future<ImageCompare.Result> s4o1F = async(() -> imageCompare(CHARM_S4O1));
    Future<ImageCompare.Result> s0o2F = async(() -> imageCompare(CHARM_S0O2));
    Future<ImageCompare.Result> s1o2F = async(() -> imageCompare(CHARM_S1O2));
    Future<ImageCompare.Result> s2o2F = async(() -> imageCompare(CHARM_S2O2));
    Future<ImageCompare.Result> s3o2F = async(() -> imageCompare(CHARM_S3O2));
    Future<ImageCompare.Result> s4o2F = async(() -> imageCompare(CHARM_S4O2));
    Future<ImageCompare.Result> s0o3F = async(() -> imageCompare(CHARM_S0O3));
    Future<ImageCompare.Result> s1o3F = async(() -> imageCompare(CHARM_S1O3));
    Future<ImageCompare.Result> s2o3F = async(() -> imageCompare(CHARM_S2O3));
    Future<ImageCompare.Result> s3o3F = async(() -> imageCompare(CHARM_S3O3));
    Future<ImageCompare.Result> s4o3F = async(() -> imageCompare(CHARM_S4O3));

    String rare = rareF.get().getTextWithoutSpace().toUpperCase();
    rare = rare.startsWith("RARE") ? rare.substring(4) : "?";
    boolean hasSkill2 = hasSkills2.get().getSimilarity() > 0.8;

    String skill1 = skill1F.get();
    String skill2 = hasSkill2 ? skill2F.get() : "";
    Long level1 = level1F.get().getTextAsNumber();
    Long level2 = level2F.get().getTextAsNumber();

    List<ImageCompare.Result> o1L = Arrays.asList(s0o1F.get(), s1o1F.get(), s2o1F.get(),
        s3o1F.get(), s4o1F.get());
    List<ImageCompare.Result> o2L = Arrays.asList(s0o2F.get(), s1o2F.get(), s2o2F.get(),
        s3o2F.get(), s4o2F.get());
    List<ImageCompare.Result> o3L = Arrays.asList(s0o3F.get(), s1o3F.get(), s2o3F.get(),
        s3o3F.get(), s4o3F.get());

    int o1 = getMax(o1L);
    int o2 = getMax(o2L);
    int o3 = getMax(o3L);

    boolean isTarget = checkSkill(skill1, level1, skill2, level2) && checkGem(o1, o2, o3);
    String result = String.format("R%s %s%s %s%s S%s%s%s", rare, skill1, level1,
        hasSkill2 ? skill2 : "",
        hasSkill2 ? level2 : "",
        o1,
        o2,
        o3);
    info("DingZhen the One-Eye identified the charm as : {}", result);
    if (isTarget) {
      warn("find a target charm:{}", result);
      push("find a target charm:" + result);
      press(PLUS);
      sleep(200);
      if (config.getCaptureScreenWhenFind().get()) {
        hold(CAPTURE);
        sleep(2000);
        release(CAPTURE);
        sleep(200);
      }
    }
    number++;
    return isTarget;
  }

  private void toMainMenu() {
    until(() -> imageCompare(MAIN_MENU),
        input -> input.getSimilarity() > 0.8,
        () -> {
          press(HOME);
          sleep(1000);
        });
  }

  private String detectSkill(OCR.Param param) {
    try {
      OCR.Result until = until(() -> ocr(param),
          input -> {
            if (input.getConfidence() < 10) {
              return false;
            }
            String text = input.getTextWithoutSpace();
            boolean containsKey = SKILLS.containsKey(text);
            if (!Strings.isNullOrEmpty(input.getTextWithoutSpace()) && !containsKey) {
              info("Unknown skill detected: {} , confidence: {} ", text, input.getConfidence());
              calculateSkill(text);
            }
            return containsKey;
          },
          () -> sleep(50), 30);
      return until == null ? "" : SKILLS.get(until.getTextWithoutSpace());
    } catch (Exception e) {
      return "";
    }

  }

  private void calculateSkill(String text) {
    LevenshteinDistance defaultInstance = LevenshteinDistance.getDefaultInstance();
    List<Pair<Integer, String>> list = new ArrayList<>();
    SKILLS.forEach((k, v) -> {
      Integer score = defaultInstance.apply(v, text);
      list.add(Pair.create(score, v));
    });
    list.sort(Comparator.comparing(Pair::getFirst));
    String formatted = "skills.put(\"%s\", \"%s\"); //%s\n".formatted(text, list.get(0).getSecond(),
        list.get(0).getFirst());
    System.out.println(formatted);
    SKILLS.put(text, list.get(0).getSecond());
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

  private boolean checkGem(int o1, int o2, int o3) {
    for (List<Integer> target : slotTargets) {
      if (o1 >= target.get(0) && o2 >= target.get(1) && o3 >= target.get(2)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean isLoop() {
    return true;
  }

  @Override
  public void load() {
    String ts = config.getTarget_Slots().getValue();
    slotTargets = getSlotTargets(ts);
    if (slotTargets.isEmpty()) {
      throw new AlertException("Slot targets is empty!");
    }
    info("Slot targets: {}", slotTargets);
    Set<String> collect = new HashSet<>(SKILLS.values());
    debug("all available skills: {}", collect);
    String st = config.getTarget_Skills().getValue();
    skillTargets = getSkillTargets(st, collect);
    info("Skills targets: {}", skillTargets);
  }

  private static List<List<Integer>> getSlotTargets(String str) {
    Set<List<Integer>> set = new HashSet<>();
    String[] split = str.split("\n");
    for (String ss : split) {
      String s = ss.trim();
      if (s.length() == 0) {
        continue;
      }
      if (s.length() != 3) {
        throw new AlertException("Invalid slot target: " + s);
      }
      List<Integer> ints = new ArrayList<>();
      for (int i = 0; i < s.length(); i++) {
        char c = s.charAt(i);
        if (c < 48 || c > 52) {
          throw new AlertException("Invalid slot target: " + s);
        }
        ints.add(c - 48);
      }
      ints.sort(Comparator.<Integer>comparingInt(i -> i).reversed());
      set.add(ints);
    }
    return new ArrayList<>(set);
  }

  private static Map<String, Integer> getSkillTargets(String st, Set<String> collect) {
    Map<String, Integer> target = Maps.newHashMap();
    String[] split = st.split("\n");
    for (String ss : split) {
      String s = ss.trim();
      if (s.trim().length() == 0) {
        continue;
      }
      if (s.length() < 2) {
        throw new AlertException("Invalid skill target: " + s);
      }
      char level = s.charAt(s.length() - 1);
      if (level < 48 || level > 57) {
        throw new AlertException("Invalid skill level: " + s);
      }
      String skill = s.substring(0, s.length() - 1);
      if (!collect.contains(skill)) {
        throw new AlertException("Invalid skill: " + s);
      }
      target.put(skill, level - 48);
    }
    return target;
  }

  @Override
  public void clear() {
  }

  @Override
  public Object registerConfig() {
    return config;
  }

  @Data
  public static class Config {

    @FormFactory(TextAreaFactory.class)
    private SimpleStringProperty Target_Skills = new SimpleStringProperty();
    @FormFactory(TextAreaFactory.class)
    private SimpleStringProperty Target_Slots = new SimpleStringProperty();
    @ConfigLabel("Capture Screen When Find")
    private SimpleBooleanProperty captureScreenWhenFind = new SimpleBooleanProperty(true);

  }


}

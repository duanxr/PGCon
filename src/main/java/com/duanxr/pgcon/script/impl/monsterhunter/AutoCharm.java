package com.duanxr.pgcon.script.impl.monsterhunter;

import static org.bytedeco.tesseract.global.tesseract.PSM_SINGLE_CHAR;

import com.dooapp.fxform.annotation.FormFactory;
import com.dooapp.fxform.view.factory.impl.TextAreaFactory;
import com.duanxr.pgcon.core.detect.api.ImageCompare;
import com.duanxr.pgcon.core.detect.api.OCR;
import com.duanxr.pgcon.core.detect.api.OCR.ApiConfig;
import com.duanxr.pgcon.core.detect.api.OCR.Method;
import com.duanxr.pgcon.core.detect.api.OCR.Param;
import com.duanxr.pgcon.core.detect.api.OCR.Result;
import com.duanxr.pgcon.core.model.Area;
import com.duanxr.pgcon.gui.exception.AbortScriptException;
import com.duanxr.pgcon.gui.exception.GuiAlertException;
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
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import lombok.Data;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

/**
 * @author 段然 2022/7/25
 */
@Component
public class AutoCharm extends ScriptEngine implements ConfigurableScript {

  private static final String CHARM_LEVEL_WHITELIST = "1234567890";
  private static final OCR.Param CHARM_LEVEL_1 = Param.builder()
      .area(Area.ofPoints(1447, 453, 1482, 484))
      .apiConfig(ApiConfig.builder()
          .method(OCR.Method.NMU)
          .whitelist(CHARM_LEVEL_WHITELIST)
          .pageSegMode(PSM_SINGLE_CHAR)
          .build())
      .preProcessor(
          com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder().enable(true)
              .binaryThreshold(0.5).inverse(true).threshType(
                  com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.OTSU)
              .build())
      .build();
  private static final OCR.Param CHARM_LEVEL_2 = OCR.Param.builder()
      .area(Area.ofPoints(1447, 528, 1482, 561))
      .apiConfig(ApiConfig.builder()
          .method(OCR.Method.NMU)
          .whitelist(CHARM_LEVEL_WHITELIST)
          .pageSegMode(PSM_SINGLE_CHAR)
          .build())
      .preProcessor(
          com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder().enable(true)
              .binaryThreshold(0.5).inverse(true).threshType(
                  com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.OTSU)
              .build())
      .build();
  private static final Area CHARM_O1 = Area.ofRect(1346, 314, 62, 48);
  private static final Area CHARM_O2 = Area.ofRect(1394, 314, 52, 48);
  private static final Area CHARM_O3 = Area.ofRect(1432, 314, 56, 48);
  private static final String CHARM_RARE_WHITELIST = "RAE0123456789";
  private static final OCR.Param CHARM_RARE = OCR.Param.builder()
      .area(Area.ofPoints(1362, 283, 1482, 319))
      .apiConfig(ApiConfig.builder()
          .method(OCR.Method.ENG)
          .whitelist(CHARM_RARE_WHITELIST)
          .build())
      .preProcessor(
          com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder().enable(true)
              .binaryThreshold(0.5).inverse(true).threshType(
                  com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.OTSU)
              .build())
      .build();
  private static final String CHARM_S0 = "{\"data\":\"HwABAP//Zx//AQAPDyoAbA8BAP//t1AAAAAAAA==\",\"length\":1512,\"rows\":36,\"type\":0,\"cols\":42}";
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
  private static final String CHARM_S1 = "{\"data\":\"HwABALhf////AP8BAAEPLwAEDwEABw8vABwv//8vABwv//8vAAwOEgAOXgAPMAALH/8vAAwPMQAKDxoAAw8wAE0fAGAANh8AMAArDiABDoEBDjAAD+EBGQwRAA9wAgAPMAAdHwAwAAcfAAEDDww+AA8xAw4PMAAUD5ADdgyBAR//wAMaDg4CD+ABCw8PAgAPQAIqH/+gAhwPHQEBDzAAOQ4xAA/hBA8PsQELDwEA31AAAAAAAA==\",\"length\":1920,\"rows\":40,\"type\":0,\"cols\":48}";
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
  private static final String CHARM_S2 = "{\"data\":\"HwABAP9pH/8BAAEPKwAYDzEABS///y8AGAcaAA8KAAAMLwAPFwAMH/8VAAcJBgAOLwAOLQAPLwAAD4cADg8dAAMOWwAPLgAVH/8uABYe/1wADucADkABD1sBDA5dAA+IAQkItgEu//+eAQ+2ARoPoQASD7UCBA8rAQQPLgARAh8AL/8AEAMODz0DMQ8tABoPagMeD5gDLw8zAAIvAADLAQMPYgACBxwADFsALwAAgwACD1YCAQ+/AAIfAC8AAQOhAA+KAAMPPwIEDxcBBA8uACAPcAECDywCBg8BANVQAAAAAAA=\",\"length\":2024,\"rows\":44,\"type\":0,\"cols\":46}";
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
  private static final String CHARM_S3 = "{\"data\":\"HwABAK4f/wEABQ8tABov//8tABov//8tAAYCCwASACMAAwoADC8ADp8ADx0ACAoQAA95AAIPLQAWCyEADogAD14BAA8tABcIIwAU/wEBC4kABxAAD4oAAB//twACH/+4AB4PFQEBD3EBGgXHAA+IAAUOXgAPWgAGDi8ADy0AFg4vAA4XAA9yABIf/z0DGg9qA10PmAMpHwCYAw8GMwAYAAsADy4AAwZiAAYKAAQLAA/5AQMHeQAGawENiQEFSgMHjwEGrgEP9AMGBbcABxUADNEBDqkED64ECg8uAA8PAQD/A1AAAAAAAA==\",\"length\":1840,\"rows\":40,\"type\":0,\"cols\":46}";
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
  private static final String CHARM_S4 = "{\"data\":\"HwABAIov//8rABgBAQAPKwAYBgEADywAEAk4AA8sABAOEQAPhAAHDpAADy4AGgsvAA91AA0PEwAAD84AEwQsAAOVAR3/qgEOLgAPwwEFHf8vAA8tAAgDFwAPLgAGH/8uAAQPigAUBiEAD+YAAQ4uAA4VAQ8vAAQAGAANXwEELwAMuQAELQAPhQECHwDMARMOzgEORgAPMwMADy4AEw5XAg4XAA6wAg+zAgUPLgAbH/87AgEHGQAHQQAuAACEAgpJAA0OAAtWAg6PAA8uAA4KVQAGmQAMhAIGowANkAAKhQIv//94BAUOFgEPLgAJHwBDAQMOdAEOfAQO0QEPLQAWCqUADi4ADwEApFAAAAAAAA==\",\"length\":1840,\"rows\":40,\"type\":0,\"cols\":46}";
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
  private static final ImageCompare.Param MAIN_MENU = ImageCompare.Param.builder()
      .area(Area.ofRect(1244, 778, 84, 78)).method(ImageCompare.Method.TM_CCOEFF).preProcessor(
          com.duanxr.pgcon.core.preprocessing.config.NormalizePreProcessorConfig.builder()
              .enable(true).build()).preProcessor(
          com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder().enable(true)
              .binaryThreshold(0.5).inverse(false).threshType(
                  com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.OTSU)
              .adaptiveThreshC(2).adaptiveBlockSize(11).build()).template(
          "{\"data\":\"H/8BAP///wgQAAEAD1MAPQ9VAEAPVACbD1AB/yUvAAD8ABQvAADgAigPVAADDrsAD5ABFRoAAQAPEAEiCj0AC1IAB1YAHwBUACQOUgAFVgAKewAPVgAPDwEAGB//VAAUD1MAQC///zsABg9TACcv//86AAcPUwAmCDsADhsAD1MAJB//OAAKD1QAKw+nBDkOigAP/AQCDzAFOw5VAA+DBRUPVAA/DyMAEA+nADkOVQAPVABnDyUAEg+nADoOVQAP0AYQDycAMg5zBA8wABoPVAD/Uyn/ACgADwwGKB//oAIsD/QCaA9EBJwPQAURHwDoBWsf/1QARw4zAA6NBw/gB2sO4QcPiAg3DzkAAi8AAPsAKQ46AA8bAAcPdAQeD4AKMA8oCxkP0AtnBy8AD0oABy8AAMwMOx//IA0lDwkBBg75Ag9wDiEOPgEPeBEfDpUCD8QOLQ+8AAgPYBKUD6gAPA9UAO4PpQFDDwEA//8KUP//////\",\"length\":6552,\"rows\":78,\"type\":0,\"cols\":84}")
      .build();
  private static final ImageCompare.Param MELDING_POT_SELECTION = ImageCompare.Param.builder()
      .area(Area.ofRect(270, 984, 54, 38)).method(ImageCompare.Method.TM_CCOEFF).preProcessor(
          com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder().enable(true)
              .binaryThreshold(0.5).inverse(false).threshType(
                  com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.OTSU)
              .adaptiveThreshC(2).adaptiveBlockSize(11).build()).template(
          "{\"data\":\"HwABAP82J///DAAPNQAXBjcAGP9CAAIbAAwBAA4qAAlCAA40AAg4AApBAAAMAB//NgAXDhoADzYABwYLAA6+AA74AA3wAA82AAovAAA3AAQeAEoBDmsADnoBDjYADjUADzYASwnsABz/pQEODgEP6QEADzYAFA/kAQEOegEPNgAxD7wBBwYZAA0OAR//NgAcB1wBDygCBg82AAgfADYADQ85AgsONgAOawAPNgBMD28CCQ/KAwQe/2sADzYAEg5rAA9sADEfAKIEBB//YwUQDl0ADwEA/yNQAAAAAAA=\",\"length\":2052,\"rows\":38,\"type\":0,\"cols\":54}")
      .build();
  private static final ImageCompare.Param MELDING_POT_DONE = ImageCompare.Param.builder()
      .area(Area.ofRect(1174, 150, 262, 82)).method(ImageCompare.Method.TM_CCOEFF).preProcessor(
          com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder().enable(true)
              .binaryThreshold(0.5).inverse(false).threshType(
                  com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.OTSU)
              .adaptiveThreshC(2).adaptiveBlockSize(11).build()).template(
          "{\"data\":\"HwABAP////8RH/9JADof/6MAQh//8QA7DwEA//////9wPv///wsADzAAEQwIAAhAAA5KAA8GAaMf/wEAAgMZAAguAAxAAA0xAA8GAcIaAAgAG/9AAB//BgGpDrsABhcAGwDUAAkNAgs6AQ8LAqIP8gEHBiAADcoAD/kCBgs0AQ8GAbkPOgASBxIADwYBqAoeBA8LAgsfABECAAtLAA8GAZwIGAQO5QYPCwIBATgBHgAyAQ8GAQQf/wYBpAoACA8GAQsLEQEPJAYAH/8MAq4PMAQDH/9VBgAPBgGyD7oAAw8YAAUPBgELDnQHDyYGpQ8GAREJ+QEPBgGsDtMECfoCDjUHDnkMDoEJDyoHpA8eBQAv//8GAREfAAYBsxQAKggMFAcMRQgMBgEOOQUPBgGpHwAGAQcfAAYB4Q4bAA8GAQgPOAUIH/8GAdYLPAgvAAAGAbMf/wYBIw4eBQ8GAakPsQAED0gICB8ABgEJDScADysHkg5BDw8GAQ0fAAYBDh//BgHPCfcADmMEDwYBog4JHA8YBAoKIgAODgAHBgEfAAEA////////////////////uw7gEw/bAN0OCRIPAwHADtwAD2oeDQ8FAccP3QAFH/8GAT0OQwAP9C8jDo0ADwYBMw8jFwMPBwEBDwYBUg4nAA+qBAMPcyZIDicADwYBYQ/CABMPBgE9Hv8mAA8GAYkvAAAGAUMf/wYBTy8AAAYBFw9NABQPBgFLCBEADxUcOQ8SLxYPBgFRD+EADgkQAA8GAc0P4QAFCRYADIEqDxIDTQ5nAA8SA0oLKh8PJAAFDwYBPA8LAnIaAOEAAAYADlQACJIHDwYBywdfJQuJMQ4kAA8GAYAf/zYJMgbzHQ4GAQ4kAA8GAVgPBQEXH/9/KDQKkCwPBgEAH/8GAYkfAAYBMB//hCoLCioBDwYBfR//TQ0yCfABDgYBDiQADwYBVg5NBw8QA0ANdi8HEAgfAKEwBQ57AA8GAUcPBQFTHQCCMQcLBg+uAAMPBgGCHwCKADgPJAYLH/9ODUEPCwJyD+IABA8GAZ8OPAoPBgHaDo8ADwYBnA7lAA8LAgAPBgHvHwAGARcPEgNVD4oXvw/iABEPAQD//////////yRQAAAAAAA=\",\"length\":21484,\"rows\":82,\"type\":0,\"cols\":262}")
      .build();
  private static final ImageCompare.Param MHR_IN_GAME_MENU = ImageCompare.Param.builder()
      .area(Area.ofRect(936, 902, 48, 40)).method(ImageCompare.Method.TM_CCOEFF).preProcessor(
          com.duanxr.pgcon.core.preprocessing.config.ColorPickFilterPreProcessorConfig.builder()
              .enable(true).targetColor(
                  javafx.scene.paint.Color.color(1.0, 0.6000000238418579, 0.4000000059604645))
              .hueRange(0.27216494646558176).saturationRange(0.7734106521016544)
              .valueRange(0.46288657850884346).inverse(true).build())
      .preProcessor(
          com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder().enable(true)
              .binaryThreshold(0.5).inverse(false).threshType(
                  com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.OTSU)
              .adaptiveThreshC(2).adaptiveBlockSize(11).build()).template(
          "{\"data\":\"HwABAFs/////MAAaDy8AHg8xABsR/wMAD74AHgBnAA5qAA+OAAIW/y8AL///MAARAJMADzEAAB//MAAHBi8ABTIAD2cBAw1fAAgBAA8wAC8OjwAPXwAQHv8mAQ4wAA9gACIf/78AAy8AAL8AFQNOAg9gAAQa/zAAAKoCCD8ADx8BDgLdAg/vABsvAADvAA4PMAAdBygAHwDeARcuAAAwAA8fAQoMWwEPMAAQHwAwABEf/zAAFQI0AC8A/z4CGS8AAM0CFx8AMAAcChcAD18AEgkYAA+PABMOMQAOHQEPYQQFDi0ADwEAsA7RAA+ABmdQAAAA//8=\",\"length\":1920,\"rows\":40,\"type\":0,\"cols\":48}")
      .build();
  private static final double MHR_IN_GAME_MENU_THRESHOLD = 0.9;
  private static final ImageCompare.Param MP_NAME = ImageCompare.Param.builder()
      .area(Area.ofRect(490, 288, 206, 56)).method(ImageCompare.Method.TM_CCOEFF).preProcessor(
          com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder().enable(true)
              .binaryThreshold(0.5).inverse(false).threshType(
                  com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.OTSU)
              .adaptiveThreshC(2).adaptiveBlockSize(11).build()).template(
          "{\"data\":\"HwABAP//////////////////i0//////zQC6L///zgD/Th//zgBHByIACS0ADwwABQ0bAB//TwADL///MwAIDzcDRg/OAAce/4AADxsACxj/PwEMTQAf/84AKwRcAA88AAkv///OAAkf/84AGx7/zgAHDQAv///OACEELAADygAOLQEPjAICBIoAKP//QAAEQQAv//93AQEMGgAJ2QIFPwAFMwAPzgAkCnkBCBQBD84AGQsUAAu0AA/pAAQJvgAIzgAPTgACD84AHQnDAA/OAAYCKQAcAJgCDrIAD84AGhsAsgAv/wDOACAeAM4AD1wDDi///wMCBAP9AgLBAQ7OAAU0AAd8AgKAAAkXAASUAA/OACEfAM4AMi3//84ABkwAD84AAwvCAQblAARZAAXjAATkAQ/OADsFlQAPUQIDAz8AD84AAh8AzgAJBs0BAqsBCd0BHwDOAGQb/0YGCZsBDzQAAAf8AwrVAR8AzgAADwYEPwhjAQWnAwOhBgVUBQjzAAbjAw/OAAYnAP+MAQ0wBg+cAR0IYgUJzgAP5wUEATsDDxoICAm7AAaBAR//nAEPC0AHDjgDD84AIR//zgAQD/MAAg6JAQl6Ag/UBAQL4wAO+ggPcAYjDQMBD3AGCA9nAAcPRwMFDr0EDXwCByAAD84AJgSsAwnJBgf1BQ7NAA/SAwMHHwQJGAIPzgANB1MJBqkCBn4DAyAADgYED3ECFA7XBA/1CgELfQAIzwcLGQADlgAPVQICBV0CD84ABghrAA/NACEPpgUQD84ABR//zgABD9QEBgctAA6BAQ/OACwJSAIPzgASDX8AB+0DBCAAD84AAx8AkQUCDe8CD84AIAYrAAtSCQ7CDgxCDA4DBRv/CAALzgAYAK8GKQAA+wAPzgA1H//OAAQf/84ACgvRBQ4LCA7sBQ6XDgemCgw2Aw/OAAAf/84AIB8AzgAlHgD4BA/OABkDwAQvAADCBQcPagIVCS8KAzgDD84ADAhUAQ6SBQzsCA/OAAsH0wAODAkPcAYaHAAvDQ7TAQ/OABIHFgALYgIPzgAOHgDOAA4rCA9wBj4PzgABD8cKCA2ZBw/OAAEP0wcIDCwAHwDPABcOBAEPAQCqD80AugTRAB8AAQD/////////E1AAAAAAAA==\",\"length\":11536,\"rows\":56,\"type\":0,\"cols\":206}")
      .build();
  private static final String NUM_WHITELIST = "0123456789";
  private static final OCR.Param MP_NUM = OCR.Param.builder()
      .area(Area.ofRect(908, 296, 106, 46))
      .preProcessor(
          com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder().enable(true)
              .binaryThreshold(0.5).inverse(true).threshType(
                  com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.OTSU)
              .adaptiveThreshC(2).adaptiveBlockSize(11).build())
      .apiConfig(ApiConfig.builder().method(OCR.Method.NMU).whitelist(NUM_WHITELIST).build())
      .build();
  private static final OCR.Param MPA_NUM = OCR.Param.builder()
      .area(Area.ofRect(1464, 32, 138, 26))
      .preProcessor(
          com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder().enable(true)
              .binaryThreshold(0.5).inverse(true).threshType(
                  com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.OTSU)
              .adaptiveThreshC(2).adaptiveBlockSize(11).build())
      .apiConfig(ApiConfig.builder().method(OCR.Method.NMU).whitelist(NUM_WHITELIST).build())
      .build();
  private static final OCR.Param POINT_LACK = OCR.Param.builder()
      .area(Area.ofPoints(134, 982, 250, 1024))
      .preProcessor(
          com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder().enable(true)
              .binaryThreshold(0.5).inverse(true).threshType(
                  com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.OTSU)
              .adaptiveThreshC(2).adaptiveBlockSize(11).build())
      .apiConfig(ApiConfig.builder()
          .method(OCR.Method.CHS)
          .whitelist("点数不足")
          .build())
      .build();

  private static final Map<String, String> SKILLS = getSkills();
  private static final String SKILLS_WHITELIST = getSkillsWhitelist();
  private static final OCR.Param CHARM_SKILL_1 = Param.builder()
      .area(Area.ofPoints(1158, 415, 1351, 451))
      .apiConfig(ApiConfig.builder()
          .method(Method.CHS)
          .whitelist(SKILLS_WHITELIST)
          .build())
      .preProcessor(
          com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder().enable(true)
              .binaryThreshold(0.5).inverse(true).threshType(
                  com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.OTSU)
              .adaptiveThreshC(2).adaptiveBlockSize(11).build())
      .build();
  private static final OCR.Param CHARM_SKILL_2 = OCR.Param.builder()
      .area(Area.ofPoints(1156, 492, 1474, 522))
      .apiConfig(ApiConfig.builder()
          .method(Method.CHS)
          .whitelist(SKILLS_WHITELIST)
          .build())
      .preProcessor(
          com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.builder().enable(true)
              .binaryThreshold(0.5).inverse(true).threshType(
                  com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig.ThreshType.OTSU)
              .adaptiveThreshC(2).adaptiveBlockSize(11).build())
      .build();
  private static Map<String, Integer> skillTargets;
  private static List<List<Integer>> slotTargets;
  int number = 0;
  private Config config = new Config();
  private Long meldingPuddingCount;
  private Long mpAccelerantCount;
  private int pt = 10;

  private static String getSkillsWhitelist() {
    Set<Character> characterSet = new HashSet<>();
    for (String skill : SKILLS.keySet()) {
      skill.trim().chars().boxed().map(input -> (char) input.intValue()).forEach(characterSet::add);
    }
    StringBuilder sb = new StringBuilder();
    characterSet.stream().sorted().forEach(sb::append);
    return sb.toString();
  }

  private static Map<String, String> getSkills() {
    Map<String, String> skills = Maps.newHashMap();
    skills.put("逆袭", "逆袭");
    skills.put("逆效", "逆袭");
    skills.put("逆获", "逆袭");
    skills.put("逆约", "逆袭");
    skills.put("逆装", "逆袭");
    skills.put("挑战者", "挑战者");
    skills.put("无伤", "无伤");
    skills.put("怨恨", "怨恨");
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
    skills.put("夺取耐力", "夺取耐力");
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
    return skills;
  }

  @Override
  public void execute() {
    info("{} charms has been checked.", number);
    launchGame();
    walkToShop();
    toMeldingPot();
    if (!watchStars(pt)) {
      toNewCharmRandomSeed();
    }
  }

  @Override
  public String getScriptName() {
    return "MHR auto charm(CHS.Ver)";
  }

  private boolean watchStars(int pt) {
    for (int i = 0; i < pt; i++) {
      fillPot(10);
      boolean findTarget = checkCharm();
      if (findTarget) {
        backToGameMenu();
        saveAndExit();
        return true;
      }
      press(A);
      sleep(200);
      press(D_BOTTOM);
      sleep(150);
    }
    return false;
  }


  private void checkMeldingPuddingCount(int need) {
    if (meldingPuddingCount == null) {
      ImageCompare.Result result = until(() -> imageCompare(MP_NAME),
          input -> input.getSimilarity() > 0.8,
          () -> sleep(50), 10);
      if (result == null) {
        throw new AbortScriptException("Cannot find any Melding Pudding! Get some with amiibo!");
      }
      sleep(150);
      meldingPuddingCount = until(() -> ocrNumber(MP_NUM),
          Objects::nonNull,
          () -> sleep(50), 10);
      if (meldingPuddingCount < need * 5) {
        //throw new AbortScriptException("You have only " + meldingPuddingCount + "  MP Accelerants!  Get some with amiibo");
      }
      info("You have " + meldingPuddingCount + " Melding Pudding!");
      mpAccelerantCount = ocrNumber(MPA_NUM);
      mpAccelerantCount = until(() -> ocrNumber(MPA_NUM),
          Objects::nonNull,
          () -> sleep(50), 10);
      if (mpAccelerantCount < need) {
        //throw new AbortScriptException("You have only " + mpAccelerantCount + "  MP Accelerants!  Get some with amiibo");
      }
      info("You have " + mpAccelerantCount + " MP Accelerants");
    }
  }

  private void cleanMaterialNum() {
    mpAccelerantCount = null;
    meldingPuddingCount = null;
  }

  private void toNewCharmRandomSeed() {
    abortGame();
    launchGame();
    walkToShop();
    toMeldingPot();
    fillPot(1);
    getAllCharms();
    backToGameMenu();
    saveAndExit();
  }

  private void fillPot(int times) {
    cleanMaterialNum();
    for (int i = 0; i < times; i++) {
      fillPotOnce(times);
    }
  }

  private void launchGame() {
    until(() -> imageCompare(MHR_IN_GAME_MENU),
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
    sleep(500);
  }

  private void fillPotOnce(int times) {
    press(A);
    sleep(200);
    checkMeldingPuddingCount(times);
    press(A);
    sleep(200);
    press(D_BOTTOM);
    sleep(200);
    press(A);
    sleep(200);
    checkPoint();
    press(D_TOP);
    sleep(200);
    press(A);
    sleep(200);
    press(D_LEFT);
    sleep(200);
    press(A);
    sleep(200);
    checkPoint();
    press(A);
    sleep(200);
  }

  private void checkPoint() {
    Result ocr = ocr(POINT_LACK);
    if (ocr.getTextWithoutSpace().contains("点数不足")) {
      throw new AbortScriptException("You have no point! Get some with hunt!");
    }
  }

  private boolean checkCharm() {
    press(D_TOP);
    sleep(200);
    press(A);
    sleep(2000);
    AtomicBoolean find = new AtomicBoolean(false);
    until(() -> imageCompare(MELDING_POT_DONE),
        input -> input.getSimilarity() < 0.9,
        () -> {
          if (charmAnalyze()) {
            find.set(true);
          }
          press(A);
          sleep(200);
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
    until(() -> imageCompare(MHR_IN_GAME_MENU),
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
    Future<OCR.Result> rareF = async(() -> ocr(CHARM_RARE));
    Future<OCR.Result> level1F = async(() -> ocr(CHARM_LEVEL_1));
    Future<String> skill1F = async(() -> detectSkill(CHARM_SKILL_1));
    Future<OCR.Result> level2F = async(() -> ocr(CHARM_LEVEL_2));
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
    String skill1 = skill1F.get();
    String skill2 = skill2F.get();
    Long level1 = level1F.get().getTextAsNumber();
    Long level2 = level2F.get().getTextAsNumber();
    level1 = level1 == null ? 1 : level1;
    level2 = Strings.isNullOrEmpty(skill2) ? null : level2 == null ? 1 : level2;

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
    String result = String.format("R%s %s%s %s%s S%s%s%s", rare, skill1, level1, skill2,
        Strings.isNullOrEmpty(skill2) ? "" : level2 == null ? "" : level2, o1,
        o2, o3);
    info("DingZhen the One-Eye identified the charm as : {}", result);
    if (isTarget) {
      warn("find a target charm:{}", result);
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
    OCR.Result until = until(() -> ocr(param),
        input -> {
          String text = input.getTextWithoutSpace();
          if (input.getConfidence() < 10) {
            return true;
          }
          boolean b = SKILLS.containsKey(text);
          if (!b) {
            info("Unknown skill detected: {} , confidence: {} ", text, input.getConfidence());
          }
          return b;
        },
        () -> sleep(100), 5);
    return until == null ? ""
        : until.getConfidence() < 30 ? "" : SKILLS.get(until.getTextWithoutSpace());
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
    Integer pt = config.getHow_Many_Round_To_Change().getValue();
    if (pt == null || pt < 0) {//TODO
      throw new GuiAlertException("How_Many_Pot_To_Change must be greater than 0!");
    }
    this.pt = pt;
    info("How many rounds to change: {}", pt);
    String ts = config.getTarget_Slots().getValue();
    slotTargets = getSlotTargets(ts);
    if (slotTargets.isEmpty()) {
      throw new GuiAlertException("Slot targets is empty!");
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
        throw new GuiAlertException("Invalid slot target: " + s);
      }
      List<Integer> ints = new ArrayList<>();
      for (int i = 0; i < s.length(); i++) {
        char c = s.charAt(i);
        if (c < 48 || c > 52) {
          throw new GuiAlertException("Invalid slot target: " + s);
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
        throw new GuiAlertException("Invalid skill target: " + s);
      }
      char level = s.charAt(s.length() - 1);
      if (level < 48 || level > 57) {
        throw new GuiAlertException("Invalid skill level: " + s);
      }
      String skill = s.substring(0, s.length() - 1);
      if (!collect.contains(skill)) {
        throw new GuiAlertException("Invalid skill: " + s);
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

    private SimpleIntegerProperty How_Many_Round_To_Change = new SimpleIntegerProperty(10);
    @FormFactory(TextAreaFactory.class)
    private SimpleStringProperty Target_Skills = new SimpleStringProperty();
    @FormFactory(TextAreaFactory.class)
    private SimpleStringProperty Target_Slots = new SimpleStringProperty();
    @ConfigLabel("Capture Screen When Find")
    private SimpleBooleanProperty captureScreenWhenFind = new SimpleBooleanProperty(true);

  }


}

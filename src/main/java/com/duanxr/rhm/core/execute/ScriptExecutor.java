package com.duanxr.rhm.core.execute;

import static com.duanxr.rhm.config.ConstantConfig.MULTIPLE_OCR_TIMEOUT;
import static com.duanxr.rhm.config.ConstantConfig.MULTIPLE_TEMPLATE_MATCH_TIMEOUT;
import static com.duanxr.rhm.config.ConstantConfig.TEMPLATE_MATCH_THRESHOLD;

import com.duanxr.rhm.cache.ImageAreaCache;
import com.duanxr.rhm.cache.entity.CachedImageArea;
import com.duanxr.rhm.cache.entity.CachedImageTemplate;
import com.duanxr.rhm.core.handler.ActionHandler;
import com.duanxr.rhm.core.handler.DisplayHandler;
import com.duanxr.rhm.core.handler.InputHandler;
import com.duanxr.rhm.core.handler.action.ButtonAction;
import com.duanxr.rhm.core.handler.action.PressAction;
import com.duanxr.rhm.core.handler.action.StickAction;
import com.duanxr.rhm.core.handler.action.StickSimpleAction;
import com.duanxr.rhm.core.parser.MultipleParserAllHelper;
import com.duanxr.rhm.core.parser.MultipleParserBestHelper;
import com.duanxr.rhm.core.parser.MultipleParserFirstHelper;
import com.duanxr.rhm.core.parser.image.ImageCompareParser;
import com.duanxr.rhm.core.parser.image.ImageOcrParser;
import com.duanxr.rhm.core.parser.image.define.DefineImageOcr;
import com.duanxr.rhm.core.parser.image.define.DefineImageTemplate;
import com.duanxr.rhm.core.parser.image.models.ImageCompareResult;
import com.duanxr.rhm.core.parser.image.models.ImageOcrResult;
import com.google.common.base.Strings;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.opencv.core.Mat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Duanran 2019/12/13
 */
@Slf4j
@Service
public class ScriptExecutor {

  @Autowired
  private ScriptRunner scriptRunner;

  @Autowired
  private InputHandler inputHandler;

  @Autowired
  private ActionHandler actionHandler;

  @Autowired
  private DisplayHandler displayHandler;

  @Autowired
  private ImageCompareParser imageCompareParser;

  @Autowired
  private ImageOcrParser imageOcrParser;


  public void press(ButtonAction buttonType) {
    actionHandler.sendAction(buttonType, PressAction.DOWN_AND_UP);
  }

  public void hold(ButtonAction buttonType) {
    actionHandler.sendAction(buttonType, PressAction.DOWN);
  }

  public void release(ButtonAction buttonType) {
    actionHandler.sendAction(buttonType, PressAction.UP);
  }

  public void press(StickSimpleAction stickSimpleAction) {
    actionHandler.sendAction(stickSimpleAction, PressAction.DOWN_AND_UP);
  }

  public void hold(StickSimpleAction stickSimpleAction) {
    actionHandler.sendAction(stickSimpleAction, PressAction.DOWN);
  }

  public void release(StickSimpleAction stickSimpleAction) {
    actionHandler.sendAction(stickSimpleAction, PressAction.UP);
  }

  public void hold(StickAction stickAction) {
    actionHandler.sendAction(stickAction);
  }

  @SneakyThrows
  public void sleep(int waitMillisecond) {
    TimeUnit.MILLISECONDS.sleep(waitMillisecond);
  }

  @SneakyThrows
  private ImageCompareResult singleImageCompare(double v, DefineImageTemplate imageTemplate) {
    CachedImageTemplate detectionImage = imageTemplate.loadImageTemplate();
    List<CachedImageArea> detectionImageAreas = ImageAreaCache.get(imageTemplate.getName());
    MultipleParserFirstHelper<ImageCompareResult> multipleSearchHelper = new MultipleParserFirstHelper<>(
        detectionImageAreas.size(),
        MULTIPLE_TEMPLATE_MATCH_TIMEOUT, null);
    for (CachedImageArea cachedImageArea : detectionImageAreas) {
      scriptRunner.getScriptExecutorService().execute(() -> {
        try {
          double point = imageCompareParser.imageCompare(detectionImage, cachedImageArea);
          if (point > v) {
            ImageCompareResult result = new ImageCompareResult(detectionImage.getName(),
                cachedImageArea.getNumber(), point);
            displayHandler.setDetectRect(cachedImageArea,imageTemplate.getName(),
                String.format("%.2f", point * 100) + "%");
            multipleSearchHelper.hit(result);
          } else {
            multipleSearchHelper.notHit();
          }
        } catch (Exception e) {
          multipleSearchHelper.notHit();
          log.warn("single image detection exception.", e);
        }
      });
    }
    return multipleSearchHelper.get();
  }

  @SneakyThrows
  public ImageCompareResult imageCompareFirst(double v,DefineImageTemplate... imageTemplates) {
    Mat screen = inputHandler.getScreenshot(true);
    if (imageTemplates.length == 1) {
      return singleImageCompare(v, imageTemplates[0]);
    }
    MultipleParserFirstHelper<ImageCompareResult> multipleSearchHelper = new MultipleParserFirstHelper<>(
        imageTemplates.length,
        MULTIPLE_TEMPLATE_MATCH_TIMEOUT, new ImageCompareResult());
    for (DefineImageTemplate imageTemplate : imageTemplates) {
      scriptRunner.getScriptExecutorService().execute(() -> {
        try {
          ImageCompareResult result = singleImageCompare(v, imageTemplate);
          if (result != null) {
            multipleSearchHelper.hit(result);
          } else {
            multipleSearchHelper.notHit();
          }
        } catch (Exception e) {
          multipleSearchHelper.notHit();
          log.warn("multiple image detection loop exception.", e);
        }
      });
    }
    return multipleSearchHelper.get();
  }

  @SneakyThrows
  public ImageCompareResult imageCompareBest(double v, DefineImageTemplate... imageTemplates) {
    Mat screen = inputHandler.getScreenshot(true);
    if (imageTemplates.length == 1) {
      return singleImageCompare(v,imageTemplates[0]);
    }
    MultipleParserBestHelper<ImageCompareResult> multipleSearchHelper = new MultipleParserBestHelper<>(
        imageTemplates.length,
        MULTIPLE_TEMPLATE_MATCH_TIMEOUT, new ImageCompareResult());
    for (DefineImageTemplate imageTemplate : imageTemplates) {
      scriptRunner.getScriptExecutorService().execute(() -> {
        try {
          ImageCompareResult result = singleImageCompare(v, imageTemplate);
          if (result != null) {
            multipleSearchHelper.hit(result, result.getPoint());
          } else {
            multipleSearchHelper.notHit();
          }
        } catch (Exception e) {
          multipleSearchHelper.notHit();
          log.warn("multiple image detection loop exception.", e);
        }
      });
    }
    return multipleSearchHelper.get();
  }

  @SneakyThrows
  public List<ImageCompareResult> imageCompareAll(double v,DefineImageTemplate... imageTemplates) {
    Mat screen = inputHandler.getScreenshot(true);
    if (imageTemplates.length == 1) {
      return Collections.singletonList(singleImageCompare(v, imageTemplates[0]));
    }
    MultipleParserAllHelper<ImageCompareResult> multipleSearchHelper = new MultipleParserAllHelper<>(
        imageTemplates.length,
        MULTIPLE_TEMPLATE_MATCH_TIMEOUT);
    for (DefineImageTemplate imageTemplate : imageTemplates) {
      scriptRunner.getScriptExecutorService().execute(() -> {
        try {
          ImageCompareResult result = singleImageCompare(v, imageTemplate);
          if (result != null) {
            multipleSearchHelper.hit(result);
          } else {
            multipleSearchHelper.notHit();
          }
        } catch (Exception e) {
          multipleSearchHelper.notHit();
          log.warn("multiple image detection loop exception.", e);
        }
      });
    }
    return multipleSearchHelper.get();
  }

  @SneakyThrows
  public List<ImageOcrResult> imageOcrAll(DefineImageOcr... defineImageOcrs) {
    Mat screen = inputHandler.getScreenshot(true);
    if (defineImageOcrs.length == 1) {
      return Collections.singletonList(singleImageOcr(defineImageOcrs[0]));
    }
    MultipleParserAllHelper<ImageOcrResult> multipleSearchHelper = new MultipleParserAllHelper<>(
        defineImageOcrs.length, MULTIPLE_OCR_TIMEOUT);
    for (DefineImageOcr defineImageOcr : defineImageOcrs) {
      scriptRunner.getScriptExecutorService().execute(() -> {
        try {
          ImageOcrResult result = singleImageOcr(defineImageOcr);
          if (result != null && !Strings.isNullOrEmpty(result.getResult())) {
            multipleSearchHelper.hit(result);
          } else {
            multipleSearchHelper.notHit();
          }
        } catch (Exception e) {
          multipleSearchHelper.notHit();
          e.printStackTrace();
          log.warn("multiple image detection loop exception.", e);
        }
      });
    }
    return multipleSearchHelper.get();
  }

  @SneakyThrows
  private ImageOcrResult singleImageOcr(DefineImageOcr defineImageOcr) {
    List<CachedImageArea> detectionImageAreas = ImageAreaCache.get(defineImageOcr.getName());
    MultipleParserFirstHelper<ImageOcrResult> multipleSearchHelper = new MultipleParserFirstHelper<>(
        detectionImageAreas.size(),
        MULTIPLE_OCR_TIMEOUT, new ImageOcrResult(defineImageOcr.getName(), null));
    for (CachedImageArea cachedImageArea : detectionImageAreas) {
      scriptRunner.getScriptExecutorService().execute(() -> {
        try {
          String imageOcrResult = imageOcrParser
              .imageOcr(cachedImageArea, defineImageOcr.getOcrType());
          if (!Strings.isNullOrEmpty(imageOcrResult)) {
            imageOcrResult = imageOcrResult.trim().replaceAll("\\s","");
            displayHandler.setDetectRect(cachedImageArea,defineImageOcr.getName(), imageOcrResult);
            ImageOcrResult result = new ImageOcrResult(defineImageOcr.getName(), imageOcrResult);
            multipleSearchHelper.hit(result);
          } else {
            multipleSearchHelper.notHit();
          }
        } catch (Exception e) {
          multipleSearchHelper.notHit();
          e.printStackTrace();
          log.warn("single image detection exception.", e);
        }
      });
    }
    return multipleSearchHelper.get();
  }
}

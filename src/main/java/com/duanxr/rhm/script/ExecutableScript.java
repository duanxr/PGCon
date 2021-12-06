package com.duanxr.rhm.script;

import static com.duanxr.rhm.config.ConstantConfig.TEMPLATE_MATCH_THRESHOLD;

import com.duanxr.rhm.config.ConstantConfig;
import com.duanxr.rhm.core.execute.ScriptExecutor;
import com.duanxr.rhm.core.handler.action.ButtonAction;
import com.duanxr.rhm.core.handler.action.StickSimpleAction;
import com.duanxr.rhm.core.parser.image.define.DefineImageOcr;
import com.duanxr.rhm.core.parser.image.define.DefineImageOcrCondition;
import com.duanxr.rhm.core.parser.image.define.DefineImageTemplateCondition;
import com.duanxr.rhm.core.parser.image.models.ImageCompareResult;
import com.duanxr.rhm.core.parser.image.models.ImageOcrResult;
import com.google.common.base.Strings;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Setter;

/**
 * @author Duanran 2019/12/17
 */
public abstract class ExecutableScript implements Script {

  @Setter
  private ScriptExecutor scriptExecutor;

  protected List<Subscript> subscriptList = new ArrayList<>();

  @Override
  public List<Subscript> getSubscript() {
    return this.subscriptList;
  }

  @Override
  public void setExecutor(ScriptExecutor scriptExecutor) {
    this.scriptExecutor = scriptExecutor;
  }

  protected void sleep(int m) {
    scriptExecutor.sleep(m);
  }

  protected void press(ButtonAction action) {
    scriptExecutor.press(action);
    sleep(ConstantConfig.OUTPUT_INTERVAL);
  }

  protected void hold(StickSimpleAction action) {
    scriptExecutor.hold(action);
    sleep(ConstantConfig.OUTPUT_INTERVAL);
  }

  protected void release(StickSimpleAction action) {
    scriptExecutor.release(action);
    sleep(ConstantConfig.OUTPUT_INTERVAL);
  }


  protected void press(StickSimpleAction action) {
    scriptExecutor.press(action);
    sleep(ConstantConfig.OUTPUT_INTERVAL);
  }

  protected void press(ButtonAction action, int m) {
    scriptExecutor.hold(action);
    sleep(m);
    scriptExecutor.release(action);
    sleep(ConstantConfig.OUTPUT_INTERVAL);
  }

  protected void press(StickSimpleAction action, int m) {
    scriptExecutor.hold(action);
    sleep(m);
    scriptExecutor.release(action);
    sleep(ConstantConfig.OUTPUT_INTERVAL);
  }

  protected void waitImage(DefineImageTemplateCondition imageTemplateCondition) {
    while (!isImageExits(imageTemplateCondition)) {
      sleep(ConstantConfig.SCRIPT_IMAGE_CHECK_INTERVAL);
    }
  }

  protected void waitImage(DefineImageTemplateCondition imageTemplateCondition, Function function) {
    while (!isImageExits(imageTemplateCondition)) {
      sleep(ConstantConfig.SCRIPT_IMAGE_CHECK_INTERVAL);
      function.run();
    }
  }

  protected void waitNotImage(DefineImageTemplateCondition imageTemplateCondition) {
    while (isImageExits(imageTemplateCondition)) {
      sleep(ConstantConfig.SCRIPT_IMAGE_CHECK_INTERVAL);
    }
  }

  protected void waitNotImage(DefineImageTemplateCondition imageTemplateCondition,
      Function function) {
    while (isImageExits(imageTemplateCondition)) {
      sleep(ConstantConfig.SCRIPT_IMAGE_CHECK_INTERVAL);
      function.run();
    }
  }

  protected void waitText(DefineImageOcrCondition imageOcrCondition) {
    while (!isTextExits(imageOcrCondition)) {
      sleep(ConstantConfig.SCRIPT_IMAGE_CHECK_INTERVAL);
    }

  }

  protected void waitText(DefineImageOcrCondition imageOcrCondition, Function function) {
    while (!isTextExits(imageOcrCondition)) {
      sleep(ConstantConfig.SCRIPT_IMAGE_CHECK_INTERVAL);
      function.run();
    }
  }

  protected void waitNotText(DefineImageOcrCondition imageOcrCondition) {
    while (isTextExits(imageOcrCondition)) {
      sleep(ConstantConfig.SCRIPT_IMAGE_CHECK_INTERVAL);
    }

  }

  protected void waitNotText(DefineImageOcrCondition imageOcrCondition, Function function) {
    while (isTextExits(imageOcrCondition)) {
      sleep(ConstantConfig.SCRIPT_IMAGE_CHECK_INTERVAL);
      function.run();
    }
  }

  protected Map<String, String> getOcr(DefineImageOcr... defineImageOcr) {
    return scriptExecutor.imageOcrAll(defineImageOcr).stream().filter(
        imageOcrResult -> imageOcrResult != null && !Strings
            .isNullOrEmpty(imageOcrResult.getResult()))
        .collect(Collectors.toMap(ImageOcrResult::getOcrName, ImageOcrResult::getResult));
  }

  protected boolean isImageExits(DefineImageTemplateCondition... defineImageTemplateConditions) {
    ImageCompareResult imageCompareResult = scriptExecutor
        .imageCompareBest(TEMPLATE_MATCH_THRESHOLD,defineImageTemplateConditions);
    if (imageCompareResult == null) {
      return false;
    }
    for (DefineImageTemplateCondition defineImageTemplateCondition : defineImageTemplateConditions) {
      if (defineImageTemplateCondition.getName().equals(imageCompareResult.getImageName())&&defineImageTemplateCondition.isHit(imageCompareResult.getAreaNumber())) {
        return true;
      }
    }
    return false;
  }

  protected boolean isImageExits(double v, DefineImageTemplateCondition... defineImageTemplateConditions) {
    ImageCompareResult imageCompareResult = scriptExecutor
        .imageCompareBest(v,defineImageTemplateConditions);
    if (imageCompareResult == null) {
      return false;
    }
    for (DefineImageTemplateCondition defineImageTemplateCondition : defineImageTemplateConditions) {
      if (defineImageTemplateCondition.getName().equals(imageCompareResult.getImageName())&&defineImageTemplateCondition.isHit(imageCompareResult.getAreaNumber())) {
        return true;
      }
    }
    return false;
  }

  protected boolean isTextExits(DefineImageOcrCondition defineImageOcrCondition) {
    List<ImageOcrResult> imageOcrResults = scriptExecutor
        .imageOcrAll(defineImageOcrCondition);
    return imageOcrResults != null && !imageOcrResults.isEmpty() && defineImageOcrCondition
        .isHit(imageOcrResults.get(0).getResult());
  }

  @FunctionalInterface
  public interface Function {

    void run();

  }
}


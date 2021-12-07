package com.duanxr.pgcon.core;

import com.duanxr.pgcon.core.detect.image.compare.ImageCompare;
import com.duanxr.pgcon.core.detect.ocr.Ocr;
import com.duanxr.pgcon.output.Controller;
import lombok.extern.slf4j.Slf4j;

/**
 * @author 段然 2021/12/7
 */
@Slf4j
public abstract class BaseScript implements Runnable {

  protected final Ocr ocr;
  protected final Controller controller;
  protected final ImageCompare imageCompare;

  BaseScript(Controller controller, Ocr ocr,
      ImageCompare imageCompare) {
    this.ocr = ocr;
    this.controller = controller;
    this.imageCompare = imageCompare;
    controller.clear();
  }

  @Override
  public void run() {
    try {
      execute();
    } catch (Exception e) {
      log.error("An error occurred while executing the script, please report this to the developer. Trying to save Switch video.",e);
      controller.capture();
    }
  }

  protected abstract void execute() throws Exception;

}

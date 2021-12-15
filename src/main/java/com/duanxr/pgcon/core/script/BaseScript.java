package com.duanxr.pgcon.core.script;

import com.duanxr.pgcon.core.detect.image.compare.ImageCompare;
import com.duanxr.pgcon.core.detect.ocr.Ocr;
import com.duanxr.pgcon.core.util.Pokemon;
import com.duanxr.pgcon.output.Controller;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.extern.slf4j.Slf4j;

/**
 * @author 段然 2021/12/7
 */
@Slf4j
public abstract class BaseScript implements Script {

  public final Ocr ocr;
  public final Pokemon pokemon;
  public final Controller controller;
  public final AtomicBoolean running;
  public final ImageCompare imageCompare;
  public final ExecutorService executors;

  protected BaseScript(ScriptLoader scriptLoader) {
    this.ocr = scriptLoader.getOcr();
    this.pokemon = scriptLoader.getPokemon();
    this.executors = scriptLoader.getExecutors();
    this.controller = scriptLoader.getController();
    this.imageCompare = scriptLoader.getImageCompare();
    this.running = new AtomicBoolean(false);
    scriptLoader.add(this);
  }

  protected abstract void execute() throws Exception;

  @Override
  public void run() {
    try {
      log.info("开始执行脚本:{}", name());
      running.set(true);
      controller.clear();
      while (running.get()) {
        execute();
      }
      log.info("脚本执行结束:{}", name());
    } catch (Exception e) {
      log.error(
          "An error occurred while executing the script, please report this to the developer. Trying to save Switch video.",
          e);
      controller.capture();
    }
  }

  @Override
  public void stop() {
    running.set(false);
    controller.clear();
  }

}

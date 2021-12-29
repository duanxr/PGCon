package com.duanxr.pgcon.core.script;

import com.duanxr.pgcon.core.PGCon;
import com.duanxr.pgcon.core.detect.image.compare.ImageCompare;
import com.duanxr.pgcon.core.detect.ocr.OCR;
import com.duanxr.pgcon.output.Controller;
import com.google.common.eventbus.EventBus;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import lombok.SneakyThrows;

/**
 * @author 段然 2021/12/29
 */
public abstract class PGScript implements Script {

  protected final OCR ocr;
  protected final PGCon pg;
  protected final EventBus eventBus;
  protected final Controller controller;
  protected final ImageCompare imageCompare;
  protected final ExecutorService executors;

  protected PGScript(PGCon pg) {
    this.pg = pg;
    this.controller = pg.getController();
    this.executors = pg.getExecutors();
    this.eventBus = pg.getEventBus();
    this.imageCompare = new ImageCompare() {
      @Override
      public Result detect(Param area) {
        return pg.getImageCompare().detect(area);
      }

      @Override
      public Future<Result> asyncDetect(Param area) {
        return pg.getImageCompare().asyncDetect(area);
      }
    };
    this.ocr = new OCR() {
      @Override
      public List<Result> detect(Param area) {
        return pg.getOcr().detect(area);
      }

      @Override
      public Future<List<Result>> asyncDetect(Param area) {
        return pg.getOcr().asyncDetect(area);
      }
    };
  }

  @SneakyThrows
  protected void sleep(long millis) {
    Thread.sleep(millis);
  }

  protected void addExtraPanel(Component component) {
    GridBagConstraints bagConstraints = new GridBagConstraints();
    bagConstraints.fill = GridBagConstraints.BOTH;
    bagConstraints.anchor = GridBagConstraints.WEST;
    bagConstraints.gridheight = 1;
    bagConstraints.gridwidth = 1;
    bagConstraints.gridx = 1;
    bagConstraints.gridy = 3;
    pg.getControlPanel().add(component, bagConstraints);
    pg.getControlPanel().revalidate();
  }
}

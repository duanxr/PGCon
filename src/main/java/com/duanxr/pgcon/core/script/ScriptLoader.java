package com.duanxr.pgcon.core.script;

import com.duanxr.pgcon.core.detect.image.compare.ImageCompare;
import com.duanxr.pgcon.core.detect.ocr.Ocr;
import com.duanxr.pgcon.output.Controller;
import java.util.LinkedList;
import java.util.List;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Duanran 2019/12/18
 */
@Component
public class ScriptLoader {

  @Getter
  private final Ocr ocr;
  @Getter
  private final Controller controller;
  @Getter
  private final ImageCompare imageCompare;
  private final List<Script> scriptList = new LinkedList<>();

  public ScriptLoader(@Autowired Ocr ocr, @Autowired Controller controller,
      @Autowired ImageCompare imageCompare) {
    this.ocr = ocr;
    this.controller = controller;
    this.imageCompare = imageCompare;
  }

  public void add(Script script) {
    scriptList.add(script);
  }

  public List<Script> getScriptList() {
    return scriptList;
  }

}

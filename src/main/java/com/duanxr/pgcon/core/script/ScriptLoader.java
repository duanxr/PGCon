package com.duanxr.pgcon.core.script;

import com.duanxr.pgcon.core.detect.ImageCompare;
import com.duanxr.pgcon.core.detect.ocr.Ocr;
import com.duanxr.pgcon.core.util.Pokemon;
import com.duanxr.pgcon.output.Controller;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Duanran 2019/12/18
 */
@Component
public class ScriptLoader {

  //TODO add controll manager
  @Getter
  private final Ocr ocr;
  @Getter
  private final Controller controller;
  @Getter
  private final ImageCompare imageCompare;
  @Getter
  private final Pokemon pokemon;
  @Getter
  private final ExecutorService executors = Executors.newFixedThreadPool(60);
  private final List<Script> scriptList = new LinkedList<>();

  public ScriptLoader(@Autowired Ocr ocr, @Autowired Controller controller,
      @Autowired ImageCompare imageCompare, @Autowired Pokemon pokemon) {
    this.ocr = ocr;
    this.controller = controller;
    this.imageCompare = imageCompare;
    this.pokemon = pokemon;
  }

  public void add(Script script) {
    scriptList.add(script);
  }

  public List<Script> getScriptList() {
    return scriptList;
  }

}

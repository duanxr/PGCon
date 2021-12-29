package com.duanxr.pgcon.core.script;

import com.duanxr.pgcon.core.PGCon;
import java.util.LinkedList;
import java.util.List;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Duanran 2019/12/18
 */
@Getter
@Component
public class ScriptLoader {

  private final List<Script> scriptList = new LinkedList<>();

  public void add(Script script) {
    scriptList.add(script);
  }


}

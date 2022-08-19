package com.duanxr.pgcon.script.api;

import com.duanxr.pgcon.component.PGConComponents;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

/**
 * @author 段然 2021/12/9
 */
public interface Script<T> {

  void execute() throws InterruptedException;
  default void load() {
    // do nothing
  }

  default void destroy() {
    // do nothing
  }

  ScriptInfo<T> getInfo();

  void setComponents(PGConComponents components);

}

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

  default void reset() {
    // do nothing
  }

  ScriptInfo<T> getInfo();

  void setComponents(PGConComponents components);

  @Data
  @Builder
  class ScriptInfo<T> {
    private T config;
    private boolean isLoop;
    @NonNull
    private String name;

    public boolean isConfigurable() {
      return config != null;
    }
  }
}

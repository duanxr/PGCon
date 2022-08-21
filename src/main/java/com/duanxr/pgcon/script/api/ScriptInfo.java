package com.duanxr.pgcon.script.api;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

/**
 * @author 段然 2022/8/20
 */
@Data
@Builder
public class ScriptInfo<T> {
  private T config;
  private boolean isLoop;
  private boolean isHidden;
  @NonNull
  private String description;

  public boolean isConfigurable() {
    return config != null;
  }
}
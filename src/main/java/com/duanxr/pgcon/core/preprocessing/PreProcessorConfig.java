package com.duanxr.pgcon.core.preprocessing;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author 段然 2022/8/1
 */
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class PreProcessorConfig {

  private boolean enable = false;

}

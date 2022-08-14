package com.duanxr.pgcon.core.preprocessing.config;

import com.duanxr.pgcon.core.preprocessing.PreProcessorConfig;
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
public class ResizePreProcessorConfig extends PreProcessorConfig {

  private double scale = 1.0;

}

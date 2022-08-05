package com.duanxr.pgcon.algo.preprocessing.config;

import com.duanxr.pgcon.algo.preprocessing.PreProcessorConfig;
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
public class ChannelsFilterPreProcessorConfig extends PreProcessorConfig {

  private double redWeight = 1.0;

  private double greenWeight = 1.0;

  private double blueWeight = 1.0;

}

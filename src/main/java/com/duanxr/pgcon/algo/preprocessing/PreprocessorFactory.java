package com.duanxr.pgcon.algo.preprocessing;

import com.duanxr.pgcon.algo.preprocessing.config.ChannelsFilterPreProcessorConfig;
import com.duanxr.pgcon.algo.preprocessing.config.ThreshPreProcessorConfig;
import com.duanxr.pgcon.algo.preprocessing.impl.ChannelsFilterPreProcessor;
import com.duanxr.pgcon.algo.preprocessing.impl.ThreshPreProcessor;
import com.duanxr.pgcon.gui.exception.AbortScriptException;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * @author 段然 2022/8/1
 */
@Component
public class PreprocessorFactory {
  private final LoadingCache<List<PreProcessorConfig>, List<PreProcessor>> preProcessorCache;

  public PreprocessorFactory() {
    preProcessorCache = Caffeine.newBuilder().build(PreprocessorFactory::loadList);
  }

  private static List<PreProcessor> loadList(List<PreProcessorConfig> preProcessorConfigs) {
    return preProcessorConfigs.stream()
        .map(PreprocessorFactory::load).collect(Collectors.toList());
  }

  private static PreProcessor load(PreProcessorConfig config) {
    if (config instanceof ThreshPreProcessorConfig) {
      return new ThreshPreProcessor((ThreshPreProcessorConfig) config);
    } else if (config instanceof ChannelsFilterPreProcessorConfig) {
      return new ChannelsFilterPreProcessor((ChannelsFilterPreProcessorConfig) config);
    } else {
      throw new AbortScriptException("unknown preprocessor config: " + config.getClass());
    }
  }

  public List<PreProcessor> getPreProcessors(List<PreProcessorConfig> preProcessorConfigs) {
    return preProcessorCache.get(preProcessorConfigs);
  }

}
package com.duanxr.pgcon.core.preprocessing;

import com.duanxr.pgcon.core.preprocessing.config.ChannelsFilterPreProcessorConfig;
import com.duanxr.pgcon.core.preprocessing.config.ColorPickFilterPreProcessorConfig;
import com.duanxr.pgcon.core.preprocessing.config.NormalizePreProcessorConfig;
import com.duanxr.pgcon.core.preprocessing.config.ResizePreProcessorConfig;
import com.duanxr.pgcon.core.preprocessing.config.SmoothingPreProcessorConfig;
import com.duanxr.pgcon.core.preprocessing.config.ThreshPreProcessorConfig;
import com.duanxr.pgcon.core.preprocessing.impl.ChannelsFilterPreProcessor;
import com.duanxr.pgcon.core.preprocessing.impl.ColorPickFilterPreProcessor;
import com.duanxr.pgcon.core.preprocessing.impl.NormalizePreProcessor;
import com.duanxr.pgcon.core.preprocessing.impl.ResizePreProcessor;
import com.duanxr.pgcon.core.preprocessing.impl.SmoothingPreProcessor;
import com.duanxr.pgcon.core.preprocessing.impl.ThreshPreProcessor;
import com.duanxr.pgcon.exception.InterruptScriptException;
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
    preProcessorCache = Caffeine.newBuilder().maximumSize(100).build(PreprocessorFactory::loadList);
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
    } else if (config instanceof NormalizePreProcessorConfig) {
      return new NormalizePreProcessor((NormalizePreProcessorConfig) config);
    } else if (config instanceof ColorPickFilterPreProcessorConfig) {
      return new ColorPickFilterPreProcessor((ColorPickFilterPreProcessorConfig) config);
    } else if (config instanceof ResizePreProcessorConfig) {
      return new ResizePreProcessor((ResizePreProcessorConfig) config);
    } else if (config instanceof SmoothingPreProcessorConfig) {
      return new SmoothingPreProcessor((SmoothingPreProcessorConfig) config);
    } else {
      throw new InterruptScriptException("unknown preprocessor config: " + config.getClass());
    }
  }

  public List<PreProcessor> getPreProcessors(List<PreProcessorConfig> preProcessorConfigs) {
    return preProcessorCache.get(preProcessorConfigs);
  }

}

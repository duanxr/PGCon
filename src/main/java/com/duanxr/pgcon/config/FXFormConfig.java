package com.duanxr.pgcon.config;

import com.dooapp.fxform.view.factory.DefaultTooltipFactoryProvider;
import com.dooapp.fxform.view.factory.FactoryProvider;
import com.duanxr.pgcon.gui.fxform.factory.PercentageFactory;
import com.duanxr.pgcon.gui.fxform.provider.ConfigEditorFactoryProvider;
import com.duanxr.pgcon.gui.fxform.provider.ConfigLabelFactoryProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 段然 2022/7/31
 */
@Configuration
public class FXFormConfig {
  @Bean
  public FactoryProvider editorFactoryProvider() {
    ConfigEditorFactoryProvider factoryProvider = new ConfigEditorFactoryProvider();
    factoryProvider.addFactory(new PercentageFactory());
    return factoryProvider;
  }
  @Bean
  public FactoryProvider labelFactoryProvider() {
    return new ConfigLabelFactoryProvider();
  }

  @Bean FactoryProvider tooltipFactoryProvider() {
    return new DefaultTooltipFactoryProvider();
  }
}

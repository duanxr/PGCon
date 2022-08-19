package com.duanxr.pgcon.gui;

import com.dooapp.fxform.FXForm;
import com.dooapp.fxform.view.factory.FactoryProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * @author 段然 2022/8/14
 */
@Component
public class FXFormGenerator {

  private final FactoryProvider tooltipFactoryProvider;
  private final FactoryProvider editorFactoryProvider;
  private final FactoryProvider labelFactoryProvider;

  public FXFormGenerator(
      @Qualifier("tooltipFactoryProvider") FactoryProvider tooltipFactoryProvider,
      @Qualifier("editorFactoryProvider") FactoryProvider editorFactoryProvider,
      @Qualifier("labelFactoryProvider") FactoryProvider labelFactoryProvider) {
    this.tooltipFactoryProvider = tooltipFactoryProvider;
    this.editorFactoryProvider = editorFactoryProvider;
    this.labelFactoryProvider = labelFactoryProvider;
  }

  public FXForm<?> generateNode(Object bean) {
    return new FXForm<>(bean, labelFactoryProvider,
        tooltipFactoryProvider, editorFactoryProvider);
  }

}

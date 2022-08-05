package com.duanxr.pgcon.gui.fxform.provider;

import com.dooapp.fxform.view.factory.DefaultFactoryProvider;
import com.duanxr.pgcon.gui.fxform.factory.AnnotationFactory;
import com.duanxr.pgcon.gui.fxform.handler.AnnotationHandler;

/**
 * @author 段然 2022/7/31
 */
public class ConfigEditorFactoryProvider extends DefaultFactoryProvider {
  public void addFactory(AnnotationFactory factory) {
    this.addFactory(new AnnotationHandler(factory.getAnnotationClass()),factory);
  }

}

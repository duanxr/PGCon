package com.duanxr.pgcon.gui.fxform.factory;

import com.dooapp.fxform.view.FXFormNode;
import java.lang.annotation.Annotation;
import javafx.util.Callback;
import lombok.Getter;

/**
 * @author 段然 2022/7/31
 */
public abstract class AnnotationFactory implements Callback<Void, FXFormNode> {

  @Getter
  private final Class<? extends Annotation> annotationClass;


  protected AnnotationFactory(Class<? extends Annotation> annotationClass) {
    this.annotationClass = annotationClass;
  }
}

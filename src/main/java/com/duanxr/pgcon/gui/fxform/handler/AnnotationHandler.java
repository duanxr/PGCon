package com.duanxr.pgcon.gui.fxform.handler;

import com.dooapp.fxform.handler.ElementHandler;
import com.dooapp.fxform.model.Element;
import java.lang.annotation.Annotation;

/**
 * @author 段然 2022/7/31
 */
public class AnnotationHandler implements ElementHandler {

  private final Class<? extends Annotation> annotationClass;

  public AnnotationHandler(Class<? extends Annotation> annotationClass) {
    this.annotationClass = annotationClass;
  }

  public boolean handle(Element element) {
    try {
      Annotation annotation = element.getAnnotation(annotationClass);
      return annotation != null;
    } catch (Exception ignored) {
    }
    return false;
  }
}

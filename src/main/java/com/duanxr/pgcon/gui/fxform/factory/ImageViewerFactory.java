package com.duanxr.pgcon.gui.fxform.factory;

import com.dooapp.fxform.view.FXFormNode;
import com.dooapp.fxform.view.FXFormNodeWrapper;
import javafx.scene.control.PasswordField;
import javafx.scene.image.ImageView;
import javafx.util.Callback;

/**
 * @author 段然 2022/7/31
 */
public class ImageViewerFactory implements Callback<Void, FXFormNode> {

  @Override
  public FXFormNode call(Void aVoid) {
     ImageView imageView = new ImageView();
    return new FXFormNodeWrapper(imageView, imageView.imageProperty());
  }

}

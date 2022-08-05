package com.duanxr.pgcon.gui.debug;

import com.dooapp.fxform.annotation.FormFactory;
import com.duanxr.pgcon.gui.fxform.annotation.ConfigLabel;
import com.duanxr.pgcon.gui.fxform.factory.ImageViewerFactory;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.image.Image;
import lombok.Data;

/**
 * @author 段然 2022/8/1
 */
@Data
public class DebugMainConfig {

  @FormFactory(ImageViewerFactory.class)
  @ConfigLabel("Selected Image")
  private ObjectProperty<Image> selectedImage = new SimpleObjectProperty<>();

  @FormFactory(ImageViewerFactory.class)
  @ConfigLabel("Converted Image")
  private ObjectProperty<Image> convertedImage = new SimpleObjectProperty<>();

  @FormFactory(ImageViewerFactory.class)
  @ConfigLabel("Live Image")
  private ObjectProperty<Image> liveImage = new SimpleObjectProperty<>();


}


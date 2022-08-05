package com.duanxr.pgcon.component.eg;

import com.dooapp.fxform.annotation.FormFactory;
import com.dooapp.fxform.view.factory.impl.PasswordFieldFactory;
import com.dooapp.fxform.view.factory.impl.TextAreaFactory;
import java.time.LocalDate;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.MapProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.scene.paint.Color;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * @author 段然 2022/7/29
 */
@Data
@Builder
@AllArgsConstructor
public class ConfigBeanExample {
  public enum Subject {
    CONTACT, QUESTION, BUG, FEEDBACK
  }

  private final StringProperty name = new SimpleStringProperty();

  private final ReadOnlyStringProperty welcome = new SimpleStringProperty();

  private final StringProperty email = new SimpleStringProperty();

  @FormFactory(PasswordFieldFactory.class)
  private final StringProperty password = new SimpleStringProperty();

  @FormFactory(PasswordFieldFactory.class)
  private final StringProperty repeatPassword = new SimpleStringProperty();

  private final BooleanProperty subscribe = new SimpleBooleanProperty();

  private final ReadOnlyBooleanProperty unsubscribe = new SimpleBooleanProperty();

  private final ObjectProperty<Subject> subject = new SimpleObjectProperty<>();

  private final IntegerProperty year = new SimpleIntegerProperty();

  @FormFactory(TextAreaFactory.class)
  private final StringProperty message = new SimpleStringProperty();

  private final ObjectProperty<LocalDate> date = new SimpleObjectProperty<LocalDate>();

  private final ObjectProperty<Color> color = new SimpleObjectProperty<Color>();

  private final ListProperty<TableBean> list = new SimpleListProperty<TableBean>(
      FXCollections.<TableBean>observableArrayList());

  private final MapProperty<String, String> map = new SimpleMapProperty(
      FXCollections.observableHashMap());
}

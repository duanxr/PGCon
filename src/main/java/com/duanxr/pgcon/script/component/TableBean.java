package com.duanxr.pgcon.script.component;

import com.duanxr.pgcon.script.component.ConfigBeanExample.Subject;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 段然 2022/7/29
 */
@Data
@Builder
@AllArgsConstructor
public class TableBean {

  private final StringProperty name = new SimpleStringProperty();

  private final IntegerProperty age = new SimpleIntegerProperty();

  private final ObjectProperty<Subject> subject = new SimpleObjectProperty<Subject>();
}
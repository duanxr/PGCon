package com.duanxr.pgcon.test.eg;

import com.duanxr.pgcon.test.eg.ConfigBeanExample.Subject;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

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
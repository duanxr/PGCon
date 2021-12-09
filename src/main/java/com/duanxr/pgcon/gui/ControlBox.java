package com.duanxr.pgcon.gui;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComboBox;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Duanran 2019/12/18
 */
@Slf4j
public class ControlBox<T> extends JComboBox<String> {

  private final List<T> selectionList;

  private final Function<T> selectionFunction;

  public ControlBox(Function<T> selectionFunction, String boxName) {
    this.selectionList = new ArrayList<>();
    this.selectionFunction = selectionFunction;
    this.addActionListener(this::selectionCallBack);
    this.setVisible(true);
    this.addItem(null, boxName);
  }

  //TODO ADD CONFIRM BUTTON
  private void selectionCallBack(ActionEvent actionEvent) {
    try {
      int selectedIndex = super.getSelectedIndex();
      selectionFunction.apply(selectionList.get(selectedIndex));
    } catch (Exception e) {
      log.error("selectionCallBack Exception.", e);
    }
  }

  public void addItem(T item, String name) {
    selectionList.add(item);
    super.addItem(name);
  }

  @FunctionalInterface
  public interface Function<T> {

    void apply(T t);
  }
}

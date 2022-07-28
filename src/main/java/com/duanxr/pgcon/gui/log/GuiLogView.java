package com.duanxr.pgcon.gui.log;

import java.text.SimpleDateFormat;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.css.PseudoClass;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Duration;

/**
 * @author 段然 2022/7/28
 */
public class GuiLogView extends ListView<GuiLogRecord> {
  private final static PseudoClass DEBUG = PseudoClass.getPseudoClass("debug");
  private final static PseudoClass ERROR = PseudoClass.getPseudoClass("error");
  private final static PseudoClass INFO = PseudoClass.getPseudoClass("info");
  private static final int MAX_ENTRIES = 10_000;
  private final static SimpleDateFormat TIMESTAMP_FORMATTER = new SimpleDateFormat("HH:mm:ss.SSS");
  private final static PseudoClass WARN = PseudoClass.getPseudoClass("warn");
  private final ObjectProperty<GuiLogLevel> filterLevel = new SimpleObjectProperty<>(null);
  private final ObservableList<GuiLogRecord> logItems = FXCollections.observableArrayList();
  private final BooleanProperty paused = new SimpleBooleanProperty(false);
  private final DoubleProperty refreshRate = new SimpleDoubleProperty(60);
  private final BooleanProperty showTimestamp = new SimpleBooleanProperty(false);
  private final BooleanProperty tail = new SimpleBooleanProperty(false);

  public GuiLogView(GuiLogger guiLogger) {
    getStyleClass().add("log-view");
    Timeline logTransfer = new Timeline(
        new KeyFrame(
            Duration.seconds(1),
            event -> {
              guiLogger.getLog().drainTo(logItems);

              if (logItems.size() > MAX_ENTRIES) {
                logItems.remove(0, logItems.size() - MAX_ENTRIES);
              }

              if (tail.get()) {
                scrollTo(logItems.size());
              }
            }
        )
    );
    logTransfer.setCycleCount(Timeline.INDEFINITE);
    logTransfer.rateProperty().bind(refreshRateProperty());

    this.pausedProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue && logTransfer.getStatus() == Animation.Status.RUNNING) {
        logTransfer.pause();
      }

      if (!newValue && logTransfer.getStatus() == Animation.Status.PAUSED
          && getParent() != null) {
        logTransfer.play();
      }
    });

    this.parentProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue == null) {
        logTransfer.pause();
      } else {
        if (!paused.get()) {
          logTransfer.play();
        }
      }
    });

    filterLevel.addListener((observable, oldValue, newValue) -> {
      setItems(
          new FilteredList<>(
              logItems,
              guiLogRecord ->
                  guiLogRecord.getLevel().ordinal() >=
                      filterLevel.get().ordinal()
          )
      );
    });
    filterLevel.set(GuiLogLevel.DEBUG);

    setCellFactory(param -> new ListCell<>() {
      {
        showTimestamp.addListener(observable -> updateItem(this.getItem(), this.isEmpty()));
      }

      @Override
      protected void updateItem(GuiLogRecord item, boolean empty) {
        super.updateItem(item, empty);

        pseudoClassStateChanged(DEBUG, false);
        pseudoClassStateChanged(INFO, false);
        pseudoClassStateChanged(WARN, false);
        pseudoClassStateChanged(ERROR, false);

        if (item == null || empty) {
          setText(null);
          return;
        }

        String context =
            (item.getContext() == null)
                ? ""
                : item.getContext() + " ";

        if (showTimestamp.get()) {
          String timestamp =
              (item.getTimestamp() == null)
                  ? ""
                  : TIMESTAMP_FORMATTER.format(item.getTimestamp()) + " ";
          setText(timestamp + context + item.getMessage());
        } else {
          setText(context + item.getMessage());
        }

        switch (item.getLevel()) {
          case DEBUG -> pseudoClassStateChanged(DEBUG, true);
          case INFO -> pseudoClassStateChanged(INFO, true);
          case WARN -> pseudoClassStateChanged(WARN, true);
          case ERROR -> pseudoClassStateChanged(ERROR, true);
        }
      }
    });
  }

  public DoubleProperty refreshRateProperty() {
    return refreshRate;
  }

  public BooleanProperty pausedProperty() {
    return paused;
  }

  public BooleanProperty showTimeStampProperty() {
    return showTimestamp;
  }

  public ObjectProperty<GuiLogLevel> filterLevelProperty() {
    return filterLevel;
  }

  public BooleanProperty tailProperty() {
    return tail;
  }
}


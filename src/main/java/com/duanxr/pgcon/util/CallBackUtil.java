package com.duanxr.pgcon.util;

import com.duanxr.pgcon.exception.AlertErrorException;
import com.duanxr.pgcon.log.GuiLogger;
import java.io.PrintWriter;
import java.io.StringWriter;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import lombok.Setter;

/**
 * @author 段然 2022/8/14
 */
public class CallBackUtil {

  @Setter
  private static GuiLogger guiLogger;

  public static void callbackWithExceptionAlert(Runnable runnable) {
    try {
      runnable.run();
    } catch (Exception e) {
      alertException(e);
    }
  }

  private static void alertException(Exception e) {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle("Error!");
    if (e instanceof AlertErrorException) {
      if (guiLogger != null) {
        guiLogger.error("Error", e);
      }
      alert.setHeaderText(e.getMessage());
    } else {
      if (guiLogger != null) {
        guiLogger.error("A unexpected error occurred!", e);
      }
      alert.setHeaderText("A unexpected error occurred!");
      alert.setContentText("The exception stacktrace was:");
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);
      e.printStackTrace(pw);
      String exceptionText = sw.toString();
      TextArea textArea = new TextArea(exceptionText);
      textArea.setEditable(false);
      textArea.setWrapText(true);
      textArea.setMaxWidth(Double.MAX_VALUE);
      textArea.setMaxHeight(Double.MAX_VALUE);
      GridPane.setVgrow(textArea, Priority.ALWAYS);
      GridPane.setHgrow(textArea, Priority.ALWAYS);
      GridPane expContent = new GridPane();
      expContent.setMaxWidth(Double.MAX_VALUE);
      expContent.add(textArea, 0, 1);
      alert.getDialogPane().setExpandableContent(expContent);
    }
    alert.showAndWait();
  }

  public static void callbackWithExceptionCatch(Runnable runnable) {
    try {
      runnable.run();
    } catch (Exception e) {
      logException(e);
    }
  }

  private static void logException(Exception e) {
    if (e instanceof AlertErrorException) {
      if (guiLogger != null) {
        guiLogger.error(e.getMessage());
      }
    } else {
      if (guiLogger != null) {
        guiLogger.error("GUI callback error", e);
      }
    }
  }

}

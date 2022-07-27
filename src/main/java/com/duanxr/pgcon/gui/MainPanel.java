package com.duanxr.pgcon.gui;


import com.duanxr.pgcon.PGConApplication;
import com.github.sarxos.webcam.Webcam;
import java.awt.image.BufferedImage;
import java.net.URL;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;


/**
 * @author 段然 2022/7/27
 */
@Slf4j
public class MainPanel extends Application {

  private ConfigurableApplicationContext context;

  @Override
  public void init() {
    ApplicationContextInitializer<GenericApplicationContext> initializer =
        applicationContext -> {
          applicationContext.registerBean(Parameters.class, this::getParameters);
          applicationContext.registerBean(HostServices.class, this::getHostServices);
        };
    this.context = new SpringApplicationBuilder().sources(PGConApplication.class)
        .initializers(initializer).run(getParameters().getRaw().toArray(new String[0]));
  }

  @Override
  public void stop() {
    context.close();
  }

  @Override
  @SneakyThrows
  public void start(Stage primaryStage) {
    primaryStage.setTitle("Connecting WebCam Using Sarxos API");
    FXMLLoader loader = new FXMLLoader();
    loader.setControllerFactory(context::getBean);
    URL resource = MainPanel.class.getResource("/fxml/Panel.fxml");
    loader.setLocation(resource);
    AnchorPane load = loader.load();
    Scene scene = new Scene(load);
    primaryStage.setScene(scene);
    primaryStage.show();
  }

}
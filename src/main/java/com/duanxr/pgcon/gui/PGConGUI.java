package com.duanxr.pgcon.gui;


import com.duanxr.pgcon.PGConApplication;
import com.duanxr.pgcon.config.ConstantConfig;
import java.net.URL;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
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
public class PGConGUI extends Application {

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
    System.exit(0);
  }

  @Override
  @SneakyThrows
  public void start(Stage primaryStage) {
    primaryStage.setTitle(ConstantConfig.MAIN_PANEL_TITLE);
    FXMLLoader loader = new FXMLLoader();
    loader.setControllerFactory(context::getBean);
    URL resource = PGConGUI.class.getResource("/javafx/MainPanel.fxml");
    loader.setLocation(resource);
    AnchorPane load = loader.load();
    Scene scene = new Scene(load);
    primaryStage.setScene(scene);
    primaryStage.setResizable(false);
    primaryStage.show();
  }

}
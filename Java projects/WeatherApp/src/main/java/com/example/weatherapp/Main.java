package com.example.weatherapp;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.Formatter;

public class Main extends Application {

    public static Stage mainStage = null;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("weather.fxml"));
        mainStage = stage;
        Scene scene = new Scene(fxmlLoader.load(), Color.web("#555555"));
        mainStage.initStyle(StageStyle.TRANSPARENT);
        mainStage.setResizable(false);
        mainStage.setScene(scene);

        Controller c = new Controller();
        mainStage.setOnCloseRequest(windowEvent -> {
            try {
                Formatter f = new Formatter("src/data.txt");
                f.format("%s\n%s", c.getCityName(), c.getUnits());
                f.close();
            } catch(Exception e) {
                e.printStackTrace();
            }
        });

        mainStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.stage.*;
import javafx.scene.image.*;

import java.util.Objects;

public class Main extends Application {

    public static String title = null;
    public static Stage stage = null;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("Main.fxml"))); //root node

        Scene scene = new Scene(root); // new Scene(root, 300, 475); - node, width, height

        Image icon = new Image("calc.png");
        primaryStage.getIcons().add(icon);

        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        stage = primaryStage;
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}

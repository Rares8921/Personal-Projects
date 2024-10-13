package sample.autoclicker;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.Objects;

public class Main extends Application {

    public static Stage stage = null;

    @Override
    public void start(Stage mainStage) throws IOException {
        Parent parent = FXMLLoader.load((Objects.requireNonNull(Main.class.getResource("hello-view.fxml"))));
        Scene scene = new Scene(parent, 165, 263);
        scene.setFill(Color.TRANSPARENT);
        Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("appicon.jpg")));
        mainStage.getIcons().add(icon);
        mainStage.setTitle("Autoclicker");
        mainStage.setScene(scene);
        mainStage.setResizable(false);
        mainStage.initStyle(StageStyle.TRANSPARENT);
        stage = mainStage;
        // Terminate the process
        stage.setOnCloseRequest(e -> System.exit(0));
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
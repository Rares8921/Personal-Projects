package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class MetronomeMenu extends Application {

    public static Stage metronomeStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/metronome-menu.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT); // For rounded corners
        primaryStage.initStyle(StageStyle.UNDECORATED); // To eliminate the title bar
        primaryStage.initStyle(StageStyle.TRANSPARENT); // For rounded corners
        primaryStage.setTitle("Virtual piano"); // Respecting the case when the user hovers the app in taskbar
        primaryStage.setScene(scene);
        primaryStage.setX(Main.stage.getX() + 860);
        primaryStage.setY(Main.stage.getY() + 200);
        primaryStage.initModality(Modality.APPLICATION_MODAL);
        metronomeStage = primaryStage;
        metronomeStage.show();
    }
}

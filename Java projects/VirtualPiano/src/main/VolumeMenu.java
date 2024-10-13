package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class VolumeMenu extends Application {

    public static Stage volumeStage = new Stage();

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/volume-menu.fxml"));
        VolumeController controller = loader.getController();
        Parent root = loader.load();
        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT); // For rounded corners
        stage.initStyle(StageStyle.UNDECORATED); // To eliminate the title bar
        stage.initStyle(StageStyle.TRANSPARENT); // For rounded corners
        stage.setTitle("Volume box"); // Respecting the case when the user hovers the app in taskbar
        stage.setScene(scene);
        stage.setX(Main.stage.getX() + 1000);
        stage.setY(Main.stage.getY() + 200);
        stage.initModality(Modality.APPLICATION_MODAL);
        volumeStage = stage;
        // If the application is closed from taskbar
        volumeStage.setOnCloseRequest(e -> {
            int sliderValue = controller.getSliderValue();
            if(sliderValue != -1) {
                try {
                    MetafileUpdater.setLineValue(1, "" + sliderValue);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        volumeStage.show();
    }
}

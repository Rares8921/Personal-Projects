package sample;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class ErrorBox implements Initializable {

    public static Stage stage = null;

    @FXML
    private AnchorPane box;

    public void closeBox() {
        stage.close();
    }

    public void dragBox() {
        box.setOnMousePressed((event) -> { // getting the position
            xOffSet = event.getSceneX();
            yOffSet = event.getSceneY();
        });
        box.setOnMouseDragged((event) -> { // reposition
            stage.setX(event.getScreenX() - xOffSet);
            stage.setY(event.getScreenY() - yOffSet);
        });
    }

    private static double xOffSet;
    private static double yOffSet;

    public void display() throws IOException {
        stage = new Stage();

        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("InputBox.fxml")));

        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setResizable(false);

        stage.setScene(new Scene(root));

        stage.showAndWait();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dragBox();
    }
}

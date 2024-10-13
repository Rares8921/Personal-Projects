package sample;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class BuyBox extends Application implements Initializable {

    @FXML
    public AnchorPane buyParent;

    @FXML
    public Label label;

    public static Stage buyStage;

    private double xOffSet;
    private double yOffSet;
    public static long coins;
    public static int required;
    public static boolean bought;
    private static boolean status;

    public void close() {
        buyStage.close();
    }

    private void setDraggable() {
        buyParent.setOnMousePressed((event) -> { // get current position on the screen
            xOffSet = event.getSceneX();
            yOffSet = event.getSceneY();
        });
        buyParent.setOnMouseDragged((event) -> { // change position
            buyStage.setX(event.getScreenX() - xOffSet);
            buyStage.setY(event.getScreenY() - yOffSet);
        });
    }

    private void setLabel() {
        if(bought) {
            status = false;
            label.setText("Already purchased!");
        } else if(coins < required) {
            status = false;
            label.setText("You need " + (required - coins) + " coins");
        } else {
            status = true;
        }
    }

    public boolean getStatus() {
        return status;
    }

    protected void display() throws Exception {
        start(buyStage);
    }

    @Override
    public void start(Stage stage) throws Exception {
        buyStage = new Stage();
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("resources/buymenu.fxml")));
        buyStage.initModality(Modality.APPLICATION_MODAL);
        buyStage.initStyle(StageStyle.UNDECORATED);
        buyStage.setResizable(false);
        buyStage.getIcons().add(new Image("sample/icon.png"));
        buyStage.setTitle("Status");
        buyStage.setScene(new Scene(root));
        buyStage.show();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setDraggable();
        setLabel();
    }

}

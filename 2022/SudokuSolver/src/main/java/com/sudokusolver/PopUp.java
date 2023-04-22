package com.sudokusolver;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class PopUp extends Application implements Initializable {

    public static Stage popUpStage;

    public void close() {
        popUpStage.close();
    }

    public void display() throws Exception {
        start(popUpStage);
    }

    @Override
    public void start(Stage stage) throws Exception {
        popUpStage = new Stage();
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("sudoku.fxml")));
        popUpStage.initModality(Modality.APPLICATION_MODAL);
        popUpStage.initStyle(StageStyle.UNDECORATED);
        popUpStage.setResizable(false);
        popUpStage.setScene(new Scene(root));
        popUpStage.showAndWait();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
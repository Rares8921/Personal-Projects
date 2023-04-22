package com.sudokusolver;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

public class Sudoku implements Initializable {
    @FXML
    private AnchorPane view;

    private double xOffSet;
    private double yOffSet;

    public void setDraggable() {
        view.setOnMousePressed((event) -> { // getting the position
            xOffSet = event.getSceneX();
            yOffSet = event.getSceneY();
        });
        view.setOnMouseDragged((event) -> { // reposition
            SudokuSolver.stage.setX(event.getScreenX() - xOffSet);
            SudokuSolver.stage.setY(event.getScreenY() - yOffSet);
        });
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setDraggable();
    }
}

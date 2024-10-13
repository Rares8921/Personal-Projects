package main;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;

import java.net.URL;
import java.util.ResourceBundle;

public class MetronomeController implements Initializable {

    @FXML
    private Slider bpmSlider;

    @FXML
    private Label bpmLabel, playLabel;

    @FXML
    private Button decrementBPM, incrementBPM;

    private static boolean metronomeActive;
    private static Metronome metronome;
    private static Thread thread;

    public void close() {
        MetronomeMenu.metronomeStage.close();
    }

    private void declareVariables() {
        metronome = new Metronome();
        thread = new Thread(metronome);
    }

    private void updateUI() {
        bpmLabel.setText(String.valueOf((int) Metronome.bpm));
        bpmSlider.setValue(Metronome.bpm);
        metronomeActive = metronome.isRunning();
        playLabel.setStyle(metronomeActive ? "-fx-text-fill: #FF9913" : "-fx-text-fill: white;");
    }

    private void addActions() {
        decrementBPM.setOnAction(e -> {
            bpmSlider.setValue(Math.max(bpmSlider.getValue() - 1, 40));
            Metronome.bpm = bpmSlider.getValue();
            bpmLabel.setText(String.valueOf((int) Metronome.bpm));
        });
        incrementBPM.setOnAction(e -> {
            bpmSlider.setValue(Math.min(bpmSlider.getValue() + 1, 220));
            Metronome.bpm = bpmSlider.getValue();
            bpmLabel.setText(String.valueOf((int) Metronome.bpm));
        });
        bpmSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            Metronome.bpm = newValue.intValue();
            bpmLabel.setText(String.valueOf((int) Metronome.bpm));
        });
        playLabel.setOnMouseClicked(e -> {
            if(metronomeActive) {
                metronome.end();
                playLabel.setStyle("-fx-text-fill: white;");
            } else {
                playLabel.setStyle("-fx-text-fill: #FF9913");
                declareVariables();
                thread.start();
            }
            metronomeActive = !metronomeActive;
        });
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        declareVariables();
        updateUI();
        addActions();
    }
}

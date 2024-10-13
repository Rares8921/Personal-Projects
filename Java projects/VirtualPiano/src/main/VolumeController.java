package main;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;

import java.net.URL;
import java.util.ResourceBundle;

public class VolumeController implements Initializable {

    @FXML public Label volumeLabel;

    @FXML
    private Slider volumeSlider;

    private static int sliderValue = -1;

    public void close() {
        if(sliderValue != -1) {
            Controller.setVolume(sliderValue / 100.0);
        }
        VolumeMenu.volumeStage.close();
    }

    public int getSliderValue() {
        return sliderValue;
    }

    private void addSliderEvent() {
        volumeSlider.setValue(Controller.getVolume() * 100);
        volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            volumeLabel.setText(String.valueOf(sliderValue = newValue.intValue()));
        });
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if(sliderValue != -1) {
            volumeLabel.setText(String.valueOf(sliderValue));
        }
        addSliderEvent();
    }
}

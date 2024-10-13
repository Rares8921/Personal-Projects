package main;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class KeyAssistController implements Initializable {

    @FXML
    private AnchorPane parent;

    @FXML
    private Button keyboardON, letterNotesON, musicalNotesON;

    @FXML
    private Button keyboardOFF, letterNotesOFF, musicalNotesOFF;

    @FXML
    private HBox keyboardHBox, letterNotesHBox, musiscalNotesHBox;

    private static boolean keyboardValue, letterValue, musicalValue;


    public void close() {
        Main.controller.setKeyboardNotesActive(keyboardValue);
        Main.controller.setLetterNotesActive(letterValue);
        Main.controller.setMusicalNotesActive(musicalValue);
        Main.controller.updateNotes();
        KeyAssistMenu.keyAssistStage.close();
    }

    private void updateUI() {
        if(keyboardValue) {
            keyboardHBox.setLayoutX(204);
            keyboardHBox.setPrefWidth(172);
        } else {
            keyboardHBox.setLayoutX(157);
            keyboardHBox.setPrefWidth(160);
        }
        if(letterValue) {
            letterNotesHBox.setLayoutX(204);
            letterNotesHBox.setPrefWidth(172);
        } else {
            letterNotesHBox.setLayoutX(157);
            letterNotesHBox.setPrefWidth(160);
        }
        if(musicalValue) {
            musiscalNotesHBox.setLayoutX(204);
            musiscalNotesHBox.setPrefWidth(172);
        } else {
            musiscalNotesHBox.setLayoutX(157);
            musiscalNotesHBox.setPrefWidth(160);
        }
    }

    private void addActions() {
        keyboardON.setOnAction(e -> {
            // Design
            Timeline timeline =  new Timeline();
            timeline.getKeyFrames().addAll(
                    new KeyFrame(Duration.ZERO, new KeyValue(keyboardHBox.layoutXProperty(), 202)),
                    new KeyFrame(Duration.millis(400.0), new KeyValue(keyboardHBox.layoutXProperty(), 157))
            );
            timeline.play();
            timeline.setOnFinished(evt -> {
                keyboardHBox.setLayoutX(157);
                keyboardHBox.setPrefWidth(160);
            });
            // Functionality
            keyboardValue = false;
        });
        keyboardOFF.setOnAction(e -> {
            // Design
            Timeline timeline =  new Timeline();
            timeline.getKeyFrames().addAll(
                    new KeyFrame(Duration.ZERO, new KeyValue(keyboardHBox.layoutXProperty(), 157)),
                    new KeyFrame(Duration.millis(400.0), new KeyValue(keyboardHBox.layoutXProperty(), 204)),
                    new KeyFrame(Duration.millis(400.0), new KeyValue(letterNotesHBox.layoutXProperty(), 157)),
                    new KeyFrame(Duration.millis(400.0), new KeyValue(musiscalNotesHBox.layoutXProperty(), 157))
            );
            timeline.play();
            timeline.setOnFinished(evt -> {
                keyboardHBox.setLayoutX(204);
                keyboardHBox.setPrefWidth(172);
                letterNotesHBox.setPrefWidth(160);
                musiscalNotesHBox.setPrefWidth(160);
            });
            // Functionality
            keyboardValue = true;
            letterValue = musicalValue = false;
        });

        letterNotesON.setOnAction(e -> {
            // Design
            Timeline timeline =  new Timeline();
            timeline.getKeyFrames().addAll(
                    new KeyFrame(Duration.ZERO, new KeyValue(letterNotesHBox.layoutXProperty(), 202)),
                    new KeyFrame(Duration.millis(400.0), new KeyValue(letterNotesHBox.layoutXProperty(), 157))
            );
            timeline.play();
            timeline.setOnFinished(evt -> {
                letterNotesHBox.setLayoutX(157);
                letterNotesHBox.setPrefWidth(160);
            });
            // Functionality
            letterValue = false;
        });
        letterNotesOFF.setOnAction(e -> {
            // Design
            Timeline timeline =  new Timeline();
            timeline.getKeyFrames().addAll(
                    new KeyFrame(Duration.ZERO, new KeyValue(letterNotesHBox.layoutXProperty(), 157)),
                    new KeyFrame(Duration.millis(400.0), new KeyValue(letterNotesHBox.layoutXProperty(), 204)),
                    new KeyFrame(Duration.millis(400.0), new KeyValue(keyboardHBox.layoutXProperty(), 157)),
                    new KeyFrame(Duration.millis(400.0), new KeyValue(musiscalNotesHBox.layoutXProperty(), 157))
            );
            timeline.play();
            timeline.setOnFinished(evt -> {
                letterNotesHBox.setLayoutX(204);
                letterNotesHBox.setPrefWidth(172);
                keyboardHBox.setPrefWidth(160);
                musiscalNotesHBox.setPrefWidth(160);
            });
            // Functionality
            letterValue = true;
            keyboardValue = musicalValue = false;
        });

        musicalNotesON.setOnAction(e -> {
            // Design
            Timeline timeline =  new Timeline();
            timeline.getKeyFrames().addAll(
                    new KeyFrame(Duration.ZERO, new KeyValue(musiscalNotesHBox.layoutXProperty(), 202)),
                    new KeyFrame(Duration.millis(400.0), new KeyValue(musiscalNotesHBox.layoutXProperty(), 157))
            );
            timeline.play();
            timeline.setOnFinished(evt -> {
                musiscalNotesHBox.setLayoutX(157);
                musiscalNotesHBox.setPrefWidth(160);
            });
            // Functionality
            musicalValue = false;
        });
        musicalNotesOFF.setOnAction(e -> {
            // Design
            Timeline timeline =  new Timeline();
            timeline.getKeyFrames().addAll(
                    new KeyFrame(Duration.ZERO, new KeyValue(musiscalNotesHBox.layoutXProperty(), 157)),
                    new KeyFrame(Duration.millis(400.0), new KeyValue(musiscalNotesHBox.layoutXProperty(), 204)),
                    new KeyFrame(Duration.millis(400.0), new KeyValue(keyboardHBox.layoutXProperty(), 157)),
                    new KeyFrame(Duration.millis(400.0), new KeyValue(letterNotesHBox.layoutXProperty(), 157))
            );
            timeline.play();
            timeline.setOnFinished(evt -> {
                musiscalNotesHBox.setLayoutX(204);
                musiscalNotesHBox.setPrefWidth(172);
                keyboardHBox.setPrefWidth(160);
                letterNotesHBox.setPrefWidth(160);
            });
            // Functionality
            musicalValue = true;
            keyboardValue = letterValue = false;
        });
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        updateUI();
        addActions();
    }
}

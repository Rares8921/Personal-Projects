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

public class InputBox implements Initializable {

    public static Stage stage = null;

    @FXML
    private TextField input;

    @FXML
    private AnchorPane box;

    @FXML
    private Label Title;

    @FXML
    private Button close;

    @FXML
    private Button inputButton;

    private static double number;

    public static double getNumber() {
        return number;
    }

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

    public void inputBox() {
        input.setOnKeyPressed( event -> {
            if(Main.title.equals("Power Input")) {
                String inputText = input.getText();
                if(event.getCode() == KeyCode.MINUS) {
                    if(inputText.contains("-")) {
                        input.setText(inputText.substring(1));
                    } else {
                        input.setText("-" + inputText);
                    }
                } else if(event.getCode() == KeyCode.COMMA) {
                    int caret = input.getCaretPosition();
                    if(inputText.contains(",")) {
                        input.setText(inputText.replace(",",""));
                        input.positionCaret(caret - 1);
                    } else {
                        input.setText(inputText.substring(0, caret) + "," + inputText.substring(caret));
                        input.positionCaret(caret + 1);
                    }
                }
            }
             if( event.getCode() == KeyCode.ENTER && input.getText().length() > 0) {
                if(input.getText().length() == 1 && input.getText().equals(",")) {
                    number = 0;
                } else {
                    number = Double.parseDouble(input.getText().replace(",", "."));
                    stage.close();
                }
            }
        } );
        input.textProperty().addListener((observable, oldValue, newValue) -> {
            if(Main.title.equals("Power Input")) {
                if (!newValue.matches("^[-0-9,]+$")) {
                    input.setText(newValue.replaceAll("[^\\d]", ""));
                }
            } else {
                if (!newValue.matches("^[0-9]+$")) {
                    input.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
    }

    public void inputButton() {
        if(input.getText().length() > 0) {
            if(input.getText().length() == 1 && input.getText().equals(",")) {
                number = 0;
            } else {
                number = Double.parseDouble(input.getText().replace(",", "."));
                stage.close();
            }
        }
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

        Tooltip input1 = new Tooltip("Submit");
        input1.setStyle("-fx-background-color: #464646; -fx-font-size: 14px");
        inputButton.setTooltip(input1);

        Tooltip closeTip = new Tooltip("Close");
        closeTip.setStyle("-fx-background-color: #464646; -fx-font-size: 14px");
        close.setTooltip(closeTip);

        EventHandler<KeyEvent> handler = new EventHandler<KeyEvent>() {

            private boolean willConsume = false;

            @Override
            public void handle(KeyEvent event) {

                if (willConsume) {
                    event.consume();
                }

                if (event.getCode() == KeyCode.MINUS || event.getCode() == KeyCode.COMMA) {
                    if (event.getEventType() == KeyEvent.KEY_PRESSED) {
                        willConsume = true;
                    } else if (event.getEventType() == KeyEvent.KEY_RELEASED) {
                        willConsume = false;
                    }
                }
            }

        };

        input.addEventFilter(KeyEvent.ANY, handler);

        Title.setText(Main.title);
        inputBox();
        dragBox();
    }
}

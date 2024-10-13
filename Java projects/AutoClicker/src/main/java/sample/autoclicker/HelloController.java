package sample.autoclicker;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class HelloController implements Initializable {

    @FXML
    private Button actionButton;

    @FXML
    private AnchorPane parent;

    @FXML
    private Slider slider;

    @FXML
    private Label cpsLabel;

    @FXML
    Label onOffLabel;

    public static Label staticOnOffLabel;

    private double xOffSet = 0, yOffSet = 0;

    public void close() {
        // To close the frame firstly
        Main.stage.close();
        // To terminate the process
        System.exit(0);
    }

    private void setDraggable() {
        parent.setOnMousePressed((event) -> { // getting the position
            xOffSet = event.getSceneX();
            yOffSet = event.getSceneY();
        });
        parent.setOnMouseDragged((event) -> { // reposition
            Main.stage.setX(event.getScreenX() - xOffSet);
            Main.stage.setY(event.getScreenY() - yOffSet);
        });
    }

    private void addAction() {
        actionButton.setOnAction(actionEvent -> {
            AutoClicker.toggle();
            if(AutoClicker.toggled) {
                onOffLabel.setText("- ON");
            } else {
                onOffLabel.setText("- OFF");
            }
        });
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            cpsLabel.setText(String.valueOf(newValue.intValue()));
            AutoClicker.CPS = newValue.intValue();
        });
        staticOnOffLabel = onOffLabel;
        setDraggable();
        addAction();
        ImageView imageView = new ImageView(new Image(Objects.requireNonNull(HelloController.class.getResourceAsStream("icon.png"))));
        imageView.setFitHeight(70);
        imageView.setFitWidth(70);
        imageView.setPreserveRatio(true);
        actionButton.setGraphic(imageView);
    }
}
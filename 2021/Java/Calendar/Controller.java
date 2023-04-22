package sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.time.YearMonth;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    private AnchorPane parent;

    @FXML
    private GridPane cal;

    @FXML
    private Tooltip tooltip;

    private double xOffSet;
    private double yOffSet;

    public void close() {
        Main.stage.close();
    }

    public void minimize() {
        Main.stage.setIconified(true);
    }

    public void setDraggable() {
        parent.setOnMousePressed((event) -> { // getting the position
            xOffSet = event.getSceneX();
            yOffSet = event.getSceneY();
        });
        parent.setOnMouseDragged((event) -> { // reposition
            Main.stage.setX(event.getScreenX() - xOffSet);
            Main.stage.setY(event.getScreenY() - yOffSet);
        });
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tooltip.setPrefWidth(150);
        tooltip.setWrapText(true);
        cal.getChildren().add(new CalendarView(YearMonth.now()).getView());
        setDraggable();
    }
}

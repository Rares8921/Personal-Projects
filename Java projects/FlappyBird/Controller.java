package sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Scanner;

public class Controller implements Initializable {

    @FXML
    public AnchorPane parent;

    @FXML
    public Label highscore;

    private double xOffSet;
    private double yOffSet;

    public void close() {
        Main.stage.close();
    }

    public void minimize() {
        Main.stage.setIconified(true);
    }

    public void soundMenu() throws Exception {
        SoundBox sound = new SoundBox();
        sound.display();
    }

    public void shopMenu() throws Exception {
        ShopBox shop = new ShopBox();
        shop.display();
    }

    public void inventoryMenu() throws Exception {
        InventoryBox inventory = new InventoryBox();
        inventory.display();
    }

    private void setDraggable() {
        parent.setOnMousePressed((event) -> { // get current position on the screen
            xOffSet = event.getSceneX();
            yOffSet = event.getSceneY();
        });
        parent.setOnMouseDragged((event) -> { // change position
            Main.stage.setX(event.getScreenX() - xOffSet);
            Main.stage.setY(event.getScreenY() - yOffSet);
        });
    }

    public void setHighscore() {
        try {
            File file = new File("src/sample/highscore.txt");
            Scanner scan = new Scanner(file);
            String score = scan.next();
            scan.close();
            if(highscore != null) {
                highscore.setText(score);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setDraggable();
        setHighscore();
    }
}

package sample;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.net.URL;
import java.util.Formatter;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Scanner;

public class InventoryBox extends Application implements Initializable {

    @FXML
    public AnchorPane inventoryParent;

    @FXML
    public Label coinsLabel, bird1, bird2, bird3, bird4, pipe1, pipe2, pipe3, pipe4, back1, back2, back3, back4;

    @FXML
    public ImageView bi1, bi2, bi3, bi4, pi1, pi2, pi3, pi4, bai1, bai2, bai3, bai4;

    public static Stage inventoryStage;
    public static long coins;

    private double xOffSet;
    private double yOffSet;

    private void updateData() {
        try {
            Formatter f = new Formatter("src/sample/inventory.txt");
            f.format("%s\n%s\n%s", birds, pipes, back);
            f.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void close() {
        updateData();
        inventoryStage.close();
    }
    private static String birds, pipes, back;

    private void setDraggable() {
        inventoryParent.setOnMousePressed((event) -> { // get current position on the screen
            xOffSet = event.getSceneX();
            yOffSet = event.getSceneY();
        });
        inventoryParent.setOnMouseDragged((event) -> { // change position
            inventoryStage.setX(event.getScreenX() - xOffSet);
            inventoryStage.setY(event.getScreenY() - yOffSet);
        });
    }

    private void setCoins() {
        File file = new File("src/sample/coins.txt");
        try {
            Scanner scan = new Scanner(file);
            coins = scan.nextLong();
            scan.close();
            coins = Math.min(coins, 999999999);
            if(coinsLabel != null) {
                coinsLabel.setText(coins + " $");
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void loadVariables() {
        try {
            File file = new File("src/sample/inventory.txt");
            Scanner scan = new Scanner(file);
            birds = scan.next();
            pipes = scan.next();
            back = scan.next();
            scan.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void loadBirdsLabels() {
        if(birds.charAt(0) == '0') {
            bird1.setTextFill(Color.BLACK);
        } else if(birds.charAt(0) == '2') {
            bird1.setUnderline(true);
        }
        if(birds.charAt(1) == '0') {
            bird2.setTextFill(Color.BLACK);
        } else if(birds.charAt(1) == '2') {
            bird2.setUnderline(true);
        }
        if(birds.charAt(2) == '0') {
            bird3.setTextFill(Color.BLACK);
        } else if(birds.charAt(2) == '2') {
            bird3.setUnderline(true);
        }
        if(birds.charAt(3) == '0') {
            bird4.setTextFill(Color.BLACK);
        } else if(birds.charAt(3) == '2') {
            bird4.setUnderline(true);
        }
    }

    private void loadPipesLabels() {
        if(pipes.charAt(0) == '0') {
            pipe1.setTextFill(Color.BLACK);
        } else if(pipes.charAt(0) == '2') {
            pipe1.setUnderline(true);
        }
        if(pipes.charAt(1) == '0') {
            pipe2.setTextFill(Color.BLACK);
        } else if(pipes.charAt(1) == '2') {
            pipe2.setUnderline(true);
        }
        if(pipes.charAt(2) == '0') {
            pipe3.setTextFill(Color.BLACK);
        } else if(pipes.charAt(2) == '2') {
            pipe3.setUnderline(true);
        }
        if(pipes.charAt(3) == '0') {
            pipe4.setTextFill(Color.BLACK);
        } else if(pipes.charAt(3) == '2') {
            pipe4.setUnderline(true);
        }
    }

    private void loadBackLabels() {
        if(back.charAt(0) == '0') {
            back1.setTextFill(Color.BLACK);
        } else if(back.charAt(0) == '2') {
            back1.setUnderline(true);
        }
        if(back.charAt(1) == '0') {
            back2.setTextFill(Color.BLACK);
        } else if(back.charAt(1) == '2') {
            back2.setUnderline(true);
        }
        if(back.charAt(2) == '0') {
            back3.setTextFill(Color.BLACK);
        } else if(back.charAt(2) == '2') {
            back3.setUnderline(true);
        }
        if(back.charAt(3) == '0') {
            back4.setTextFill(Color.BLACK);
        } else if(back.charAt(3) == '2') {
            back4.setUnderline(true);
        }
    }

    private void loadBirdsImages() {
        if(birds.charAt(1) != '0') {
            bi2.setImage(new Image("sample/yellowBird1.png"));
            bi2.setFitWidth(73); bi2.setFitHeight(60);
            bi2.setLayoutX(183); bi2.setLayoutY(74);
        }

        if(birds.charAt(2) != '0') {
            bi3.setImage(new Image("sample/blueBird1.png"));
            bi3.setFitWidth(68); bi3.setFitHeight(60);
            bi3.setLayoutX(311); bi3.setLayoutY(77);
        }

        if(birds.charAt(3) != '0') {
            bi4.setImage(new Image("sample/redBird1.png"));
            bi4.setFitWidth(73); bi4.setFitHeight(60);
            bi4.setLayoutX(457); bi4.setLayoutY(74);
        }
    }

    private void loadPipesImages() {
        if(pipes.charAt(1) != '0') {
            pi2.setImage(new Image("sample/down_pipe1.png"));
            pi2.setFitWidth(30); pi2.setFitHeight(75);
            pi2.setLayoutX(204); pi2.setLayoutY(197);
        }

        if(pipes.charAt(2) != '0') {
            pi3.setImage(new Image("sample/down_pipe2.png"));
            pi3.setFitWidth(30); pi3.setFitHeight(75);
            pi3.setLayoutX(341); pi3.setLayoutY(197);
        }

        if(pipes.charAt(3) != '0') {
            pi4.setImage(new Image("sample/down_pipe3.png"));
            pi4.setFitWidth(30); pi4.setFitHeight(75);
            pi4.setLayoutX(476); pi4.setLayoutY(197);
        }
    }

    private void loadBackImages() {
        if(back.charAt(1) != '0') {
            bai2.setImage(new Image("sample/back1.png"));
            bai2.setFitWidth(49); bai2.setFitHeight(83);
            bai2.setLayoutX(193); bai2.setLayoutY(328);
        }

        if(back.charAt(2) != '0') {
            bai3.setImage(new Image("sample/back2.png"));
            bai3.setFitWidth(49); bai3.setFitHeight(83);
            bai3.setLayoutX(329); bai3.setLayoutY(330);
        }

        if(back.charAt(3) != '0') {
            bai4.setImage(new Image("sample/back3.png"));
            bai4.setFitWidth(49); bai4.setFitHeight(83);
            bai4.setLayoutX(467); bai4.setLayoutY(328);
        }
    }

    public void commands() {
        inventoryParent.setOnMouseClicked(mouseEvent -> {
            /// 0x000000ff - black
            /// 0xffffffff - white
            if(mouseEvent.getTarget() == bi1) {
                if(!bird1.getTextFill().toString().equals("0x000000ff") && !bird1.isUnderline()) {
                    birds = birds.replace("2", "1");
                    StringBuilder s = new StringBuilder(birds);
                    s.setCharAt(0, '2');
                    bird1.setUnderline(true);
                    bird2.setUnderline(false); bird3.setUnderline(false); bird4.setUnderline(false);
                    birds = s.toString();
                }
            } else if(mouseEvent.getTarget() == bi2) {
                if(!bird2.getTextFill().toString().equals("0x000000ff") && !bird2.isUnderline()) {
                    birds = birds.replace("2", "1");
                    StringBuilder s = new StringBuilder(birds);
                    s.setCharAt(1, '2');
                    bird2.setUnderline(true);
                    bird1.setUnderline(false); bird3.setUnderline(false); bird4.setUnderline(false);
                    birds = s.toString();
                }
             } else if(mouseEvent.getTarget() == bi3) {
                if(!bird3.getTextFill().toString().equals("0x000000ff") && !bird3.isUnderline()) {
                    birds = birds.replace("2", "1");
                    StringBuilder s = new StringBuilder(birds);
                    s.setCharAt(2, '2');
                    bird3.setUnderline(true);
                    bird1.setUnderline(false); bird2.setUnderline(false); bird4.setUnderline(false);
                    birds = s.toString();
                }
            } else if(mouseEvent.getTarget() == bi4) {
                if(!bird4.getTextFill().toString().equals("0x000000ff") && !bird4.isUnderline()) {
                    birds = birds.replace("2", "1");
                    StringBuilder s = new StringBuilder(birds);
                    s.setCharAt(3, '2');
                    bird4.setUnderline(true);
                    bird1.setUnderline(false); bird2.setUnderline(false); bird3.setUnderline(false);
                    birds = s.toString();
                }
            } else if(mouseEvent.getTarget() == pi1) {
                if(!pipe1.getTextFill().toString().equals("0x000000ff") && !pipe1.isUnderline()) {
                    pipes = pipes.replace("2", "1");
                    StringBuilder s = new StringBuilder(pipes);
                    s.setCharAt(0, '2');
                    pipe1.setUnderline(true);
                    pipe2.setUnderline(false); pipe3.setUnderline(false); pipe4.setUnderline(false);
                    pipes = s.toString();
                }
            } else if(mouseEvent.getTarget() == pi2) {
                if(!pipe2.getTextFill().toString().equals("0x000000ff") && !pipe2.isUnderline()) {
                    pipes = pipes.replace("2", "1");
                    StringBuilder s = new StringBuilder(pipes);
                    s.setCharAt(1, '2');
                    pipe2.setUnderline(true);
                    pipe1.setUnderline(false); pipe3.setUnderline(false); pipe4.setUnderline(false);
                    pipes = s.toString();
                }
            } else if(mouseEvent.getTarget() == pi3) {
                if(!pipe3.getTextFill().toString().equals("0x000000ff") && !pipe3.isUnderline()) {
                    pipes = pipes.replace("2", "1");
                    StringBuilder s = new StringBuilder(pipes);
                    s.setCharAt(2, '2');
                    pipe3.setUnderline(true);
                    pipe1.setUnderline(false); pipe2.setUnderline(false); pipe4.setUnderline(false);
                    pipes = s.toString();
                }
            } else if(mouseEvent.getTarget() == pi4) {
                if(!pipe4.getTextFill().toString().equals("0x000000ff") && !pipe4.isUnderline()) {
                    pipes = pipes.replace("2", "1");
                    StringBuilder s = new StringBuilder(pipes);
                    s.setCharAt(3, '2');
                    pipe4.setUnderline(true);
                    pipe1.setUnderline(false); pipe2.setUnderline(false); pipe3.setUnderline(false);
                    pipes = s.toString();
                }
            } else if(mouseEvent.getTarget() == bai1) {
                if(!back1.getTextFill().toString().equals("0x000000ff") && !back1.isUnderline()) {
                    back = back.replace("2", "1");
                    StringBuilder s = new StringBuilder(back);
                    s.setCharAt(0, '2');
                    back1.setUnderline(true);
                    back2.setUnderline(false); back3.setUnderline(false); back4.setUnderline(false);
                    back = s.toString();
                }
            } else if(mouseEvent.getTarget() == bai2) {
                if(!back2.getTextFill().toString().equals("0x000000ff") && !back2.isUnderline()) {
                    back = back.replace("2", "1");
                    StringBuilder s = new StringBuilder(back);
                    s.setCharAt(1, '2');
                    back2.setUnderline(true);
                    back1.setUnderline(false); back3.setUnderline(false); back4.setUnderline(false);
                    back = s.toString();
                }
            } else if(mouseEvent.getTarget() == bai3) {
                if(!back3.getTextFill().toString().equals("0x000000ff") && !back3.isUnderline()) {
                    back = back.replace("2", "1");
                    StringBuilder s = new StringBuilder(back);
                    s.setCharAt(2, '2');
                    back3.setUnderline(true);
                    back1.setUnderline(false); back2.setUnderline(false); back4.setUnderline(false);
                    back = s.toString();
                }
            } else if(mouseEvent.getTarget() == bai4) {
                if(!back4.getTextFill().toString().equals("0x000000ff") && !back4.isUnderline()) {
                    back = back.replace("2", "1");
                    StringBuilder s = new StringBuilder(back);
                    s.setCharAt(3, '2');
                    back4.setUnderline(true);
                    back1.setUnderline(false); back2.setUnderline(false); back3.setUnderline(false);
                    back = s.toString();
                }
            }
        });
    }

    private void loadContent() {
        loadVariables();
        loadBirdsLabels();
        loadBirdsImages();
        loadPipesLabels();
        loadPipesImages();
        loadBackLabels();
        loadBackImages();
    }

    protected void display() throws Exception {
        start(inventoryStage);
    }

    @Override
    public void start(Stage stage) throws Exception {
        inventoryStage = new Stage();
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("resources/inventorymenu.fxml")));
        inventoryStage.initModality(Modality.APPLICATION_MODAL);
        inventoryStage.initStyle(StageStyle.UNDECORATED);
        inventoryStage.setResizable(false);
        inventoryStage.getIcons().add(new Image("sample/icon.png"));
        inventoryStage.setTitle("Inventory Menu");
        inventoryStage.setScene(new Scene(root));
        inventoryStage.showAndWait();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setDraggable();
        setCoins();
        loadContent();
        commands();
    }
}

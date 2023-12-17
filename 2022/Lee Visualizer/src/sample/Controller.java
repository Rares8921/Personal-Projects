package sample;

import javafx.animation.Animation;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    private AnchorPane parent;

    @FXML
    private RadioButton obstacleButton, laserButton, tpButton, laserTpButton, horizontalButton, verticalButton, diagonalButton, knightButton;

    @FXML
    Button actionButton, startButton, helpMenuButton;

    @FXML
    public Label start, end;

    public static Label staticStart, staticEnd;

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

    private void buttonRelations() {
        // maze buttons
        obstacleButton.setOnMouseClicked(e -> {
            Main.start = Main.end = Main.tp = Main.lasers = Main.laser_tp = false;
            Main.obstacles = obstacleButton.isSelected();
            obstacleButton.setUnderline(obstacleButton.isSelected());
            start.setUnderline(false); end.setUnderline(false); tpButton.setUnderline(false); laserButton.setUnderline(false); laserTpButton.setUnderline(false);
        });
        tpButton.setOnMouseClicked(e -> {
            Main.start = Main.end = Main.obstacles = Main.lasers = Main.laser_tp = false;
            Main.tp = tpButton.isSelected();
            laserTpButton.setSelected(false);
            tpButton.setUnderline(tpButton.isSelected());
            start.setUnderline(false); end.setUnderline(false); obstacleButton.setUnderline(false); laserTpButton.setUnderline(false); laserButton.setUnderline(false);
        });
        laserButton.setOnMouseClicked(e -> {
            Main.start = Main.end = Main.obstacles = Main.tp = Main.laser_tp = false;
            Main.lasers = laserButton.isSelected();
            laserTpButton.setSelected(false);
            laserButton.setUnderline(laserButton.isSelected());
            start.setUnderline(false); end.setUnderline(false); obstacleButton.setUnderline(false); tpButton.setUnderline(false); laserTpButton.setUnderline(false);
        });
        laserTpButton.setOnMouseClicked(e -> {
            Main.start = Main.end = Main.obstacles = Main.tp = Main.lasers = false;
            Main.laser_tp = laserTpButton.isSelected();
            laserButton.setSelected(false);
            tpButton.setSelected(false);
            laserTpButton.setUnderline(laserTpButton.isSelected());
            start.setUnderline(false); end.setUnderline(false); obstacleButton.setUnderline(false); tpButton.setUnderline(false); laserButton.setUnderline(false);
        });
        start.setOnMouseClicked(e -> {
            Main.end = Main.obstacles = Main.tp = Main.lasers = Main.laser_tp = false;
            Main.start = true;
            obstacleButton.setSelected(false);
            laserButton.setSelected(false);
            tpButton.setSelected(false);
            start.setUnderline(true);
            end.setUnderline(false); obstacleButton.setUnderline(false); tpButton.setUnderline(false); laserButton.setUnderline(false);
        });
        end.setOnMouseClicked(e -> {
            Main.start = Main.obstacles = Main.tp = Main.lasers = Main.laser_tp = false;
            Main.end = true;
            obstacleButton.setSelected(false);
            laserButton.setSelected(false);
            tpButton.setSelected(false);
            end.setUnderline(true);
            start.setUnderline(false); obstacleButton.setUnderline(false); tpButton.setUnderline(false); laserButton.setUnderline(false);
        });
        // movement buttons
        horizontalButton.setOnMouseClicked(e -> {
            knightButton.setSelected(false);
            Main.horizontal = horizontalButton.isSelected();
        });
        verticalButton.setOnMouseClicked(e -> {
            knightButton.setSelected(false);
            Main.vertical = verticalButton.isSelected();
        });
        diagonalButton.setOnMouseClicked(e -> {
            knightButton.setSelected(false);
            Main.diagonal = diagonalButton.isSelected();
        });
        knightButton.setOnMouseClicked(e -> {
            horizontalButton.setSelected(false);
            verticalButton.setSelected(false);
            diagonalButton.setSelected(false);
            Main.knight = knightButton.isSelected();
        });
    }

    private void startLee() {
        Main m = new Main();
        m.lee();
    }

    private void addListeners() {

        actionButton.setOnMouseClicked(e -> {
            if(Main.path != null && Main.path.getStatus() == Animation.Status.RUNNING) { // To not let the user clear the board while the animation runs
                return;
            }
            // de completat
            Main.iStart = Main.jStart = Main.iFinish = Main.jFinish = -1;
            Main.obstacles = Main.lasers = Main.tp = Main.laser_tp = Main.horizontal = Main.vertical = Main.diagonal = Main.knight = false;
            Main.cntLaserTp = Main.cntTp = 0;
            Main.currentTpI = Main.currentTpJ = Main.lastTpI = Main.lastTpJ = -1;
            for(int i = 0; i <= 20; i++) {
                for(int j = 0; j <= 20; j++) {
                    Main.matr[i][j] = '_';
                    Main.s[i][j] = new int[]{-1, -1};
                    Main.matrLee[i][j] = 0;
                }
            }
            obstacleButton.setSelected(false); laserButton.setSelected(false); tpButton.setSelected(false); laserTpButton.setSelected(false);
            horizontalButton.setSelected(false); verticalButton.setSelected(false); diagonalButton.setSelected(false); knightButton.setSelected(false);
            start.setText("-1,-1");
            end.setText("-1,-1");
            startButton.setDisable(false);
            Main.background(Main.canvas.getGraphicsContext2D());
        });
        startButton.setOnMouseClicked(e -> {
            // To not let the user start another lee while the current one is running or if the app was not cleared(the last cell on the canvas was modified).
            if((Main.path != null && Main.path.getStatus() == Animation.Status.RUNNING) || Main.canvas.getGraphicsContext2D().getStroke().toString().equals("0x7fffd4ff")) {
                return;
            }
            try {
                startLee();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        helpMenuButton.setOnMouseClicked(e -> {
            HelpBox box = new HelpBox();
            try {
                box.display();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        staticStart = start; staticEnd = end;
        buttonRelations();
        addListeners();
        setDraggable();
    }
}

package sample;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

public class Floor {

    protected Pane root;
    protected Image groundImg = new Image("/Land.png");
    protected ImageView groundOne = new ImageView(this.groundImg);
    protected ImageView groundTwo = new ImageView(this.groundImg);
    public static Timeline floorTimeLine;

    public Floor(Pane pane) {
        this.root = pane;
        this.init();
    }

    protected void init() {
        groundOne.setLayoutY(320);
        groundOne.setLayoutX(0);
        groundTwo.setLayoutY(320);
        groundTwo.setLayoutX(1200);
        this.animation();
        root.getChildren().add(groundOne);
        root.getChildren().add(groundTwo);
    }

    /**
     * Animation process
     */
    public void animation() {
        floorTimeLine = new Timeline(
                new KeyFrame(Duration.seconds(9), new KeyValue(groundOne.translateXProperty(), -1200)),
                new KeyFrame(Duration.seconds(9), new KeyValue(groundTwo.translateXProperty(), -1200))
        );
        floorTimeLine.setCycleCount(Timeline.INDEFINITE);
        floorTimeLine.play();
    }

}
package sample;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

public class Cloud {

    protected Pane root;
    protected Image cloudMiniImg = new Image("/Cloud1.png");
    protected Image cloudBigImg = new Image("/Cloud2.png");
    protected ImageView cloudImageViewOne = new ImageView(this.cloudMiniImg);
    protected ImageView cloudImageViewTwo = new ImageView(this.cloudMiniImg);
    protected ImageView cloudImageViewTree = new ImageView(this.cloudBigImg);
    protected ImageView cloudImageViewFour = new ImageView(this.cloudMiniImg);
    protected ImageView cloudImageViewFive = new ImageView(this.cloudBigImg);
    protected ImageView cloudImageViewSix = new ImageView(this.cloudMiniImg);
    public static Timeline cloudTimeLine;

    public Cloud(Pane pane) {
        this.root = pane;
        this.init();
    }

    protected void init() {
        this.setImageLayoutXY();
        this.setAnimation();
        this.setImage();
    }

    /**
     * Images layout
     */
    protected void setImageLayoutXY() {
        cloudImageViewOne.setLayoutX(700);
        cloudImageViewOne.setLayoutY(75);

        cloudImageViewTwo.setLayoutX(1400);
        cloudImageViewTwo.setLayoutY(60);

        cloudImageViewTree.setLayoutX(1000);
        cloudImageViewTree.setLayoutY(60);

        cloudImageViewFour.setLayoutX(2200);
        cloudImageViewFour.setLayoutY(75);

        cloudImageViewFive.setLayoutX(2500);
        cloudImageViewFive.setLayoutY(60);

        cloudImageViewSix.setLayoutX(3000);
        cloudImageViewSix.setLayoutY(60);
    }

    /**
     * Set images in root
     */
    protected void setImage() {
        root.getChildren().add(cloudImageViewOne);
        root.getChildren().add(cloudImageViewTwo);
        root.getChildren().add(cloudImageViewTree);
        root.getChildren().add(cloudImageViewFour);
        root.getChildren().add(cloudImageViewFive);
        root.getChildren().add(cloudImageViewSix);
    }


    /**
     * Animation process
     */
    public void setAnimation() {
        cloudTimeLine= new Timeline(
                new KeyFrame(Duration.seconds(10), new KeyValue(cloudImageViewOne.translateXProperty(), -1500)),
                new KeyFrame(Duration.seconds(10), new KeyValue(cloudImageViewTwo.translateXProperty(), -1500)),
                new KeyFrame(Duration.seconds(10), new KeyValue(cloudImageViewTree.translateXProperty(), -1500)),
                new KeyFrame(Duration.seconds(10), new KeyValue(cloudImageViewFour.translateXProperty(), -1500)),
                new KeyFrame(Duration.seconds(10), new KeyValue(cloudImageViewFive.translateXProperty(), -1500)),
                new KeyFrame(Duration.seconds(10), new KeyValue(cloudImageViewSix.translateXProperty(), -1500))
        );
        cloudTimeLine.setCycleCount(Timeline.INDEFINITE);
        cloudTimeLine.play();
    }

}

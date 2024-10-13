package sample;

import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Random;

public class Enemy {
    protected Pane root;
    protected ImageView enemy;
    protected Image cactusOne = new Image("/Cactus1.png");
    protected Image cactusTwo = new Image("/Cactus3.png");
    protected Image cactusThree = new Image("/Cactus4.png");
    protected Image birdOne = new Image("/bird1.png");
    protected Image birdTwo = new Image("/bird2.png");
    public static ArrayList<Image> listImage = new ArrayList<>();
    protected ArrayList<ImageView> listImageView = new ArrayList<>();
    public static ArrayList<Timeline> birdTimelines = new ArrayList<>();
    public static AnimationTimer animationTimer;

    Enemy(Pane pane) {
        this.root = pane;
        this.setListImage();
        this.setPositionImg();
        this.animation();
        animationTimer.start();
    }

    /**
     * Set ListImage
     */
    protected void setListImage() {
        if(listImage.isEmpty()) {
            listImage.add(cactusOne);
            listImage.add(cactusTwo);
            listImage.add(cactusThree);
            listImage.add(birdOne);
            listImage.add(birdOne);
        }
    }

    /**
     * Set PositionImg
     */
    protected void setPositionImg() {
        for(Image image : listImage) {
            enemy = new ImageView();
            enemy.setImage(image);
            if(image.getHeight() == 68) { // if it is a bird
                enemy.setFitWidth(45.0);
                enemy.setFitHeight(35.0);
                int heightIndex = new Random().nextInt(3);
                enemy.setLayoutY(heightIndex == 0 ? 215 : (heightIndex == 1 ? 271 : 290)); // 215 - high, 271 - mid, 290 - low
                birdTimelines.add(addAnimation(enemy));
            } else {
                enemy.setLayoutY(285);
            }
            enemy.setLayoutX(random());
            listImageView.add(enemy);
        }
        root.getChildren().addAll(listImageView);
    }

    public static void switchState(int state) {
        for(Timeline t : birdTimelines) {
            if(state == 1) {
                t.stop();
            } else {
                t.play();
            }
        }
    }

    private Timeline addAnimation(ImageView enemy) {
        Timeline birdTimeLine = new Timeline(
                new KeyFrame(Duration.millis(300), t1 -> enemy.setImage(birdOne)),
                new KeyFrame(Duration.millis(500), t2 -> enemy.setImage(birdTwo))
        );
        birdTimeLine.setCycleCount(Timeline.INDEFINITE);
        birdTimeLine.play();
        return birdTimeLine;
    }

    /**
     * Random enemy
     */
    private double random() {
        double result = 300 + (int) (Math.random() * 10) * 100;
        for (ImageView y : listImageView) {
            if (Math.abs(result - y.getLayoutX()) < 250) {
                result = -50;
            }
        }
        return result;
    }

    private void shuffle() {
        ArrayList<Image> tempList = new ArrayList<>();
        while(listImage.size() > 0) {
            int randomIndex = new Random().nextInt(listImage.size());
            tempList.add(listImage.get(randomIndex));
            listImage.remove(randomIndex);
        }
        listImage = tempList;
    }

    /**
     * Enemy animation
     */
    public void animation() {
        animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                for (ImageView i : listImageView) {
                    i.setLayoutX(i.getLayoutX() - 2.6);
                    if (i.getLayoutX() < -50) {
                        i.setLayoutX(random());
                    }
                }
                shuffle();
            }
        };
    }
}
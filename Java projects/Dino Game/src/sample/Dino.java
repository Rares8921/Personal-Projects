package sample;

import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

public class Dino {
    protected Pane root;
    protected Image dinoImg = new Image("dino1.png"), dinoImgCrouch = new Image("dino5.png");
    public static ImageView dinoView = new ImageView();
    public static AnimationTimer animationTimer;
    public static Timeline dinoTimeline;

    Dino(Pane pane) {
        this.root = pane;
        this.animationRun(1); // state 1 - normal, 2 - crouch, 3 - jump
        this.animationJump();
        this.root.getChildren().add(dinoView);
    }

    /**
     * Animation Dino Run
     */
    public void animationRun(int state) {
        if(state == 3) {
            dinoTimeline.stop();
            dinoView.setImage(new Image("dino3.png"));
        } else {
            String photo1 = (state == 1 ? "/dino1.png" : "dino5.png");
            String photo2 = (state == 1 ? "/dino2.png" : "dino6.png");
            //dinoView.setImage(state == 1 ? dinoImg : dinoImgCrouch);
            //dinoView = new ImageView(state == 1 ? dinoImg : dinoImgCrouch);
            dinoView.setImage(state == 1 ? dinoImg : dinoImgCrouch);
            dinoView.setLayoutY(state == 1 ? 290 : 305);
            dinoView.setLayoutX(20);
            dinoTimeline = new Timeline(
                    new KeyFrame(Duration.millis(200), t1 -> dinoView.setImage(new Image(photo1))),
                    new KeyFrame(Duration.millis(400), t2 -> dinoView.setImage(new Image(photo2)))
            );
            dinoTimeline.setCycleCount(Timeline.INDEFINITE);
            dinoTimeline.play();
        }
    }

    /**
     * Animation Dino Jump
     */
    public void animationJump() {
        animationTimer = new AnimationTimer() {
            private double velocity = -3;   // Initial jump velocity (negative means going up)
            private double positionY = dinoView.getLayoutY();  // Dino's current position

            @Override
            public void handle(long now) {
                // Update the Y position based on current velocity
                positionY += velocity;
                // If going up and not reached peak, apply gravity to slow it down
                // Gravity force, controls how fast it slows down/speeds up
                double gravity = 0.25;
                // Y position of the peak of the jump
                double jumpPeakY = 180;
                if (velocity < 0 && positionY <= jumpPeakY) {
                    velocity += gravity;  // Slow down the upward movement smoothly
                }
                // If the dino has reached the peak and starts falling down
                // Y position of the ground (where the dino lands)
                double groundY = 290;
                if (velocity >= 0 && positionY < 290) {
                    velocity += gravity;  // Apply gravity to accelerate falling
                }
                // If the dino reaches the ground, stop the jump
                if (positionY >= groundY) {
                    positionY = groundY;  // Ensure it doesn't go below the ground
                    velocity = -3;        // Reset the velocity for the next jump
                    animationTimer.stop(); // Stop the timer once the jump is complete
                    dinoTimeline.stop();   // Stop any other animations
                    animationRun(1);
                }
                // Update the dinoView position
                dinoView.setLayoutY(positionY);
            }
        };

    }
}


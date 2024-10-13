package sample;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

import java.io.File;
import java.net.URL;
import java.util.Formatter;
import java.util.ResourceBundle;
import java.util.Scanner;

public class Controller implements Initializable {

    @FXML
    private AnchorPane parent, gamePane;

    @FXML
    private Label score, highScore, startLabel, pauseLabel;

    @FXML
    private Button startButton, soundButton;

    private double xOffSet;
    private double yOffSet;

    private Sound deathSound, jumpSound, scoreSound;

    private static ImageView dinoImg;

    private Dino dino;
    private Enemy enemy;

    private Timeline scoreTimeline, endTimeLine;

    private int playerScore, playerHighScore, state = 1;

    private boolean gameStarted, gamePaused, soundOn;

    private final int FINAL_SCORE = 1_000_000;

    public void close() {
        saveData();
        Main.stage.close();
    }

    private void saveData() {
        try {
            Formatter f = new Formatter(System.getenv("APPDATA") + "\\dinogamefile.dat");
            f.format("%s\n%d", (soundOn ? "ON" : "OFF"), playerHighScore);
            f.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void minimize() {
        if(gameStarted && !gamePaused) {
            gamePause();
        }
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

    private void scoreAnimation() {
        scoreTimeline = new Timeline(new KeyFrame(Duration.millis(130), e -> {
                playerScore++;
                score.setText("Score: " + playerScore);
                if(playerScore > playerHighScore) {
                    playerHighScore = playerScore;
                    highScore.setText("HScore: " + playerHighScore);
                }
                if(playerScore % 100 == 0) {
                    playScore();
                }
                if(playerScore == FINAL_SCORE) {
                    // animation for the end
                    gamePause();
                    scoreTimeline.stop();
                    endTimeLine = new Timeline();
                    KeyFrame key = new KeyFrame(Duration.millis(1700), new KeyValue(gamePane.opacityProperty(), 0));
                    endTimeLine.getKeyFrames().add(key);
                    endTimeLine.setOnFinished(ee -> {
                        collision(enemy, true);
                    });
                    endTimeLine.play();
                }
            })
        );
        scoreTimeline.setCycleCount(Timeline.INDEFINITE);
        scoreTimeline.play();
    }

    private ImageView dinoImage() {
        ImageView dinoImgView = new ImageView();
        dinoImgView.setLayoutX(15);
        dinoImgView.setLayoutY(state == 1 ? 290 : 305);
        return dinoImgView;
    }

    private void gamePause() {
        if(gamePaused) {
            gamePane.getChildren().remove(pauseLabel);
            gamePaused = false;
            scoreTimeline.play();
            Enemy.switchState(2);
            Enemy.animationTimer.start();
            Floor.floorTimeLine.play();
            Cloud.cloudTimeLine.play();
            Dino.dinoTimeline.play();
        } else if(Dino.dinoView.getLayoutY() >= 290) {
            gamePane.getChildren().add(pauseLabel);
            gamePaused = true;
            scoreTimeline.stop();
            Enemy.switchState(1);
            Enemy.animationTimer.stop();
            Floor.floorTimeLine.stop();
            Cloud.cloudTimeLine.stop();
            Dino.dinoTimeline.stop();
        }
    }

    public void keyEvents() {
        parent.requestFocus();
        parent.setOnKeyPressed(event -> {
            if(gameStarted) {
                parent.requestFocus();
                if(event.getCode() == KeyCode.SPACE && !gamePaused && state == 1 && Dino.dinoView.getLayoutY() >= 290) { // jump animation
                    Dino.animationTimer.start();
                    dinoImg = dinoImage();
                    Dino.dinoTimeline.stop();
                    dino.animationRun(3);
                    playJump();
                }
                if(event.getCode() == KeyCode.DOWN && !gamePaused && state == 1) { // crouch animation
                    if(Dino.dinoView.getLayoutY() >= 290) { // if dino is on ground
                        Dino.dinoTimeline.stop();
                        state = 2;
                        dinoImg.setLayoutY(305);
                        dinoImg.setLayoutX(20);
                        dino.animationRun(2);
                    }
                }
                if (event.getCode() == KeyCode.K) { // pause the game
                    gamePause();
                }
            }
        });

        parent.setOnKeyReleased(event -> {
            if(gameStarted) {
                if(event.getCode() == KeyCode.DOWN && !gamePaused) {
                    if(Dino.dinoView.getLayoutY() >= 290) { // if dino is on ground
                        Dino.dinoTimeline.stop();
                        state = 1;
                        dinoImg.setLayoutY(290);
                        dinoImg.setLayoutX(20);
                        dino.animationRun(1);
                    }
                }
            }
        });

    }

    public void game() {
        gameStarted = true;
        dino = new Dino(gamePane);
        gamePane.getChildren().removeAll(startLabel, pauseLabel, startButton);
        dinoImg = dinoImage();
        gamePane.getChildren().add(dinoImg);
        scoreAnimation();
        new Floor(gamePane);
        new Cloud(gamePane);
        enemy = new Enemy(gamePane);
        keyEvents();
        collision(enemy, false);
    }

    private void collision(Enemy enemy, boolean finish) {
        Timeline t = new Timeline(
                new KeyFrame(Duration.millis(1), t1 -> {
                    for (ImageView i1 : enemy.listImageView) {
                        if ((Dino.dinoView.getBoundsInParent().intersects(i1.getLayoutX() + 0, i1.getLayoutY() + 0,
                                i1.getBoundsInParent().getWidth() - 16,
                                i1.getBoundsInParent().getHeight() - 16) && gameStarted) || finish) {
                            if(playerScore < FINAL_SCORE) {
                                playDeath();
                            } else {
                                gamePane.setOpacity(1);
                            }
                            if(playerScore == FINAL_SCORE) {
                                startLabel.setText("Congratulations! You finished the game!");
                            } else if(playerScore == playerHighScore) {
                                startLabel.setText("Congratulations! You set a new high score.");
                            } else {
                                startLabel.setText("Game over!");
                            }
                            pauseLabel.setText("Press start to play again!");
                            gameStarted = gamePaused = false; // end the game
                            scoreTimeline.stop(); // stop score animation
                            playerScore = 0; // reset player score
                            state = 1; // reset animation
                            Enemy.listImage.clear();
                            enemy.listImageView.clear();
                            Dino.dinoTimeline.stop();
                            Dino.animationTimer.stop();
                            gamePane.getChildren().clear();
                            gamePane.getChildren().addAll(startLabel, startButton, pauseLabel, score, highScore); // add menu components
                            break;
                        }
                    }
                })
        );
        t.setCycleCount(Timeline.INDEFINITE);
        t.play();
    }

    private void style() {
        AnchorPane.setLeftAnchor(startLabel, 0.0);
        AnchorPane.setRightAnchor(startLabel, 0.0);
        startLabel.setAlignment(Pos.CENTER);

        AnchorPane.setLeftAnchor(pauseLabel, 0.0);
        AnchorPane.setRightAnchor(pauseLabel, 0.0);
        pauseLabel.setAlignment(Pos.CENTER);
    }

    private void extractData() {
        try {
            File file = new File(System.getenv("APPDATA") + "\\dinogamefile.dat");
            if(!file.exists()) {
                Formatter f = new Formatter(System.getenv("APPDATA") + "\\dinogamefile.dat");
                f.format("%s %d", "ON", 0);
                f.close();
            }
            Scanner scan = new Scanner(file);
            String sound = scan.next();
            int hScore = scan.nextInt();
            scan.close();
            soundOn = sound.equals("ON");
            playerHighScore = hScore;
            highScore.setText("HScore: " + playerHighScore);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void setSound() {
        deathSound = new Sound("/dead.wav");
        jumpSound = new Sound("/jump.wav");
        scoreSound = new Sound("/scoreup.wav");
        soundButton.setText(soundOn ? "\uD83D\uDD0A" : "\uD83D\uDD08");
    }

    private void playDeath() {
        if(soundOn) {
            deathSound.playClip();
        }
    }

    private void playJump() {
        if(soundOn) {
            jumpSound.playClip();
        }
    }

    private void playScore() {
        if(soundOn) {
            scoreSound.playClip();
        }
    }

    public void onAndOff() {
        soundButton.setText(soundOn ? "\uD83D\uDD08" : "\uD83D\uDD0A");
        soundOn = !soundOn;
        parent.requestFocus();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setDraggable();
        extractData();
        setSound();
        style();
    }
}

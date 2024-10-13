package com.example.pong;

import java.util.Random;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontSmoothingType;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class Pong extends Application {

    private static final int width = 800;
    private static final int height = 600;
    private static final int PLAYER_HEIGHT = 100;
    private static final int PLAYER_WIDTH = 15;
    private static final double BALL_R = 15;
    private int ballYSpeed = 1;
    private int ballXSpeed = 1;
    private double playerOneYPos = height / 2.0;
    private double playerTwoYPos = height / 2.0;
    private double ballXPos = width / 2.0;
    private double ballYPos = height / 2.0;
    private int scoreP1 = 0;
    private int scoreP2 = 0;
    private boolean gameStarted;

    public void start(Stage stage) {
        stage.initStyle(StageStyle.UNDECORATED);

        Canvas canvas = new Canvas(width, height);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        Timeline tl = new Timeline(new KeyFrame(Duration.millis(10), e -> run(gc)));
        tl.setCycleCount(Timeline.INDEFINITE);

        //mouse control (move and click)
        canvas.setOnMouseMoved(e ->  {if(gameStarted) playerOneYPos  = e.getY();} );
        canvas.setOnMouseClicked(e ->  gameStarted = true);

        Button minimizeButton = new Button("─");
        minimizeButton.setStyle("-fx-background-radius: 0; -fx-background-color: #141414; -fx-font-size: 20px; -fx-text-fill: white; -fx-cursor: HAND;");
        minimizeButton.setOnMouseEntered(e -> minimizeButton.setStyle("-fx-background-radius: 0; -fx-background-color: #212121; -fx-font-size: 20px; -fx-text-fill: white; -fx-cursor: HAND;"));
        minimizeButton.setOnMouseExited(e -> minimizeButton.setStyle("-fx-background-radius: 0; -fx-background-color: #141414; -fx-font-size: 20px; -fx-text-fill: white; -fx-cursor: HAND;"));
        minimizeButton.setOnMouseClicked(e -> stage.setIconified(true));
        minimizeButton.setLayoutX(720);
        minimizeButton.setLayoutY(0);

        Button closeButton = new Button("X");
        closeButton.setStyle("-fx-background-radius: 0; -fx-background-color: #141414; -fx-font-size: 20px; -fx-text-fill: white; -fx-cursor: HAND;");
        closeButton.setOnMouseEntered(e -> closeButton.setStyle("-fx-background-radius: 0; -fx-background-color: #212121; -fx-font-size: 20px; -fx-text-fill: white; -fx-cursor: HAND;"));
        closeButton.setOnMouseExited(e -> closeButton.setStyle("-fx-background-radius: 0; -fx-background-color: #141414; -fx-font-size: 20px; -fx-text-fill: white; -fx-cursor: HAND;"));
        closeButton.setOnMouseClicked(e -> stage.close());
        closeButton.setLayoutX(760);
        closeButton.setLayoutY(0);

        stage.setScene(new Scene(new AnchorPane(canvas, minimizeButton, closeButton)));
        stage.show();
        tl.play();
    }

    private void run(GraphicsContext gc) {
        //set background color
        gc.setFill(Color.web("#141414"));
        gc.fillRect(0, 0, width, height);

        //set text
        gc.setFill(Color.WHITE);
        gc.setFontSmoothingType(FontSmoothingType.LCD);
        gc.setFont(Font.font(25));

        if(gameStarted) {
            //set ball movement
            ballXPos += ballXSpeed;
            ballYPos += ballYSpeed;

            if(ballXPos < width - width  / 4.0) {
                playerTwoYPos = ballYPos - PLAYER_HEIGHT / 2.0;
            }  else {
                playerTwoYPos =  ballYPos > playerTwoYPos + PLAYER_HEIGHT / 2.0 ? playerTwoYPos + 1 : playerTwoYPos - 1;
            }
            //draw the ball
            gc.fillOval(ballXPos, ballYPos, BALL_R, BALL_R);

        } else {
            //set the start text
            gc.setStroke(Color.WHITE);
            gc.setTextAlign(TextAlignment.CENTER);
            gc.strokeText("Click to start", width / 2.0, height / 2.0);

            //reset the ball start position
            ballXPos = width / 2.0;
            ballYPos = height / 2.0;

            //reset the ball speed and the direction
            ballXSpeed = new Random().nextInt(2) == 0 ? 1: -1;
            ballYSpeed = new Random().nextInt(2) == 0 ? 1: -1;
        }

        //makes sure the ball stays in the canvas
        if(ballYPos > height || ballYPos < 0) ballYSpeed *=-1;

        // If player1 misses the ball
        int playerOneXPos = 0;
        if(ballXPos < playerOneXPos - PLAYER_WIDTH) {
            scoreP2++;
            gameStarted = false;
        }

        // If player2 misses the ball
        double playerTwoXPos = width - PLAYER_WIDTH;
        if(ballXPos > playerTwoXPos + PLAYER_WIDTH) {
            scoreP1++;
            gameStarted = false;
        }

        //increase the speed after the ball hits the player
        if( ( (ballXPos + BALL_R > playerTwoXPos) && ballYPos >= playerTwoYPos && ballYPos <= playerTwoYPos + PLAYER_HEIGHT) ||
                ( (ballXPos < playerOneXPos + PLAYER_WIDTH) && ballYPos >= playerOneYPos && ballYPos <= playerOneYPos + PLAYER_HEIGHT) ) {
            ballYSpeed += 1 * Math.signum(ballYSpeed);
            ballXSpeed += 1 * Math.signum(ballXSpeed);
            ballXSpeed *= -1;
            ballYSpeed *= -1;
        }

        //draw score
        gc.fillText(scoreP1 + "\t\t\t\t\t\t\t\t" + scoreP2, width / 2.0, 100);

        gc.fillText("Pong", width / 2.0, 35);

        //draw player 1 & 2
        gc.fillRect(playerTwoXPos, playerTwoYPos, PLAYER_WIDTH, PLAYER_HEIGHT);
        gc.fillRect(playerOneXPos, playerOneYPos, PLAYER_WIDTH, PLAYER_HEIGHT);
    }

    // start the application
    public static void main(String[] args) {
        launch(args);
    }
}
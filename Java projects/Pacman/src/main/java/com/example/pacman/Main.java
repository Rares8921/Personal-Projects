package com.example.pacman;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.File;
import java.util.*;

public class Main extends Application {

    public static Stage stage = null;

    // game variables

    private boolean gameStarted = false, gameOver = false, isPaused = false, dying = false, wonGame = false;

    private static final ImageView heart1 = new ImageView(new Image(Objects.requireNonNull(Main.class.getResourceAsStream("images/heart.png"))));
    private static final ImageView heart2 = new ImageView(new Image(Objects.requireNonNull(Main.class.getResourceAsStream("images/heart.png"))));
    private static final ImageView heart3 = new ImageView(new Image(Objects.requireNonNull(Main.class.getResourceAsStream("images/heart.png"))));

    private final Label scoreLabel = new Label("Score: 0");
    private Label highScoreLabel = new Label();

    private final int BLOCK_SIZE = 45;
    private final int N_BLOCKS = 15;
    private final int SCREEN_SIZE = N_BLOCKS * BLOCK_SIZE;
    //private final int PACMAN_SPEED = 6;

    private int n_ghosts = 6, lives = 0, score = 0, highScore = -1;
    private int[] dx, dy;
    private int[] ghost_x, ghost_y, ghost_dx, ghost_dy, ghost_speed;
    private final boolean[][] mp = new boolean[15][15];

    private Image ghost;
    private Image up, down, left, right;

    private int currentDirection;
    private double pacman_x, pacman_y;
    private int pacman_dx, pacman_dy;
    private final int RIGHT = 0;
    private final int LEFT = 1;
    private final int UP = 2;
    private final int DOWN = 3;

    private final int[] VALID_SPEEDS = {1, 2, 3, 4, 6, 4};
    //private final int MAX_SPEED = 6;
    private int currentSpeed = 3;
    private static short[][] screenData;
    private Timeline timeLine; // for game animation
    private GraphicsContext game; // for drawing

    /**
     * @see 0 - obstacle cell
     *      1 - cell with white dot and left border
     *      2 - -"- top border
     *      4 - -"- right border
     *      8 - -"- bottom border
     *      16 - white dot
     */

    private static final short[][] levelData = {
            {19, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 22},
            {17, 16, 16, 16, 16, 24, 16, 16, 16, 16, 16, 16, 16, 16, 20},
            {25, 24, 24, 24, 28, 0, 17, 16, 16, 16, 16, 16, 16, 16, 20},
            {21,  0,  0,  0,  0,  0, 17, 16, 16, 16, 16, 16, 16, 16, 20},
            {19, 18, 18, 18, 18, 18, 16, 16, 16, 16, 24, 24, 24, 24, 20},
            {17, 16, 16, 16, 16, 16, 16, 16, 16, 20, 0,  0,  0,   0, 21},
            {17, 16, 16, 16, 16, 16, 16, 16, 16, 20, 0,  0,  0,   0, 21},
            {17, 16, 16, 16, 24, 16, 16, 16, 16, 20, 0,  0,  0,   0, 21},
            {17, 16, 16, 20, 0, 17, 16, 16, 16, 16, 18, 18, 18, 18, 20},
            {17, 24, 24, 28, 0, 25, 24, 24, 16, 16, 16, 16, 16, 16, 20},
            {21, 0,  0,  0,  0,  0,  0,   0, 17, 16, 16, 16, 16, 16, 20},
            {17, 18, 18, 22, 0, 19, 18, 18, 16, 16, 16, 16, 16, 16, 20},
            {17, 16, 16, 20, 0, 17, 16, 16, 16, 16, 16, 16, 16, 16, 20},
            {17, 16, 16, 20, 0, 17, 16, 16, 16, 16, 16, 16, 16, 16, 20},
            {25, 24, 24, 24, 26, 24, 24, 24, 24, 24, 24, 24, 24, 24, 28}
    };

    private static int getNumberOfPoints() {
        int cnt = 0;
        for(int i = 0; i < 15; i++) {
            for(int j = 0; j < 15; j++) {
                if(levelData[i][j] != 0) {
                    cnt++;
                }
            }
        }
        return cnt;
    }

    private static final int numberOfPoints = getNumberOfPoints();
    private static int collectedPoints = 0;

    @Override
    public void stop() {
        try {
            Formatter f = new Formatter("src/main/resources/com/example/pacman/highscore.txt");
            f.format("%d", highScore);
            f.close();
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Fxml component with title bar
        Parent parent = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("sample.fxml")));


        // Application icon
        Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("images/pacman-icon.jpg")));
        primaryStage.getIcons().add(icon);

        // Game component
        Canvas canvas = new Canvas(SCREEN_SIZE, SCREEN_SIZE);

        initVariables();

        // Merging the 2 components
        Group root = new Group();
        root.getChildren().add(canvas);
        root.getChildren().add(parent);
        // adding the 3 lives
        heart1.setFitWidth(30); heart1.setFitHeight(30);
        heart2.setFitWidth(30); heart2.setFitHeight(30);
        heart3.setFitWidth(30); heart3.setFitHeight(30);
        heart1.setLayoutX(118); heart1.setLayoutY(8);
        heart2.setLayoutX(148); heart2.setLayoutY(8);
        heart3.setLayoutX(178); heart3.setLayoutY(8);
        heart1.setVisible(false); heart2.setVisible(false); heart3.setVisible(false);
        root.getChildren().addAll(heart1, heart2, heart3);
        // adding score and highScore labels
        scoreLabel.setFont(new Font("Consolas", 20));
        scoreLabel.setStyle("-fx-font-family: Consolas; -fx-font-size: 20px; -fx-text-fill: white; -fx-background-color: transparent;");
        scoreLabel.setLayoutX(280); scoreLabel.setLayoutY(2);
        highScoreLabel.setStyle("-fx-font-family: Consolas; -fx-font-size: 20px; -fx-text-fill: white; -fx-background-color: transparent;");
        highScoreLabel.setLayoutX(280); highScoreLabel.setLayoutY(22);
        root.getChildren().addAll(scoreLabel, highScoreLabel);

        primaryStage.setTitle("Pacman");
        primaryStage.setResizable(false);
        primaryStage.initStyle(StageStyle.UNDECORATED); // remove the title bar

        // Game scene
        Scene scene = getScene(root);

        primaryStage.setScene(scene);
        root.requestFocus();

        stage = primaryStage;
        stage.requestFocus();
        stage.show();

        canvas.setLayoutY(45);
        canvas.setWidth(675);
        canvas.setHeight(675);
        game = canvas.getGraphicsContext2D();

        loadResources();
        background(game);
        drawScore();
    }

    private Scene getScene(Group root) {
        Scene scene = new Scene(root);

        // keyBinds

        scene.setOnKeyPressed(keyEvent -> {
            KeyCode code = keyEvent.getCode();
            if(code == KeyCode.RIGHT || code == KeyCode.D) {
                currentDirection = RIGHT;
            } else if(code == KeyCode.LEFT || code == KeyCode.A) {
                currentDirection = LEFT;
            } else if(code == KeyCode.UP || code == KeyCode.W) {
                currentDirection = UP;
            } else if(code == KeyCode.DOWN || code == KeyCode.S) {
                currentDirection = DOWN;
            } else if(code == KeyCode.SPACE) {
                if(!gameOver) {
                    if(!gameStarted) {
                        startGame();
                    } else {
                        if (isPaused) {
                            timeLine.play();
                        } else {
                            timeLine.stop();
                        }
                        isPaused = !isPaused;
                    }
                } else {
                    startGame();
                }
            }
        });
        return scene;
    }

    private void loadResources() {
        //Image heart = new Image(Objects.requireNonNull(getClass().getResourceAsStream("images/heart.png")));
        ghost = new Image(Objects.requireNonNull(getClass().getResourceAsStream("images/ghost.gif")));
        up = new Image(Objects.requireNonNull(getClass().getResourceAsStream("images/up.gif")));
        down = new Image(Objects.requireNonNull(getClass().getResourceAsStream("images/down.gif")));
        left = new Image(Objects.requireNonNull(getClass().getResourceAsStream("images/left.gif")));
        right = new Image(Objects.requireNonNull(getClass().getResourceAsStream("images/right.gif")));
    }

    private void initVariables() {
        screenData = new short[N_BLOCKS][N_BLOCKS];
        int MAX_GHOSTS = 12;
        ghost_x = new int[MAX_GHOSTS];
        ghost_y = new int[MAX_GHOSTS];
        ghost_dx = new int[MAX_GHOSTS];
        ghost_dy = new int[MAX_GHOSTS];
        ghost_speed = new int[MAX_GHOSTS];
        dx = new int[4];
        dy = new int[4];
        timeLine = new Timeline(new KeyFrame(Duration.millis(12), e -> run(game)));
        timeLine.setCycleCount(Animation.INDEFINITE);
        // extract high score from the file
        try {
            File file = new File("src/main/resources/com/example/pacman/highscore.txt");
            Scanner scan = new Scanner(file);
            highScore = scan.nextInt();
            scan.close();
        } catch(Exception e) {
            e.printStackTrace(System.out);
        }
        highScoreLabel = new Label("Highscore: " + highScore);
    }

    private void startGame() {
        heart1.setVisible(true); heart2.setVisible(true); heart3.setVisible(true);
        pacman_dx = 9; pacman_dy = 7;
        pacman_x = 333; pacman_y = 423;
        gameOver = wonGame = false;
        lives = 3; n_ghosts = 6; currentSpeed = 3;
        collectedPoints = 0;
        initLevel();
        timeLine.play();
        gameStarted = true;
    }

    private void initLevel() {
        for(int i = 0; i < N_BLOCKS; i++) {
            System.arraycopy(levelData[i], 0, screenData[i], 0, N_BLOCKS);
        }
        continueLevel();
    }

    private void continueLevel() {
        int dx = 1, random;
        for (int i = 0; i < n_ghosts; i++) {
            ghost_y[i] = 4 * BLOCK_SIZE; //start position
            ghost_x[i] = 4 * BLOCK_SIZE;
            ghost_dy[i] = 0;
            ghost_dx[i] = dx;
            dx = -dx;
            random = (int) (Math.random() * (currentSpeed + 1));

            if (random > currentSpeed) {
                random = currentSpeed;
            }

            ghost_speed[i] = VALID_SPEEDS[random];
        }

        pacman_x = 7 * BLOCK_SIZE;  //start position
        pacman_y = 11 * BLOCK_SIZE;
        pacman_dx = 0;	//reset direction move
        pacman_dy = 0;
        currentDirection = 0;
        dying = false;
    }

    private void run(GraphicsContext gct) {

        if(gameOver) {
            score = 0;
            for(int i = 0; i < 15; i++) {
                java.util.Arrays.fill(mp[i], false);
            }
            gct.setFill(Color.WHITE);
            gct.setFont(new Font("Consolas", 45));
            gct.fillText("Game Over!", SCREEN_SIZE / 3.1, SCREEN_SIZE / 2.0);
            gct.setFont(new Font("Consolas", 25));
            gct.fillText("Press SpaceBar to restart!", SCREEN_SIZE / 4.3, SCREEN_SIZE / 1.75);
            return;
        } else if(wonGame) {
            for(int i = 0; i < 15; i++) {
                java.util.Arrays.fill(mp[i], false);
            }
            timeLine.stop();
            startGame();
            timeLine.play();
            return;
        }

        if(dying) {
            lives--;
            if(lives < 1) {
                heart1.setVisible(false);
            } else if(lives < 2) {
                heart2.setVisible(false);
            } else if(lives < 3) {
                heart3.setVisible(false);
            }
            dying = false;
            continueLevel();
        }
        background(gct);
        drawPacman(gct);
        moveGhosts(gct);
        drawScore();

        switch(currentDirection) {
            case LEFT:
                left();
                break;
            case RIGHT:
                right();
                break;
            case UP:
                up();
                break;
            case DOWN:
                down();
                break;
            default:
                break;
        }
        gameOver();
        scoreUp();
        checkScore();
        checkWin();
    }

    private void background(GraphicsContext gct) {
        // the start message:
        if(!gameStarted) {
            gct.setFill(Color.web("18191A"));
            gct.fillRect(0, 0, SCREEN_SIZE, SCREEN_SIZE);
            gct.setFill(Color.WHITE);
            gct.setFont(new Font("Consolas", 35));
            gct.fillText("Press Space to start!", SCREEN_SIZE / 5.0, SCREEN_SIZE / 2.05); // text, x and y coordinates
        } else {
            for (int i = 0; i < N_BLOCKS; i++) {
                for (int j = 0; j < N_BLOCKS; j++) {
                    if(screenData[i][j] == 0) {
                        gct.setFill(Color.web("352f95"));
                        gct.setStroke(Color.web("352f95"));
                        gct.fillRect(j * BLOCK_SIZE, i * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
                    } else {
                        gct.setFill(Color.web("18191A"));
                        gct.fillRect(j * BLOCK_SIZE, i * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
                        // arguments: x position, y position, width, height

                        double x = (j + j + 0.8) * BLOCK_SIZE / 2.0, y = (i + i + 0.8) * BLOCK_SIZE / 2.0;
                        if ((screenData[i][j] & 16) != 0 && !mp[i][j]) {
                            gct.setFill(Color.GOLD);
                            gct.fillOval(x, y, BLOCK_SIZE - 35, BLOCK_SIZE - 35);
                            // arguments: x position, y position, radius, radius
                        }
                    }
                }
            }
            gct.setStroke(Color.web("8b780d"));
            gct.setFill(Color.web("8b780d"));
            gct.setLineWidth(2.5);
            gct.strokeLine(0, 0, SCREEN_SIZE, 0);
            gct.strokeLine(0, 0, 0, SCREEN_SIZE);
            gct.strokeLine(0, SCREEN_SIZE, SCREEN_SIZE, SCREEN_SIZE);
            gct.strokeLine(SCREEN_SIZE, 0, SCREEN_SIZE, SCREEN_SIZE);
        }
    }

    private void left() {
        pacman_dx = (int) Math.round(Math.max(pacman_x - 1, 0) / 45);
        pacman_dy = (int) Math.round(pacman_y / 45);
        if(screenData[pacman_dy][pacman_dx] != 0) {
            pacman_x = Math.max(pacman_x - 1, 0);
        }
    }

    private void right() {
        pacman_dx = (int) Math.round(Math.min(pacman_x + 1, SCREEN_SIZE - 45) / 45);
        pacman_dy = (int) Math.round(pacman_y / 45);
        if(screenData[pacman_dy][pacman_dx] != 0) {
            pacman_x = Math.min(pacman_x + 1, SCREEN_SIZE - 45);
        }
    }

    private void up() {
        pacman_dx = (int) Math.round(pacman_x / 45);
        pacman_dy = (int) Math.round(Math.max(pacman_y - 1, 0) / 45);
        if(screenData[pacman_dy][pacman_dx] != 0) {
            pacman_y = Math.max(pacman_y - 1, 0);
        }
    }

    private void down() {
        pacman_dx = (int) Math.round(pacman_x / 45);
        pacman_dy = (int) Math.round(Math.min(pacman_y + 1, SCREEN_SIZE - 45) / 45);
        if(screenData[pacman_dy][pacman_dx] != 0) {
            pacman_y = Math.min(pacman_y + 1, SCREEN_SIZE - 45);
        }
    }

    private void drawPacman(GraphicsContext gct) {
        switch(currentDirection) {
            case LEFT:
                gct.drawImage(left, pacman_x, pacman_y, 30, 30);
                break;
            case RIGHT:
                gct.drawImage(right, pacman_x, pacman_y, 30, 30);
                break;
            case UP:
                gct.drawImage(up, pacman_x, pacman_y, 30, 30);
                break;
            case DOWN:
                gct.drawImage(down, pacman_x, pacman_y, 30, 30);
                break;
            default:
                break;
        }
    }

    private void moveGhosts(GraphicsContext gct) {
        int cnt;
        for(int ii = 0; ii < n_ghosts; ii++) {
            int i = (int) Math.round(ghost_x[ii] / 45.0);
            int j = (int) Math.round(ghost_y[ii] / 45.0);
            if (ghost_x[ii] % BLOCK_SIZE == 0 && ghost_y[ii] % BLOCK_SIZE == 0) {
                cnt = 0;
                if ((screenData[j][i] & 1) == 0 && ghost_dx[ii] != 1) {
                    dx[cnt] = -1;
                    dy[cnt] = 0;
                    cnt++;
                }
                if ((screenData[j][i] & 2) == 0 && ghost_dx[ii] != 1) {
                    dx[cnt] = 0;
                    dy[cnt] = -1;
                    cnt++;
                }
                if ((screenData[j][i] & 4) == 0 && ghost_dx[ii] != -1) {
                    dx[cnt] = 1;
                    dy[cnt] = 0;
                    cnt++;
                }
                if ((screenData[j][i] & 8) == 0 && ghost_dx[ii] != -1) {
                    dx[cnt] = 0;
                    dy[cnt] = 1;
                    cnt++;
                }
                if (cnt == 0) {
                    if ((screenData[j][i] & 15) == 15) {
                        ghost_dx[ii] = 0;
                        ghost_dy[ii] = 0;
                    } else {
                        ghost_dx[ii] = -ghost_dx[ii];
                        ghost_dy[ii] = -ghost_dy[ii];
                    }
                } else {
                    cnt = (int) (Math.random() * cnt);
                    cnt = Math.min(cnt, 3);
                    ghost_dx[ii] = dx[cnt];
                    ghost_dy[ii] = dy[cnt];
                }
            }
            int temp1 = ghost_x[ii], temp2 = ghost_y[ii];
            ghost_x[ii] = ghost_x[ii] + (ghost_dx[ii] * ghost_speed[ii]);
            if(ghost_x[ii] < 0) {
                ghost_x[ii] = 10;
                ghost_dx[ii] *= -1;
            } else if(ghost_x[ii] > SCREEN_SIZE - 45) {
                ghost_x[ii] = SCREEN_SIZE - 90;
                ghost_dx[ii] *= -1;
            }
            ghost_y[ii] = ghost_y[ii] + (ghost_dy[ii] * ghost_speed[ii]);
            if(ghost_y[ii] < 0) {
                ghost_y[ii] = 10;
                ghost_dy[ii] *= -1;
            } else if(ghost_y[ii] > SCREEN_SIZE - 45) {
                ghost_y[ii] = SCREEN_SIZE - 90;
                ghost_dy[ii] *= -1;
            }
            int ghost_dx = (int) Math.round(ghost_x[ii] / 45.0);
            int ghost_dy = (int) Math.round(ghost_y[ii] / 45.0);
            if(screenData[ghost_dy][ghost_dx] == 0) {
                ghost_x[ii] = Math.max(0, temp1 - 1); ghost_y[ii] = Math.max(0, temp2 - 1);
            }
            drawGhost(gct, ghost_x[ii] + 1, ghost_y[ii] + 1);
            if (pacman_x > (ghost_x[ii] - 30) && pacman_x < (ghost_x[ii] + 30)
                    && pacman_y > (ghost_y[ii] - 30) && pacman_y < (ghost_y[ii] + 30)
                    && gameStarted && !isPaused) {

                dying = true;
            }
        }
    }

    private void drawGhost(GraphicsContext gct, int x, int y) {
        gct.drawImage(ghost, x, y, 32, 32);
    }

    private void scoreUp() {
        pacman_dx = (int) Math.round(pacman_x / 45);
        pacman_dy = (int) Math.round(pacman_y / 45);
        if((screenData[pacman_dy][pacman_dx] & 16) != 0 && !mp[pacman_dy][pacman_dx]) {
            mp[pacman_dy][pacman_dx] = true;
            collectedPoints++;
            score++;
        }
    }

    private void checkWin() {
        if(collectedPoints == numberOfPoints) {
            wonGame = true;
        }
    }

    private void gameOver() {
        if(lives == 0) {
            gameOver = true;
        }
    }

    private void drawScore() {
        checkScore();
        scoreLabel.setText("Score: " + score);
        highScoreLabel.setText("HighScore: " + highScore);
    }

    private void checkScore() {
        highScore = Math.max(highScore, score);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
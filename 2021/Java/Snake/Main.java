package sample;

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
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.awt.Point;
import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

public class Main extends Application {

    private static final int WIDTH = 800;
    private static final int HEIGHT = 800;
    private static final int ROWS = 20;
    private static final int COLUMNS = 20;
    private static final int SQUARE = WIDTH / ROWS; // size of a square is 40
    private static final String[] IMAGES = new String[]{"/imgs/orange.png", "/imgs/apple.png", "/imgs/cherry.png",
            "/imgs/berry.png", "/imgs/coconut.png", "/imgs/peach.png", "/imgs/watermelon.png", "/imgs/tomato.png",
            "/imgs/pomegranate.png"};

    private static final int RIGHT = 0;
    private static final int LEFT = 1;
    private static final int UP = 2;
    private static final int DOWN = 3;

    private GraphicsContext gc;
    private final List<Point> snake = new ArrayList<>();
    private Point snakeHead;
    private Image foodImage;
    private int foodX;
    private int foodY;
    private boolean gameOver, isPause, gameStarted;
    private int currentDirection;
    private int score, highScore = -1;
    private Timeline timeline;

    public static Stage stage = null;

    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent parent = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("sample.fxml")));

        Image icon = new Image("/imgs/snake.png");
        primaryStage.getIcons().add(icon);

        Group root = new Group();

        Canvas canvas = new Canvas(WIDTH, HEIGHT);

        root.getChildren().add(canvas);
        root.getChildren().add(parent);

        primaryStage.setResizable(false);
        primaryStage.setTitle("Snake");
        primaryStage.initStyle(StageStyle.UNDECORATED);

        Scene scene = new Scene(root);
        scene.getStylesheets().add("/sample/snake.css");
        primaryStage.setScene(scene);
        root.requestFocus();

        stage = primaryStage;
        stage.requestFocus();
        stage.show();

        gc = canvas.getGraphicsContext2D();

        snake.add(new Point(5, ROWS / 2));
        snakeHead = snake.get(0);

        generateFood();

        timeline = new Timeline(new KeyFrame(Duration.millis(120), e -> run(gc)));
        timeline.setCycleCount(Animation.INDEFINITE);
        background(gc);
        drawScore();

        scene.setOnKeyPressed(keyEvent -> {
            KeyCode code = keyEvent.getCode();
            if( (code == KeyCode.RIGHT || code == KeyCode.D) && currentDirection != LEFT) {
                currentDirection = RIGHT;
            } else if( (code == KeyCode.LEFT || code == KeyCode.A) && currentDirection != RIGHT) {
                currentDirection = LEFT;
            } else if( (code == KeyCode.UP || code == KeyCode.W) && currentDirection != DOWN) {
                currentDirection = UP;
            } else if( (code == KeyCode.DOWN || code == KeyCode.S) && currentDirection != UP) {
                currentDirection = DOWN;
            } else if(code == KeyCode.SPACE) {
                if(!gameOver) {
                    if(!gameStarted) {
                        timeline.play();
                        gameStarted = true;
                    } else {
                        if (isPause) {
                            timeline.play();
                        } else {
                            timeline.stop();
                        }
                        isPause = !isPause;
                    }
                } else {
                    gameOver = false;
                    score = 0;
                    snake.clear();
                    generateFood();
                    snake.add(new Point(5, ROWS / 2));
                    snakeHead = snake.get(0);
                    timeline.play();
                }
            }
        });

    }

    private void run(GraphicsContext gct) {

        /// game over message
        if(gameOver) {
            gct.setFill(Color.WHITE);
            gct.setFont(new Font("Consolas", 55));
            gct.fillText("Game Over!", WIDTH / 3.2, HEIGHT / 2.0);
            gct.setFont(new Font("Consolas", 35));
            gct.fillText("Press SpaceBar to restart!", WIDTH / 5.1, HEIGHT / 1.75);
            return;
        }

        background(gct);
        food(gct);
        drawSnake(gct);
        drawScore();

        if(highScore == -1) {
            highScore = getHighScore();
        }

        /// the current part of the snake will get the coordinates of the previous part
        for(int i = snake.size() - 1; i >= 1; i--) {
            snake.get(i).x = snake.get(i - 1).x;
            snake.get(i).y = snake.get(i - 1).y;
        }

        // directions
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
        eatFood();

    }

    private void background(GraphicsContext gct) {
        /// the background will be created in the form of a table
        for(int i = 0; i < ROWS; i++) {
            for(int j = 0; j < COLUMNS; j++) {
                /// j = 0 => title bar
                if(j == 0) {
                    gct.setFill(Color.web("292929"));
                } else {
                    // alternating colors on the table
                    // consider it as a matrix where every element is different from the last one, but equal to the 'last but one' element
                    gct.setFill((i + j) % 2 == 0 ? Color.web("1a1a1a") : Color.web("303030"));
                }
                // arguments: x position, y position, width, height
                gct.fillRect(i * SQUARE, j * SQUARE, SQUARE, SQUARE);
            }
        }
        // the start message:
        if(!gameStarted) {
            gct.setFill(Color.WHITE);
            gct.setFont(new Font("Consolas", 35));
            gct.fillText("Press Space to start!", WIDTH / 4.0, HEIGHT / 2.05); // text, x and y coordinates
        }
    }

    private void generateFood() {
        game: // reach point
        while(true) {
            foodX = (int) (Math.random() * ROWS);
            foodY = (int) (Math.random() * COLUMNS);

            // the y must be greater than 1 to not interfere with the title bar
            while(foodY == 0) {
                foodY = (int) (Math.random() * COLUMNS);
            }

            // if the food is generated in one of the snake's body part we generate another coordinates for the food
            for(Point point : snake) {
                if(point.getX() == foodX && point.getY() == foodY) {
                    continue game;
                }
            }
            // if the coordinates are correct a random image is chosen from the list and the process will end
            foodImage = new Image(IMAGES[(int) (Math.random() * IMAGES.length)]);
            break;
        }
    }

    private void food(GraphicsContext gct) {
        // arguments: x position, y position, width, height
        gct.drawImage(foodImage, foodX * SQUARE, foodY * SQUARE, SQUARE, SQUARE);
    }

    private void drawSnake(GraphicsContext gct) {
        // setting the color and shape for every point of the snake
        gct.setFill(Color.MEDIUMSEAGREEN);
        gct.fillRect(snake.get(0).getX() * SQUARE, snake.get(0).getY() * SQUARE, SQUARE - 1, SQUARE - 1);
        for(int i = 1; i < snake.size(); i++) {
            Point point = snake.get(i);
            gct.setFill(Color.SEAGREEN);
            // arguments: x position, y position, width, height
            gct.fillRect(point.getX() * SQUARE, point.getY() * SQUARE, SQUARE - 1, SQUARE - 1);
        }
    }

    private void right() {
        snakeHead.x++;
    }

    private void left() {
        snakeHead.x--;
    }

    private void up() {
        snakeHead.y--;
    }

    private void down() {
        snakeHead.y++;
    }

    private void gameOver() {
        // snake touches border
        if(snakeHead.x < 0 || snakeHead.y < 1 || snakeHead.x >= 20 || snakeHead.y >= 20) {
            gameOver = true;
        }
        // snake kills itself
        for(int i = 1; i < snake.size(); i++) {
            if(snakeHead.getX() == snake.get(i).getX() && snakeHead.getY() == snake.get(i).getY()) {
                gameOver = true;
                break;
            }
        }
    }

    private void eatFood() {
        // if the food's coordinates coincide with the head's, snake's size will increase
        // score will increase as well and a new food will appear
        if(snakeHead.getX() == foodX && snakeHead.getY() == foodY) {
            snake.add(new Point(-1, -1));
            score++;
            generateFood();
        }
    }

    private void drawScore() {
        gc.setFill(Color.WHITE);
        gc.setFont(new Font("Consolas", 30));
        checkScore();
        gc.fillText("Score: " + score + " | HighScore: " + highScore, 223 , 30);
    }

    private int getHighScore() {
        BufferedReader br = null;
        try {
            File file = new File(System.getenv("APPDATA") + "\\highscore.dat");
            br = new BufferedReader(new FileReader(file));
            return Integer.parseInt(br.readLine());
        } catch(IOException e) {
            return 0;
        } finally {
            try {
                if(br != null) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void checkScore() {
        if(highScore == -1) {
            highScore = getHighScore();
        }
        if(score > highScore && score > 0) {
            highScore = score;
            File file = new File(System.getenv("APPDATA") + "\\highscore.dat");
            if(!file.exists()) {
                try {
                    file.createNewFile();
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
            FileWriter fw;
            BufferedWriter bw = null;
            try {
                fw = new FileWriter(file);
                bw = new BufferedWriter(fw);
                bw.write("" + highScore);
            } catch(Exception e) {
                //errors
            } finally {
                try {
                    if(bw != null) {
                        bw.close();
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
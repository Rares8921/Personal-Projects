package sample;

import javafx.animation.AnimationTimer;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.File;
import java.util.*;

public class FlappyBird extends Application {

    private final int APP_HEIGHT = 700;
    private final int APP_WIDTH = 400;
    private int TOTAL_SCORE = 0, TOTAL_COINS = 0;
    private long spaceClickA;
    private double motionTime, elapsedTime;
    private boolean CLICKED, GAME_START, HIT_PIPE, GAME_OVER;
    private LongValue startNanoTime;
    private Sprite firstFloor, secondFloor, birdSprite;
    private Bird bird;
    private Text scoreLabel;
    private GraphicsContext gc, birdGC;
    private AnimationTimer timer;
    private ArrayList<Pipe> pipes;
    private Sound coin, hit, wing, swoosh, die;
    private ImageView gameOver, startGame;
    private AnchorPane root;
    private static String birds, Pipes, back;
    static Scene main;
    private static Stage flappyStage;

    private double xOffSet;
    private double yOffSet;

    private void setDraggable() {
        root.setOnMousePressed((event) -> { // get current position on the screen
            xOffSet = event.getSceneX();
            yOffSet = event.getSceneY();
        });
        root.setOnMouseDragged((event) -> { // change position
            flappyStage.setX(event.getScreenX() - xOffSet);
            flappyStage.setY(event.getScreenY() - yOffSet);
        });
    }

    private void quitGame() {
        Main.GAME = false;
        try {
            File file = new File("src/sample/highscore.txt");
            Scanner scan = new Scanner(file);
            int score = Integer.parseInt(scan.next());
            scan.close();
            if(TOTAL_SCORE > score) {
                Formatter f = new Formatter("src/sample/highscore.txt");
                f.format("%d", TOTAL_SCORE);
                f.close();
                Controller c = new Controller();
                c.highscore = new Label("" + TOTAL_SCORE);
            }
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    private void updateCoins() {
        try {
            File file = new File("src/sample/coins.txt");
            Scanner scan = new Scanner(file);
            int coins = Integer.parseInt(scan.next());
            scan.close();
            if(!GAME_OVER) {
                TOTAL_COINS += TOTAL_SCORE;
            }
            TOTAL_COINS += coins;
            TOTAL_COINS = Math.min(TOTAL_COINS, 999999999);
            Formatter f = new Formatter("src/sample/coins.txt");
            f.format("%d", TOTAL_COINS);
            f.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage primaryStage) {
        flappyStage = primaryStage;
        flappyStage.setTitle("Flappy Bird");
        flappyStage.setResizable(false);
        flappyStage.initStyle(StageStyle.UNDECORATED);

        loadVariables();

        Parent root = getContent();
        main = new Scene(root, APP_WIDTH, APP_HEIGHT);
        setKeyFunctions(main);
        setDraggable();
        flappyStage.setScene(main);
        flappyStage.setOnCloseRequest(e -> {
            updateCoins();
            quitGame();
        });

        flappyStage.show();

        startGame();
    }

    private void setKeyFunctions(Scene scene) {
        scene.setOnKeyPressed(e -> {
            if(e.getCode() == KeyCode.SPACE) {
                setOnUserInput();
            }
        });

        scene.setOnMousePressed(e -> setOnUserInput());
    }

    private void setOnUserInput() {
        if (!HIT_PIPE) {
            CLICKED = true;
            if (!GAME_START) {
                root.getChildren().remove(startGame);
                swoosh.playClip();
                GAME_START = true;
            } else {
                wing.playClip();
                spaceClickA = System.currentTimeMillis();
                birdSprite.setVelocity(0, -250);
            }
        }
        if (GAME_OVER) {
            try {
                File file = new File("src/sample/highscore.txt");
                Scanner scan = new Scanner(file);
                int score = Integer.parseInt(scan.next());
                scan.close();
                if(TOTAL_SCORE > score) {
                    Formatter f = new Formatter("src/sample/highscore.txt");
                    f.format("%d", TOTAL_SCORE);
                    f.close();
                }
                Controller c = new Controller();
                c.setHighscore();
            } catch(Exception ex) {
                ex.printStackTrace();
            }
            startNewGame();
        }
    }

    private Parent getContent() {
        root = new AnchorPane();
        Canvas canvas = new Canvas(APP_WIDTH, APP_HEIGHT);
        Canvas birdCanvas = new Canvas(APP_WIDTH, APP_HEIGHT);
        gc = canvas.getGraphicsContext2D();
        birdGC = birdCanvas.getGraphicsContext2D();

        ImageView bg = setBackground();
        setFloor();
        pipes = new ArrayList<>();
        setPipes();
        setBird();
        setLabels();
        setSounds();

        Button close = new Button("X");
        final String style = "-fx-background-radius: 0; -fx-font-size: 18px; -fx-font-family: Microsoft SansSerif; -fx-underline: true; -fx-text-fill: white; -fx-cursor: hand;";
        final String style1 = "-fx-background-radius: 0; -fx-font-size: 16px; -fx-font-family: Microsoft SansSerif; -fx-text-fill: white; -fx-cursor: hand;";
        close.setStyle(style + "-fx-background-color: transparent;");
        close.setLayoutX(365);

        close.setOnMouseClicked(mouseEvent -> {
            updateCoins();
            quitGame();
            flappyStage.close();
        });

        close.setOnMouseEntered(mouseEvent -> {
            close.setStyle(style + " -fx-background-color: red");
        });

        close.setOnMouseExited(mouseEvent -> {
            close.setStyle(style + "-fx-background-color: transparent;");
        });

        Button minimize = new Button("─");
        minimize.setLayoutX(330);
        minimize.setStyle(style1 + "-fx-background-color: transparent;");

        minimize.setOnMouseClicked(mouseEvent -> flappyStage.setIconified(true));

        minimize.setOnMouseEntered(mouseEvent -> {
            minimize.setStyle(style1 + "-fx-background-color: #6cdceb;");
        });

        minimize.setOnMouseExited(mouseEvent -> {
            minimize.setStyle(style1 + "-fx-background-color: transparent;");
        });

        root.getChildren().addAll(bg, canvas, birdCanvas, scoreLabel, startGame, close, minimize);
        return root;
    }

    private ImageView setBackground() {
        String filePath = "/sample/back" + ("" + (back.indexOf("2"))) + ".png";
        ImageView imageView = new ImageView(new Image(Objects.requireNonNull(getClass().getResource(filePath)).toExternalForm()));
        imageView.setFitWidth(APP_WIDTH);
        imageView.setFitHeight(APP_HEIGHT);
        return imageView;
    }

    private void setLabels() {
        scoreLabel = new Text("0");
        scoreLabel.setFont(Font.font("Courier New", FontWeight.EXTRA_BOLD, 50));
        scoreLabel.setStroke(Color.BLACK);
        scoreLabel.setFill(Color.WHITE);
        scoreLabel.setLayoutX(20);
        scoreLabel.setLayoutY(40);

        gameOver = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("/sample/game_over.png")).toExternalForm()));
        gameOver.setFitWidth(178);
        gameOver.setFitHeight(50);
        gameOver.setLayoutX(110);
        gameOver.setLayoutY(100);

        startGame = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("/sample/ready.png")).toExternalForm()));
        startGame.setFitWidth(178);
        startGame.setFitHeight(50);
        startGame.setLayoutX(100);
        startGame.setLayoutY(100);
    }

    protected void setSounds() {
        try {
            File file = new File("src/sample/sounds.txt");
            Scanner scan = new Scanner(file);
            char[] status = scan.nextLine().toCharArray();
            scan.close();
            if(status[0] == 't') {
                coin = new Sound("resources/score.mp3");
            } else {
                coin = new Sound("resources/empty.mp3");
            }
            if(status[1] == 't') {
                hit = new Sound("resources/hit.mp3");
            } else {
                hit = new Sound("resources/empty.mp3");
            }
            if(status[2] == 't') {
                wing = new Sound("resources/wing.mp3");
            } else {
                wing = new Sound("resources/empty.mp3");
            }
            if(status[3] == 't') {
                swoosh = new Sound("resources/swoosh.mp3");
            } else {
                swoosh = new Sound("resources/empty.mp3");
            }
            if(status[4] == 't') {
                die = new Sound("resources/die.mp3");
            } else {
                die = new Sound("resources/empty.mp3");
            }
        } catch(Exception e) {
            //nothing
        }
    }

    private void setBird() {
        int x = birds.indexOf("2");
        String color = "";
        switch(x) {
            case 0:
                color = "gold";
                break;
            case 1:
                color = "yellow";
                break;
            case 2:
                color = "blue";
                break;
            case 3:
                color = "red";
                break;
            default:
                break;
        }
        bird = new Bird(color);
        birdSprite = bird.getBird();
        birdSprite.render(gc);
    }

    private void setFloor() {
        firstFloor = new Sprite();
        firstFloor.resizeImage("sample/floor.png", 400, 140);
        firstFloor.setPositionXY(0, APP_HEIGHT - 100);
        firstFloor.setVelocity(- 0.4, 0);
        firstFloor.render(birdGC);

        secondFloor = new Sprite();
        secondFloor.resizeImage("sample/floor.png", 400, 140);
        secondFloor.setPositionXY(firstFloor.getWidth(), APP_HEIGHT - 100);
        secondFloor.setVelocity(- 0.4, 0);
        secondFloor.render(gc);
    }

    private void startGame() {
        startNanoTime = new LongValue(System.nanoTime());

        timer = new AnimationTimer() {
            public void handle(long now) {
                elapsedTime = (now - startNanoTime.value) / 1000000000.0; // nanoseconds
                startNanoTime.value = now;

                gc.clearRect(0, 0, APP_WIDTH, APP_HEIGHT);
                birdGC.clearRect(0, 0, APP_WIDTH, APP_HEIGHT);
                moveFloor();
                checkTimeBetweenSpaceHits();

                if (GAME_START) {
                    renderPipes();
                    checkPipeScroll();
                    updateTotalScore();

                    if (birdHitPipe()) {
                        root.getChildren().add(gameOver);
                        stopScroll();
                        playHitSound();
                        motionTime += 0.18;
                        if (motionTime > 0.5) {
                            birdSprite.addVelocity(-200, 400);
                            birdSprite.render(gc);
                            birdSprite.update(elapsedTime);
                            motionTime = 0;
                        }
                    }

                    if (birdHitFloor() || birdHitCeiling()) {
                        if (!root.getChildren().contains(gameOver)) {
                            root.getChildren().add(gameOver);
                            playHitSound();
                            showHitEffect();
                        }
                        timer.stop();
                        GAME_OVER = true;
                        die.playClip();
                    }
                    if(GAME_OVER) {
                        TOTAL_COINS += TOTAL_SCORE;
                    }
                }
            }
        };
        timer.start();
    }

    private void startNewGame() {
        root.getChildren().remove(gameOver);
        root.getChildren().add(startGame);
        pipes.clear();
        setFloor();
        setPipes();
        setBird();
        resetVariables();
        startGame();
    }

    private void resetVariables() {
        updateScoreLabel(0);
        TOTAL_SCORE = 0;
        HIT_PIPE = false;
        CLICKED = false;
        GAME_OVER = false;
        GAME_START = false;
    }

    private void checkTimeBetweenSpaceHits() {
        long difference = (System.currentTimeMillis() - spaceClickA) / 300;

        if (difference >= .001 && CLICKED) {
            CLICKED = false;
            birdSprite.addVelocity(0, 800);
            birdSprite.render(birdGC);
            birdSprite.update(elapsedTime);
        } else {
            animateBird();
        }
    }

    private void updateTotalScore() {
        if (!HIT_PIPE) {
            for (Pipe pipe : pipes) {
                if (pipe.getPipe().getPositionX() == birdSprite.getPositionX()) {
                    updateScoreLabel(++TOTAL_SCORE);
                    coin.playClip();
                    break;
                }
            }
        }
    }

    private void updateScoreLabel(int score) {
        scoreLabel.setText(Integer.toString(score));
    }

    private void moveFloor() {
        firstFloor.render(gc);
        secondFloor.render(gc);
        firstFloor.update(5);
        secondFloor.update(5);
        if (firstFloor.getPositionX() <= -APP_WIDTH) {
            firstFloor.setPositionXY(secondFloor.getPositionX() + secondFloor.getWidth(), APP_HEIGHT - 100);
        } else if (secondFloor.getPositionX() <= -APP_WIDTH) {
            secondFloor.setPositionXY(firstFloor.getPositionX() + firstFloor.getWidth(), APP_HEIGHT - 100);
        }
    }

    private void animateBird() {
        birdSprite.render(birdGC);
        birdSprite.update(elapsedTime);

        motionTime += 0.18;
        if (motionTime > 0.5 && CLICKED) {
            Sprite temp = birdSprite;
            birdSprite = bird.animate();
            birdSprite.setPositionXY(temp.getPositionX(), temp.getPositionY());
            birdSprite.setVelocity(temp.getVelocityX(), temp.getVelocityY());
            motionTime = 0;
        }
    }

    private boolean birdHitPipe() {
        for (Pipe pipe : pipes) {
            if (!HIT_PIPE && birdSprite.intersectsSprite(pipe.getPipe())) {
                HIT_PIPE = true;
                showHitEffect();
                return true;
            }
        }
        return false;
    }

    private void showHitEffect() {
        ParallelTransition parallelTransition = new ParallelTransition();
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(0.10), root);
        fadeTransition.setToValue(0);
        fadeTransition.setCycleCount(2);
        fadeTransition.setAutoReverse(true);
        parallelTransition.getChildren().add(fadeTransition);
        parallelTransition.play();
    }

    private void playHitSound() {
        hit.playClip();
    }

    private boolean birdHitFloor() {
        return birdSprite.intersectsSprite(firstFloor) ||
                birdSprite.intersectsSprite(secondFloor) ||
                birdSprite.getPositionX() < 0;
    }

    private boolean birdHitCeiling() {
        return birdSprite.getPositionY() <= 0;
    }

    private void stopScroll() {
        for (Pipe pipe : pipes) {
            pipe.getPipe().setVelocity(0, 0);
        }
        firstFloor.setVelocity(0, 0);
        secondFloor.setVelocity(0, 0);
    }

    private void checkPipeScroll() {
        if (pipes.size() > 0) {
            Sprite p = pipes.get(pipes.size() - 1).getPipe();
            if (p.getPositionX() == APP_WIDTH / 2.0 - 80) {
                setPipes();
            } else if (p.getPositionX() <= -p.getWidth()) {
                pipes.remove(0);
                pipes.remove(0);
            }
        }
    }

    private void setPipes() {
        int height = getRandomPipeHeight();

        Pipe pipe = new Pipe(true, height, "" + (Pipes.indexOf("2")));
        Pipe downPipe = new Pipe(false, 425 - height, "" + (Pipes.indexOf("2")));

        pipe.getPipe().setVelocity(- 0.4, 0);
        downPipe.getPipe().setVelocity(- 0.4, 0);

        pipe.getPipe().render(gc);
        downPipe.getPipe().render(gc);

        pipes.addAll(Arrays.asList(pipe, downPipe));
    }

    private int getRandomPipeHeight() {
        return (int) (Math.random() * (410 - 25)) + 25;
    }

    private void renderPipes() {
        for(Pipe pipe : pipes) {
            Sprite p = pipe.getPipe();
            p.render(gc);
            p.update(5);
        }
    }

    public static class LongValue {
        public long value;

        public LongValue(long i) {
            this.value = i;
        }
    }

    private void loadVariables() {
        try {
            File file = new File("src/sample/inventory.txt");
            Scanner scan = new Scanner(file);
            birds = scan.next();
            Pipes = scan.next();
            back = scan.next();
            scan.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}

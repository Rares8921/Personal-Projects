package com.example.tetris;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class Tetris extends Application {

    public static final int MOVE = 25;
    public static final int SIZE = 25;
    public static int X_MAX = SIZE * 12;
    public static int Y_MAX = SIZE * 24;
    public static int[][] tetrisMatr = new int[X_MAX / SIZE][Y_MAX / SIZE];
    static Pane layout = new Pane();
    private static final Scene scene = new Scene(layout, X_MAX + 150, Y_MAX);
    // The score will increase when a line is removed or when the player pushes faster the object
    protected static int score = 0;
    private static boolean gameIsRunning = true;
    private static Form nextObject = Controller.makeObject(), currentObject;
    private static int numberOfLines = 0;
    private static int objectTop = 0; // Marking the number of times when an object is at the top
    // If the number is two, it means that two object of different colors are at the same top coordinate so the game is over.

    private void addTitleBar() {

    }

    @Override
    public void start(Stage stage) {
        addTitleBar();

        for(int[] v : tetrisMatr) {
            java.util.Arrays.fill(v, 0);
        }

        // The line between the game and the display of the next shape
        Line line = new Line(X_MAX, 0, X_MAX, Y_MAX);
        Text scoreText = new Text("Score: ");
        scoreText.setStyle("-fx-font: 20 consolas;");
        scoreText.setX(X_MAX + 5); scoreText.setY(50);
        Text linesText = new Text("Lines: ");
        linesText.setStyle("-fx-font: 20 consolas;");
        linesText.setX(X_MAX + 5); linesText.setY(100);
        linesText.setFill(Color.FORESTGREEN);
        layout.getChildren().addAll(line, scoreText, linesText);

        // Create the first block and add key listeners
        Form form = nextObject;
        moveOnKeyPress(form);
        currentObject = form;
        layout.getChildren().addAll(form.a, form.b, form.c, form.d);
        nextObject = Controller.makeObject();

        stage.setTitle("Tetris");
        stage.setScene(scene);
        stage.show();

        // Create a thread for the gameplay
        Timer gameThread = new Timer();
        TimerTask gameTask = new TimerTask() {
            @Override
            public void run() {
                // Lambda expression
                if(gameIsRunning) {
                    Platform.runLater(() -> {
                        if (currentObject.a.getY() == 0 || currentObject.b.getY() == 0 ||
                                currentObject.c.getY() == 0 || currentObject.d.getY() == 0) {
                            ++objectTop;
                        } else {
                            objectTop = 0;
                        }
                        if (objectTop == 2) {
                            // Game over text
                            Text gameOverText = new Text("Game over!");
                            gameOverText.setFill(Color.INDIANRED);
                            gameOverText.setStyle("-fx-font: 70 consolas;");
                            gameOverText.setX(10);
                            gameOverText.setY(250);
                            layout.getChildren().add(gameOverText);
                            gameIsRunning = false;
                        }
                        if (objectTop == 10) {
                            System.exit(0);
                        }
                        if (gameIsRunning) {
                            moveDown(currentObject);
                            scoreText.setText("Score: " + score);
                            linesText.setText("Lines: " + numberOfLines);
                        }
                    });
                }
            }
        };
        gameThread.schedule(gameTask, 0, 300);
    }

    private void moveOnKeyPress(Form form) {
        scene.setOnKeyPressed(keyEvent -> {
            if(!gameIsRunning) {
                return;
            }
            switch(keyEvent.getCode()) {
                case RIGHT:
                    Controller.moveRight(form);
                    break;
                case LEFT:
                    Controller.moveLeft(form);
                    break;
                case DOWN:
                    moveDown(form);
                    ++score;
                    break;
                case UP:
                    turnForm(form);
                    break;
                default:
                    break;
            }
        });
    }

    private void removeRows(Pane layout) {
        ArrayList<Node> rectangles = new ArrayList<>(), newRectangles = new ArrayList<>();
        ArrayList<Integer> lines = new ArrayList<>();
        for(int i = 0; i < tetrisMatr[0].length; ++i) {
            int elementsOnColumn = 0;
            for (int[] ints : tetrisMatr) {
                if (ints[i] == 1) {
                    ++elementsOnColumn;
                }
            }
            if(elementsOnColumn == tetrisMatr.length) {
                lines.add(i);
            }
        }
        if(!lines.isEmpty()) {
            do {
                for(Node node : layout.getChildren()) {
                    if (node instanceof Rectangle) {
                        rectangles.add(node);
                    }
                }
                score += 50;
                ++numberOfLines;
                for(Node node : rectangles) {
                    Rectangle rectangle = (Rectangle) node;
                    if(rectangle.getY() == lines.get(0) * SIZE) {
                        tetrisMatr[(int) rectangle.getX() / SIZE][(int) rectangle.getY() / SIZE] = 0;
                        layout.getChildren().remove(node);
                    } else {
                        newRectangles.add(node);
                    }
                }
                for(Node node : newRectangles) {
                    Rectangle rectangle = (Rectangle) node;
                    if(rectangle.getY() < lines.get(0) * SIZE) {
                        tetrisMatr[(int) rectangle.getX() / SIZE][(int) rectangle.getY() / SIZE] = 0;
                        rectangle.setY(rectangle.getY() + SIZE);
                    }
                }
                lines.remove(0);
                rectangles.clear();
                newRectangles.clear();
                for(Node node : layout.getChildren()) {
                    if(node instanceof Rectangle) {
                        rectangles.add(node);
                    }
                }
                for(Node node : rectangles) {
                    Rectangle a = (Rectangle) node;
                    try {
                        tetrisMatr[(int) a.getX() / SIZE][(int) a.getY() / SIZE] = 1;
                    } catch (ArrayIndexOutOfBoundsException e) {
                        e.printStackTrace(System.err);
                    }
                }
                rectangles.clear();
            } while(!lines.isEmpty());
        }
    }

    // Trying to move up an object based on its position
    // Moreover, the verifications imply that the color must match and the next position should be available
    // So, for every possible color, further checking about the availability is necessary to move up the current object.
    // The possible forms are described in Form.java
    private void turnForm(Form form) {
        int shapeNumber = form.shapeNumber;
        Rectangle a = form.a, b = form.b, c = form.c, d = form.d;
        switch(form.getColorName()) {
            case "Gray":
                if(shapeNumber == 1 && checkRectangle(a, 1, -1) && checkRectangle(c, -1, -1) && checkRectangle(d, -2, -2)) {
                    moveRight(form.a);
                    moveDown(form.a);
                    moveDown(form.c);
                    moveLeft(form.c);
                    moveDown(form.d);
                    moveDown(form.d);
                    moveLeft(form.d);
                    moveLeft(form.d);
                    form.changeShape();
                }
                if(shapeNumber == 2 && checkRectangle(a, -1, -1) && checkRectangle(c, -1, 1) && checkRectangle(d, -2, 2)) {
                    moveDown(form.a);
                    moveLeft(form.a);
                    moveLeft(form.c);
                    moveUp(form.c);
                    moveLeft(form.d);
                    moveLeft(form.d);
                    moveUp(form.d);
                    moveUp(form.d);
                    form.changeShape();
                }
                if (shapeNumber == 3 && checkRectangle(a, -1, 1) && checkRectangle(c, 1, 1) && checkRectangle(d, 2, 2)) {
                    moveLeft(form.a);
                    moveUp(form.a);
                    moveUp(form.c);
                    moveRight(form.c);
                    moveUp(form.d);
                    moveUp(form.d);
                    moveRight(form.d);
                    moveRight(form.d);
                    form.changeShape();
                }
                if (shapeNumber == 4 && checkRectangle(a, 1, 1) && checkRectangle(c, 1, -1) && checkRectangle(d, 2, -2)) {
                    moveUp(form.a);
                    moveRight(form.a);
                    moveRight(form.c);
                    moveDown(form.c);
                    moveRight(form.d);
                    moveRight(form.d);
                    moveDown(form.d);
                    moveDown(form.d);
                    form.changeShape();
                }
                break;
            case "Red":
                if (shapeNumber == 1 && checkRectangle(a, 1, -1) && checkRectangle(c, 1, 1) && checkRectangle(b, 2, 2)) {
                    moveRight(form.a);
                    moveDown(form.a);
                    moveUp(form.c);
                    moveRight(form.c);
                    moveUp(form.b);
                    moveUp(form.b);
                    moveRight(form.b);
                    moveRight(form.b);
                    form.changeShape();
                }
                if (shapeNumber == 2 && checkRectangle(a, -1, -1) && checkRectangle(b, 2, -2) && checkRectangle(c, 1, -1)) {
                    moveDown(form.a);
                    moveLeft(form.a);
                    moveRight(form.b);
                    moveRight(form.b);
                    moveDown(form.b);
                    moveDown(form.b);
                    moveRight(form.c);
                    moveDown(form.c);
                    form.changeShape();
                }
                if (shapeNumber == 3 && checkRectangle(a, -1, 1) && checkRectangle(c, -1, -1) && checkRectangle(b, -2, -2)) {
                    moveLeft(form.a);
                    moveUp(form.a);
                    moveDown(form.c);
                    moveLeft(form.c);
                    moveDown(form.b);
                    moveDown(form.b);
                    moveLeft(form.b);
                    moveLeft(form.b);
                    form.changeShape();
                }
                if (shapeNumber == 4 && checkRectangle(a, 1, 1) && checkRectangle(b, -2, 2) && checkRectangle(c, -1, 1)) {
                    moveUp(form.a);
                    moveRight(form.a);
                    moveLeft(form.b);
                    moveLeft(form.b);
                    moveUp(form.b);
                    moveUp(form.b);
                    moveLeft(form.c);
                    moveUp(form.c);
                    form.changeShape();
                }
                break;
            case "Gold":
                break;
            case "Green":
                if (shapeNumber == 1 && checkRectangle(a, -1, -1) && checkRectangle(c, -1, 1) && checkRectangle(d, 0, 2)) {
                    moveDown(form.a);
                    moveLeft(form.a);
                    moveLeft(form.c);
                    moveUp(form.c);
                    moveUp(form.d);
                    moveUp(form.d);
                    form.changeShape();
                }
                if (shapeNumber == 2 && checkRectangle(a, 1, 1) && checkRectangle(c, 1, -1) && checkRectangle(d, 0, -2)) {
                    moveUp(form.a);
                    moveRight(form.a);
                    moveRight(form.c);
                    moveDown(form.c);
                    moveDown(form.d);
                    moveDown(form.d);
                    form.changeShape();
                }
                if (shapeNumber == 3 && checkRectangle(a, -1, -1) && checkRectangle(c, -1, 1) && checkRectangle(d, 0, 2)) {
                    moveDown(form.a);
                    moveLeft(form.a);
                    moveLeft(form.c);
                    moveUp(form.c);
                    moveUp(form.d);
                    moveUp(form.d);
                    form.changeShape();
                }
                if (shapeNumber == 4 && checkRectangle(a, 1, 1) && checkRectangle(c, 1, -1) && checkRectangle(d, 0, -2)) {
                    moveUp(form.a);
                    moveRight(form.a);
                    moveRight(form.c);
                    moveDown(form.c);
                    moveDown(form.d);
                    moveDown(form.d);
                    form.changeShape();
                }
                break;
            case "Blue":
                if (shapeNumber == 1 && checkRectangle(a, 1, 1) && checkRectangle(d, -1, -1) && checkRectangle(c, -1, 1)) {
                    moveUp(form.a);
                    moveRight(form.a);
                    moveDown(form.d);
                    moveLeft(form.d);
                    moveLeft(form.c);
                    moveUp(form.c);
                    form.changeShape();
                }
                if (shapeNumber == 2 && checkRectangle(a, 1, -1) && checkRectangle(d, -1, 1) && checkRectangle(c, 1, 1)) {
                    moveRight(form.a);
                    moveDown(form.a);
                    moveLeft(form.d);
                    moveUp(form.d);
                    moveUp(form.c);
                    moveRight(form.c);
                    form.changeShape();
                }
                if (shapeNumber == 3 && checkRectangle(a, -1, -1) && checkRectangle(d, 1, 1) && checkRectangle(c, 1, -1)) {
                    moveDown(form.a);
                    moveLeft(form.a);
                    moveUp(form.d);
                    moveRight(form.d);
                    moveRight(form.c);
                    moveDown(form.c);
                    form.changeShape();
                }
                if (shapeNumber == 4 && checkRectangle(a, -1, 1) && checkRectangle(d, 1, -1) && checkRectangle(c, -1, -1)) {
                    moveLeft(form.a);
                    moveUp(form.a);
                    moveRight(form.d);
                    moveDown(form.d);
                    moveDown(form.c);
                    moveLeft(form.c);
                    form.changeShape();
                }
                break;
            case "Pink":
                if (shapeNumber == 1 && checkRectangle(b, 1, 1) && checkRectangle(c, -1, 1) && checkRectangle(d, -2, 0)) {
                    moveUp(form.b);
                    moveRight(form.b);
                    moveLeft(form.c);
                    moveUp(form.c);
                    moveLeft(form.d);
                    moveLeft(form.d);
                    form.changeShape();
                }
                if (shapeNumber == 2 && checkRectangle(b, -1, -1) && checkRectangle(c, 1, -1) && checkRectangle(d, 2, 0)) {
                    moveDown(form.b);
                    moveLeft(form.b);
                    moveRight(form.c);
                    moveDown(form.c);
                    moveRight(form.d);
                    moveRight(form.d);
                    form.changeShape();
                }
                if (shapeNumber == 3 && checkRectangle(b, 1, 1) && checkRectangle(c, -1, 1) && checkRectangle(d, -2, 0)) {
                    moveUp(form.b);
                    moveRight(form.b);
                    moveLeft(form.c);
                    moveUp(form.c);
                    moveLeft(form.d);
                    moveLeft(form.d);
                    form.changeShape();
                }
                if (shapeNumber == 4 && checkRectangle(b, -1, -1) && checkRectangle(c, 1, -1) && checkRectangle(d, 2, 0)) {
                    moveDown(form.b);
                    moveLeft(form.b);
                    moveRight(form.c);
                    moveDown(form.c);
                    moveRight(form.d);
                    moveRight(form.d);
                    form.changeShape();
                }
                break;
            case "Brown":
                if (shapeNumber == 1 && checkRectangle(a, 2, 2) && checkRectangle(b, 1, 1) && checkRectangle(d, -1, -1)) {
                    moveUp(form.a);
                    moveUp(form.a);
                    moveRight(form.a);
                    moveRight(form.a);
                    moveUp(form.b);
                    moveRight(form.b);
                    moveDown(form.d);
                    moveLeft(form.d);
                    form.changeShape();
                }
                if (shapeNumber == 2 && checkRectangle(a, -2, -2) && checkRectangle(b, -1, -1) && checkRectangle(d, 1, 1)) {
                    moveDown(form.a);
                    moveDown(form.a);
                    moveLeft(form.a);
                    moveLeft(form.a);
                    moveDown(form.b);
                    moveLeft(form.b);
                    moveUp(form.d);
                    moveRight(form.d);
                    form.changeShape();
                }
                if (shapeNumber == 3 && checkRectangle(a, 2, 2) && checkRectangle(b, 1, 1) && checkRectangle(d, -1, -1)) {
                    moveUp(form.a);
                    moveUp(form.a);
                    moveRight(form.a);
                    moveRight(form.a);
                    moveUp(form.b);
                    moveRight(form.b);
                    moveDown(form.d);
                    moveLeft(form.d);
                    form.changeShape();
                }
                if (shapeNumber == 4 && checkRectangle(a, -2, -2) && checkRectangle(b, -1, -1) && checkRectangle(d, 1, 1)) {
                    moveDown(form.a);
                    moveDown(form.a);
                    moveLeft(form.a);
                    moveLeft(form.a);
                    moveDown(form.b);
                    moveLeft(form.b);
                    moveUp(form.d);
                    moveRight(form.d);
                    form.changeShape();
                }
                break;
            default:
                System.err.print("The color was not found!\n");
                break;
        }
    }

    private void moveDown(Form form) {
        if (form.a.getY() == Y_MAX - SIZE || form.b.getY() == Y_MAX - SIZE || form.c.getY() == Y_MAX - SIZE
                || form.d.getY() == Y_MAX - SIZE || moveA(form) || moveB(form) || moveC(form) || moveD(form)) {
            tetrisMatr[(int) form.a.getX() / SIZE][(int) form.a.getY() / SIZE] = 1;
            tetrisMatr[(int) form.b.getX() / SIZE][(int) form.b.getY() / SIZE] = 1;
            tetrisMatr[(int) form.c.getX() / SIZE][(int) form.c.getY() / SIZE] = 1;
            tetrisMatr[(int) form.d.getX() / SIZE][(int) form.d.getY() / SIZE] = 1;
            removeRows(layout);

            Form a = nextObject;
            nextObject = Controller.makeObject();
            currentObject = a;
            layout.getChildren().addAll(a.a, a.b, a.c, a.d);
            moveOnKeyPress(a);
        }

        if (form.a.getY() + MOVE < Y_MAX && form.b.getY() + MOVE < Y_MAX && form.c.getY() + MOVE < Y_MAX
                && form.d.getY() + MOVE < Y_MAX) {
            int moveA = tetrisMatr[(int) form.a.getX() / SIZE][((int) form.a.getY() / SIZE) + 1];
            int moveB = tetrisMatr[(int) form.b.getX() / SIZE][((int) form.b.getY() / SIZE) + 1];
            int moveC = tetrisMatr[(int) form.c.getX() / SIZE][((int) form.c.getY() / SIZE) + 1];
            int moveD = tetrisMatr[(int) form.d.getX() / SIZE][((int) form.d.getY() / SIZE) + 1];
            if (moveA == 0 && moveA == moveB && moveB == moveC && moveC == moveD) {
                form.a.setY(form.a.getY() + MOVE);
                form.b.setY(form.b.getY() + MOVE);
                form.c.setY(form.c.getY() + MOVE);
                form.d.setY(form.d.getY() + MOVE);
            }
        }
    }

    private void moveDown(Rectangle rect) {
        if(rect.getY() + MOVE < Y_MAX) {
            rect.setY(rect.getY() + MOVE);
        }
    }

    private void moveUp(Rectangle rect) {
        if(rect.getY() - MOVE > 0) {
            rect.setY(rect.getY() - MOVE);
        }
    }

    private void moveLeft(Rectangle rect) {
        if(rect.getX() - MOVE >= 0) {
            rect.setX(rect.getX() - MOVE);
        }
    }

    private void moveRight(Rectangle rect) {
        if(rect.getX() + MOVE <= X_MAX - SIZE) {
            rect.setX(rect.getX() + MOVE);
        }
    }

    // Check if the rectangle can move upwards the specified coordinates
    // That means to be inside the grid and to occupy an empty slot
    private boolean checkRectangle(Rectangle rect, int x, int y) {
        boolean validX = (x >= 0 ? rect.getX() + x * MOVE <= X_MAX - SIZE : rect.getX() + x * MOVE > 0);
        boolean validY = (y >= 0 ? rect.getY() + y * MOVE > 0 : rect.getY() + y * MOVE < Y_MAX);
        return validX && validY && tetrisMatr[(int)rect.getX() / SIZE + x][(int) rect.getY() / SIZE - y] == 0;
    }

    // Check if it is tried to move a rectangle to a marked position.
    private boolean moveA(Form form) {
        return (tetrisMatr[(int) form.a.getX() / SIZE][((int) form.a.getY() / SIZE) + 1] == 1);
    }

    private boolean moveB(Form form) {
        return (tetrisMatr[(int) form.b.getX() / SIZE][((int) form.b.getY() / SIZE) + 1] == 1);
    }

    private boolean moveC(Form form) {
        return (tetrisMatr[(int) form.c.getX() / SIZE][((int) form.c.getY() / SIZE) + 1] == 1);
    }

    private boolean moveD(Form form) {
        return (tetrisMatr[(int) form.d.getX() / SIZE][((int) form.d.getY() / SIZE) + 1] == 1);
    }

    public static void main(String[] args) {
        launch();
    }
}
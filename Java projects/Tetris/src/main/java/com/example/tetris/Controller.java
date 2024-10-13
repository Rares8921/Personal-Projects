package com.example.tetris;

import javafx.scene.shape.Rectangle;

public class Controller {

    public static final int MOVE = Tetris.MOVE;
    public static final int SIZE = Tetris.SIZE;
    public static int X_MAX = Tetris.X_MAX;
    public static int[][] tetrisMatr = Tetris.tetrisMatr;

    // Try to move every square from the shape in the right direction
    // If the move is possible for all squares, then perform the action
    public static void moveRight(Form form) {
        if(form.a.getX() + MOVE <= X_MAX - SIZE && form.b.getX() + MOVE <= X_MAX - SIZE &&
           form.c.getX() + MOVE <= X_MAX - SIZE && form.d.getX() + MOVE <= X_MAX - SIZE)
        {
            int moveA = tetrisMatr[(int) form.a.getX() / SIZE + 1][(int) form.a.getY() / SIZE];
            int moveB = tetrisMatr[(int) form.b.getX() / SIZE + 1][(int) form.b.getY() / SIZE];
            int moveC = tetrisMatr[(int) form.c.getX() / SIZE + 1][(int) form.c.getY() / SIZE];
            int moveD = tetrisMatr[(int) form.d.getX() / SIZE + 1][(int) form.d.getY() / SIZE];
            if(moveA == 0 && moveA == moveB && moveB == moveC && moveC == moveD) {
                form.a.setX(form.a.getX() + MOVE);
                form.b.setX(form.b.getX() + MOVE);
                form.c.setX(form.c.getX() + MOVE);
                form.d.setX(form.d.getX() + MOVE);
            }
        }
    }

    // Same logic for the left move
    public static void moveLeft(Form form) {
        if(form.a.getX() - MOVE >= 0 && form.b.getX() - MOVE >= 0 &&
           form.c.getX() - MOVE >= 0 && form.d.getX() - MOVE >= 0)
        {
            int moveA = tetrisMatr[(int) form.a.getX() / SIZE - 1][(int) form.a.getY() / SIZE];
            int moveB = tetrisMatr[(int) form.b.getX() / SIZE - 1][(int) form.b.getY() / SIZE];
            int moveC = tetrisMatr[(int) form.c.getX() / SIZE - 1][(int) form.c.getY() / SIZE];
            int moveD = tetrisMatr[(int) form.d.getX() / SIZE - 1][(int) form.d.getY() / SIZE];
            if(moveA == 0 && moveA == moveB && moveB == moveC && moveC == moveD) {
                form.a.setX(form.a.getX() - MOVE);
                form.b.setX(form.b.getX() - MOVE);
                form.c.setX(form.c.getX() - MOVE);
                form.d.setX(form.d.getX() - MOVE);
            }
        }
    }

    // Creating the objects
    public static Form makeObject() {
        // Select a random color and arandom shape for the object
        int shapeCode = (int) (Math.random() * 100);
        String colorName = "";
        Rectangle a = new Rectangle(SIZE - 1, SIZE - 1);
        Rectangle b = new Rectangle(SIZE - 1, SIZE - 1);
        Rectangle c = new Rectangle(SIZE - 1, SIZE - 1);
        Rectangle d = new Rectangle(SIZE - 1, SIZE - 1);
        // Now adjust the position accordingly (note: the shapes description are inside Form.java)
        if(shapeCode < 15) {
            a.setX(X_MAX / 2.0 - SIZE);
            b.setX(X_MAX / 2.0 - SIZE); b.setY(SIZE);
            c.setX(X_MAX / 2.0); c.setY(SIZE);
            d.setX(X_MAX / 2.0 + SIZE); d.setY(SIZE);
            colorName = "Gray";
        } else if(shapeCode < 30) {
            a.setX(X_MAX / 2.0 + SIZE);
            b.setX(X_MAX / 2.0 - SIZE); b.setY(SIZE);
            c.setX(X_MAX / 2.0); c.setY(SIZE);
            d.setX(X_MAX / 2.0 + SIZE); d.setY(SIZE);
            colorName = "Red";
        } else if(shapeCode < 45) {
            a.setX(X_MAX / 2.0 - SIZE);
            b.setX(X_MAX / 2.0);
            c.setX(X_MAX / 2.0 - SIZE); c.setY(SIZE);
            d.setX(X_MAX / 2.0); d.setY(SIZE);
            colorName = "Gold";
        } else if(shapeCode < 60) {
            a.setX(X_MAX / 2.0 + SIZE);
            b.setX(X_MAX / 2.0);
            c.setX(X_MAX / 2.0); c.setY(SIZE);
            d.setX(X_MAX / 2.0 - SIZE); d.setY(SIZE);
            colorName = "Green";
        } else if(shapeCode < 75) {
            a.setX(X_MAX / 2.0 - SIZE);
            b.setX(X_MAX / 2.0);
            c.setX(X_MAX / 2.0); c.setY(SIZE);
            d.setX(X_MAX / 2.0 + SIZE);
            colorName = "Blue";
        }  else if(shapeCode < 90) {
            a.setX(X_MAX / 2.0 + SIZE);
            b.setX(X_MAX / 2.0);
            c.setX(X_MAX / 2.0 + SIZE); c.setY(SIZE);
            d.setX(X_MAX / 2.0 + 2 * SIZE); d.setY(SIZE);
            colorName = "Pink";
        } else {
            a.setX(X_MAX / 2.0 - 2 * SIZE);
            b.setX(X_MAX / 2.0 - SIZE);
            c.setX(X_MAX / 2.0);
            d.setX(X_MAX / 2.0 + SIZE);
            colorName = "Brown";
        }
        return new Form(a, b, c, d, colorName);
    }
}
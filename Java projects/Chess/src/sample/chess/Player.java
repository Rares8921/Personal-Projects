package sample.chess;

import javafx.scene.paint.Color;

public class Player {
    private Color  color;

    //CONSTRUCTOR
    public Player(String name, Color color) {
        this.color = color;
    }

    //METHODS
    //returns true if the player plays his own color, if not returns false.
    public Boolean checkMove(Move m) {
        return m.getPiece().getColor() == color;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

}

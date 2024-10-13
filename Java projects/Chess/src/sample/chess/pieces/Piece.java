package sample.chess.pieces;

import sample.chess.Game;
import sample.chess.Move;
import javafx.scene.paint.Color;

import java.util.Objects;

public abstract class Piece {
    private final Color color;
    private boolean hasMoved = false;


    //CONSTRUCTOR
    protected Piece (Color color) {
        PieceManager.addObject(this);
        this.color = color;
    }

    //METHODS
    //the end field has to be empty, or it has to contain a piece with a different color.
    public boolean checkMove(Game g, Move m) {
        return  !m.endPiecePresent() || Objects.requireNonNull(m.getEnd().getPiece().orElse(null)).getColor() != getColor();
    }

    public abstract Piece makeCopy();

    //GETTERS & SETTERS
    public Color getColor() {
        return color;
    }

    public boolean hasMoved() {
        return hasMoved;
    }


    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }
}

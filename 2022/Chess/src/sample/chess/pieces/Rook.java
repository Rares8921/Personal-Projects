package sample.chess.pieces;

import static sample.chess.util.Movement.straight;

import sample.chess.Game;
import sample.chess.Move;
import javafx.scene.paint.Color;

public class Rook extends Piece {

    //CONSTRUCTOR
    public Rook(Color color) {
        super(color);
    }

    //METHODS
    public boolean checkMove(Game g, Move m) {
        return super.checkMove(g, m) && straight.test(m);
    }

    @Override
    public Piece makeCopy() {
        return new Rook(getColor());

    }
}
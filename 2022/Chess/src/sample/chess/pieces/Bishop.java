package sample.chess.pieces;

import static sample.chess.util.Movement.diagonal;

import sample.chess.Game;
import sample.chess.Move;
import javafx.scene.paint.Color;

public class Bishop extends Piece {

    //CONSTUCTOR
    public Bishop(Color color) {
        super(color);
    }

    //METHODS
    public boolean checkMove(Game g, Move m) {
        return super.checkMove(g, m) && diagonal.test(m);
    }

    @Override
    public Piece makeCopy() {
        return new Bishop(getColor());
    }

}
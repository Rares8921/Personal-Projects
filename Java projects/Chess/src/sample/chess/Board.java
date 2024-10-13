package sample.chess;

import static sample.chess.util.Converter.modelPieceAtIndexes;
import java.util.Optional;

import sample.chess.pieces.Piece;

public class Board {

    private final Field[][] playingField = new Field[8][8];

    //CONSTRUCTOR
    public Board () {
        this.set();
    }

    //METHODS
    /*
     * creates all fields for the board and sets the pieces or null on empty fields.
     */
    private void set() {
        for(int x=0; x<8; x++){
            for(int y=0; y<8; y++){
                playingField[x][y] = new Field (x, y);
                setPiece(x, y, modelPieceAtIndexes.apply(x, y).orElse(null));
            }
        }
    }

    //GETTERS & SETTERS
    public Field[][] getBoardState() {
        return playingField;
    }

    public Optional<Piece> getPiece(int x, int y) {
        return playingField[x][y].getPiece();
    }

    public void setPiece(int x, int y, Piece piece) {
        playingField[x][y].setPiece(piece);
    }

}
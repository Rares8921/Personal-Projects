package sample.chess;

import java.util.List;

import sample.chess.pieces.Piece;

public class GameState {

    private final Move        lastMove;

    //CONSTRUCTOR
    public GameState(String gameState, Move lastMoveMade, List<Piece> takenPieces) {
        this.lastMove    = lastMoveMade;
    }

    //GETTERS & SETTERS
    public int getLastMEndX() {
        return lastMove.getEndX();
    }

    public int getLastMEndY() {
        return lastMove.getEndY();
    }

    public int getLastMStartX() {
        return lastMove.getStartX();
    }

    public Piece getPieceLastM() {
        return lastMove.getPiece();
    }

}

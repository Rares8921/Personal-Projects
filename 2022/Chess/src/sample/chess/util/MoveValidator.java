package sample.chess.util;

import static sample.chess.Game.kingSave;
import static sample.chess.Game.pathUnobstructed;
import static sample.chess.util.Movement.noRows;
import static sample.chess.util.Movement.oneRowOrOneColumnOrBoth;
import static sample.chess.util.Movement.twoColumns;
import static sample.chess.util.MoveValidator.ValidationResult.CHECK_ON_KING_CANNOT_CASTLE;
import static sample.chess.util.MoveValidator.ValidationResult.ILLEGAL_MOVE_FOR_PIECE;
import static sample.chess.util.MoveValidator.ValidationResult.KING_HAS_MOVED_CANNOT_CASTLE;
import static sample.chess.util.MoveValidator.ValidationResult.CHECK_ON_KING;
import static sample.chess.util.MoveValidator.ValidationResult.KING_MOVES_OVER_FIELD_IN_CHECK;
import static sample.chess.util.MoveValidator.ValidationResult.LEGAL_MOVE;
import static sample.chess.util.MoveValidator.ValidationResult.MOVE_ILLEGAL_MOVE;
import static sample.chess.util.MoveValidator.ValidationResult.PATH_IS_OBSTRUCTED;
import static sample.chess.util.MoveValidator.ValidationResult.PLAYING_THE_WRONG_COLOR;
import static sample.chess.util.MoveValidator.ValidationResult.ROOK_HAS_MOVED_CANNOT_CASTLE;

import java.util.Objects;
import java.util.function.BiFunction;

import sample.chess.Game;
import sample.chess.Move;
import sample.chess.pieces.King;
import sample.chess.util.MoveValidator.ValidationResult;

@FunctionalInterface
public interface MoveValidator extends BiFunction <Game, Move, ValidationResult>{

    public enum ValidationResult {
        LEGAL_MOVE,
        PATH_IS_OBSTRUCTED,
        CHECK_ON_KING,
        ILLEGAL_MOVE_FOR_PIECE,
        MOVE_ILLEGAL_MOVE,
        PLAYING_THE_WRONG_COLOR,
        KING_HAS_MOVED_CANNOT_CASTLE,
        CHECK_ON_KING_CANNOT_CASTLE,
        KING_MOVES_OVER_FIELD_IN_CHECK,
        ROOK_HAS_MOVED_CANNOT_CASTLE;

        @Override
        public String toString() {
            return name().substring(0, 1) + name().substring(1).replace('_', ' ').toLowerCase()+".";
        }
    }

    static MoveValidator legalForPiece() {
        return (g, m) ->  m.getPiece().checkMove(g, m) ?
                LEGAL_MOVE : reasonForKing().apply(g, m);
    }

    static MoveValidator pathUnobstructed() {
        return (g, m) -> pathUnobstructed.test(g, m) ?
                LEGAL_MOVE : PATH_IS_OBSTRUCTED;
    }

    static MoveValidator legalForPlayer() {
        return (g, m) -> g.getActivePlayer().checkMove(m) ?
                LEGAL_MOVE : PLAYING_THE_WRONG_COLOR;
    }

    static MoveValidator legalForMove() {
        return (g, m) -> m.checkMove() ?
                LEGAL_MOVE : MOVE_ILLEGAL_MOVE;
    }

    static MoveValidator kingSave() {
        return (g, m) -> kingSave.test(g, m) ?
                LEGAL_MOVE : CHECK_ON_KING;
    }

    static MoveValidator reasonForKing() {
        return (g, m) -> {
            if (m.getPiece() instanceof King && !((King) m.getPiece()).castling(g, m)) {
                King king = (King) m.getPiece();
                //we need this check because all further checks depends on knowing that the king makes a legal move.
                if (oneRowOrOneColumnOrBoth.test(m) || (twoColumns.test(m) && noRows.test(m))) {
                    if (! king.notInCheck.test(g))              return CHECK_ON_KING_CANNOT_CASTLE;
                    if (king.hasMoved())                        return KING_HAS_MOVED_CANNOT_CASTLE;
                    if (! king.rookEligibleToCastle.test(g, m)) return ROOK_HAS_MOVED_CANNOT_CASTLE;
                    if (! king.kingEligibleToCastle.test(g, m)) return KING_MOVES_OVER_FIELD_IN_CHECK;
                }
            }
            return ILLEGAL_MOVE_FOR_PIECE;
        };
    }

    default MoveValidator and (MoveValidator other) {
        //fail safe
        Objects.requireNonNull(other);
        return (g, m) -> {
            ValidationResult result = this.apply(g, m);
            return result.equals(LEGAL_MOVE) ? other.apply(g, m) : result;
        };
    }

}

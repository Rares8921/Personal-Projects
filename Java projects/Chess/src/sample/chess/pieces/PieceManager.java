package sample.chess.pieces;

import java.util.ArrayList;
import java.util.List;

public class PieceManager {
    private static final List<Piece> objects = new ArrayList<>();

    public static void addObject(Piece obj) {
        objects.add(obj);
    }

    public static List<Piece> getObjects() {
        return objects;
    }
}

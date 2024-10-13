package sample.chess.pieces;

import java.lang.reflect.Field;

import java.util.List;

public class PieceResetter {
    public static void resetAllObjects(Class<?> clazz) {
        List<Piece> instances = PieceManager.getObjects(); // Get all instances

        for (Piece obj : instances) {
            try {
                // Use reflection to reset fields
                for (Field field : clazz.getDeclaredFields()) {
                    field.setAccessible(true); // Make private field accessible
                    if (field.getType() == boolean.class) {
                        field.setBoolean(obj, false); // Reset hasMoved state
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace(System.out);
            }
        }
    }
}

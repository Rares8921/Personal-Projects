package sample.chess.util;

import java.util.function.Predicate;

import sample.chess.Move;


public final class Movement {

    public static Predicate<Move> noRows =  m -> m.getEndX() - m.getStartX() == 0;

    public static Predicate<Move> noColumns =  m -> m.getEndY() - m.getStartY() == 0;

    public static Predicate<Move> diagonal =  m -> m.numberOfRows() == m.numberOfColumns();

    public static Predicate<Move> straight =  m -> m.numberOfRows() == 0 ^ m.numberOfColumns() == 0;

    public static Predicate<Move> jump =  m -> m.numberOfRows() == 2 && m.numberOfColumns() == 1
            || m.numberOfRows() == 1 && m.numberOfColumns() == 2;

    public static Predicate<Move> oneRow =  m -> m.numberOfRows() == 1;

    public static Predicate<Move> oneColumn =  m -> m.numberOfColumns() == 1;

    public static Predicate<Move> twoRows =  m -> m.numberOfRows() == 2;

    public static Predicate<Move> twoColumns =  m -> m.numberOfColumns() == 2;

    public static Predicate<Move> oneRowOrOneColumnOrBoth = m -> m.numberOfRows() <= 1 &&  m.numberOfColumns() <= 1;

    public static Predicate<Move> left = m -> m.getEndY() - m.getStartY() >= 1;
    public static Predicate<Move> right = m -> m.getStartY() - m.getEndY() >= 1;
    public static Predicate<Move> up = m -> m.getEndX() - m.getStartX() >= 1;
    public static Predicate<Move> down = m -> m.getStartX() - m.getEndX() >= 1;
}

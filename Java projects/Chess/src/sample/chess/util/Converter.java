package sample.chess.util;

import javafx.util.Pair;
import sample.chess.pieces.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static javafx.scene.paint.Color.BLACK;
import static javafx.scene.paint.Color.WHITE;

public final class Converter {

    private static final Map<Piece, List<String>> FEN_MAP = new HashMap<>();
    private static final Map<List<Pair<Integer, Integer>>,Pair<String, Piece>> PIECES = new HashMap<>();

    private Converter() {}

    static {
        try (
            FileInputStream whiteKingNormal = new FileInputStream("src/sample/resources/images/normal/whiteKing.png");
            FileInputStream whiteKingBig = new FileInputStream("src/sample/resources/images/big/whiteKing2.png");
            FileInputStream whiteQueenNormal = new FileInputStream("src/sample/resources/images/normal/whiteQueen.png");
            FileInputStream whiteQueenBig = new FileInputStream("src/sample/resources/images/big/whiteQueen2.png");
            FileInputStream whiteRookNormal = new FileInputStream("src/sample/resources/images/normal/whiteRook.png");
            FileInputStream whiteRookBig = new FileInputStream("src/sample/resources/images/big/whiteRook2.png");
            FileInputStream whiteBishopNormal = new FileInputStream("src/sample/resources/images/normal/whiteBishop.png");
            FileInputStream whiteBishopBig = new FileInputStream("src/sample/resources/images/big/whiteBishop2.png");
            FileInputStream whiteKnightNormal = new FileInputStream("src/sample/resources/images/normal/whiteKnight.png");
            FileInputStream whiteKnightBig = new FileInputStream("src/sample/resources/images/big/whiteKnight2.png");
            FileInputStream whitePawnNormal = new FileInputStream("src/sample/resources/images/normal/whitePawn.png");
            FileInputStream whitePawnBig = new FileInputStream("src/sample/resources/images/big/whitePawn2.png");
            FileInputStream blackKingNormal = new FileInputStream("src/sample/resources/images/normal/blackKing.png");
            FileInputStream blackKingBig = new FileInputStream("src/sample/resources/images/big/blackKing2.png");
            FileInputStream blackQueenNormal = new FileInputStream("src/sample/resources/images/normal/blackQueen.png");
            FileInputStream blackQueenBig = new FileInputStream("src/sample/resources/images/big/blackQueen2.png");
            FileInputStream blackRookNormal = new FileInputStream("src/sample/resources/images/normal/blackRook.png");
            FileInputStream blackRookBig = new FileInputStream("src/sample/resources/images/big/blackRook2.png");
            FileInputStream blackBishopNormal = new FileInputStream("src/sample/resources/images/normal/blackBishop.png");
            FileInputStream blackBishopBig = new FileInputStream("src/sample/resources/images/big/blackBishop2.png");
            FileInputStream blackKnightNormal = new FileInputStream("src/sample/resources/images/normal/blackKnight.png");
            FileInputStream blackKnightBig = new FileInputStream("src/sample/resources/images/big/blackKnight2.png");
            FileInputStream blackPawnNormal = new FileInputStream("src/sample/resources/images/normal/blackPawn.png");
            FileInputStream blackPawnBig = new FileInputStream("src/sample/resources/images/big/blackPawn2.png")
        ) {
            // white pieces
            FEN_MAP.put(new King(WHITE), Arrays.asList("K","♔", whiteKingNormal.toString(), whiteKingBig.toString()));
            FEN_MAP.put(new Queen(WHITE), Arrays.asList("Q","♕", whiteQueenNormal.toString(), whiteQueenBig.toString()));
            FEN_MAP.put(new Rook(WHITE), Arrays.asList("R","♖", whiteRookNormal.toString(), whiteRookBig.toString()));
            FEN_MAP.put(new Bishop(WHITE), Arrays.asList("B","♗", whiteBishopNormal.toString(), whiteBishopBig.toString()));
            FEN_MAP.put(new Knight(WHITE), Arrays.asList("N","♘", whiteKnightNormal.toString(), whiteKnightBig.toString()));
            FEN_MAP.put(new Pawn(WHITE), Arrays.asList("P","♙", whitePawnNormal.toString(), whitePawnBig.toString()));
            // black pieces
            FEN_MAP.put(new King(BLACK), Arrays.asList("k","♚", blackKingNormal.toString(), blackKingBig.toString()));
            FEN_MAP.put(new Queen(BLACK), Arrays.asList("q","♛", blackQueenNormal.toString(), blackQueenBig.toString()));
            FEN_MAP.put(new Rook(BLACK), Arrays.asList("r","♜", blackRookNormal.toString(), blackRookBig.toString()));
            FEN_MAP.put(new Bishop(BLACK), Arrays.asList("b","♝", blackBishopNormal.toString(), blackBishopBig.toString()));
            FEN_MAP.put(new Knight(BLACK), Arrays.asList("n","♞", blackKnightNormal.toString(), blackKnightBig.toString()));
            FEN_MAP.put(new Pawn(BLACK), Arrays.asList("p","♟", blackPawnNormal.toString(), blackPawnBig.toString()));
        } catch (IOException e) {
            e.printStackTrace(System.out);
        }
    }

    static {
        PIECES.put(new ArrayList<>(Collections.singletonList(new Pair<>(0, 3))),                  new Pair<>("♔", new King(WHITE)));
        PIECES.put(new ArrayList<>(Collections.singletonList(new Pair<>(7, 3))),                  new Pair<>("♚", new King(BLACK)));
        PIECES.put(new ArrayList<>(Collections.singletonList(new Pair<>(0, 4))),                  new Pair<>("♕", new Queen(WHITE)));
        PIECES.put(new ArrayList<>(Collections.singletonList(new Pair<>(7, 4))),                  new Pair<>("♛", new Queen(BLACK)));
        PIECES.put(new ArrayList<>(Arrays.asList(new Pair<>(0,0), new Pair<>(0,7))), new Pair<>("♖", new Rook(WHITE)));
        PIECES.put(new ArrayList<>(Arrays.asList(new Pair<>(7,0), new Pair<>(7,7))), new Pair<>("♜", new Rook(BLACK)));
        PIECES.put(new ArrayList<>(Arrays.asList(new Pair<>(0,2), new Pair<>(0,5))), new Pair<>("♗", new Bishop(WHITE)));
        PIECES.put(new ArrayList<>(Arrays.asList(new Pair<>(7,2), new Pair<>(7,5))), new Pair<>("♝", new Bishop(BLACK)));
        PIECES.put(new ArrayList<>(Arrays.asList(new Pair<>(0,1), new Pair<>(0,6))), new Pair<>("♘", new Knight(WHITE)));
        PIECES.put(new ArrayList<>(Arrays.asList(new Pair<>(7,1), new Pair<>(7,6))), new Pair<>("♞", new Knight(BLACK)));
    }

    /*
     * Helpmethod. Given a row - and columnindex, returns a Stream containing a pair, that has the corresponding view piece as key and the
     * corresponding model piece as value.
     */
    private static Stream<Pair<String, Piece>> getViewModelPair(Integer rowIndex, Integer columnIndex) {
        return PIECES.entrySet()
                .stream()
                .filter(entryset -> entryset.getKey().contains(new Pair<>(rowIndex, columnIndex)))
                .map(Entry::getValue);
    }

    /*
     * returns an Optional containing the symbol for the piece to be used in the view or no value, given a row - and column index.
     */
    public static BiFunction<Integer, Integer, Optional<String>> viewPieceAtIndexes = (rowIndex, columnIndex) -> {
        if (rowIndex == 1) return Optional.of("♙");
        if (rowIndex == 6) return Optional.of("♟");
        return getViewModelPair(rowIndex, columnIndex)
                .map(Pair::getKey)
                .findFirst();
    };

    /*
     * returns an Optional containing the piece to be used in the model or no value, given a row - and column index.
     */
    public static BiFunction<Integer, Integer, Optional<Piece>> modelPieceAtIndexes = (rowIndex, columnIndex) -> {
        if (rowIndex == 1)    return Optional.of(new Pawn(WHITE));
        if (rowIndex == 6)    return Optional.of(new Pawn(BLACK));
        return getViewModelPair(rowIndex, columnIndex)
                .map(Pair::getValue)
                .findFirst();
    };

    /*
     * help method to get what we need from FenMap. The predicate filters the values. Values are: 0 (FEN notation)
     * 1 (symbol for the piece in the view), 2 (path to image used in the DragView when moving pieces). The key
     * of FenMap is a piece for the model.
     */
    private static List<String> getValues(Predicate<Entry<Piece, List<String>>> predicate, int value) {
        return FEN_MAP
                .entrySet()
                .stream()
                .filter(predicate)
                .map(e -> e.getValue().get(value))
                .sorted()
                .collect(Collectors.toList());
    }

    //help method.
    private static Piece getKey(Predicate<Entry<Piece, List<String>>> predicate) {
        return FEN_MAP
                .entrySet()
                .stream()
                .filter(predicate)
                .map(Entry::getKey)
                .findFirst()
                .orElse(null);
    }

    //given the view piece, this method returns a corresponding model piece.
    public static Piece convert(String viewPiece) {
        return getKey(e -> Objects.equals(e.getValue().get(1), viewPiece));
    }

    //given a model piece, returns the corresponding view piece.
    public static String convert(Piece piece) {
        return getValues(e -> e.getKey().getClass() == piece.getClass()
                && e.getKey().getColor() == piece.getColor(), 1).get(0);
    }

    //given a FEN notation for a piece, returns a piece for the view.
    public static String getViewPieceFromFEN(String fen) {
        return getValues(e -> Objects.equals(e.getValue().get(0), fen), 1).get(0);
    }

    //given a model piece, return it's FEN-notation
    public static String getFENFromModelPiece(Piece piece) {
        return getValues(e -> e.getKey().getClass() == piece.getClass()
                && e.getKey().getColor() == piece.getColor(), 0).get(0);
    }

    //returns all white pieces used in the view.
    public static List<String> getAllWhiteViewPieces() {
        return getValues(e -> e.getKey().getColor() == WHITE, 1);
    }

}
package board;

import android.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import kotlin.Triple;

/**
 * a class that controls the board and gives the needed commands to the ui
 */
public class Controller {
    public static class BoardInstance {
        public final Board INSTANCE;
        public Side side;

        public BoardInstance(Board INSTANCE, Side side) {
            try {
                this.INSTANCE = new Board(INSTANCE);
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
            this.side = side;
        }

        @Override
        public String toString() {
            return INSTANCE.toString();
        }
    }

    private static Place chosenPlace;
    private static Place lastPlace;
    private static Board board = Board.newInstance();


    private static Piece whoIn(Place place) {
        return board.whoIn(place);
    }

    private static Piece whoIn(int rank, int file) {
        return board.whoIn(rank, file);
    }

    public static void start() {
        chosenPlace = null;
        lastPlace = null;
        board.pawnToPromote = null;

        board = Board.newInstance();
        GameControl.restartGame();
    }

    public static void start(List<Triple<Place, Place,Character>> moves) {
        start();
        board = Board.makeMoves(moves);
    }

    public static Boolean isPieceChosen() {
        return chosenPlace != null;
    }

    public static char getPieceTypeAt(int rank, int file) {
        return whoIn(rank, file) == null ? ' ' : switch (whoIn(rank, file).getClass().getSimpleName()) {
            case "King" -> 'k';
            case "Queen" -> 'q';
            case "Rook" -> 'r';
            case "Bishop" -> 'b';
            case "Knight" -> 'n';
            case "Pawn" -> 'p';
            case "GhostPawn" -> ' ';
            default ->
                    throw new IllegalStateException("Unexpected value: " + whoIn(rank, file).getClass().getSimpleName());
        };
    }

    public static void printMoveRecording() {
        board.printMoveRecording();
    }
    public static String getMoveRecording() {
        return board.getMoveRecordingString();
    }

    public static Side getSide() {
        return GameControl.getSide();
    }

    public static Side getPieceSideAt(int rank, int file) {
        return whoIn(rank, file) == null ? null : whoIn(rank, file).SIDE;
    }

    public static List<Place> getAvailablePlaces() {

        return chosenPlace != null ? whoIn(chosenPlace).whereCanIMove : whoIn(lastPlace).whereCanIMove;
    }


    public static boolean setMovingPiece(int rank, int file) {
        if (chosenPlace != null || board.pawnToPromote != null) return false;
        if (getSide() == Controller.getPieceSideAt(rank, file)) {
            chosenPlace = new Place(rank, file);
            return true;
        }
        return false;
    }


    public static boolean movePieceTo(int rank, int file) {
        if (board.pawnToPromote != null) return false;
        if (chosenPlace != null && getAvailablePlaces().contains(new Place(rank, file))) {
            board.move(chosenPlace.rank, chosenPlace.file, rank, file);
            lastPlace = chosenPlace;
            chosenPlace = null;
            GameControl.changeTurn();
            return true;
        }
        lastPlace = chosenPlace;
        chosenPlace = null;
        return false;
    }

    public static boolean makeMove(Pair<Place, Place> move) {
        board.move(move.first.rank, move.first.file, move.second.rank, move.second.file);
        lastPlace = move.first;
        chosenPlace = null;
        GameControl.changeTurn();
        return true;
    }



    public static String getGameStatus() {
        return board.getGameStatus();
    }

    public static Place getEndangeredKing() {
        return board.endangeredKing;
    }

    public static Boolean isReadyToPromote() {
        return board.pawnToPromote != null;
    }

    public static void promote(char pieceType) {
        board.promote(pieceType);
        if (board.myKing(getSide().oppositeSide()).isEndangered()) {
            board.checkKing(board.myKing(getSide().oppositeSide()));
        }
    }

    public static List<Triple<Place, Place,Character>> getMoves() {
        return board.getMoveRecording();
    }

    public static List<Pair<Place, Place>> getLegalMoves() {
        ArrayList<Pair<Place, Place>> legalMoves = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                int finalJ = j;
                int finalI = i;
                if (whoIn(i, j) != null && whoIn(i, j).SIDE == getSide())
                    legalMoves.addAll(whoIn(i, j).whereCanIMove.stream().map(place -> new Pair<>(new Place(finalI, finalJ), place)).collect(Collectors.toList()));
            }
        }
        return legalMoves;
    }

    public static int getMyMaterial(Side side) {
        return board.mySide(side).stream().mapToInt(p -> {
            if (p instanceof Pawn) return 1;
            if (p instanceof Knight) return 3;
            if (p instanceof Bishop) return 3;
            if (p instanceof Rook) return 5;
            if (p instanceof Queen) return 9;
            return 0;
        }).sum();
    }

    public static boolean isTie(Side side) {
        return getGameStatus().endsWith("tie");
    }

    public static boolean isWin(Side side) {
        return getGameStatus().startsWith(side.toString());
    }

    public static boolean isLost(Side side) {
        return getGameStatus().startsWith(side.oppositeSide().toString());
    }

    public static BoardInstance getInstance() {
        return new BoardInstance(board, GameControl.getSide());
    }

    public static void setInstance(BoardInstance instance) {
        board = instance.INSTANCE;
        GameControl.setSide(instance.side);
        GameControl.setBoard(board);
    }

    public static String getBoardString() {
        return board.toString();
    }
}

package board;


import static board.Side.BLACK;
import static board.Side.WHITE;

import android.util.Log;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import kotlin.Triple;


/**
 * @author Meni Deutsch<br>
 * The class that manages the board, moves the pieces, and stores them
 */

class Board {
    private static Board instance;
    final King WHITE_KING;
    final King BLACK_KING;
    Place pawnToPromote = null;
    final private Piece[][] board;
    private final List<State> recordingOfBoards = new ArrayList<>(List.of());
    private final List<Triple<Place, Place, String>> moveRecording = new ArrayList<>();
    final private List<Piece> whitePieces = new LinkedList<>();
    final private List<Piece> blackPieces = new LinkedList<>();
    Place endangeredKing;
    /**
     * A variable that says if the game is on going, or over, and if it's over from which cause
     */
    private String gameStatus = "on going";
    private int numOfMovesFromPawnMovingOrPieceCaptured = 0;

    /**
     * Makes a new game board. resets the board and the pieces, adds all the pieces to the board.
     * Updates there possible moves, and open the user interface.
     */
    private Board() {
        //region making the pieces and inserting them to the players' pieces
        board = new Piece[8][8];
        instance = this;
        WHITE_KING = new King(WHITE);
        BLACK_KING = new King(BLACK);
        add(WHITE_KING);
        add(BLACK_KING);
        add(new Queen(WHITE));
        add(new Queen(BLACK));
        add(new Rook(WHITE, new Place("a1")));
        add(new Rook(WHITE, new Place("h1")));
        add(new Rook(BLACK, new Place("a8")));
        add(new Rook(BLACK, new Place("h8")));
        add(new Knight(WHITE, new Place("b1")));
        add(new Knight(WHITE, new Place("g1")));
        add(new Knight(BLACK, new Place("b8")));
        add(new Knight(BLACK, new Place("g8")));
        add(new Bishop(WHITE, new Place("c1")));
        add(new Bishop(WHITE, new Place("f1")));
        add(new Bishop(BLACK, new Place("c8")));
        add(new Bishop(BLACK, new Place("f8")));
        for (int i = 0; i < 8; i++) {
            add(new Pawn(BLACK, new Place(6, i)));
            add(new Pawn(WHITE, new Place(1, i)));
        }
        //endregion making the pieces and inserting them to the players' pieces
        updateWhereCanThePiecesGo(WHITE);
        updateWhereCanThePiecesGo(BLACK);

    }


    static Board getInstance() {
        if (instance == null) {
            instance = new Board();
        }
        return instance;
    }

    static Board newInstance() {
        instance = new Board();
        return instance;
    }

    static Board makeMoves(List<Triple<Place, Place, Character>> moves) {
        instance = getInstance();
        if (moves == null) {
            return instance;
        }
        for (Triple<Place, Place, Character> move : moves) {
            instance.move(move.getFirst(), move.getSecond());
            if (move.getThird() != ' ') {
                instance.promote(move.getThird());
            }
            GameControl.changeTurn();
        }
        return instance;
    }

    void promote(char pieceType) {
        if (pawnToPromote == null || whoIn(pawnToPromote) == null || pieceType == ' ') {
            return;
        }
        Side side = whoIn(pawnToPromote).SIDE;
        whoIn(pawnToPromote).remove();
        Triple<Place, Place, String> lastMove = moveRecording.get(moveRecording.size() - 1);
        pieceType = Character.toLowerCase(pieceType);
        moveRecording.set(moveRecording.size() - 1, new Triple<>(lastMove.getFirst(), lastMove.getSecond(), lastMove.getThird() + "->" + pieceType));
        switch (pieceType) {
            case 'b' -> add(new Bishop(side, pawnToPromote));
            case 'n' -> add(new Knight(side, pawnToPromote));
            case 'q' -> add(new Queen(side, pawnToPromote));
            case 'r' -> add(new Rook(side, pawnToPromote));
            default -> throw new IllegalArgumentException("pieceType must be b,n,q,r or space");
        }
        pawnToPromote = null;

    }

    /**
     * is the place inputted by x and y isn't in the bounds of the board
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @return is the place inputted by x and y isn't in the bounds of the board
     */
    static boolean isOutOfBounds(int x, int y) {
        return 0 > x || x > 7 || 0 > y || y > 7;
    }

    private static boolean isWhole(float num) {
        return num == Math.ceil(num);
    }

    /**
     * Returns true if the available pieces can checkmate the other side. The method <b>isn't perfect</b>. There a lot of
     * cases that a checkmate isn't possible, no matter which moves would be done, but the method wouldn't return true.
     * The method checks only if the pieces are enough to checkmate, not if the position of the pieces is checkmate-able.
     *
     * @param side the side the method checks if the pieces can checkmate.
     * @return can the side checkmate
     */
    boolean canCheckMate(Side side) {

        List<Piece> mySide = mySide(side);
        if (mySide.stream().anyMatch(x -> x instanceof Rook || x instanceof Queen || x instanceof Pawn))
            return true;
        if (mySide.stream().filter(x -> x instanceof Bishop || x instanceof Knight).count() >= 2 && mySide.stream().anyMatch(x -> x instanceof Knight))
            return true;
        if (mySide.stream().filter(x -> x instanceof Bishop).count() >= 2) {
            //noinspection OptionalGetWithoutIsPresent
            return !isWhole((float) (mySide.stream().filter(x -> x instanceof Bishop)
                    .map(x -> (x.getFile() + x.getRank()) % 2).reduce(Integer::sum).get()) /
                    mySide.stream().filter(x -> x instanceof Bishop).count());
        }
        return false;
    }

    /**
     * Checks if there are a piece that can move. If there are non, ends the game by changing the {@code gameStatus} to
     * "stalemate - draw".
     *
     * @param side which side to check
     */
    void checkIfPiecesCanMove(Side side) {
        for (Piece piece : mySide(side)
        ) {
            if (!piece.whereCanIMove.isEmpty()) {
                return;
            }
        }
        if (myKing(side).isEndangered())
            gameStatus = (side == WHITE ? BLACK : WHITE) + " win";
        else
            gameStatus = "stalemate - draw";
    }

    /**
     * Like {@link Board#canCheckMate} but for both of the sides. If both of the sides can't checkmate, ends the game by
     * changing {@code gameStatus} to "dead position - draw".
     */
    void isDeadPosition() {
        gameStatus = canCheckMate(WHITE) || canCheckMate(BLACK) ? gameStatus : "dead position - draw";
    }

    /**
     * For all the Pieces updates there possible moves.
     *
     * @param side which pieces to update the white or the black
     */
    void updateWhereCanThePiecesGo(@NotNull Side side) {
        mySide(side).forEach(Piece::whereCanIMove);
        deleteGhostPawns(side);
    }

    /**
     * Checks all the pieces from one side, and deletes all the GhostPawns
     *
     * @param side the side to delete from
     */
    void deleteGhostPawns(Side side) {

        for (Piece piece : mySide(side)
        ) {
            piece.deleteIfGhostPawn();
        }
    }

    Place getGhostPawnPlace() {
        for (Piece piece : mySide(BLACK)) {
            if (piece instanceof GhostPawn) {
                return piece.getPlace();
            }
        }
        return null;
    }

    /**
     * Returns true if and only if, who in place is a rook that is not null, and has castling rights with his king.
     *
     * @param place the location of the piece to check if it has castling rights.
     * @return does the piece has castling right with his king.
     */
    boolean kingHasCastlingRightsWith(Place place) {
        if (!(whoIn(place) instanceof Rook))
            return false;
        if (whoIn(place) == null)
            return false;
        return ((Rook) whoIn(place)).rightToCastling();
    }

    /**
     * A convenience method for the method {@link Board#kingHasCastlingRightsWith(Place)}.
     *
     * @param place a string that tells the location of the piece to check if it has castling rights.
     * @return does the piece has castling right with his king.
     */
    boolean kingHasCastlingRightsWith(String place) {
        return kingHasCastlingRightsWith(new Place(place));
    }

    /**
     * returns the king of the inserted side.
     *
     * @param side the side of the king wanted.
     * @return the king of the side, side.
     */
    King myKing(@NotNull Side side) {
        return side == WHITE ? WHITE_KING : BLACK_KING;
    }

    /**
     * makes a schematic of the board and returns it. every piece is turned into his side plus his type.
     * for example a black pawn.svg would be written as "black Pawn".
     *
     * @return a schematic of the board.
     */
    private String[][] getBoardSchematics() {
        String[][] boardSchematics = new String[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                boardSchematics[i][j] = whoIn(i, j) == null ? null : whoIn(i, j).SIDE + " " + whoIn(i, j).getClass().getSimpleName();

            }

        }
        return boardSchematics;

    }

    /**
     * This is a convenience method for the method {@link Board#whoIn(int, int)}.<br>
     * tells, who is in place.
     *
     * @param place the location of the wanted piece.
     * @return the piece in place.
     */
    @Nullable
    Piece whoIn(@NotNull Place place) {
        return whoIn(place.getRank(), place.getFile());
    }

    /**
     * who in the location specified by the rank and file inserted
     *
     * @param rank the rank of the wanted piece. {@link Place#rank}
     * @param file the file of the wanted piece. {@link Place#file}
     * @return the piece in rank and file
     */
    @Nullable
    Piece whoIn(int rank, int file) {
        if (rank < 0 || file < 0 || rank > 7 || file > 7)
            return null;
        return board[rank][file];
    }

    /**
     * changes the piece in the inputted place to inputted piece. if piece != null then changes the {@code piece.place} to place
     *
     * @param place the place to change in.
     * @param piece the piece to change to, can be null.
     */
    void change(@NotNull Place place, Piece piece) {
        board[place.getRank()][place.getFile()] = piece;
        if (piece != null)
            piece.setPlace(place);
    }

    /**
     * Returns the piece in the num-th position in {@code whitePieces}. used for iterating over {@code whitePieces}
     *
     * @param num where in {@code whitePieces} to get
     * @return the piece in the num-th position in {@code whitePieces}.
     */
    Piece whiteNumber(int num) {
        return whitePieces.get(num);
    }

    /**
     * Returns the piece in the num-th position in {@code blackPieces}. used for iterating over {@code blackPieces}
     *
     * @param num where in {@code blackPieces} to get
     * @return the piece in the num-th position in {@code blackPieces}.
     */
    Piece blackNumber(int num) {
        return blackPieces.get(num);
    }

    /**
     * the number of white pieces. used for iterating over {@code whitePieces}.
     *
     * @return the number of white pieces.
     */
    int numberOfWhite() {
        return whitePieces.size();
    }

    /**
     * the number of black pieces. used for iterating over {@code blackPieces}.
     *
     * @return the number of black pieces.
     */
    int numberOfBlack() {
        return blackPieces.size();
    }

    /**
     * copies the list of the pieces of the opposite side
     *
     * @param side the side that is opposite to the wanted side
     * @return the pieces of the opposite side
     */
    List<Piece> oppositeSide(@NotNull Side side) {
        return side.equals(WHITE) ? List.copyOf(blackPieces) : List.copyOf(whitePieces);
    }

    /**
     * copies the list of the pieces of the inputted side
     *
     * @param side the side of the wanted pieces
     * @return the pieces of the opposite side
     */
    List<Piece> mySide(@NotNull Side side) {
        return side == WHITE ? List.copyOf(whitePieces) : List.copyOf(blackPieces);
    }

    /**
     * removes a piece from the board. removes the piece from the list of pieces.
     *
     * @param piece the piece to remove
     */
    void remove(Piece piece) {
        if (piece == null)
            return;
        List<Piece> myPieces = piece.SIDE.equals(WHITE) ? whitePieces : blackPieces;
        myPieces.remove(piece);
        change(piece.getPlace(), null);
    }

    /**
     * adds a piece from the board. adds the piece from the list of pieces.
     *
     * @param piece the piece to add
     */
    void add(@NotNull Piece piece) {
        piece.board = this;
        List<Piece> myPieces = piece.SIDE.equals(WHITE) ? whitePieces : blackPieces;

        myPieces.add(piece);
        change(piece.getPlace(), piece);

    }

    void unCheckKing() {
        endangeredKing = null;
    }

    void checkKing(@NotNull King king) {
        endangeredKing = king.getPlace();
    }


    /**
     * returns a symbol corresponding with the type of the pieces
     *
     * @param piece the pieces to make into a symbol
     * @return a symbol corresponding with the type of the pieces
     */
    @NotNull
    String toSymbol(Piece piece) {
        if (piece instanceof King)
            return piece.SIDE.equals(WHITE) ? "\u265A" : "\u2654";
        if (piece instanceof Queen)
            return piece.SIDE.equals(WHITE) ? "\u265B" : "\u2655";
        if (piece instanceof Rook)
            return piece.SIDE.equals(WHITE) ? "\u265C" : "\u2656";
        if (piece instanceof Bishop)
            return piece.SIDE.equals(WHITE) ? "\u265D" : "\u2657";
        if (piece instanceof Knight)
            return piece.SIDE.equals(WHITE) ? "\u265E" : "\u2658";
        if (piece instanceof Pawn)
            return piece.SIDE.equals(WHITE) ? "\u265F" : "\u2659";
        return "\u9647";
    }


    /**
     * @return the status of the game
     */
    String getGameStatus() {
        return gameStatus;
    }


    public void move(@NotNull Place from, @NotNull Place to) {
        move(from.getRank(), from.getFile(), to.getRank(), to.getFile());
    }

    public void move(int fromRank, int fileFile, int toRank, int toFile) {
        Place place = new Place(toRank, toFile);
        Piece chosenPiece = whoIn(fromRank, fileFile);
        if (chosenPiece == null)
            throw new IllegalArgumentException("No piece in the chosen place");
        if (chosenPiece.whereCanIMove.contains(place)) {
            chosenPiece.moveTo(place);
        } else {
            throw new IllegalArgumentException("Illegal move");
        }
    }

//    public char getPieceTypeAt(int rank, int file) {
//        return whoIn(rank, file) == null ? ' ' : switch (whoIn(rank, file).getClass().getSimpleName()) {
//            case "King" -> 'k';
//            case "Queen" -> 'q';
//            case "Rook" -> 'r';
//            case "Bishop" -> 'b';
//            case "Knight" -> 'n';
//            case "Pawn" -> 'p';
//            case "GhostPawn" -> ' ';
//            default ->
//                    throw new IllegalStateException("Unexpected value: " + whoIn(rank, file).getClass().getSimpleName());
//        };
//    }


    /**
     * updates the recording of the prior boards, clears recording if non-retractable change was made. <br>
     * if a pawn was moved - clearRecording in the moveTo method {@link Pawn#moveTo}.<br>
     * if a piece was captured - clearRecording in the captured method {@link Piece#captured}.<br>
     * if a castling right was lost - clearRecording in the rook's moveTo method {@link Rook#moveTo}.<br>
     * if a pawn was promoted - not needed because pawn can be promoted, only if he was moved.<br>
     * add the current state and check if there are two identical to him,
     * - if there are change the game status to "threefold - draw"
     */
    void updateRecording() {
        recordingOfBoards.add(new State());
        if (recordingOfBoards.stream().filter(recordingOfBoards.get(recordingOfBoards.size() - 1)::equals).count() >= 3) {
            gameStatus = "threefold - draw";
        }
    }

    void updateMoveRecording(@NotNull Place fromWhere, @NotNull Place toWhere, Piece piece) {
        moveRecording.add(new Triple<>(fromWhere, toWhere, toSymbol(piece)));

    }

    @NonNull
    public String toString() {
        StringBuilder boardString = new StringBuilder("  a b c d e f g h\n");
        for (int i = 0; i < 8; i++) {
            boardString.append(8 - i).append(" ");
            for (int j = 0; j < 8; j++) {
                boardString.append(toSymbol(whoIn(i, j))).append(" ");

            }
            boardString.append("\n");
        }
        boardString.append("  a b c d e f g h");
        Log.d("print board:", boardString.toString());
        return boardString.toString();
    }

    public King getKing(Side side) {
        return side.equals(WHITE) ? WHITE_KING : BLACK_KING;
    }

    void resetMoveCount() {
        numOfMovesFromPawnMovingOrPieceCaptured = -1;
    }


    void updateMoveCount() {
        numOfMovesFromPawnMovingOrPieceCaptured++;
        if (numOfMovesFromPawnMovingOrPieceCaptured >= 100)//over 100 hundred because counting both sides
            gameStatus = "fifty moves from the last time the a pawn was moved, or a piece was captured - draw";

    }

    List<Triple<Place, Place, Character>> getMoveRecording() {
        return moveRecording.stream().map(triple -> {
            if (triple.getThird().length() > 3)
                return new Triple<>(triple.getFirst(), triple.getSecond(), triple.getThird().charAt(3));
            else
                return new Triple<>(triple.getFirst(), triple.getSecond(), ' ');
        }).collect(Collectors.toList());
    }

    void printMoveRecording() {
        int[] i = {1};
        moveRecording.forEach(triple -> System.out.println((i[0]++) + " " + triple.getThird() + triple.getFirst() + " -> " + triple.getSecond()));
    }

    String getMoveRecordingString() {
        StringBuilder moveRecordingString = new StringBuilder();
        int[] i = {1};
        moveRecording.forEach(triple -> moveRecordingString.append((i[0]++)).append(" ").append(triple.getThird()).append(triple.getFirst()).append(" -> ").append(triple.getSecond()).append("\n"));
        return moveRecordingString.toString();
    }

    private class State {
        public final String[][] BOARD;
        public final Side SIDE;
        public final boolean[] CASTLING_RIGHTS = new boolean[4];
        public final Place EN_PASSANT;


        public State() {
            this.BOARD = getBoardSchematics();
            this.SIDE = GameControl.getSide();
            CASTLING_RIGHTS[0] = kingHasCastlingRightsWith("a1");
            CASTLING_RIGHTS[1] = kingHasCastlingRightsWith("h1");
            CASTLING_RIGHTS[2] = kingHasCastlingRightsWith("a8");
            CASTLING_RIGHTS[3] = kingHasCastlingRightsWith("h8");
            EN_PASSANT = getGhostPawnPlace();

        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            State state = (State) o;
            return Arrays.deepEquals(BOARD, state.BOARD) && SIDE.equals(state.SIDE);
        }

        @Override
        public int hashCode() {
            int result = Objects.hash(SIDE);
            result = 31 * result + Arrays.deepHashCode(BOARD);
            return result;
        }
    }

    public Board(Board board) throws CloneNotSupportedException {
        this.board = new Piece[8][8];
        this.gameStatus = board.gameStatus;
        this.recordingOfBoards.addAll(board.recordingOfBoards);
        this.moveRecording.addAll(board.moveRecording);
        this.numOfMovesFromPawnMovingOrPieceCaptured = board.numOfMovesFromPawnMovingOrPieceCaptured;

        this.WHITE_KING = (King) board.WHITE_KING.clone();
        this.BLACK_KING = (King) board.BLACK_KING.clone();
        this.endangeredKing = board.endangeredKing;
        this.pawnToPromote = board.pawnToPromote;
        board.whitePieces.forEach(piece -> {
            try {
                Piece clone;
                if (piece instanceof King)
                    clone = this.WHITE_KING;
                else
                    clone = (Piece) piece.clone();
                this.add(clone);


            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        });
        board.blackPieces.forEach(piece -> {
            try {
                Piece clone;
                if (piece instanceof King)
                    clone = this.BLACK_KING;
                else
                    clone = (Piece) piece.clone();
                this.add(clone);


            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        });
    }
}

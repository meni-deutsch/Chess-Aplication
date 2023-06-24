package board;

import static board.Side.WHITE;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

abstract class Piece implements Cloneable {
    /**
     * can be "white" or "black", represents with witch player the piece is
     */
    public final Side SIDE;
    protected Board board;
    protected Place place;
    protected List<Place> whereCanIMove;
    protected List<Place> whereCanIMoveWithoutCaringForTheKing;

    Piece(@NotNull Side SIDE, @NotNull Place place) {
        if (place.isOutOfBounds())
            throw new IndexOutOfBoundsException("place has to be in the board");
        this.SIDE = SIDE;
        this.place = place;
        board = Board.getInstance();
        board.change(place, this);
    }


    public void whereCanIMove() {
        whereCanIMove = makeWhereCanIMoveWithoutCaringForTheKing().stream().filter(x -> !isNotSafeToMove(x)).collect(Collectors.toList());
    }


    public boolean canIEatAt(@NotNull Place place) {
        return this.canIEatAt(place.getRank(), place.getFile());
    }

    public boolean canIEatAt(int x, int y) {
        if (Board.isOutOfBounds(x, y))
            return false;
        if (board.whoIn(x, y) != null && !(board.whoIn(x, y) instanceof GhostPawn)) {
            return !(board.whoIn(x, y).SIDE).equals(this.SIDE);
        }
        return true;
    }


    public List<Place> makeWhereCanIMoveWithoutCaringForTheKing() {
        whereCanIMoveWithoutCaringForTheKing = makeWhereIAttack();
        return whereCanIMoveWithoutCaringForTheKing;

    }

    public abstract List<Place> makeWhereIAttack();


    public Place getPlace() {
        return place;
    }

    public void setPlace(Place place) {
        this.place = place;
    }

    public int getRank() {
        return place.getRank();
    }

    public int getFile() {
        return place.getFile();
    }

    public void moveTo(Place whereToMove) {
        board.unCheckKing();
        board.updateMoveRecording(place, whereToMove, this);
        Piece eatenPiece = board.whoIn(whereToMove);
        board.change(this.place, null);
        if (eatenPiece != null) {
            eatenPiece.captured(this);
        } else {
            board.change(whereToMove, this);
        }
        if (board.myKing(this.SIDE.oppositeSide()).isEndangered()) {
            board.checkKing(board.myKing(this.SIDE.oppositeSide()));
        }

    }

    public void addLine(List<Place> places, int x, int y) {
        addLine(places, new Place(this.place.toString()), x, y);
    }


    public void addLine(List<Place> places, Place place, int x, int y) {
        place = new Place(place.getRank() + y, place.getFile() + x);
        if (place.isOutOfBounds())
            return;
        if (board.whoIn(place) != null && !(board.whoIn(place) instanceof GhostPawn)) {
            if (!board.whoIn(place).SIDE.equals(SIDE))
                places.add(place);
            return;
        }
        places.add(place);
        addLine(places, place, x, y);
    }

    public boolean isNotSafeToMove(@NotNull Place target) {
        Piece capturedPiece = board.whoIn(target);
        Place priorPosition = this.place;
        if (capturedPiece != null) {
            capturedPiece.remove();
            board.change(target, this);
            board.change(priorPosition, null);
            boolean ans = board.getKing(SIDE).isEndangered();
            board.change(priorPosition, this);
            board.add(capturedPiece);
            return ans;
        }

        board.change(target, this);
        board.change(priorPosition, null);
        boolean ans = board.getKing(SIDE).isEndangered();
        board.change(priorPosition, this);
        board.change(target, null);
        return ans;
    }

    @NonNull
    @Override
    protected Object clone() throws CloneNotSupportedException {

        return super.clone();
    }

    public void captured(Piece eater) {
        board.resetMoveCount();
        board.resetMoveCount();
        this.remove();
        board.change(this.getPlace(), eater);
    }

    public void deleteIfGhostPawn() {
    }

    public void remove() {
        board.remove(this);
    }


}

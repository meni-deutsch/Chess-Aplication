package board;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

import static board.Side.*;

import androidx.annotation.NonNull;

class King extends Piece {
    private boolean wasMoved = false;

    @NonNull
    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public King(Side SIDE) {
        super(SIDE, SIDE.equals(WHITE) ? new Place("e1") : new Place("e8"));
    }

    public boolean isEndangered() {
        return super.place.isEndangered(SIDE,board);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        King king = (King) o;
        return super.place.equals(king.place) && super.SIDE.equals(king.SIDE) && board == king.board;
    }

    @Override
    public List<Place> makeWhereCanIMoveWithoutCaringForTheKing() {
        whereCanIMoveWithoutCaringForTheKing = makeWhereIAttack();
        Piece piece;
        if (SIDE.equals(WHITE))
            for (int i = 0; i < board.numberOfWhite(); i++) {
                piece = board.whiteNumber(i);
                castling(piece, whereCanIMoveWithoutCaringForTheKing);
            }
        else {
            for (int i = 0; i < board.numberOfBlack(); i++) {
                piece = board.blackNumber(i);
                castling(piece, whereCanIMoveWithoutCaringForTheKing);
            }
        }
        return whereCanIMoveWithoutCaringForTheKing;
    }

    public List<Place> makeWhereIAttack() {
        List<Place> whereIAttack = new LinkedList<>();
        for (int x = -1; x <= 1; x++)
            for (int y = -1; y <= 1; y++) {
                if (canIEatAt(getRank() + x, getFile() + y))
                    whereIAttack.add(new Place(getRank() + x, getFile() + y));
            }
        return whereIAttack;
    }

    public void castling(Piece piece, List<Place> Places) {
        if (piece instanceof Rook)
            if (((Rook) piece).rightToCastling()) {
                int direction = (piece.getFile()-getFile()) > 0 ? 1 : -1;

                for (int i = getFile() + direction; i*direction < piece.getFile()*direction; i += direction)//check if all the places in between the rook and the king are empty
                    if (board.whoIn(getRank(), i) != null)
                        return;
                if (!isEndangered() && !(new Place(getRank(), getFile() + direction).isEndangered(SIDE,board))) {
                    Places.add(new Place(this.getRank(), getFile() + direction * 2));
                }
            }
    }

    @Override
    public String toString() {
        return "King{" +
                "wasMoved=" + wasMoved +
                ", SIDE='" + SIDE + '\'' +
                ", place=" + place +
                '}';
    }

    @Override
    public void moveTo(@NotNull Place whereToMove) {
        wasMoved = true;
        if (Math.abs(whereToMove.getFile() - getFile()) == 2) {
            int direction = ( whereToMove.getFile()-getFile() )/2;

            Rook rook = (Rook) (direction == 1 ? board.whoIn(getRank(),7) : board.whoIn(getRank(),0));
            rook.moveTo(new Place(whereToMove.getRank(), whereToMove.getFile() - direction));
        }
        super.moveTo(whereToMove);
    }

    public boolean isWasMoved() {
        return wasMoved;
    }


}

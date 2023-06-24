package board;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

import static board.Side.WHITE;


class Pawn extends Piece {
    private boolean wasMoved = false;

    @NotNull
    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
    public Pawn(Side side, Place place) {
        super(side, place);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pawn pawn = (Pawn) o;
        return super.place.equals(pawn.place) && super.SIDE.equals(pawn.SIDE) && board == pawn.board;
    }



    @Override
    public List<Place> makeWhereCanIMoveWithoutCaringForTheKing() {
        int direction = this.SIDE.equals(WHITE) ? 1 : -1;
        whereCanIMoveWithoutCaringForTheKing = new LinkedList<>();
        if ((board.whoIn(place.getRank() + direction, place.getFile())) == null) {
            whereCanIMoveWithoutCaringForTheKing.add(new Place(place.getRank() + direction, place.getFile()));
            if (!this.wasMoved)
                if (!(Board.isOutOfBounds(place.getRank() + 2 * direction, place.getFile())))
                    if ((board.whoIn(place.getRank() + 2 * direction, place.getFile())) == null)
                        whereCanIMoveWithoutCaringForTheKing.add(new Place(place.getRank() + 2 * (direction), place.getFile()));
        }

        whereCanIMoveWithoutCaringForTheKing.addAll(makeWhereIAttack());
        return whereCanIMoveWithoutCaringForTheKing;
    }

    /**
     * creates and updates whereIAttack. adds all the places where the pawn attacks.
     *
     * @return whereIAttack
     */
    public List<Place> makeWhereIAttack() {
        List<Place> whereIAttack = new LinkedList<>();
        int direction = SIDE.equals(WHITE) ? 1 : -1;
        if (board.whoIn(place.getRank() + direction, place.getFile() + 1) != null)
            if (!board.whoIn(place.getRank() + direction, place.getFile() + 1).SIDE.equals(this.SIDE))
                whereIAttack.add(new Place(place.getRank() + direction, place.getFile() + 1));
        if (board.whoIn(place.getRank() + direction, place.getFile() - 1) != null)
            if (!board.whoIn(place.getRank() + direction, place.getFile() - 1).SIDE.equals(this.SIDE))
                whereIAttack.add(new Place(place.getRank() + direction, place.getFile() - 1));
        return whereIAttack;
    }


    @Override
    public String toString() {
        return "Pawn{" +
                "wasMoved=" + wasMoved +
                ", SIDE='" + SIDE + '\'' +
                ", place=" + place +
                '}';
    }

    @Override
    public void moveTo(@NotNull Place whereToMove) {
        wasMoved = true;
        board.resetMoveCount();

        Boolean isEnPassant = Math.abs(whereToMove.getRank() - getRank()) == 2;

        super.moveTo(whereToMove);
        if (isEnPassant) {
            new GhostPawn(this);
        }
        if (whereToMove.getRank() == 7||whereToMove.getRank() == 0) {
            //EventQueue.invokeLater(board::updateUI);
            promotion();
        }
    }


    public void promotion() {
        board.pawnToPromote = getPlace();



    }
}

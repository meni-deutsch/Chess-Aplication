package board;

import java.util.LinkedList;
import java.util.List;

import static board.Side.WHITE;

class Queen extends Piece {

    public Queen(Side side) {
        super(side, side.equals(WHITE) ? new Place("d1") : new Place("d8"));
    }

    public Queen(Side side,Place place) {
        super(side, place);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Queen queen = (Queen) o;
        return super.place.equals(queen.place) && super.SIDE.equals(queen.SIDE) && board == queen.board;
    }

    public List<Place> makeWhereIAttack(){
        List<Place> whereIAttack = new LinkedList<>();
        for (int x = -1; x <= 1; x++)
            for (int y = -1; y <= 1; y++) {
                addLine(whereIAttack,x,y);
            }
        return whereIAttack;
    }
    @Override
    public String toString() {
        return "Queen{" +
                ", SIDE='" + SIDE + '\'' +
                ", place=" + place +
                '}';
    }
}

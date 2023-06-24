package board;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

class Rook extends Piece {

    private boolean wasMoved = false;

    @NotNull
    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public Rook(Side side, Place place) {
        super(side, place);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rook rook = (Rook) o;
        return rightToCastling() == rook.rightToCastling() && super.place.equals(rook.place) && super.SIDE.equals(rook.SIDE) && super.board.getKing(SIDE) == rook.board.getKing(SIDE);
    }

    @Override
    public List<Place> makeWhereIAttack(){
        List<Place> whereIAttack = new ArrayList<>();
        addLine(whereIAttack,1,0);
        addLine(whereIAttack,-1,0);
        addLine(whereIAttack,0,1);
        addLine(whereIAttack,0,-1);
        return whereIAttack;
    }
    @Override
    public void moveTo(@NotNull Place whereToMove){
        wasMoved = true;
        super.moveTo(whereToMove);
    }
    @Override
    public String toString() {
        return "Rook{" +
                "wasMoved=" + wasMoved +
                ", SIDE='" + SIDE + '\'' +
                ", place=" + place +
                '}';
    }


    public boolean rightToCastling() {
        return !this.wasMoved && !board.getKing(SIDE).isWasMoved() &&getRank()==board.getKing(SIDE).getRank();
    }
}

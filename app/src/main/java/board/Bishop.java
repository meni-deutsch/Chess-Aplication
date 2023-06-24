package board;

import androidx.annotation.NonNull;

import java.util.LinkedList;
import java.util.List;


/**
 * A class that implements the Piece "Bishop" from chess. this Piece can move only in the diagonal. <br>
 * the class extends {@link Piece}
 */
class  Bishop extends Piece {

    /**
     * construct a bishop with the same parameters as Piece constructor
     */
    public Bishop(Side side, Place place) {
        super(side, place);
    }



    /**
     * compares {@code place}, {@code SIDE} and {@code MY_CODE}
     * @param o the object to be compared to
     * @return   {@code o} equals {@code this}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bishop bishop = (Bishop) o;
        return place.equals(bishop.place) && SIDE.equals(bishop.SIDE) && board == bishop.board;
    }

    /**
     *
     * @return a list of all the places that this can attack in
     */
    @Override
    public List<Place> makeWhereIAttack(){
        List<Place> whereIAttack = new LinkedList<>();
        for (int x = 0; x < 2; x++) {
            for (int y = 0; y < 2; y++) {
                addLine(whereIAttack,x*2-1,y*2-1);
            }
        }
        return whereIAttack;
    }

    /**
     * Returns a string with all the important information. including {@code SIDE, board.getKing(SIDE), place} and the Type(Bishop).
     * to ensure that there would not be an infinite loop, {@code board.getKing(SIDE)} is written as what color king he is.
     * for example the white king would be written as "white King".
     * @return a string with all the important information
     */
    @Override
    public String toString() {
        return "Bishop{" +
                ", SIDE='" + SIDE + '\'' +
                ", board.getKing(SIDE)=" + SIDE + " King" +
                ", place=" + place +
                '}';
    }
}

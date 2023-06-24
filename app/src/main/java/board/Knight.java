package board;

import java.util.LinkedList;
import java.util.List;

class Knight extends Piece {


    public Knight(Side side, Place place) {
        super(side, place);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Knight knight = (Knight) o;
        return place.equals(knight.place) && SIDE.equals(knight.SIDE) && board == knight.board;
    }



    public List<Place> makeWhereIAttack(){
        List<Place> whereIAttack = new LinkedList<>();
        List<Place> possibleLocations = List.of(
                new Place(getRank() +2,getFile()+1 ),
                new Place(getRank() +2,getFile()-1 ),
                new Place(getRank() -2,getFile()+1 ),
                new Place(getRank() -2,getFile()-1 ),
                new Place(getRank() +1,getFile()+2 ),
                new Place(getRank() +1,getFile()-2 ),
                new Place(getRank() -1,getFile()+2 ),
                new Place(getRank() -1,getFile()-2 )
                );
        for(Place place:possibleLocations){

            if(canIEatAt(place))
                whereIAttack.add(place);
        }
        return whereIAttack;
    }

    @Override
    public String toString() {
        return "Knight{" +
                ", SIDE='" + SIDE + '\'' +
                ", place=" + place +
                '}';
    }
}

package board;


import androidx.annotation.NonNull;

import org.jetbrains.annotations.Contract;

public enum Side {
    WHITE, BLACK;

    @NonNull
    public String toString() {
        return this == WHITE ? "white" : "black";
    }

    public Side oppositeSide() {
        return this == WHITE ? BLACK : WHITE;
    }
}



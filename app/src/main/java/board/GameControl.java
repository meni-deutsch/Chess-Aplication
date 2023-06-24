package board;


import static board.Side.*;
class GameControl {
    private static Side side = WHITE;
    private static Board board = Board.getInstance();


    static void changeTurn(){
        side = side.equals(WHITE) ? BLACK : WHITE;
        board.updateWhereCanThePiecesGo(side);
        checkGameStatus();

    }
    static void checkGameStatus() {
        board.updateRecording();
        board.updateMoveCount();
        board.isDeadPosition();
        board.checkIfPiecesCanMove(side);
    }

    public static Side getSide() {
        return side;
    }
    public static void restartGame(){
        board = Board.getInstance();
        side = WHITE;
    }


    public static void setSide(Side side) {
        GameControl.side = side;
    }
    public static void setBoard(Board board) {
        GameControl.board = board;
    }
}

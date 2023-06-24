package AI;

import board.Controller;
import board.Place;
import board.Side;


import android.util.Pair;

import com.example.chessaplication.Game;

public class AIController {
     int defaultDepth;

    public AIController(int defaultDepth) {
        this.defaultDepth = defaultDepth;
    }

    public void move() {
        move(defaultDepth);
    }

    public void move(int depth) {
        Pair<Place, Place> move = findBestMove(depth);
        Controller.makeMove(move);

    }

    private Pair<Place, Place> findBestMove(int depth) {
        double bestScore = 0;
        Pair<Place,Place> bestMove = null;
        for (Pair<Place,Place> move : Controller.getLegalMoves()){
            Controller.BoardInstance boardInstance = Controller.getInstance();
            Controller.makeMove(move);
            double score = 1/evaluate(depth - 1);
            Controller.setInstance(boardInstance);
            if (score == Double.POSITIVE_INFINITY) return move;
            if (score > bestScore){
                bestScore = score;
                bestMove = move;
            }
        }
        return bestMove;
    }

    private double evaluate(int depth) {
        if (Controller.isWin(Controller.getSide())) return Double.POSITIVE_INFINITY;
        if (Controller.isLost(Controller.getSide())) return 0;
        if (Controller.isTie(Controller.getSide())) return 1;
        if (depth == 0) return evaluate();
        double bestScore = 0;
        for (Pair<Place,Place> move: Controller.getLegalMoves()){
            double score = getMoveScore(move, depth);
            bestScore = Math.max(bestScore, score);
        }
        return bestScore;
    }
    private double getMoveScore(Pair<Place,Place> move, int depth){
        Controller.BoardInstance boardInstance = Controller.getInstance();
        Controller.makeMove(move);
        if (Controller.isReadyToPromote()){
            Controller.promote('q');
            double score = 1/evaluate(depth - 1);
            Controller.setInstance(boardInstance);
            if (score == Double.POSITIVE_INFINITY) return Double.POSITIVE_INFINITY;
            Controller.setInstance(boardInstance);
            Controller.promote('n');
            double score2 = 1/evaluate(depth - 1);
            Controller.setInstance(boardInstance);
            if (score2 == Double.POSITIVE_INFINITY) return Double.POSITIVE_INFINITY;
            return Math.max(score, score2);
        }
        double score = 1/evaluate(depth - 1);
        Controller.setInstance(boardInstance);
        if (score == Double.POSITIVE_INFINITY) return Double.POSITIVE_INFINITY;
        return score;

    }
    private  double evaluate() {
        return Controller.getMyMaterial(Controller.getSide()) /
                (double) Controller.getMyMaterial(Controller.getSide().oppositeSide());
    }
}

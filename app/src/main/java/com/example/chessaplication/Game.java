package com.example.chessaplication;

import android.util.Pair;

import java.util.List;

import board.Place;
import board.Side;
import kotlin.Triple;

public class Game {
    private  long totalTimePlayed;
    private String gameName;
    private int gameID;
    private String gameStatus;
    private String gameType;
    private String gameTime;
    private int WhitePlayerId;
    private int BlackPlayerId;
    private String lastTimePlayed;

    private List<Triple<Place,Place,Character>> moves;


    public Game(String gameName, int gameID, String gameStatus, String gameType, String gameTime, int WhitePlayerId, int BlackPlayerId, String lastTimePlayed,long totalTimePlayed) {
        this.gameName = gameName;
        this.gameID = gameID;
        this.gameStatus = gameStatus;
        this.gameType = gameType;
        this.gameTime = gameTime;
        this.WhitePlayerId = WhitePlayerId;
        this.BlackPlayerId = BlackPlayerId;
        this.lastTimePlayed = lastTimePlayed;
        this.totalTimePlayed = totalTimePlayed;
    }

    public Side getCmpSide(){
        if(WhitePlayerId == -1)
            return Side.WHITE;
        else if (BlackPlayerId == -1)
            return Side.BLACK;
        else
            return null;
    }
    public void setMoves(List<Triple<Place,Place,Character>> moves) {
        this.moves = moves;
    }
    public List<Triple<Place,Place,Character>> getMoves() {
        return moves;
    }
    public String getGameName() {
        return gameName;
    }


    public String getGameStatus() {
        return gameStatus;
    }


    public String getGameType() {
        return gameType;
    }

    public int getGameID() {
        return gameID;
    }


    public String getGameTime() {
        return gameTime;
    }

    public int getWhitePlayerId() {
        return WhitePlayerId;
    }

    public int getBlackPlayerId() {
        return BlackPlayerId;
    }
    public int getOpponentId(int id){
        if(id == WhitePlayerId)
            return BlackPlayerId;
        else
            return WhitePlayerId;
    }
    public String getLastTimePlayed() {
        return lastTimePlayed;
    }

    public long getTotalTimePlayed() {
        return totalTimePlayed;
    }

    public void setTotalTimePlayed(long totalTimePlayed) {
        this.totalTimePlayed = totalTimePlayed;
    }

    public void setGameStatus(String gameStatus) {
        this.gameStatus = gameStatus;
    }
}

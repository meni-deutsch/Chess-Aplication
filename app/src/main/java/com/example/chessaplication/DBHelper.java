package com.example.chessaplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Pair;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import board.Place;
import kotlin.Triple;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "playersdb";
    private static final int DB_VERSION = 2;
    private static final String TABLE_NAME = "players";

    public static final String ID_COL = "id";
    private static final String USER_NAME_COL = "username";
    private static final String NAME_COL = "name";

    private static final String PASS_COL = "password";
    public static final String WHITE_ID_COL = "white_id";
    public static final String BLACK_ID_COL = "black_id";
    public static final String NUM_COL = "number";
    public static final String FROM_X_COL = "from_x";
    public static final String FROM_Y_COL = "from_y";
    public static final String TO_X_COL = "to_x";
    public static final String TO_Y_COL = "to_y";
    public static final String STATUS_COL = "status";
    public static final String TYPE_COL = "type";
    public static final String STARTING_TIME_COL = "starting_time";
    public static final String LAST_TIME_COL = "last_time";
    private static final String TOTAL_TIME = "total_time";
    private static final String PROMOTION = "promotion";


    public void openDataBase() {
        SQLiteDatabase db = this.getWritableDatabase();
    }


    private static final HashMap<String, String> UsersTable = new LinkedHashMap<>() {{
        put(ID_COL, "INTEGER PRIMARY KEY AUTOINCREMENT");
        put(USER_NAME_COL, "TEXT");
        put(PASS_COL, "TEXT");


    }};
    private static final HashMap<String, String> UsersGamesTable = new LinkedHashMap<>() {{
        put(ID_COL, "INTEGER PRIMARY KEY AUTOINCREMENT");
        put(NAME_COL, "TEXT");
        put(STATUS_COL, "TEXT");
        put(WHITE_ID_COL, "INTEGER");
        put(BLACK_ID_COL, "INTEGER");
        put(STARTING_TIME_COL, "TEXT");
        put(TYPE_COL, "TEXT");
        put(LAST_TIME_COL, "TEXT");
        put(TOTAL_TIME, "INTEGER");


    }};
    private static final HashMap<String, String> GameDetailsTable = new LinkedHashMap<>() {{
        put(ID_COL, "INTEGER PRIMARY KEY AUTOINCREMENT");
        put(FROM_X_COL, "INTEGER");
        put(FROM_Y_COL, "INTEGER");
        put(TO_X_COL, "INTEGER");
        put(TO_Y_COL, "INTEGER");
        put(PROMOTION, "TEXT");
    }};

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        StringBuilder query = new StringBuilder("CREATE TABLE IF NOT EXISTS  " + TABLE_NAME + " (");
        UsersTable.forEach((colName, colType) -> query.append(colName).append(" ").append(colType).append(", "));
        query.delete(query.length() - 2, query.length());// remove last ", "
        query.append(");");
        db.execSQL(query.toString());
        StringBuilder query2 = new StringBuilder("CREATE TABLE IF NOT EXISTS  " + "UsersGamesTable" + " (");
        UsersGamesTable.forEach((colName, colType) -> query2.append(colName).append(" ").append(colType).append(", "));
        query2.delete(query2.length() - 2, query2.length());// remove last ", "
        query2.append(");");
        db.execSQL(query2.toString());
    }

    public boolean isNameExists(String playerName) {
        SQLiteDatabase db = this.getReadableDatabase();
        boolean ans = isNameExists(playerName, db);
        db.close();
        return ans;
    }

    public boolean isNameExists(String playerName, @NonNull SQLiteDatabase db) {
        Cursor idCursor = db.rawQuery("SELECT " + ID_COL + "\n FROM " + TABLE_NAME + "\n WHERE " + USER_NAME_COL + " = " + "?", new String[]{playerName});
        if (idCursor.getCount() > 0) {
            idCursor.close();
            return true;
        }
        idCursor.close();
        return false;
    }

    public void addNewPlayer(String playerName, String playerPassword) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(USER_NAME_COL, playerName);
        values.put(PASS_COL, playerPassword);
        db.insert(TABLE_NAME, null, values);
        db.close();
    }


    public int getIdByName(String playerName) {
        SQLiteDatabase db = this.getReadableDatabase();
        int id = getIdByName(playerName, db);
        db.close();
        return id;
    }


    public int getIdByName(String playerName, SQLiteDatabase db) {

        Cursor idCursor = db.rawQuery("SELECT " + ID_COL + "\n FROM " + TABLE_NAME + "\n WHERE " + USER_NAME_COL + " = " + "?", new String[]{playerName});
        if (!idCursor.moveToFirst()) {
            idCursor.close();
            throw new IllegalArgumentException("player name doesn't exists");
        }
        int id = idCursor.getInt(0);
        idCursor.close();
        return id;
    }

    public boolean checkPassword(int id, String password) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor passCursor = db.rawQuery("SELECT " + PASS_COL + "\n FROM " + TABLE_NAME + "\n WHERE " + ID_COL + "=" + id, null);
        if (!passCursor.moveToFirst()) {
            passCursor.close();
            db.close();
            return false;
        }
        boolean correctness = passCursor.getString(0).equals(password);
        passCursor.close();
        db.close();
        return correctness;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        deleteAllUsers(db);
    }

    private HashSet<Integer> getAllGamesId() {
        SQLiteDatabase db = this.getReadableDatabase();
        HashSet<Integer> ids = getAllGamesId(db);
        db.close();
        return ids;
    }

    private HashSet<Integer> getAllGamesId(SQLiteDatabase db) {
        HashSet<Integer> ids = new HashSet<>();

        Cursor idCursor = db.query("UsersGamesTable", new String[]{ID_COL}, null, null, null, null, null);
        if (idCursor.moveToFirst()) {

            do {
                ids.add(idCursor.getInt(0));



            } while (idCursor.moveToNext());
        }
        idCursor.close();
        return ids;
    }




    private List<Game> getAllGames() {
        List<Game> games = new LinkedList<>();
        getAllGamesId().forEach((game_id) -> games.add(getGameById(game_id)));
        return games;
    }



    public void updateGame(Game game) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(NAME_COL, game.getGameName());
        values.put(STATUS_COL, game.getGameStatus());
        values.put(TYPE_COL, game.getGameType());
        values.put(STARTING_TIME_COL, game.getGameTime());
        values.put(WHITE_ID_COL, game.getWhitePlayerId());
        values.put(BLACK_ID_COL, game.getBlackPlayerId());
        values.put(LAST_TIME_COL, game.getLastTimePlayed());
        values.put(TOTAL_TIME, game.getTotalTimePlayed());
        db.update("UsersGamesTable", values, ID_COL + " = " + game.getGameID(), null);
        setMoves(game.getGameID(), game.getMoves());
        db.close();
    }

    private List<Triple<Place, Place,Character>> getMoves(Integer id, SQLiteDatabase db) {
        createNewTable("GameDetailsTable", id, GameDetailsTable, db);
        List<Triple<Place, Place,Character>> moves = new ArrayList<>();
        Cursor cursor = db.query("GameDetailsTable" + id, new String[]{FROM_X_COL, FROM_Y_COL, TO_X_COL, TO_Y_COL,PROMOTION}, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                String promotion = cursor.getString(4);
                char promotionChar;
                if (promotion.isEmpty())
                    promotionChar = ' ';
                else
                    promotionChar = promotion.charAt(0);
                moves.add(new Triple<>(new Place(cursor.getInt(0), cursor.getInt(1)), new Place(cursor.getInt(2), cursor.getInt(3)),promotionChar));

            } while (cursor.moveToNext());
        }
        cursor.close();
        return moves;
    }

    public void setMoves(Integer id, List<Triple<Place, Place,Character>> moves) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("GameDetailsTable" + id, null, null);
        createNewTable("GameDetailsTable", id, GameDetailsTable, db);
        for (Triple<Place, Place,Character> move : moves) {
            ContentValues values = new ContentValues();
            values.put(FROM_X_COL, move.getFirst().rank);
            values.put(FROM_Y_COL, move.getFirst().file);
            values.put(TO_X_COL, move.getSecond().rank);
            values.put(TO_Y_COL, move.getSecond().file);
            values.put(PROMOTION,move.getThird()==' '?"":move.getThird().toString());
            db.insert("GameDetailsTable" + id, null, values);
        }

        db.close();
    }

    public Game getGameById(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query("UsersGamesTable", new String[]{NAME_COL, ID_COL, STATUS_COL, TYPE_COL, STARTING_TIME_COL, WHITE_ID_COL, BLACK_ID_COL, LAST_TIME_COL, TOTAL_TIME}, ID_COL + " = " + id, null, null, null, null);
        if (!cursor.moveToFirst()) {
            cursor.close();
            db.close();
            throw new IllegalArgumentException("game id doesn't exists");
        }


        Game game = new Game(cursor.getString(0), cursor.getInt(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getInt(5), cursor.getInt(6), cursor.getString(7), cursor.getLong(8));
        game.setMoves(getMoves(id, db));
        return game;
    }

    public void deleteAllUsers(SQLiteDatabase db) {
        getAllGamesId(db).forEach(id -> db.execSQL("DROP TABLE IF EXISTS " + "GameDetailsTable" + id));
        db.execSQL("DROP TABLE IF EXISTS GameDetailsTable");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public List<Game> getAllUsersGames(int id) {
        List<Game> games = new LinkedList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor idCursor = db.query("UsersGamesTable", new String[]{ID_COL}, WHITE_ID_COL + " = " + id + " OR " + BLACK_ID_COL + " = " + id, null, null, null, null);
        if (idCursor.moveToFirst()) {
            do {
                int gameId = idCursor.getInt(0);
                games.add(getGameById(gameId));
            } while (idCursor.moveToNext());
        }
        idCursor.close();
        db.close();
        return games;
    }

    public void deleteAllGames() {
        SQLiteDatabase db = this.getWritableDatabase();
        getAllGamesId().forEach(id -> db.execSQL("DROP TABLE IF EXISTS " + "GameDetailsTable" + id));
        db.execSQL("DROP TABLE IF EXISTS GameDetailsTable");
    }


    public void deleteAllUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        deleteAllUsers(db);
        db.close();
    }




    public User getUser(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[]{USER_NAME_COL, PASS_COL}, ID_COL + "=" + id, null, null, null, null);
        if (!cursor.moveToFirst()) {
            cursor.close();
            db.close();
            return null;
        }
        String name = cursor.getString(0);
        String pass = cursor.getString(1);
        cursor.close();
        db.close();
        return new User(id, name, pass);
    }

    public List<User> getAllUsers() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[]{ID_COL, USER_NAME_COL, PASS_COL}, null, null, null, null, null);
        List<User> list = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                String pass = cursor.getString(2);
                list.add(new User(id, name, pass));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    public User searchUser(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[]{ID_COL, USER_NAME_COL, PASS_COL}, NAME_COL + "='" + name + "'", null, null, null, null);
        if (!cursor.moveToFirst()) {
            cursor.close();
            db.close();
            return null;
        }
        int id = cursor.getInt(0);
        String pass = cursor.getString(2);
        cursor.close();
        db.close();
        return new User(id, name, pass);
    }

    public void addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(USER_NAME_COL, user.getUsername());
        values.put(PASS_COL, user.getPassword());
        db.insert(TABLE_NAME, null, values);
        //find the id of the new user and change is id to the new one
        Cursor cursor = db.query(TABLE_NAME, new String[]{ID_COL}, USER_NAME_COL + "='" + user.getUsername() + "'", null, null, null, null);
        if (cursor.moveToFirst()) {
            int id = cursor.getInt(0);
            user.setId(id);
        }
        cursor.close();
        db.close();
    }

    public void deleteUser(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, ID_COL + "=" + id, null);
        db.close();
    }

    public void updateUserDetails(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(USER_NAME_COL, user.getUsername());
        values.put(PASS_COL, user.getPassword());
        db.update(TABLE_NAME, values, ID_COL + "=" + user.getId(), null);
        db.close();
    }

    /**
     * creates a new game and inserts it to the games table and into the users' games table
     *
     * @param whiteID  the id of the white player, if cmp then -1
     * @param blackID  the id of the black player, if cmp then -1
     * @param gameName the name of the game
     * @param gameType pvp - player vs. player or pvc - player vs. computer
     * @return the new game
     */
    public Game create_new_game(int whiteID, int blackID, String gameName, String gameType) {
        int id = getAllGamesId().stream().max(Integer::compareTo).orElse(0) + 1;

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(NAME_COL, gameName);
        values.put(STATUS_COL, "on going");
        values.put(ID_COL, id);
        values.put(WHITE_ID_COL, whiteID);
        values.put(BLACK_ID_COL, blackID);
        Cursor timeCursor = db.rawQuery("SELECT date('now')", null);
        timeCursor.moveToFirst();
        values.put(STARTING_TIME_COL, timeCursor.getString(0));
        values.put(LAST_TIME_COL, timeCursor.getString(0));

        timeCursor.close();
        values.put(TYPE_COL, gameType);
        createNewTable("GameDetailsTable", id, GameDetailsTable, db);
        db.insert("UsersGamesTable", null, values);
        db.close();
        return getGameById(id);
    }

    public void updateGameLastTimePlayed(int gameId) {
        Game game = getGameById(gameId);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        Cursor timeCursor = db.rawQuery("SELECT date('now')", null);
        timeCursor.moveToFirst();
        values.put(LAST_TIME_COL, timeCursor.getString(0));
        db.update("UsersGamesTable", values, ID_COL + "=" + gameId, null);
    }

    private void createNewTable(String TABLE_NAME, int id, HashMap<String, String> table, SQLiteDatabase db) {
        StringBuilder sql = new StringBuilder("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + id + " (");
        for (String key : table.keySet()) {
            sql.append(key).append(" ").append(table.get(key)).append(", ");
        }
        sql.delete(sql.length() - 2, sql.length());// remove last ", "
        sql.append(");");
        db.execSQL(sql.toString());
    }
}

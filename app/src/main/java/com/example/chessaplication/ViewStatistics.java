package com.example.chessaplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorSpace;
import android.graphics.Paint;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import board.Controller;
import board.Place;
import board.Side;
import kotlin.Triple;


public class ViewStatistics extends AppCompatActivity {

    ImageView board;
    Game game;
    int playerId;

    DBHelper dbHelper;
    final static int BLACK = 0XFF6F3A25;
    final static int WHITE = 0XFFFFAA6D;
    private GridView gridView;
    TextView gameName;
    TextView opponent;
    TextView playedAs;

    TextView date;
    TextView lastPlayed;
    TextView totalTime;
    TextView numberOfMoves;
    TextView status;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_statistics);
        dbHelper = new DBHelper(this);
        game = dbHelper.getGameById(getIntent().getIntExtra("gameId", 0));
        playerId = getIntent().getIntExtra("ID", 0);
        board = findViewById(R.id.last_location);
        Bitmap bitmap = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888, true, ColorSpace.get(ColorSpace.Named.SRGB));
        Canvas canvas = new Canvas(bitmap);
        draw_board(canvas);
        board.setImageBitmap(bitmap);
        gameName = findViewById(R.id.game_name);
        opponent = findViewById(R.id.opponent_name);
        playedAs = findViewById(R.id.played_as);
        date = findViewById(R.id.date);
        lastPlayed = findViewById(R.id.last_played);
        totalTime = findViewById(R.id.total_time);
        numberOfMoves = findViewById(R.id.num_moves);
        status = findViewById(R.id.status);
        gameName.setText("The Game Name - " + game.getGameName());
        String opponentName = game.getOpponentId(playerId) == -1 ? "Computer" : "Player " + dbHelper.getUser(game.getOpponentId(playerId)).getUsername();
        opponent.setText("Opponent - " + opponentName);
        playedAs.setText("Played as - " + (game.getWhitePlayerId() == playerId ? "White" : "Black"));
        date.setText("Date - " + game.getGameTime());
        lastPlayed.setText("Last Played - " + game.getLastTimePlayed());
        totalTime.setText("Total Time - " + game.getTotalTimePlayed());
        numberOfMoves.setText("Number of Moves - " + game.getMoves().size());
        status.setText("Status - " + game.getGameStatus());


        gridView = findViewById(R.id.moves);
        updateTable();
        updateTable();


    }

    public int getImage(char pieceType, Side side) {
        if (side == Side.WHITE) {
            switch (pieceType) {
                case 'p':
                    return R.drawable.white_pawn;
                case 'r':
                    return R.drawable.white_rook;
                case 'n':
                    return R.drawable.white_knight;
                case 'b':
                    return R.drawable.white_bishop;
                case 'q':
                    return R.drawable.white_queen;
                case 'k':
                    return R.drawable.white_king;
                case ' ':
                    return R.drawable.empty;
            }
        } else {
            switch (pieceType) {
                case 'p':
                    return R.drawable.black_pawn;
                case 'r':
                    return R.drawable.black_rook;
                case 'n':
                    return R.drawable.black_knight;
                case 'b':
                    return R.drawable.black_bishop;
                case 'q':
                    return R.drawable.black_queen;
                case 'k':
                    return R.drawable.black_king;
                case ' ':
                    return R.drawable.empty;
            }
        }
        throw new IllegalArgumentException("Invalid piece type");
    }

    void draw_board(Canvas canvas) {
        Paint paint = new Paint();
        Controller.start(game.getMoves());
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                paint.setColor(((i + j) % 2 == 0 ? WHITE : BLACK));
                canvas.drawRect(i * 25, j * 25, (i + 1) * 25, (j + 1) * 25, paint);
                Bitmap bitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),
                        getImage(Controller.getPieceTypeAt(7 - j, i), Controller.getPieceSideAt(7 - j, i))), 25, 25, false);
                canvas.drawBitmap(bitmap, i * 25, j * 25, paint);
            }
        }
    }

    private void updateTable() {
        List<Object> allScores = new ArrayList<>();
        gridView.setNumColumns(4);
        int i = 1;
        allScores.add("No.");
        allScores.add("from");
        allScores.add("to");
        allScores.add("Promotion");

        for (Triple<Place, Place, Character> move : game.getMoves()) {
            allScores.add(i++);
            allScores.add(move.getFirst().toString());
            allScores.add(move.getSecond().toString());
            allScores.add(move.getThird());
        }


        ArrayAdapter<Object> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, allScores);
        gridView.setAdapter(adapter);
    }


}

package com.example.chessaplication;

import static android.view.View.VISIBLE;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import AI.AIController;
import board.Controller;
import board.Place;
import board.Side;

public class GameActivity extends AppCompatActivity {

    Game game;
    DBHelper dbHelper;
    TextView turn;
    TextView whitePlayer;
    TextView blackPlayer;
    final static int BLACK = (R.color.colorPrimaryDark);
    final static int WHITE = R.color.colorPrimaryWhite;
    final static int SELECTED = Color.GREEN;
    final static int WARNING = Color.RED;
    long time;
    ImageView[][] board = new ImageView[8][8];
    AIController aiController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        time = System.currentTimeMillis();
        dbHelper = new DBHelper(this);
        game = dbHelper.getGameById(getIntent().getIntExtra("gameId", 0));
        turn = findViewById(R.id.turn);
        whitePlayer = findViewById(R.id.white_player);
        blackPlayer = findViewById(R.id.black_player);
        String whiteName;
        String blackName;

        if (game.getWhitePlayerId() == -1)
            whiteName = "Computer";
        else
            whiteName = dbHelper.getUser(game.getWhitePlayerId()).getUsername();
        if (game.getBlackPlayerId() == -1)
            blackName = "Computer";
        else
            blackName = dbHelper.getUser(game.getBlackPlayerId()).getUsername();

        whitePlayer.setText(whiteName);
        blackPlayer.setText(blackName);

        Controller.start(game.getMoves());
        buildBoard();
        updateBoard();
        if (game.getGameType().equals("pvc")) {
            aiController = new AIController(2);
            if (game.getCmpSide().equals(Controller.getSide())) {
                aiController.move();
                updateBoard();
                if (!gameContinues()) close();
                updateDatabase();
            }
        }


    }

    private void updateBoard() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                board[i][j].setImageResource(getImage(Controller.getPieceTypeAt(i, j), Controller.getPieceSideAt(i, j)));//TODO: use svg
                board[i][j].setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                board[i][j].setAdjustViewBounds(false);

                board[i][j].setBackgroundResource((i + j) % 2 != 0 ? WHITE : BLACK);
            }
        }
        if (Controller.getSide() == Side.WHITE) {
            turn.setText("White's Turn");
        } else {
            turn.setText("Black's Turn");
        }
        if (Controller.getEndangeredKing() != null) {
            updateEndangered(Controller.getEndangeredKing());
        }
        if (!gameContinues()) close();

    }

    private void buildBoard() {
        board[0][0] = findViewById(R.id.squareA1);
        board[0][1] = findViewById(R.id.squareB1);
        board[0][2] = findViewById(R.id.squareC1);
        board[0][3] = findViewById(R.id.squareD1);
        board[0][4] = findViewById(R.id.squareE1);
        board[0][5] = findViewById(R.id.squareF1);
        board[0][6] = findViewById(R.id.squareG1);
        board[0][7] = findViewById(R.id.squareH1);
        board[1][0] = findViewById(R.id.squareA2);
        board[1][1] = findViewById(R.id.squareB2);
        board[1][2] = findViewById(R.id.squareC2);
        board[1][3] = findViewById(R.id.squareD2);
        board[1][4] = findViewById(R.id.squareE2);
        board[1][5] = findViewById(R.id.squareF2);
        board[1][6] = findViewById(R.id.squareG2);
        board[1][7] = findViewById(R.id.squareH2);
        board[2][0] = findViewById(R.id.squareA3);
        board[2][1] = findViewById(R.id.squareB3);
        board[2][2] = findViewById(R.id.squareC3);
        board[2][3] = findViewById(R.id.squareD3);
        board[2][4] = findViewById(R.id.squareE3);
        board[2][5] = findViewById(R.id.squareF3);
        board[2][6] = findViewById(R.id.squareG3);
        board[2][7] = findViewById(R.id.squareH3);
        board[3][0] = findViewById(R.id.squareA4);
        board[3][1] = findViewById(R.id.squareB4);
        board[3][2] = findViewById(R.id.squareC4);
        board[3][3] = findViewById(R.id.squareD4);
        board[3][4] = findViewById(R.id.squareE4);
        board[3][5] = findViewById(R.id.squareF4);
        board[3][6] = findViewById(R.id.squareG4);
        board[3][7] = findViewById(R.id.squareH4);
        board[4][0] = findViewById(R.id.squareA5);
        board[4][1] = findViewById(R.id.squareB5);
        board[4][2] = findViewById(R.id.squareC5);
        board[4][3] = findViewById(R.id.squareD5);
        board[4][4] = findViewById(R.id.squareE5);
        board[4][5] = findViewById(R.id.squareF5);
        board[4][6] = findViewById(R.id.squareG5);
        board[4][7] = findViewById(R.id.squareH5);
        board[5][0] = findViewById(R.id.squareA6);
        board[5][1] = findViewById(R.id.squareB6);
        board[5][2] = findViewById(R.id.squareC6);
        board[5][3] = findViewById(R.id.squareD6);
        board[5][4] = findViewById(R.id.squareE6);
        board[5][5] = findViewById(R.id.squareF6);
        board[5][6] = findViewById(R.id.squareG6);
        board[5][7] = findViewById(R.id.squareH6);
        board[6][0] = findViewById(R.id.squareA7);
        board[6][1] = findViewById(R.id.squareB7);
        board[6][2] = findViewById(R.id.squareC7);
        board[6][3] = findViewById(R.id.squareD7);
        board[6][4] = findViewById(R.id.squareE7);
        board[6][5] = findViewById(R.id.squareF7);
        board[6][6] = findViewById(R.id.squareG7);
        board[6][7] = findViewById(R.id.squareH7);
        board[7][0] = findViewById(R.id.squareA8);
        board[7][1] = findViewById(R.id.squareB8);
        board[7][2] = findViewById(R.id.squareC8);
        board[7][3] = findViewById(R.id.squareD8);
        board[7][4] = findViewById(R.id.squareE8);
        board[7][5] = findViewById(R.id.squareF8);
        board[7][6] = findViewById(R.id.squareG8);
        board[7][7] = findViewById(R.id.squareH8);
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                int finalI = i;
                int finalJ = j;
                board[i][j].setOnClickListener(v -> pressed(finalI, finalJ));
            }
        }
    }

    public void finish() {
        game.setTotalTimePlayed(game.getTotalTimePlayed() + (System.currentTimeMillis() - time) / 1000);
        game.setMoves(Controller.getMoves());
        dbHelper.updateGame(game);
        Intent intent = new Intent(this, UserMenuActivity.class);
        setResult(Activity.RESULT_OK, intent);
        super.finish();

    }

    public void goBack(View view) {
        finish();
    }

    public void chosePlace(Place place) {
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

    boolean gameContinues() {
        return Controller.getGameStatus().equals("on going");
    }

    ImageView getSquareAt(@NotNull Place place) {
        return board[place.getRank()][place.getFile()];
    }


    public void annotatePossibleLocations(java.util.List<Place> possibleLocations) {
        possibleLocations.forEach(location -> getSquareAt(location).setBackgroundColor(SELECTED));
    }

    public void restoreLocations(List<Place> possibleLocations) {
        possibleLocations.forEach(location -> getSquareAt(location).setBackgroundResource((location.getFile() + location.getRank()) % 2 != 0 ? WHITE : BLACK));
    }

    /**
     * closes the board. doesn't close the ui, but the player can't play after that the board is closed. prints to the console the moves.
     */
    public void close() {
        game.setGameStatus(Controller.getGameStatus());
        updateDatabase();
        Log.d("GAME END:", "The Game Ended - " + Controller.getGameStatus() + " the move were\n" + Controller.getMoves());
        askForNewGame("The Game Ended - " + Controller.getGameStatus());
    }

    public void pressed(Place place){
        pressed(place.getRank(), place.getFile());
    }
    public void pressed(int rank, int file) {
        updateDatabase();
        if (Controller.getSide() == game.getCmpSide()) {
            return;
        }
        if (Controller.isPieceChosen()) {
            if (Controller.movePieceTo(rank, file)) {
                if (Controller.isReadyToPromote()) {
                    showPromotion();
                    updateBoard();
                    updateDatabase();
                    return; // the player will chose the promotion piece
                }
                updateBoard();
                updateDatabase();
                if (!gameContinues()) close();
                if (Controller.getSide() == game.getCmpSide()) {
                    aiController.move();
                    updateBoard();
                    updateDatabase();
                    if (!gameContinues()) close();

                }
                return;
            }
            restoreLocations(Controller.getAvailablePlaces());
        }

        if (Controller.setMovingPiece(rank, file))
            annotatePossibleLocations(Controller.getAvailablePlaces());


    }

    private void updateDatabase() {
        game.setTotalTimePlayed(game.getTotalTimePlayed() + (System.currentTimeMillis() - time) / 1000);
        time = System.currentTimeMillis();
        game.setMoves(Controller.getMoves());
        game.setGameStatus(Controller.getGameStatus());
        dbHelper.updateGame(game);
    }


    private void showPromotion() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView;
        if (Controller.getSide() == Side.WHITE)
            popupView = inflater.inflate(R.layout.chose_promotion_black, null);
        else popupView = inflater.inflate(R.layout.chose_promotion_white, null);
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = false;
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
        Drawable background = ContextCompat.getDrawable(getApplicationContext(), R.drawable.res_black_menuroundfilled_corner);
        popupWindow.setBackgroundDrawable(background);
        final View fadeBackground = findViewById(R.id.fadeBackground);
        fadeBackground.setVisibility(VISIBLE);
        fadeBackground.animate().alpha(0.5f); // The higher the alpha value the more it will be grayed out
        popupWindow.setOnDismissListener(() -> {
            fadeBackground.animate().alpha(0);
            fadeBackground.setVisibility(View.GONE);
            fadeBackground.setOnClickListener(null);
        });
        popupWindow.showAtLocation(board[0][0], Gravity.CENTER, 0, 0);
        popupWindow.setFocusable(false);

        popupWindow.update();
        popupWindow.getContentView().findViewById(R.id.queen).setOnClickListener(v -> {
            popupWindow.dismiss();
            fadeBackground.animate().alpha(0);
            fadeBackground.setVisibility(View.GONE);
            fadeBackground.setOnClickListener(null);
            Controller.promote('q');
            if (Controller.getEndangeredKing() != null) {
                updateEndangered(Controller.getEndangeredKing());
            }


            updateBoard();
            if (!gameContinues()) close();
            if (Controller.getSide() == game.getCmpSide()) {
                aiController.move();
                updateBoard();
                updateDatabase();
                if (!gameContinues()) close();

            }


        });

        popupWindow.getContentView().findViewById(R.id.rook).setOnClickListener(v -> {
            popupWindow.dismiss();
            fadeBackground.animate().alpha(0);
            fadeBackground.setVisibility(View.GONE);
            fadeBackground.setOnClickListener(null);
            Controller.promote('r');
            if (Controller.getEndangeredKing() != null) {
                updateEndangered(Controller.getEndangeredKing());
            }
            updateBoard();
            if (!gameContinues()) close();
            if (Controller.getSide() == game.getCmpSide()) {
                aiController.move();
                updateBoard();
                updateDatabase();
                if (!gameContinues()) close();

            }
        });
        popupWindow.getContentView().findViewById(R.id.bishop).setOnClickListener(v -> {
            popupWindow.dismiss();
            fadeBackground.animate().alpha(0);
            fadeBackground.setVisibility(View.GONE);
            fadeBackground.setOnClickListener(null);
            Controller.promote('b');
            if (Controller.getEndangeredKing() != null) {
                updateEndangered(Controller.getEndangeredKing());
            }
            updateBoard();
            if (!gameContinues()) close();
            if (Controller.getSide() == game.getCmpSide()) {
                aiController.move();
                updateBoard();
                updateDatabase();
                if (!gameContinues()) close();

            }
        });
        popupWindow.getContentView().findViewById(R.id.knight).setOnClickListener(v -> {
            popupWindow.dismiss();
            fadeBackground.animate().alpha(0);
            fadeBackground.setVisibility(View.GONE);
            fadeBackground.setOnClickListener(null);
            Controller.promote('n');
            if (Controller.getEndangeredKing() != null) {
                updateEndangered(Controller.getEndangeredKing());
            }
            updateBoard();
            if (!gameContinues()) close();
            if (Controller.getSide() == game.getCmpSide()) {
                aiController.move();
                updateBoard();
                updateDatabase();
                if (!gameContinues()) close();

            }

        });

    }

    private void updateEndangered(@NotNull Place endangeredPlace) {
        getSquareAt(endangeredPlace).setBackgroundColor(WARNING);
    }

    /**
     * asks the user if he wants to play again with the same opponent
     * opens a menu with a text box for the message, options to chose the side and 2 buttons - start and exit
     *
     * @param message the message to show the user
     */
    private void askForNewGame(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(message);
        builder.setItems(new String[]{"play with same sides", "switch sides", "play with random sides"}, (dialog, which) -> {
            String input = game.getGameName();
            Pattern lastIntPattern = Pattern.compile("[^0-9]+([0-9]+)$");
            Matcher matcher = lastIntPattern.matcher(input);
            int lastNumberInt = 0;
            String name_without_number = input;
            if (matcher.find()) {
                String someNumberStr = matcher.group(1);
                name_without_number = input.substring(0, input.length() - someNumberStr.length());
                lastNumberInt = Integer.parseInt(someNumberStr);
            }
            String name = name_without_number + (++lastNumberInt);

            if (which == 0)
                game = dbHelper.create_new_game(game.getWhitePlayerId(), game.getBlackPlayerId(), name, game.getGameType());
            if (which == 1) {
                game = dbHelper.create_new_game(game.getBlackPlayerId(), game.getWhitePlayerId(), name, game.getGameType());
            } else {
                Random random = new Random();
                if (random.nextBoolean()) {
                    game = dbHelper.create_new_game(game.getWhitePlayerId(), game.getBlackPlayerId(), name, game.getGameType());
                } else {
                    game = dbHelper.create_new_game(game.getBlackPlayerId(), game.getWhitePlayerId(), name, game.getGameType());
                }
            }
            blackPlayer = findViewById(R.id.black_player);
            String whiteName;
            String blackName;

            if (game.getWhitePlayerId() == -1)
                whiteName = "Computer";
            else
                whiteName = dbHelper.getUser(game.getWhitePlayerId()).getUsername();
            if (game.getBlackPlayerId() == -1)
                blackName = "Computer";
            else
                blackName = dbHelper.getUser(game.getBlackPlayerId()).getUsername();

            whitePlayer.setText(whiteName);
            blackPlayer.setText(blackName);

            Controller.start();
            updateBoard();
            updateDatabase();
            dialog.dismiss();
        });
        builder.setNegativeButton("exit", (dialog, which) -> {
            dialog.dismiss();
            finish();
        });
        builder.show();

    }
}
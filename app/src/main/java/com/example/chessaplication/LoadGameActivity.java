package com.example.chessaplication;

import static android.view.View.VISIBLE;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class LoadGameActivity extends AppCompatActivity {

    Switch sortSwitch;
    EditText search;
    Chip time;
    Chip lastPlayed;
    Chip name;
    Chip opponent;
    List<Game> allGames;

    List<Game> games;
    int id;

    private DBHelper dbHelper;
    private ListView listView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_game);
        dbHelper = new DBHelper(LoadGameActivity.this);

        id = getIntent().getIntExtra("ID", 0);

        listView = findViewById(R.id.listView);
        sortSwitch = findViewById(R.id.sort_switch);
        search = findViewById(R.id.search_text);
        time = findViewById(R.id.time_chip);
        lastPlayed = findViewById(R.id.last_played_chip);
        name = findViewById(R.id.name_chip);
        opponent = findViewById(R.id.opponent_chip);
        allGames = dbHelper.getAllUsersGames(id).stream().filter(game -> game.getGameStatus().equals("on going")).collect(Collectors.toList());
        games = allGames;
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                onEditorAction();
            }

            @Override
            public void afterTextChanged(Editable s) {
                onEditorAction();

            }
        });
        sortSwitch.setOnClickListener(v -> onEditorAction());
        time.setOnClickListener(v -> {
            allGames = allGames.stream().sorted(Comparator.comparing(Game::getGameTime)).
                    collect(Collectors.toList());
            onEditorAction();
        });
        lastPlayed.setOnClickListener(v -> {
            allGames = allGames.stream().sorted((game1, game2) -> {
                if (game1.getLastTimePlayed() == null)
                    return game2.getLastTimePlayed() != null ? 1 : 0;
                else if (game2.getLastTimePlayed() == null)
                    return -1;
                else
                    return game1.getLastTimePlayed().compareTo(game2.getLastTimePlayed());
            }).collect(Collectors.toList());
            onEditorAction();
        });
        name.setOnClickListener(v -> {
            allGames = allGames.stream().sorted(Comparator.comparing(Game::getGameName)).
                    collect(Collectors.toList());
            onEditorAction();
        });
        opponent.setOnClickListener(v -> {
            allGames = allGames.stream().sorted(Comparator.comparing(game -> dbHelper.getUser(game.getOpponentId(id)).getUsername())).
                    collect(Collectors.toList());
            onEditorAction();
        });

        listView.setOnItemClickListener((parent, view, position, id1) -> {
            int gameId = games.get(position / 6).getGameID();
            dbHelper.updateGameLastTimePlayed(gameId);
            sendOn(gameId);
        });
        updateTable();


    }

    private void updateTable() {
        List<Object> objects = new ArrayList<>();
        int i = 1;
        for (Game game : games) {
            objects.add(String.format("Game %s, number %d", game.getGameName(), i++));
            String opponentName = game.getOpponentId(id) == -1 ? "Computer" : "Player " + dbHelper.getUser(game.getOpponentId(id)).getUsername();
            objects.add(String.format("\t\t\u2022Opponent: %s", opponentName));
            objects.add(String.format("\t\t\u2022Time Created: %s", game.getGameTime()));
            objects.add(String.format("\t\t\u2022Last played: %s", game.getLastTimePlayed()));
            objects.add(String.format("\t\t\u2022Total Time Played: %s", game.getTotalTimePlayed()));
            objects.add("\t\t\u2022Played as: " + (game.getWhitePlayerId()==id ? "White" : "Black"));
        }
        ArrayAdapter<Object> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, objects);
        listView.setAdapter(adapter);

    }

    private void onEditorAction() {
        if (sortSwitch.isChecked())
            games = allGames.stream().filter(game -> dbHelper.getUser(game.getOpponentId(id)).
                    getUsername().contains(search.getText().toString())).collect(Collectors.toList());
        else
            games = allGames.stream().filter(game -> game.getGameName().
                    contains(search.getText().toString())).collect(Collectors.toList());
        updateTable();
    }

    private void sendOn(int gameId) {
        Game game = dbHelper.getGameById(gameId);
        if (Objects.equals(game.getGameType(), "pvc")) {
            moveToGame(gameId);
        } else {
            User opponent = dbHelper.getUser(game.getOpponentId(id));
            LayoutInflater inflater = (LayoutInflater)
                    getSystemService(LAYOUT_INFLATER_SERVICE);
            View popupView = inflater.inflate(R.layout.pop_up_login, null);
            int width = LinearLayout.LayoutParams.WRAP_CONTENT;
            int height = LinearLayout.LayoutParams.WRAP_CONTENT;
            boolean focusable = false; // lets taps outside the popup also dismiss it
            final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
            Drawable background = ContextCompat.getDrawable(getApplicationContext(), R.drawable
                    .res_black_menuroundfilled_corner);
            popupWindow.setBackgroundDrawable(background);
            final View fadeBackground = findViewById(R.id.fadeBackground);
            fadeBackground.setVisibility(VISIBLE);
            fadeBackground.animate().alpha(0.5f); // The higher the alpha value the more it will be grayed out
            popupWindow.setOnDismissListener(() ->
            {
                fadeBackground.animate().alpha(0);
                fadeBackground.setVisibility(View.GONE);
                fadeBackground.setOnClickListener(null);
            });
            popupWindow.showAtLocation(sortSwitch, Gravity.CENTER, 0, 0);
            popupWindow.setFocusable(true);
            popupWindow.setOutsideTouchable(true);

            popupWindow.update();
            ((TextView) popupWindow.getContentView().findViewById(R.id.enterPassword)).setText("Enter password for " + opponent.getUsername());
            EditText password = popupWindow.getContentView().findViewById(R.id.password);
            popupWindow.getContentView().findViewById(R.id.cancel_button).setOnClickListener(v -> {
                popupWindow.dismiss();
                fadeBackground.animate().alpha(0);
                fadeBackground.setVisibility(View.GONE);
                fadeBackground.setOnClickListener(null);
            });
            popupWindow.getContentView().findViewById(R.id.continue_button).setOnClickListener(v -> {
                if (opponent.getPassword().equals(password.getText().toString())) {
                    popupWindow.dismiss();
                    fadeBackground.animate().alpha(0);
                    fadeBackground.setVisibility(View.GONE);
                    fadeBackground.setOnClickListener(null);
                    moveToGame(gameId);
                } else {
                    Toast.makeText(getApplicationContext(), "Wrong password", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    public void moveToGame(int gameId) {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("gameId", gameId);
        startActivity(intent);
        finish();
    }
}

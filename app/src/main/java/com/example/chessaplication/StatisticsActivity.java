package com.example.chessaplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class StatisticsActivity extends AppCompatActivity {

    Switch sortSwitch;
    EditText search;
    Chip time;
    Chip lastPlayed;
    Chip name;
    Chip opponent;
    Chip status;

    List<Game> allGames;

    List<Game> games;
    int id;

    private DBHelper dbHelper;
    private ListView listView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        dbHelper = new DBHelper(StatisticsActivity.this);

        id = getIntent().getIntExtra("ID", 0);

        listView = (ListView) findViewById(R.id.list);
        sortSwitch = findViewById(R.id.sort_switch);
        search = findViewById(R.id.search_text);
        time = findViewById(R.id.time_chip);
        lastPlayed = findViewById(R.id.last_played_chip);
        name = findViewById(R.id.name_chip);
        opponent = findViewById(R.id.opponent_chip);
        status = findViewById(R.id.status_chip);
        allGames = dbHelper.getAllUsersGames(id);
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
            int gameId = games.get(position / 7).getGameID();
            Intent intent = new Intent(StatisticsActivity.this, ViewStatistics.class);
            intent.putExtra("ID", id);
            intent.putExtra("gameId", gameId);
            startActivity(intent);
        });
        status.setOnClickListener(v -> {
            allGames = allGames.stream().sorted(Comparator.comparing(Game::getGameStatus)).
                    collect(Collectors.toList());
            onEditorAction();
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
            objects.add(String.format("\t\t\u2022Status: %s", game.getGameStatus()));
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


}
package com.example.chessaplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.appcompat.app.AppCompatActivity;

public class UserMenuActivity extends AppCompatActivity implements ActivityResultCallback<ActivityResult> {

    Button statistics_button;
    Button create_game_button;
    Button load_game_button;
    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_menu);
        statistics_button = findViewById(R.id.statistics_button);
        create_game_button = findViewById(R.id.create_game_button);
        load_game_button = findViewById(R.id.load_game_button);
        statistics_button.setOnClickListener(this::onStatistics);
        create_game_button.setOnClickListener(this::onCreateGame);
        load_game_button.setOnClickListener(this::onLoadGame);
        id = getIntent().getIntExtra("ID", 0);
        try {
            Class<?> cls = Class.forName(getIntent().getStringExtra("nextCls"));
            if(cls != UserMenuActivity.class){
                Intent intent = new Intent(this, cls);
                intent.putExtra("ID", id);
                startActivity(intent);
            }
        } catch (ClassNotFoundException ignored) {

        }


    }

    private void onCreateGame(View view) {
        Intent intent = new Intent(this, CreateGameActivity.class);
        intent.putExtra("ID", id);
        startActivity(intent);
    }

    private void onStatistics(View view) {
        Intent intent = new Intent(this, StatisticsActivity.class);
        intent.putExtra("ID", id);
        startActivity(intent);
    }

    private void onLoadGame(View view) {
        Intent intent = new Intent(this, LoadGameActivity.class);
        intent.putExtra("ID", id);
        startActivity(intent);

    }


    @Override
    public void onActivityResult(ActivityResult result) {

    }
}
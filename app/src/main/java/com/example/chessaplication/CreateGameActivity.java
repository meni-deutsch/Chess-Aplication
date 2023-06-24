package com.example.chessaplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


public class CreateGameActivity extends AppCompatActivity implements ActivityResultCallback<ActivityResult> {

    EditText name;
    Switch cmp;
    RadioButton white;
    RadioButton black;
    RadioButton random;
    Button create;
    DBHelper dbHelper;
    SIDES side = null;
    boolean isCmp;


    enum SIDES {
        WHITE, BLACK, RANDOM
    }

    private ActivityResultLauncher<Intent> activityLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_game);
        dbHelper = new DBHelper(CreateGameActivity.this);
        name = findViewById(R.id.game_name);
        cmp = findViewById(R.id.cmp);
        white = findViewById(R.id.radioButtonWhite);
        black = findViewById(R.id.radioButtonBlack);
        random = findViewById(R.id.radioButtonRandom);
        create = findViewById(R.id.submit_button);
        create.setOnClickListener(this::onCreateGame);
        activityLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), this);
    }

    private void onCreateGame(View view) {
        String gameName = name.getText().toString();
        if (gameName.isEmpty()) {
            Toast.makeText(this, "Name may not be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        //TODO check if the name already exists and if so send a warning that the user can ignore
        isCmp = this.cmp.isChecked();
        if (white.isChecked()) {
            side = SIDES.WHITE;
        } else if (black.isChecked()) {
            side = SIDES.BLACK;
        } else if (random.isChecked()) {
            side = SIDES.RANDOM;
        } else {
            Toast.makeText(this, "Choose side", Toast.LENGTH_SHORT).show();
            return;
        }
        if (side == SIDES.RANDOM) {
            if ((int) (Math.random() * 2) == 0)
                side = SIDES.WHITE;
            else
                side = SIDES.BLACK;
        }
        if (!isCmp) {
            Intent intent = new Intent(this, LoginActivity.class);
            activityLauncher.launch(intent);
        } else {

            int whiteID, blackID;
            if (side == SIDES.WHITE) {
                whiteID = getIntent().getIntExtra("ID",0);
                blackID = -1;
            } else {
                blackID = getIntent().getIntExtra("ID",0);;
                whiteID = -1;
            }
            int gameID = dbHelper.create_new_game(whiteID, blackID, gameName, "pvc").getGameID();
            Intent intent = new Intent(this, GameActivity.class);
            intent.putExtra("gameId", gameID);
            startActivity(intent);
        }
    }

    @Override
    public void onActivityResult(@NonNull ActivityResult result) {
        if(result.getResultCode()!= Activity.RESULT_OK)
            return;
        int id1 = getIntent().getIntExtra("ID",0);
        int id2 = result.getData().getIntExtra("ID", 0);
        if(id1 == id2){
            Intent intent = new Intent(this, LoginActivity.class);
            intent.putExtra("toast","users must be different");
            activityLauncher.launch(intent);
            return;
        }
        String gameName = name.getText().toString();


        int whiteID, blackID;
        if (side == SIDES.WHITE) {
            whiteID = id1;
            blackID = id2;
        } else {
            blackID = id1;
            whiteID = id2;
        }
        int gameID = dbHelper.create_new_game(whiteID, blackID, gameName, "pvp").getGameID();
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("gameId", gameID);
        startActivity(intent);
        finish();
    }
}

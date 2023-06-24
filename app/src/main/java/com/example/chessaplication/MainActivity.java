package com.example.chessaplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity implements ActivityResultCallback<ActivityResult> {
    final static private boolean DEBUGGING_REGISTER = false;
    final static private boolean CREATE_TABLE_IF_NOT_EXISTS = true;
    final static private boolean OPEN_DATA_BASE_WITHOUT_CLOSING = true;

    final static public boolean REMOVE_PASSWORD_LIMITATION = false
            ;


    Button btnNewGame;
    Button btnLoadGame;
    Button btnLogin;
    Button btnRegister;
    Class<?> nextCls;
    private DBHelper dbHelper;
    private ActivityResultLauncher<Intent> activityLauncher;

    private void debuggingTools() {


        if (DEBUGGING_REGISTER) {
            dbHelper.deleteAllUsers();
        }

        if (OPEN_DATA_BASE_WITHOUT_CLOSING) {
            dbHelper.openDataBase();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHelper = new DBHelper(MainActivity.this);
        debuggingTools();

        btnNewGame = findViewById(R.id.new_game_button);
        btnLoadGame = findViewById(R.id.continue_button);
        btnLogin = findViewById(R.id.log_in_button);
        btnRegister = findViewById(R.id.register_button);
        activityLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), this);
        btnNewGame.setOnClickListener(this::onNewGame);
        btnLoadGame.setOnClickListener(this::onLoadGame);
        btnLogin.setOnClickListener(this::onLogin);
        btnRegister.setOnClickListener(this::onRegister);
    }

    private void onRegister(View view) {
         nextCls =  UserMenuActivity.class;

        callActivity(RegisterActivity.class);
    }

    private void onLogin(View view) {
        nextCls =  UserMenuActivity.class;
        callActivity(LoginActivity.class);

    }

    private void onLoadGame(View view) {
        nextCls =  LoadGameActivity.class;
        callActivity(LoginActivity.class);

    }

    private void onNewGame(View view) {
        nextCls =  CreateGameActivity.class;
        callActivity(LoginActivity.class);
    }

    private void callActivity(Class<?> cls) {
        Intent intent = new Intent(getApplicationContext(), cls);
        activityLauncher.launch(intent);
    }

    @Override
    public void onActivityResult(@NonNull ActivityResult result) {
        if (result.getResultCode() == Activity.RESULT_CANCELED) return;
        Intent intent = new Intent(getApplicationContext(), UserMenuActivity.class);
        System.out.println(result.getClass() + "hello");
        intent.putExtra("ID", result.getData().getIntExtra("ID", 0));
        intent.putExtra("nextCls", nextCls.getName());

        startActivity(intent);
    }

}
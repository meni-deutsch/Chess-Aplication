package com.example.chessaplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class RegisterActivity extends AppCompatActivity {
    EditText username;
    EditText password;
    EditText secondPassword;
    Button register;
    Button goBack;
    private DBHelper dbHelper;

    private static final Set<String> forbiddenNames = new HashSet<>(Arrays.asList("admin", "administrator", "root", "user", "username", "login", "register", "guest", "user1", "user2", "user3", "user4", "user5", "user6", "user7", "user8", "user9",
            "cmp", "computer", "cpu", "bot", "robot", "ai", "artificial intelligence", "chess", "chessapp", "chessapplication", "chess_app", "chess_application", "chessbot", "chess_robot", "chess_bot", "chessrobot"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        secondPassword = findViewById(R.id.second_password);
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                secondPassword.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        register = findViewById(R.id.register);
        goBack = findViewById(R.id.go_back);

        dbHelper = new DBHelper(RegisterActivity.this);

        goBack.setOnClickListener(this::goBack);
        register.setOnClickListener(this::onRegister);

    }

    private void onRegister(View view) {
        String name = username.getText().toString();
        String pass = password.getText().toString();
        String verification = secondPassword.getText().toString();

        if (name.isEmpty()) {
            username.setError("username is empty");
            return;
        }
        if (name.length() < 3) {
            username.setError("username is too short");
            return;
        }
        if (name.length() > 20) {
            username.setError("username is too long");
            return;
        }

        if (name.contains(" ")) {
            username.setError("username can't contain spaces");
            return;
        }

        if (pass.isEmpty()) {
            password.setError("password is empty");
            return;
        }
        if (!MainActivity.REMOVE_PASSWORD_LIMITATION) {

            if (pass.length() < 6) {
                password.setError("password is too short");
                return;
            }
        }
        if (!verification.equals(pass)) {
            secondPassword.setError("passwords don't match");
            return;
        }
        if (dbHelper.isNameExists(name)) {
            username.setError("username already exists");
            return;
        }
        if (forbiddenNames.contains(name.toLowerCase())) {
            username.setError("username is forbidden");
            return;
        }
        dbHelper.addNewPlayer(name, pass);
        Toast toast = Toast.makeText(this, "user registered", Toast.LENGTH_SHORT);
        toast.show();
        Intent intent = new Intent();
        intent.putExtra("ID", dbHelper.getIdByName(name));
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    private void goBack(View view) {
        Toast toast = Toast.makeText(this, "going back", Toast.LENGTH_SHORT);
        toast.show();

        finish();
    }
}

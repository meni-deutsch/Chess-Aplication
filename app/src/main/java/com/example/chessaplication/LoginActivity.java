package com.example.chessaplication;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.zip.CheckedOutputStream;

public class LoginActivity extends AppCompatActivity implements ActivityResultCallback<ActivityResult> {


    EditText username;
    EditText password;
    Button login;
    Button register;
    private DBHelper dbHelper;
    private ActivityResultLauncher<Intent> activityLauncher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        String toast = getIntent().getStringExtra("toast");
        if(toast != null){
            Toast.makeText(LoginActivity.this,toast,Toast.LENGTH_SHORT).show();
        }
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login);
        register = findViewById(R.id.register);
        activityLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), this);
        dbHelper = new DBHelper(LoginActivity.this);

        login.setOnClickListener(this::onLogin);
        register.setOnClickListener(this::onRegister);


    }

    private void onRegister(View view) {
        Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
        activityLauncher.launch(intent);
    }

    public void onLogin(View v) {
        String name = username.getText().toString();
        String pass = password.getText().toString();
        if (!dbHelper.isNameExists(name) ){
            Toast toast = Toast.makeText(this, "wrong username or password!", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        int id = dbHelper.getIdByName(name);
        if (dbHelper.checkPassword(id, pass)) {
            Intent intent = new Intent();
            intent.putExtra("ID",id);
            setResult(Activity.RESULT_OK, intent);
            finish();
        }else{
            Toast toast = Toast.makeText(this, "wrong username or password!", Toast.LENGTH_SHORT);
            toast.show();
        }

    }

    @Override
    public void onActivityResult(ActivityResult result) {
        if (result.getResultCode() != Activity.RESULT_OK)
            return;
        int id =result.getData().getIntExtra("ID",0);
        Intent intent = new Intent();
        intent.putExtra("ID",id);
        setResult(Activity.RESULT_OK, intent);
        finish();


    }
}
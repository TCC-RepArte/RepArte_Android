package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login1);

        //declarando elementos
        TextView titulo, text_ou;
        EditText username, senha1;
        Button btn_login1, button5;
        ImageView avatar, lock;

        // iniciando elementos pela id
        titulo = findViewById(R.id.titulo);
        text_ou = findViewById(R.id.text_ou);
        username = findViewById(R.id.username);
        senha1 = findViewById(R.id.senha1);
        btn_login1 = findViewById(R.id.btn_login1);
        button5 = findViewById(R.id.button5);
        avatar = findViewById(R.id.imageView14);
        lock = findViewById(R.id.imageView15);

        //evento do botão de registrar (ir pra página de sign-up)
        button5.setOnClickListener(view -> {
            Intent intent = new Intent(Login.this, SignUp.class);
            startActivity(intent);
        });
    }
}